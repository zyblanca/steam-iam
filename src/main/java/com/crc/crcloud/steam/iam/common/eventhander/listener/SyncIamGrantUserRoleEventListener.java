package com.crc.crcloud.steam.iam.common.eventhander.listener;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.core.lang.Assert;
import cn.hutool.json.JSONUtil;
import com.crc.crcloud.steam.iam.api.feign.IamServiceClient;
import com.crc.crcloud.steam.iam.common.config.ChoerodonDevOpsProperties;
import com.crc.crcloud.steam.iam.common.enums.MemberType;
import com.crc.crcloud.steam.iam.common.exception.IamAppCommException;
import com.crc.crcloud.steam.iam.common.utils.EntityUtil;
import com.crc.crcloud.steam.iam.common.utils.SagaTopic;
import com.crc.crcloud.steam.iam.model.dto.*;
import com.crc.crcloud.steam.iam.model.dto.payload.UserMemberEventPayload;
import com.crc.crcloud.steam.iam.model.dto.user.IamMemberRoleWithRoleDTO;
import com.crc.crcloud.steam.iam.model.event.IamGrantUserRoleEvent;
import com.crc.crcloud.steam.iam.model.feign.role.MemberRoleDTO;
import com.crc.crcloud.steam.iam.model.feign.role.RoleDTO;
import com.crc.crcloud.steam.iam.model.feign.user.UserDTO;
import com.crc.crcloud.steam.iam.service.IamOrganizationService;
import com.crc.crcloud.steam.iam.service.IamProjectService;
import io.choerodon.asgard.saga.annotation.Saga;
import io.choerodon.asgard.saga.producer.StartSagaBuilder;
import io.choerodon.asgard.saga.producer.TransactionalProducer;
import io.choerodon.core.iam.InitRoleCode;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.core.validator.ValidList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * 用户被授权角色事件-同步到老行云
 *
 * @author LiuYang
 * @date 2019/11/25
 */
@Slf4j
@Component
public class SyncIamGrantUserRoleEventListener implements ApplicationListener<IamGrantUserRoleEvent> {
    @Autowired
    private IamServiceClient iamServiceClient;
    @Autowired
    private ChoerodonDevOpsProperties properties;
    @Autowired
    private IamOrganizationService iamOrganizationService;
    @Autowired
    private IamProjectService iamProjectService;

    @Autowired
    private TransactionalProducer producer;


    @Override
    public void onApplicationEvent(IamGrantUserRoleEvent event) {
        TimeInterval timer = DateUtil.timer();
        log.info("同步用户[{}]授权角色数据", event.getSource().getLoginName());
        try {
            sync(event.getSource(), event.getRoles());
        } catch (Exception ex) {
            log.info("同步用户[{}]角色失败", event.getSource().getLoginName());
        }

        log.info("同步用户[{}]授权角色数据;结束,耗时:{}/ms", event.getSource().getLoginName(), timer.interval());
    }

    /**
     * 该接口全量增量同步均支持
     * <p>会忽略掉项目或者组织创建时间在{@link ChoerodonDevOpsProperties#getConditionDate()}之后的数据</p>
     *
     * @param user  用户
     * @param roles 授权的角色
     */
    @Saga(code = SagaTopic.MemberRole.MEMBER_ROLE_UPDATE, description = "新行云人员授权事件", inputSchemaClass = List.class)
    public void sync(@NotNull IamUserDTO user, @NotEmpty List<IamMemberRoleWithRoleDTO> roles) throws RuntimeException {
        roles = roles.stream().filter(t -> Objects.equals(t.getIamMemberRole().getMemberId(), user.getId()) && Objects.equals(t.getIamMemberRole().getMemberType(), MemberType.USER.getValue()))
                .filter(role -> {
                    if (Objects.equals(role.getRole().getCode(), InitRoleCode.SITE_ADMINISTRATOR)) {
                        log.warn("用户[{}]平台管理员不同步到老行云", user.getLoginName());
                        return false;
                    }
                    return true;
                })
                .collect(Collectors.toList());
        Optional<UserDTO> iamServerUserOpt = getIamServerUserByLoginName(user.getLoginName());
        if (!iamServerUserOpt.isPresent()) {
            log.info("用户[{}]在老行云平台未找到", user.getLoginName());
            return;
        }
        final UserDTO iamServerUser = iamServerUserOpt.get();
        @NotNull List<RoleDTO> iamServerRoles = getIamServerRoles(roles.stream().map(IamMemberRoleWithRoleDTO::getRole).collect(Collectors.toList()));
        log.info("用户[{}]同步新行云角色[{}]到老行云角色[{}]", user.getLoginName()
                , roles.stream().map(IamMemberRoleWithRoleDTO::getRole).map(IamRoleDTO::getName).collect(Collectors.joining(","))
                , iamServerRoles.stream().map(RoleDTO::getName).collect(Collectors.joining(","))
        );
        if (iamServerRoles.isEmpty()) {
            log.warn("老行云角色列表为空");
            return;
        }
        Map<ResourceLevel, Function<List<IamMemberRoleDTO>, List<MemberRoleDTO>>> grantConsumer = new HashMap<>(3);
        final List<Long> memberIds = CollUtil.newArrayList(iamServerUser.getId());
        final DateTime conditionDate = DateTime.of(properties.getConditionDate());
        log.info("新老数据判定时间conditionDate: {}", conditionDate);


        grantConsumer.put(ResourceLevel.ORGANIZATION, items -> {
            return this.grantRole(items, list -> {
                Long organizationId = CollUtil.getFirst(list).getSourceId();
                if (iamOrganizationService.get(organizationId).map(IamOrganizationDTO::getCreationDate)/*.filter(conditionDate::isAfter)*/.isPresent()) {

                    ResponseEntity<List<MemberRoleDTO>> result = this.iamServiceClient.createOrUpdateOnOrganizationLevel(false, organizationId, MemberType.USER.getValue(), memberIds, list);
                    sendSaga(organizationId, ResourceLevel.ORGANIZATION, user);
                    return result;
                }
                return new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK);
            });
        });
        grantConsumer.put(ResourceLevel.PROJECT, items -> {
            return this.grantRole(items, list -> {
                Long projectId = CollUtil.getFirst(list).getSourceId();
                if (iamProjectService.get(projectId).map(IamProjectDTO::getCreationDate)/*.filter(conditionDate::isAfter)*/.isPresent()) {
                    ResponseEntity<List<MemberRoleDTO>> result = this.iamServiceClient.createOrUpdateOnProjectLevel(false, projectId, MemberType.USER.getValue(), memberIds, list);
                    sendSaga(projectId, ResourceLevel.ORGANIZATION, user);
                    return result;
                }
                return new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK);
            });
        });

        grantConsumer.put(ResourceLevel.SITE, items -> {
            return this.grantRole(items, list -> this.iamServiceClient.createOrUpdateOnSiteLevel(false, MemberType.USER.getValue(), memberIds, list));
        });

        List<List<@NotNull IamMemberRoleDTO>> groupLevelRole = CollUtil.groupByField(roles.stream().map(IamMemberRoleWithRoleDTO::getIamMemberRole).collect(Collectors.toList()), EntityUtil.getSimpleFieldToCamelCase(RoleDTO::getLevel));
        for (List<IamMemberRoleDTO> items : groupLevelRole) {
            String sourceType = CollUtil.getFirst(items).getSourceType();
            Optional<ResourceLevel> resourceLevel = Arrays.stream(ResourceLevel.values()).filter(t -> Objects.equals(t.value(), sourceType)).findFirst();
            if (resourceLevel.isPresent() && grantConsumer.containsKey(resourceLevel.get())) {
                //转换用户为老行云平台用户
                List<IamMemberRoleDTO> collect = items.stream().map(t -> {
                    IamMemberRoleDTO n = new IamMemberRoleDTO();
                    BeanUtil.copyProperties(t, n);
                    n.setMemberId(iamServerUser.getId());
                    return n;
                }).collect(Collectors.toList());
                grantConsumer.get(resourceLevel.get()).apply(collect);
            } else {
                log.warn("存在不兼容的级别[{}],将忽略", sourceType);
            }
        }
    }


    //发起saga事件
    //saga事件触发前提 老平台已经有数据
    private void sendSaga(Long sourceId, ResourceLevel resourceLevel, IamUserDTO user) {
        //↓↓↓↓↓↓↓↓↓↓↓↓↓↓参数封装↓↓↓↓↓↓↓↓↓↓↓↓↓
        List<UserMemberEventPayload> userMemberEventPayloads = new ArrayList<>();

        UserMemberEventPayload userMemberEventPayload = new UserMemberEventPayload();
        userMemberEventPayload.setResourceId(sourceId);
        userMemberEventPayload.setResourceType(resourceLevel.value());
        userMemberEventPayload.setUserId(user.getId());
        userMemberEventPayload.setUsername(user.getLoginName());

        userMemberEventPayloads.add(userMemberEventPayload);
        //↑↑↑↑↑↑↑↑↑↑参数封装↑↑↑↑↑↑↑↑↑↑
        //发起saga事件
        producer.applyAndReturn(
                StartSagaBuilder
                        .newBuilder()
                        .withLevel(resourceLevel)
                        .withSourceId(sourceId)
                        .withSagaCode(SagaTopic.MemberRole.MEMBER_ROLE_UPDATE),
                builder -> {
                    builder.withPayloadAndSerialize(userMemberEventPayloads)
                            .withRefType(resourceLevel.value())
                            .withRefId(user.getId().toString());
                    return userMemberEventPayloads;
                });
    }


    /**
     * 组织授权
     *
     * @param items 按照级别分组之后的数据
     * @return
     */
    private List<MemberRoleDTO> grantRole(List<IamMemberRoleDTO> items, @NotNull Function<ValidList<MemberRoleDTO>, ResponseEntity<List<MemberRoleDTO>>> sourceFeignFunc) {
        List<MemberRoleDTO> result = new ArrayList<>(items.size());
        //同资源ID分组
        List<List<IamMemberRoleDTO>> groupBySourceId = CollUtil.groupByField(items, EntityUtil.getSimpleFieldToCamelCase(IamMemberRoleDTO::getSourceId));
        for (List<IamMemberRoleDTO> list : groupBySourceId) {
            //todo 是否进行重试处理
            ResponseEntity<List<MemberRoleDTO>> responseEntity = sourceFeignFunc.apply(convert(list));
            Assert.notNull(responseEntity, "sourceFeignFunc result is must not null", responseEntity);
            //noinspection ConstantConditions
            result.addAll(responseEntity.getBody());
            IamMemberRoleDTO first = CollUtil.getFirst(list);
            String collect = list.stream().map(IamMemberRoleDTO::getRoleId).map(Object::toString).collect(Collectors.joining(","));
            log.info("授权类型[{}|{}]授权角色[{}],结果: {}", first.getSourceType(), first.getSourceId(), collect, JSONUtil.toJsonStr(responseEntity.getBody()));
        }
        return result;
    }

    private ValidList<MemberRoleDTO> convert(List<IamMemberRoleDTO> iamMemberRoleDTOS) {
        final ValidList<MemberRoleDTO> memberRoleDTOS = new ValidList<>();
        iamMemberRoleDTOS.stream().map(t -> {
            MemberRoleDTO memberRoleDTO = new MemberRoleDTO();
            BeanUtil.copyProperties(t, memberRoleDTO, CopyOptions.create().ignoreError().ignoreNullValue());
            return memberRoleDTO;
        }).forEach(memberRoleDTOS::add);
        return memberRoleDTOS;
    }

    /**
     * 通过新的角色列表查询老的角色列表
     *
     * @param roles 新关联的角色列表
     * @return 老的那边的角色列表
     */
    @NotNull
    private List<RoleDTO> getIamServerRoles(List<IamRoleDTO> roles) {
        final List<RoleDTO> result = new ArrayList<>(roles.size());
        final RetryTemplate retryTemplate = new RetryTemplate();
        //重试三次接口
        retryTemplate.setRetryPolicy(new SimpleRetryPolicy());
        //判定接口是否成功
        AtomicReference<Predicate<ResponseEntity<RoleDTO>>> isSuccess = new AtomicReference<>(t -> t.getStatusCode().is2xxSuccessful());
        isSuccess.getAndUpdate(is -> is.and(t -> JSONUtil.parseObj(t.getBody()).containsKey(EntityUtil.getSimpleFieldToCamelCase(RoleDTO::getId))));
        for (IamRoleDTO role : roles) {
            RetryCallback<RoleDTO, RuntimeException> retryCallback = retryContext -> {
                ResponseEntity<RoleDTO> responseEntity = iamServiceClient.queryByCode(role.getCode());
                if (isSuccess.get().negate().test(responseEntity)) {
                    throw new IamAppCommException("other.unable.success");
                }
                return responseEntity.getBody();
            };
            @Nullable final RoleDTO roleDTO = retryTemplate.execute(retryCallback, retryContext -> {
                log.error("通过code[{}]查询对应老行云角色失败: {}", role.getCode(), retryContext.getLastThrowable().getMessage(), retryContext.getLastThrowable());
                return null;
            });
            Optional.ofNullable(roleDTO).ifPresent(result::add);
        }
        return result;
    }

    /**
     * 根据邮箱获取对应的老行云用户
     *
     * @param loginName 邮箱
     * @return 老行云用户
     */
    private Optional<UserDTO> getIamServerUserByLoginName(@NotBlank String loginName) {
        final RetryTemplate retryTemplate = new RetryTemplate();
        //重试三次接口
        retryTemplate.setRetryPolicy(new SimpleRetryPolicy());
        //判定接口是否成功
        AtomicReference<Predicate<ResponseEntity<UserDTO>>> isSuccess = new AtomicReference<>(t -> t.getStatusCode().is2xxSuccessful());
        isSuccess.getAndUpdate(is -> is.and(t -> Objects.nonNull(t.getBody())));
        isSuccess.getAndUpdate(is -> is.and(t -> JSONUtil.parseObj(t.getBody()).containsKey(EntityUtil.getSimpleFieldToCamelCase(UserDTO::getId))));
        RetryCallback<UserDTO, RuntimeException> retryCallback = retryContext -> {
            ResponseEntity<UserDTO> responseEntity = iamServiceClient.queryByLoginName(loginName);
            if (isSuccess.get().negate().test(responseEntity)) {
                throw new IamAppCommException("other.unable.success");
            }
            return responseEntity.getBody();
        };
        @Nullable final UserDTO userDTO = retryTemplate.execute(retryCallback, retryContext -> {
            log.error("通过loginName[{}]查询对应老行云角色失败: {}", loginName, retryContext.getLastThrowable().getMessage(), retryContext.getLastThrowable());
            return null;
        });
        return Optional.ofNullable(userDTO);
    }

    public SyncIamGrantUserRoleEventListener() {
        log.info("已注册用户授权角色事件-角色同步到老行云");
    }
}

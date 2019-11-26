package com.crc.crcloud.steam.iam.common.eventhander.listener;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.core.lang.Assert;
import cn.hutool.json.JSONUtil;
import com.crc.crcloud.steam.iam.api.feign.IamServiceClient;
import com.crc.crcloud.steam.iam.common.enums.MemberType;
import com.crc.crcloud.steam.iam.common.exception.IamAppCommException;
import com.crc.crcloud.steam.iam.common.utils.EntityUtil;
import com.crc.crcloud.steam.iam.model.dto.IamMemberRoleDTO;
import com.crc.crcloud.steam.iam.model.dto.IamRoleDTO;
import com.crc.crcloud.steam.iam.model.dto.IamUserDTO;
import com.crc.crcloud.steam.iam.model.dto.user.IamMemberRoleWithRoleDTO;
import com.crc.crcloud.steam.iam.model.event.IamGrantUserRoleEvent;
import com.crc.crcloud.steam.iam.model.feign.role.MemberRoleDTO;
import com.crc.crcloud.steam.iam.model.feign.role.RoleDTO;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.core.validator.ValidList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * 用户被授权角色事件-同步到老行云
 * @author LiuYang
 * @date 2019/11/25
 */
@Slf4j
@Component
public class SyncIamGrantUserRoleEventListener implements ApplicationListener<IamGrantUserRoleEvent> {
    @Autowired
    private IamServiceClient iamServiceClient;

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
     * todo 需要检查用户是否在老行云那边存在， 检查组织是否在老行云存在
     * @param user 用户
     * @param roles 授权的角色
     */
    public void sync(@NotNull IamUserDTO user, @NotEmpty List<IamMemberRoleWithRoleDTO> roles) throws RuntimeException {
        log.info("通过角色编码获取到老行云角色列表");
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
        grantConsumer.put(ResourceLevel.ORGANIZATION, items -> {
            IamMemberRoleDTO first = CollUtil.getFirst(items);
            Long sourceId = first.getSourceId();
            List<Long> memberIds = CollUtil.newArrayList(first.getMemberId());
            return this.grantRole(items, list -> this.iamServiceClient.createOrUpdateOnOrganizationLevel(false, sourceId, MemberType.USER.getValue(), memberIds, list));
        });
        grantConsumer.put(ResourceLevel.PROJECT, grantConsumer.get(ResourceLevel.ORGANIZATION));

        grantConsumer.put(ResourceLevel.SITE, items -> {
            IamMemberRoleDTO first = CollUtil.getFirst(items);
            Long sourceId = first.getSourceId();
            List<Long> memberIds = CollUtil.newArrayList(first.getMemberId());
            return this.grantRole(items, list -> this.iamServiceClient.createOrUpdateOnSiteLevel(false, MemberType.USER.getValue(), memberIds, list));
        });

        List<List<@NotNull IamMemberRoleDTO>> groupLevelRole = CollUtil.groupByField(roles.stream().map(IamMemberRoleWithRoleDTO::getIamMemberRole).collect(Collectors.toList()), EntityUtil.getSimpleFieldToCamelCase(RoleDTO::getLevel));
        for (List<IamMemberRoleDTO> items : groupLevelRole) {
            String sourceType = CollUtil.getFirst(items).getSourceType();
            Optional<ResourceLevel> resourceLevel = Arrays.stream(ResourceLevel.values()).filter(t -> Objects.equals(t.value(), sourceType)).findFirst();
            if (resourceLevel.isPresent() && grantConsumer.containsKey(resourceLevel.get())) {
                grantConsumer.get(resourceLevel.get()).apply(items);
            } else {
                log.warn("存在不兼容的级别[{}],将忽略", sourceType);
            }
        }
    }

    /**
     * 组织授权
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
        isSuccess.getAndUpdate(is -> is.and(t -> JSONUtil.parseObj(t.getBody()).containsKey(EntityUtil.getSimpleField(RoleDTO::getId))));
        for (IamRoleDTO role : roles) {
            RetryCallback<RoleDTO, RuntimeException> retryCallback = retryContext -> {
                ResponseEntity<RoleDTO> responseEntity = iamServiceClient.queryByCode(role.getCode());
                if (isSuccess.get().negate().test(responseEntity)) {
                    throw new IamAppCommException("不满足接口成功条件");
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

    public SyncIamGrantUserRoleEventListener() {
        log.info("已注册用户授权角色事件-角色同步到老行云");
    }
}
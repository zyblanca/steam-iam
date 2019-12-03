package com.crc.crcloud.steam.iam.common.eventhander.listener;


import cn.hutool.core.collection.CollUtil;
import com.crc.crcloud.steam.iam.common.config.ChoerodonDevOpsProperties;
import com.crc.crcloud.steam.iam.common.utils.EntityUtil;
import com.crc.crcloud.steam.iam.entity.IamUserOrganizationRel;
import com.crc.crcloud.steam.iam.model.dto.IamUserDTO;
import com.crc.crcloud.steam.iam.model.dto.payload.UserEventPayload;
import com.crc.crcloud.steam.iam.model.event.IamUserManualCreateEvent;
import com.crc.crcloud.steam.iam.service.IamUserOrganizationRelService;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.choerodon.asgard.saga.annotation.Saga;
import io.choerodon.asgard.saga.annotation.SagaTask;
import io.choerodon.asgard.saga.producer.StartSagaBuilder;
import io.choerodon.asgard.saga.producer.TransactionalProducer;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.crc.crcloud.steam.iam.common.utils.SagaTopic.User.USER_CREATE;

/**
 * @Description 手动创建用户 —— Saga 事件
 * @Author YangTian
 * @Date 2019/11/25
 */
@Slf4j
@Component
// 如果 choerodon.devops.message 在配置文件中不是 true ，这个 bean 就不会被加载
@ConditionalOnProperty(prefix = ChoerodonDevOpsProperties.PREFIX, value = ChoerodonDevOpsProperties.MESSAGE_KEY, havingValue = "true")
public class SagaIamUserManualCreateEventListener implements ApplicationListener<IamUserManualCreateEvent> {

    @Autowired
    private TransactionalProducer producer;
    @Autowired
    private IamUserOrganizationRelService iamUserOrganizationRelService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public SagaIamUserManualCreateEventListener(){
        log.info("已注册创建用户事件-发送saga事件");
    }

    /**
     * 创建用户事件，兼容 批量创建用户
     * @param event
     */
    @Saga(code = USER_CREATE, description = "steam-iam创建用户", inputSchemaClass = UserEventPayload.class)
    @Override
    public void onApplicationEvent(IamUserManualCreateEvent event) {
        try {
            Long fromUserId = Optional.ofNullable(DetailsHelper.getUserDetails()).map(CustomUserDetails::getUserId).orElse(null);
            // 将 IamUserDTO 转化为 UserEventPayload
            Function<IamUserDTO, UserEventPayload> convertPayload = user -> {
                final Long organizationId = iamUserOrganizationRelService.getUserOrganizations(user.getId()).stream().findFirst().map(IamUserOrganizationRel::getOrganizationId).orElse(null);
                return UserEventPayload.builder()
                        .id(user.getId())
                        .name(user.getRealName())
                        .username(user.getLoginName())
                        .email(user.getEmail())
                        .organizationId(organizationId)
                        .fromUserId(fromUserId)
                        .ldap(user.getIsLdap())
                        .build();
            };
            List<UserEventPayload> rawPayloads = event.getSource().stream().map(convertPayload).collect(Collectors.toList());
            // 通过 组织ID 分组 用户
            List<List<UserEventPayload>> payloadGroupByOrg = CollUtil.groupByField(rawPayloads, EntityUtil.getSimpleFieldToCamelCase(UserEventPayload::getOrganizationId));
            // 有几个分组就发送几次事件
            for (List<UserEventPayload> payloads : payloadGroupByOrg) {
                String input = objectMapper.writeValueAsString(payloads);
                log.info("开始发送Saga事件[{code:{}}],内容: {}", USER_CREATE, input);
                producer.applyAndReturn(
                        StartSagaBuilder
                                .newBuilder()
                                .withLevel(ResourceLevel.ORGANIZATION)
                                .withSourceId(CollUtil.getFirst(payloads).getOrganizationId())
                                .withSagaCode(USER_CREATE),
                        builder -> {
                            builder.withPayloadAndSerialize(payloads)
                                    .withRefType("users") // iam-service 中设置为 user
                                    .withRefId(CollUtil.getFirst(payloads).getId().toString());
                            return input;
                        });
            }
        } catch (Exception e){
            throw new CommonException("error.sagaEvent.organizationUserService.createUserByManual", e);
        }
    }

    @SagaTask(code = "testSagaTask",
            sagaCode = USER_CREATE,
            description = "测试Saga事务",
            seq = 1)
    public List<UserEventPayload> testSagaTask(String data) throws IOException {
        JavaType javaType = objectMapper.getTypeFactory().constructCollectionType(List.class, UserEventPayload.class);
        List<UserEventPayload> payloads = objectMapper.readValue(data, javaType);
        log.info("测试SagaTask的Code:[{}], 测试SagaTask的入参date:[{}]", "testSagaTask", data);
        return  payloads;
    }

}

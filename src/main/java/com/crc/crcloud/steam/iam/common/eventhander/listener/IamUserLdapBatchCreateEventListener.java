package com.crc.crcloud.steam.iam.common.eventhander.listener;

import com.crc.crcloud.steam.iam.common.config.ChoerodonDevOpsProperties;
import com.crc.crcloud.steam.iam.entity.IamUser;
import com.crc.crcloud.steam.iam.model.dto.payload.UserEventPayload;
import com.crc.crcloud.steam.iam.model.event.IamUserLdapBatchCreateEvent;
import io.choerodon.asgard.saga.annotation.Saga;
import io.choerodon.asgard.saga.producer.StartSagaBuilder;
import io.choerodon.asgard.saga.producer.TransactionalProducer;
import io.choerodon.core.iam.ResourceLevel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

import static com.crc.crcloud.steam.iam.common.utils.SagaTopic.User.USER_CREATE;

/**
 * ldap批量新增用户接口
 */
@Component
@ConditionalOnProperty(prefix = ChoerodonDevOpsProperties.PREFIX, value = ChoerodonDevOpsProperties.MESSAGE_KEY, havingValue = "true")
@Slf4j
public class IamUserLdapBatchCreateEventListener implements ApplicationListener<IamUserLdapBatchCreateEvent> {
    @Autowired
    private TransactionalProducer producer;

    @Override
    @Saga(code = USER_CREATE, description = "steam-iam创建用户", inputSchemaClass = UserEventPayload.class)
    public void onApplicationEvent(IamUserLdapBatchCreateEvent event) {
        Long organizationId = event.getOrganization();
        List<IamUser> users = event.getIamUsers();
        if (CollectionUtils.isEmpty(users)) return;
        log.info("ldap新增用户，组织id{}，同步人数{}", organizationId, users.size());
        //人员信息使用老新云接口标准
        List<UserEventPayload> userEventPayloads =
                users.stream().map(v -> UserEventPayload.builder()
                        .id(v.getId().toString())
                        .organizationId(organizationId)
                        .email(v.getEmail())
                        .name(v.getRealName())
                        .username(v.getLoginName())
                        .ldap(v.getIsLdap())
                        .build()).collect(Collectors.toList());

        //发起saga创建人员服务
        producer.applyAndReturn(
                StartSagaBuilder
                        .newBuilder()
                        .withLevel(ResourceLevel.ORGANIZATION)
                        .withSourceId(organizationId)
                        .withSagaCode(USER_CREATE),
                builder -> {
                    builder.withPayloadAndSerialize(userEventPayloads)
                            .withRefType("users") // iam-service 中设置为 user
                            .withRefId(userEventPayloads.stream().map(UserEventPayload::getId).collect(Collectors.joining(",")));
                    return userEventPayloads;
                });

    }
}

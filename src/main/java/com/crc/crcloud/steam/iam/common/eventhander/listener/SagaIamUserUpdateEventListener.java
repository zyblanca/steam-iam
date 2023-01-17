package com.crc.crcloud.steam.iam.common.eventhander.listener;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.crc.crcloud.steam.iam.common.config.ChoerodonDevOpsProperties;
import com.crc.crcloud.steam.iam.entity.IamUserOrganizationRel;
import com.crc.crcloud.steam.iam.model.dto.IamUserDTO;
import com.crc.crcloud.steam.iam.model.dto.payload.UserEventPayload;
import com.crc.crcloud.steam.iam.model.event.IamUserUpdateEvent;
import com.crc.crcloud.steam.iam.service.IamUserOrganizationRelService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.choerodon.asgard.saga.annotation.Saga;
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

import javax.validation.constraints.NotNull;
import java.util.Optional;

import static com.crc.crcloud.steam.iam.common.utils.SagaTopic.User.USER_UPDATE;

@Slf4j
@Component
@ConditionalOnProperty(prefix = ChoerodonDevOpsProperties.PREFIX, value = ChoerodonDevOpsProperties.MESSAGE_KEY, havingValue = "true")
public class SagaIamUserUpdateEventListener implements ApplicationListener<IamUserUpdateEvent> {

    @Autowired
    private TransactionalProducer producer;
    @Autowired
    private IamUserOrganizationRelService service;

    final private ObjectMapper objectMapper = new ObjectMapper();

    public SagaIamUserUpdateEventListener() {
        log.info("已注册更新用户事件-发送Saga事件");
    }

    @Saga(code = USER_UPDATE, description = "steam-iam更新用户", inputSchemaClass = UserEventPayload.class)
    @Override
    public void onApplicationEvent(IamUserUpdateEvent event) {
        @NotNull IamUserDTO user = event.getSource();
        String logTitle = StrUtil.format("用户[{}|{}]", user.getId(), user.getLoginName());
        // 用户有可能属于多个组织，取第一个组织
        Long organizationId = service.getUserOrganizations(user.getId()).stream().findFirst().map(IamUserOrganizationRel::getOrganizationId).orElse(null);
        try {
            Long fromUserId = Optional.ofNullable(DetailsHelper.getUserDetails()).map(CustomUserDetails::getUserId).orElse(null);
            UserEventPayload payload = UserEventPayload.builder()
                    .id(user.getId().toString())
                    .fromUserId(fromUserId)
                    .organizationId(organizationId)
                    .username(user.getLoginName())
                    .email(user.getEmail())
                    .name(user.getRealName())
                    .ldap(user.getIsLdap())
                    .build();
            log.info("{{}:开发送Saga事件[{code:{}},内容：{}]", logTitle, USER_UPDATE, JSONUtil.toJsonStr(payload));
            String input = objectMapper.writeValueAsString(payload);
            producer.applyAndReturn(StartSagaBuilder.newBuilder()
                            .withLevel(ResourceLevel.ORGANIZATION)
                            .withSourceId(organizationId)
                            .withSagaCode(USER_UPDATE),
                    builder -> {
                        builder.withPayloadAndSerialize(payload)
                                .withRefType("user")
                                .withRefId(user.getId().toString());
                        return input;
                    });
        } catch (Exception e) {
            log.warn(e.getMessage());
            throw new CommonException("error.sagaEvent.organizationUserService.updateUser");
        }
    }
}

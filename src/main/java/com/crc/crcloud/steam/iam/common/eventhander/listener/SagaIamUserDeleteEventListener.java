package com.crc.crcloud.steam.iam.common.eventhander.listener;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.crc.crcloud.steam.iam.common.config.ChoerodonDevOpsProperties;
import com.crc.crcloud.steam.iam.entity.IamUserOrganizationRel;
import com.crc.crcloud.steam.iam.model.dto.IamUserDTO;
import com.crc.crcloud.steam.iam.model.dto.payload.UserEventPayload;
import com.crc.crcloud.steam.iam.model.event.IamUserDeleteEvent;
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

import static com.crc.crcloud.steam.iam.common.utils.SagaTopic.User.USER_DELETE;

@Slf4j
@Component
@ConditionalOnProperty(prefix = ChoerodonDevOpsProperties.PREFIX, value = ChoerodonDevOpsProperties.MESSAGE_KEY, havingValue = "true")
public class SagaIamUserDeleteEventListener implements ApplicationListener<IamUserDeleteEvent> {

    @Autowired
    private TransactionalProducer producer;
    @Autowired
    private IamUserOrganizationRelService service;

    ObjectMapper objectMapper = new ObjectMapper();

    @Saga(code = USER_DELETE, description = "steam-iam删除用户", inputSchemaClass = UserEventPayload.class)
    @Override
    public void onApplicationEvent(IamUserDeleteEvent event) {
        @NotNull IamUserDTO user = event.getSource();
        Long userId = user.getId();
        Long fromUserId = Optional.ofNullable(DetailsHelper.getUserDetails()).map(CustomUserDetails::getUserId).orElse(null);
        Long organizationId = service.getUserOrganizations(userId).stream().findFirst().map(IamUserOrganizationRel::getOrganizationId).orElse(null);
        final String logTitle = StrUtil.format("用户[{}|{}]", user.getId(), user.getLoginName());
        try {
            UserEventPayload payload = UserEventPayload.builder()
                    .id(userId)
                    .fromUserId(fromUserId)
                    .username(user.getLoginName())
                    .organizationId(organizationId)
                    .build();
            String input = objectMapper.writeValueAsString(payload);
            log.info("{};开始发送Saga事件[{code:{}}],内容: {}", logTitle, USER_DELETE, JSONUtil.toJsonStr(payload));
            producer.applyAndReturn(StartSagaBuilder.newBuilder()
                            .withLevel(ResourceLevel.ORGANIZATION)
                            .withSourceId(organizationId)
                            .withSagaCode(USER_DELETE),
                    builder -> {
                        builder.withPayloadAndSerialize(payload)
                                .withRefType("user")
                                .withRefId(userId.toString());
                        return input;
            });
        } catch (Exception e) {
            throw new CommonException("error.sagaEvent.organizationUserService.deleteUser");
        }
    }
}

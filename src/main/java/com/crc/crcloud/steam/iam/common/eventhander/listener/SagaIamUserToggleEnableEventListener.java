package com.crc.crcloud.steam.iam.common.eventhander.listener;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.crc.crcloud.steam.iam.common.config.ChoerodonDevOpsProperties;
import com.crc.crcloud.steam.iam.entity.IamUserOrganizationRel;
import com.crc.crcloud.steam.iam.model.dto.IamUserDTO;
import com.crc.crcloud.steam.iam.model.dto.payload.UserEventPayload;
import com.crc.crcloud.steam.iam.model.event.IamUserToggleEnableEvent;
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

import static com.crc.crcloud.steam.iam.common.utils.SagaTopic.User.*;

/**
 * @Description 切换用户状态 —— Saga 事件
 * @Author YangTian
 * @Date 2019/11/27
 */
@Slf4j
@Component
@ConditionalOnProperty(prefix = ChoerodonDevOpsProperties.PREFIX, value = ChoerodonDevOpsProperties.MESSAGE_KEY, havingValue = "true")
public class SagaIamUserToggleEnableEventListener implements ApplicationListener<IamUserToggleEnableEvent> {

    @Autowired
    private TransactionalProducer producer;
    @Autowired
    private IamUserOrganizationRelService service;

    final private ObjectMapper objectMapper = new ObjectMapper();

    public SagaIamUserToggleEnableEventListener(){
        log.info("已注册启用禁用用户事件-发送saga事件");
    }

    @Override
    public void onApplicationEvent(IamUserToggleEnableEvent event) {
        if (event.isEnable()){
            enableUser(event);
        } else {
            disabledUser(event);
        }
    }

    @Saga(code = USER_ENABLE, description = "steam-iam停用用户", inputSchemaClass = UserEventPayload.class)
    private void disabledUser(IamUserToggleEnableEvent event) {
        toggleStatus(USER_ENABLE, event);
    }

    @Saga(code = USER_DISABLE, description = "steam-iam启用用户", inputSchemaClass = UserEventPayload.class)
    private void enableUser(IamUserToggleEnableEvent event) {
        toggleStatus(USER_DISABLE, event);
    }

    private void toggleStatus(String sagaCode, IamUserToggleEnableEvent event) {
        @NotNull IamUserDTO user = event.getSource();
        // 用户有可能属于多个组织，取第一个组织
        Long organizationId = service.getUserOrganizations(user.getId()).stream().findFirst().map(IamUserOrganizationRel::getOrganizationId).orElse(null);
        final String logTitle = StrUtil.format("用户[{}|{}]", user.getId(), user.getLoginName());
        try {
            Long fromUserId = Optional.ofNullable(DetailsHelper.getUserDetails()).map(CustomUserDetails::getUserId).orElse(null);
            UserEventPayload payload = UserEventPayload.builder()
                    .id(user.getId().toString())
                    .username(user.getLoginName())
                    .organizationId(organizationId)
                    .fromUserId(fromUserId)
                    .build();
            log.info("{};开始发送Saga事件[{code:{}}],内容: {}", logTitle, sagaCode, JSONUtil.toJsonStr(payload));
            String input = objectMapper.writeValueAsString(payload);
            producer.applyAndReturn(StartSagaBuilder.newBuilder()
                            .withSagaCode(sagaCode)
                            .withLevel(ResourceLevel.ORGANIZATION)
                            .withSourceId(organizationId),
                    builder -> {
                        builder.withPayloadAndSerialize(payload)
                                .withRefType("user")
                                .withRefId(user.getId().toString());
                        return input;
                    });
        } catch (Exception e) {
            log.warn(e.getMessage());
            throw new CommonException("error.sagaEvent.organizationUserService.toggleUserStatus");
        }
    }
}

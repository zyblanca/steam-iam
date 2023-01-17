package com.crc.crcloud.steam.iam.common.eventhander.listener;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.crc.crcloud.steam.iam.common.config.ChoerodonDevOpsProperties;
import com.crc.crcloud.steam.iam.model.dto.payload.OrganizationEventPayload;
import com.crc.crcloud.steam.iam.model.event.IamOrganizationToggleEnableEvent;
import io.choerodon.asgard.saga.annotation.Saga;
import io.choerodon.asgard.saga.producer.StartSagaBuilder;
import io.choerodon.asgard.saga.producer.TransactionalProducer;
import io.choerodon.core.iam.ResourceLevel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.Objects;

import static com.crc.crcloud.steam.iam.common.utils.SagaTopic.Organization.ORG_DISABLE;
import static com.crc.crcloud.steam.iam.common.utils.SagaTopic.Organization.ORG_ENABLE;

/**
 * 禁用启用组织之后发送saga事件
 *
 * @author LiuYang
 * @date 2019/11/26
 */
@Slf4j
@Component
@ConditionalOnProperty(prefix = ChoerodonDevOpsProperties.PREFIX, value = ChoerodonDevOpsProperties.MESSAGE_KEY, havingValue = "true")
public class SagaIamOrganizationToggleEnableEventListener implements ApplicationListener<IamOrganizationToggleEnableEvent> {

    @Autowired
    private TransactionalProducer producer;

    public SagaIamOrganizationToggleEnableEventListener() {
        log.info("已注册启用禁用组织事件-发送saga事件");
    }

    @Override
    public void onApplicationEvent(IamOrganizationToggleEnableEvent event) {
        if (event.getCurrentEnableStatus()) {
            enable(event);
        } else {
            disable(event);
        }
    }

    private void toggleEnable(String sagaCode, IamOrganizationToggleEnableEvent event) {
        final Long id = event.getSource().getId();
        final String logTitle = StrUtil.format("组织[{}|{}]", id, event.getSource().getCode());
        OrganizationEventPayload payload = new OrganizationEventPayload(id);
        log.info("{};开始发送Saga事件[{code:{}}],内容: {}", logTitle, sagaCode, JSONUtil.toJsonStr(payload));
        StartSagaBuilder startSagaBuilder = StartSagaBuilder.newBuilder()
                .withSagaCode(sagaCode)
                .withLevel(ResourceLevel.SITE)
                .withRefType("organization")
                .withRefId(Objects.toString(id))
                .withPayloadAndSerialize(payload);
        producer.apply(startSagaBuilder, t -> {

        });
    }

    @Saga(code = ORG_ENABLE, description = "steam-iam启用组织", inputSchemaClass = OrganizationEventPayload.class)
    public void enable(IamOrganizationToggleEnableEvent event) {
        toggleEnable(ORG_ENABLE, event);
    }

    @Saga(code = ORG_DISABLE, description = "steam-iam禁用组织", inputSchemaClass = OrganizationEventPayload.class)
    public void disable(IamOrganizationToggleEnableEvent event) {
        toggleEnable(ORG_DISABLE, event);
    }
}

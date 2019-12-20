package com.crc.crcloud.steam.iam.common.eventhander.listener;

import com.crc.crcloud.steam.iam.api.feign.AsgardFeignClient;
import com.crc.crcloud.steam.iam.common.config.ChoerodonDevOpsProperties;
import com.crc.crcloud.steam.iam.model.dto.payload.ProjectEventPayload;
import com.crc.crcloud.steam.iam.model.event.IamProjectEnableEvent;
import io.choerodon.asgard.saga.annotation.Saga;
import io.choerodon.asgard.saga.producer.StartSagaBuilder;
import io.choerodon.asgard.saga.producer.TransactionalProducer;
import io.choerodon.core.iam.ResourceLevel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import static com.crc.crcloud.steam.iam.common.utils.SagaTopic.Project.PROJECT_ENABLE;

/**
 * 启用项目
 * 不做异常拦截
 */
@Component
@ConditionalOnProperty(prefix = ChoerodonDevOpsProperties.PREFIX, value = ChoerodonDevOpsProperties.MESSAGE_KEY, havingValue = "true")
@Slf4j
public class SagaProjectEnableEventListener implements ApplicationListener<IamProjectEnableEvent> {

    @Autowired
    private TransactionalProducer producer;
    @Autowired
    private AsgardFeignClient asgardFeignClient;

    @Override
    @Saga(code = PROJECT_ENABLE, description = "steam-iam-enbale-project启用项目", inputSchemaClass = ProjectEventPayload.class)
    public void onApplicationEvent(IamProjectEnableEvent event) {
        ProjectEventPayload projectEventPayload = (ProjectEventPayload) event.getSource();
        //发起saga创建项目
        producer.applyAndReturn(
                StartSagaBuilder
                        .newBuilder()
                        .withLevel(ResourceLevel.ORGANIZATION)
                        .withSourceId(projectEventPayload.getOrganizationId())
                        .withSagaCode(PROJECT_ENABLE),
                builder -> {
                    builder.withPayloadAndSerialize(projectEventPayload)
                            .withRefType("project")
                            .withRefId(projectEventPayload.getProjectId().toString());
                    return projectEventPayload;
                });
        //        asgardFeignClient.disableProj(projectEventPayload.getProjectId());

    }
}

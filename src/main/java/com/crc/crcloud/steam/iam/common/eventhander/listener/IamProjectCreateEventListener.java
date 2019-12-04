package com.crc.crcloud.steam.iam.common.eventhander.listener;

import com.crc.crcloud.steam.iam.common.config.ChoerodonDevOpsProperties;
import com.crc.crcloud.steam.iam.model.dto.payload.ProjectEventPayload;
import com.crc.crcloud.steam.iam.model.event.IamProjectCreateEvent;
import io.choerodon.asgard.saga.annotation.Saga;
import io.choerodon.asgard.saga.producer.StartSagaBuilder;
import io.choerodon.asgard.saga.producer.TransactionalProducer;
import io.choerodon.core.iam.ResourceLevel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import static com.crc.crcloud.steam.iam.common.utils.SagaTopic.Project.PROJECT_CREATE;

/**
 * 新增项目
 * 不做异常拦截
 */
@Component
@ConditionalOnProperty(prefix = ChoerodonDevOpsProperties.PREFIX, value = ChoerodonDevOpsProperties.MESSAGE_KEY, havingValue = "true")
@Slf4j
public class IamProjectCreateEventListener implements ApplicationListener<IamProjectCreateEvent> {

    @Autowired
    private TransactionalProducer producer;

    @Override
    @Saga(code = PROJECT_CREATE, description = "steam-iam-create-project创建项目", inputSchemaClass = ProjectEventPayload.class)
    public void onApplicationEvent(IamProjectCreateEvent event) {
        ProjectEventPayload projectEventPayload = (ProjectEventPayload) event.getSource();
        //发起saga创建项目
        producer.applyAndReturn(
                StartSagaBuilder
                        .newBuilder()
                        .withLevel(ResourceLevel.ORGANIZATION)
                        .withSourceId(projectEventPayload.getOrganizationId())
                        .withSagaCode(PROJECT_CREATE),
                builder -> {
                    builder.withPayloadAndSerialize(projectEventPayload)
                            .withRefType("project") // iam-service 中设置为 user
                            .withRefId(projectEventPayload.getProjectId().toString());
                    return projectEventPayload;
                });
    }
}

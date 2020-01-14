package com.crc.crcloud.steam.iam.common.eventhander.listener;

import com.crc.crcloud.steam.iam.common.config.ChoerodonDevOpsProperties;
import com.crc.crcloud.steam.iam.model.dto.payload.UserMemberEventPayload;
import com.crc.crcloud.steam.iam.model.event.IamUnbindUserRoleEvent;
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

import static com.crc.crcloud.steam.iam.common.utils.SagaTopic.MemberRole.MEMBER_ROLE_DELETE;

/**
 * 新增项目
 * 不做异常拦截
 */
@Component
@ConditionalOnProperty(prefix = ChoerodonDevOpsProperties.PREFIX, value = ChoerodonDevOpsProperties.MESSAGE_KEY, havingValue = "true")
@Slf4j
public class SagaIamUnbindUserEventListener implements ApplicationListener<IamUnbindUserRoleEvent> {

    @Autowired
    private TransactionalProducer producer;

    @Override
    @Saga(code = MEMBER_ROLE_DELETE, description = "steam-iam-delete-memberRole回收用户权限")
    public void onApplicationEvent(IamUnbindUserRoleEvent event) {
        //空数据不处理
        List<UserMemberEventPayload> userMemberEventPayloads = event.getUserMemberEventPayloads();
        if (CollectionUtils.isEmpty(userMemberEventPayloads)) {
            return;
        }

        ResourceLevel level = event.getLevel();
        Long sourceId = event.getSourceId();
        String refs = userMemberEventPayloads.stream().map(v -> v.getUserId() + "").collect(Collectors.joining(","));
        //发起saga删除项目权限
        producer.applyAndReturn(
                StartSagaBuilder
                        .newBuilder()
                        .withLevel(level)
                        .withSourceId(sourceId)
                        .withSagaCode(MEMBER_ROLE_DELETE),
                builder -> {
                    builder.withPayloadAndSerialize(userMemberEventPayloads)
                            .withRefType("users")
                            .withRefId(refs);
                    return userMemberEventPayloads;
                });
    }
}

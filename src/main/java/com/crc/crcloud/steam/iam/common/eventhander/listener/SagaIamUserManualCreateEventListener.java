package com.crc.crcloud.steam.iam.common.eventhander.listener;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.crc.crcloud.steam.iam.common.config.ChoerodonDevOpsProperties;
import com.crc.crcloud.steam.iam.model.dto.IamUserDTO;
import com.crc.crcloud.steam.iam.model.dto.payload.UserEventPayload;
import com.crc.crcloud.steam.iam.model.event.IamUserManualCreateEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.choerodon.asgard.saga.annotation.Saga;
import io.choerodon.asgard.saga.annotation.SagaTask;
import io.choerodon.asgard.saga.producer.StartSagaBuilder;
import io.choerodon.asgard.saga.producer.TransactionalProducer;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.core.oauth.DetailsHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.ArrayList;

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


    public SagaIamUserManualCreateEventListener(){
        log.info("已注册创建用户事件-发送saga事件");
    }
//    @Value("${choerodon.devops.message:false}")
    /**
     * 注解 @ConditionalOnProperty(prefix = "choerodon.devops",value = "message", havingValue = "true")
     * 如果 choerodon.devops.message 在配置文件中不是 true ，这个 bean 就不会被加载
     * 如果 类被加载 onApplicationEvent() 方法必须执行
     */
    private boolean devopsMessage = true;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Saga(code = USER_CREATE, description = "steam-iam创建用户", inputSchemaClass = UserEventPayload.class)
    @Override
    public void onApplicationEvent(IamUserManualCreateEvent event) {
        @NotNull IamUserDTO user = event.getSource();
        final String logTitle = StrUtil.format("用户[{}|{}]", user.getId(), user.getLoginName());
        if (devopsMessage){
            try {
                UserEventPayload payload = UserEventPayload.builder()
                        .userId(user.getId())
                        .realName(user.getRealName())
                        .loginName(user.getLoginName())
                        .email(user.getEmail())
                        .organizationId(user.getCurrentOrganizationId())
                        .fromUserId(DetailsHelper.getUserDetails().getUserId())
                        .isLdap(user.getIsLdap())
                        .build();
                log.info("{};开始发送Saga事件[{code:{}}],内容: {}", logTitle, USER_CREATE, JSONUtil.toJsonStr(payload));
                ArrayList<UserEventPayload> payloads = CollUtil.newArrayList(payload);
                String input = objectMapper.writeValueAsString(payloads);
                producer.applyAndReturn(
                    StartSagaBuilder
                            .newBuilder()
                            .withLevel(ResourceLevel.ORGANIZATION)
                            .withSourceId(user.getCurrentOrganizationId())
                            .withSagaCode(USER_CREATE),
                        builder -> {
                            builder.withPayloadAndSerialize(payloads)
                                    .withRefType("user") // iam-service 中设置为 user
                                    .withRefId(user.getId().toString());
                            return input;
                        });
            } catch (Exception e){
                throw new CommonException("error.sagaEvent.organizationUserService.createUserByManual", e);
            }
        }
    }

    @SagaTask(code = "testSagaTask",
            sagaCode = USER_CREATE,
            description = "测试Saga事务",
            seq = 1)
    public UserEventPayload testSagaTask(String data) throws IOException {
        UserEventPayload userEventPayload = objectMapper.readValue(data, UserEventPayload.class);
        log.info("SgagTask:[{}],date:[{}]", "testSagaTask", data);
        return  userEventPayload;
    }

}

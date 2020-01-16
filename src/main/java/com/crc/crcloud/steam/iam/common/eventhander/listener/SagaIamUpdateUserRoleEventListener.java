package com.crc.crcloud.steam.iam.common.eventhander.listener;

import com.crc.crcloud.steam.iam.api.feign.IamServiceClient;
import com.crc.crcloud.steam.iam.common.config.ChoerodonDevOpsProperties;
import com.crc.crcloud.steam.iam.common.enums.MemberType;
import com.crc.crcloud.steam.iam.common.utils.SagaTopic;
import com.crc.crcloud.steam.iam.model.dto.payload.UserMemberEventPayload;
import com.crc.crcloud.steam.iam.model.event.IamUpdateUserRoleEvent;
import com.crc.crcloud.steam.iam.model.feign.role.MemberRoleDTO;
import io.choerodon.asgard.saga.annotation.Saga;
import io.choerodon.asgard.saga.producer.StartSagaBuilder;
import io.choerodon.asgard.saga.producer.TransactionalProducer;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.core.validator.ValidList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 修改用户权限
 */
@Component
@ConditionalOnProperty(prefix = ChoerodonDevOpsProperties.PREFIX, value = ChoerodonDevOpsProperties.MESSAGE_KEY, havingValue = "true")
@Slf4j
public class SagaIamUpdateUserRoleEventListener implements ApplicationListener<IamUpdateUserRoleEvent> {

    @Autowired
    private TransactionalProducer producer;
    @Autowired
    private IamServiceClient iamServiceClient;

    @Override
    @Saga(code = SagaTopic.MemberRole.MEMBER_ROLE_UPDATE, description = "新行云人员授权事件")
    public void onApplicationEvent(IamUpdateUserRoleEvent event) {

        List<UserMemberEventPayload> userMemberEventPayloads = event.getUserMemberEventPayloads();

        String refs = userMemberEventPayloads.stream().map(v -> v.getUserId() + "").collect(Collectors.joining(","));

        //调用老服务的修改权限,项目层级
        if (Objects.equals(ResourceLevel.PROJECT, event.getSourceLevel())) {
            updateProjectUserRole(event);
        }


        //发起新服务的saga
        producer.applyAndReturn(
                StartSagaBuilder
                        .newBuilder()
                        .withLevel(event.getSourceLevel())
                        .withSourceId(event.getSourceId())
                        .withSagaCode(SagaTopic.MemberRole.MEMBER_ROLE_UPDATE),
                builder -> {
                    builder.withPayloadAndSerialize(userMemberEventPayloads)
                            .withRefType(event.getSourceLevel().value())
                            .withRefId(refs);
                    return userMemberEventPayloads;
                });


    }

    //调用老服务的修改权限
    private void updateProjectUserRole(IamUpdateUserRoleEvent event) {
        List<Long> users = event.getUserMemberEventPayloads().stream().map(UserMemberEventPayload::getUserId).collect(Collectors.toList());
        //发起老平台的修改权限接口
        //由于发起服务暂时无法判断新建还是修改，无法通过通知的方式通知老行云，只能直接调用,后续脱离老行云可优化
        ValidList<MemberRoleDTO> memberRoleDTOList = new ValidList<>();
        event.getRoleIds().forEach(v -> {
            MemberRoleDTO memberRoleDTO = new MemberRoleDTO();
            memberRoleDTO.setMemberType(MemberType.USER.getValue());
            memberRoleDTO.setRoleId(v);
            memberRoleDTO.setSourceId(event.getSourceId());
            memberRoleDTO.setSourceType(event.getSourceLevel().value());
            memberRoleDTOList.add(memberRoleDTO);
        });
        //发起老服务的调用
        iamServiceClient.createOrUpdateOnProjectLevel(Boolean.TRUE, event.getSourceId(), MemberType.USER.getValue(), users, memberRoleDTOList);

    }
}

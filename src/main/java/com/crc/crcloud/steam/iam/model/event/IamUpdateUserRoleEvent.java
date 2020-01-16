package com.crc.crcloud.steam.iam.model.event;

import com.crc.crcloud.steam.iam.model.dto.payload.UserMemberEventPayload;
import io.choerodon.core.iam.ResourceLevel;
import org.springframework.context.ApplicationEvent;

import java.util.List;

/**
 * 重新授权用户角色事件
 */
public class IamUpdateUserRoleEvent extends ApplicationEvent {
    //发送内容
    private List<UserMemberEventPayload> userMemberEventPayloads;
    //事件级别
    private ResourceLevel sourceLevel;
    //事件源
    private Long sourceId;
    //当前权限
    private List<Long> roleIds;


    public IamUpdateUserRoleEvent(Long sourceId, ResourceLevel sourceLevel, List<Long> roleIds, List<UserMemberEventPayload> userMemberEventPayloads, Object source) {
        super(source);
        this.sourceId = sourceId;
        this.sourceLevel = sourceLevel;
        this.userMemberEventPayloads = userMemberEventPayloads;
        this.roleIds = roleIds;
    }


    public List<UserMemberEventPayload> getUserMemberEventPayloads() {
        return userMemberEventPayloads;
    }


    public ResourceLevel getSourceLevel() {
        return sourceLevel;
    }

    public Long getSourceId() {
        return sourceId;
    }

    public List<Long> getRoleIds() {
        return roleIds;
    }
}

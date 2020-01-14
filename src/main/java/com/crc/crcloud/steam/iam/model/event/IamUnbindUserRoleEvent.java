package com.crc.crcloud.steam.iam.model.event;

import com.crc.crcloud.steam.iam.model.dto.payload.UserMemberEventPayload;
import io.choerodon.core.iam.ResourceLevel;
import org.springframework.context.ApplicationEvent;

import java.util.List;

/**
 * 回收权限事件
 */
public class IamUnbindUserRoleEvent extends ApplicationEvent {
    //权限数据
    List<UserMemberEventPayload> userMemberEventPayloads;
    //资源级别
    private ResourceLevel level;
    //资源id
    private Long sourceId;

    public IamUnbindUserRoleEvent(Object source,ResourceLevel level,Long sourceId, List<UserMemberEventPayload> userMemberEventPayloads) {
        super(source);
        this.level =level;
        this.sourceId = sourceId;
        this.userMemberEventPayloads = userMemberEventPayloads;
    }

    public List<UserMemberEventPayload> getUserMemberEventPayloads() {
        return userMemberEventPayloads;
    }

    public ResourceLevel getLevel() {
        return level;
    }

    public Long getSourceId() {
        return sourceId;
    }
}

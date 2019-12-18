package com.crc.crcloud.steam.iam.model.event;

import com.crc.crcloud.steam.iam.model.dto.payload.ProjectEventPayload;
import org.springframework.context.ApplicationEvent;

/**
 * 项目创建事件
 */
public class IamProjectEnableEvent extends ApplicationEvent {

    public IamProjectEnableEvent(ProjectEventPayload projectEventPayload) {
        super(projectEventPayload);
    }
}

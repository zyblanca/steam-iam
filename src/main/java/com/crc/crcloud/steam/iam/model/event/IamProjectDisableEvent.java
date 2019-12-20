package com.crc.crcloud.steam.iam.model.event;

import com.crc.crcloud.steam.iam.model.dto.payload.ProjectEventPayload;
import org.springframework.context.ApplicationEvent;

/**
 * 项目创建事件
 */
public class IamProjectDisableEvent extends ApplicationEvent {

    public IamProjectDisableEvent(ProjectEventPayload projectEventPayload) {
        super(projectEventPayload);
    }
}

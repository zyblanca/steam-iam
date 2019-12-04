package com.crc.crcloud.steam.iam.model.event;

import com.crc.crcloud.steam.iam.model.dto.payload.ProjectEventPayload;
import org.springframework.context.ApplicationEvent;

/**
 * 修改项目信息
 */
public class IamProjectUpdateEvent extends ApplicationEvent {

    public IamProjectUpdateEvent(ProjectEventPayload projectEventPayload) {
        super(projectEventPayload);
    }
}

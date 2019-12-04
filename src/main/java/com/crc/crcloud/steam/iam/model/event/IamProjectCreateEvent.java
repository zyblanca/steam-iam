package com.crc.crcloud.steam.iam.model.event;

import com.crc.crcloud.steam.iam.entity.IamProject;
import com.crc.crcloud.steam.iam.model.dto.payload.ProjectEventPayload;
import org.springframework.context.ApplicationEvent;

/**
 * 项目创建事件
 */
public class IamProjectCreateEvent extends ApplicationEvent {

    public IamProjectCreateEvent(ProjectEventPayload projectEventPayload) {
        super(projectEventPayload);
    }
}

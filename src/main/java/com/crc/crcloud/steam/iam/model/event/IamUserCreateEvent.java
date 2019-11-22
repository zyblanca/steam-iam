package com.crc.crcloud.steam.iam.model.event;

import com.crc.crcloud.steam.iam.model.dto.IamUserDTO;
import org.springframework.context.ApplicationEvent;

import javax.validation.constraints.NotNull;

/**
 * 用户创建事件
 * @author LiuYang
 */
public class IamUserCreateEvent extends ApplicationEvent {
    public IamUserCreateEvent(@NotNull IamUserDTO source) {
        super(source);
    }

    @NotNull
    @Override
    public IamUserDTO getSource() {
        return (IamUserDTO) super.getSource();
    }
}

package com.crc.crcloud.steam.iam.model.event;

import com.crc.crcloud.steam.iam.model.dto.IamUserDTO;
import org.springframework.context.ApplicationEvent;

import javax.validation.constraints.NotNull;

/**
 * 用户删除之后事件
 * @author LiuYang
 */
public class IamUserDeleteEvent extends ApplicationEvent {
    public IamUserDeleteEvent(@NotNull IamUserDTO source) {
        super(source);
    }

    /**
     * @return 删除之前的用户数据
     */
    @NotNull
    @Override
    public IamUserDTO getSource() {
        return (IamUserDTO) super.getSource();
    }
}

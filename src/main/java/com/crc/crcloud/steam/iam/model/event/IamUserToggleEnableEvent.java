package com.crc.crcloud.steam.iam.model.event;

import com.crc.crcloud.steam.iam.model.dto.IamUserDTO;
import org.springframework.context.ApplicationEvent;

import javax.validation.constraints.NotNull;

/**
 * 用户禁用启用事件
 * @author LiuYang
 */
public class IamUserToggleEnableEvent extends ApplicationEvent {
    /**
     * 用户状态：true：启用，false：禁用
     */
    private boolean isEnable;

    public IamUserToggleEnableEvent(@NotNull IamUserDTO source, boolean isEnable) {
        super(source);
        this.isEnable = isEnable;
    }

    @NotNull
    @Override
    public IamUserDTO getSource() {
        return (IamUserDTO) super.getSource();
    }

    public boolean isEnable() {
        return isEnable;
    }
}

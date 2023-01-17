package com.crc.crcloud.steam.iam.model.event;

import com.crc.crcloud.steam.iam.model.dto.IamUserDTO;
import org.springframework.context.ApplicationEvent;

import javax.validation.constraints.NotNull;
import java.util.Optional;

/**
 * 用户禁用启用事件
 *
 * @author LiuYang
 */
public class IamUserToggleEnableEvent extends ApplicationEvent {


    public IamUserToggleEnableEvent(@NotNull IamUserDTO source) {
        super(source);
    }

    @NotNull
    @Override
    public IamUserDTO getSource() {
        return (IamUserDTO) super.getSource();
    }

    @NotNull
    public Boolean isEnable() {
        return Optional.ofNullable(getSource().getIsEnabled()).orElse(Boolean.TRUE);
    }
}

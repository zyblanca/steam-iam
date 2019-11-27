package com.crc.crcloud.steam.iam.model.event;

import com.crc.crcloud.steam.iam.model.dto.IamUserDTO;
import org.springframework.context.ApplicationEvent;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

/**
 * 更新用户事件
 * @author LiuYang
 */
public class IamUserUpdateEvent extends ApplicationEvent {
    /**
     * 更新之前数据
     */
    @Nullable
    private IamUserDTO before;

    public IamUserUpdateEvent(@NotNull IamUserDTO source) {
        super(source);
    }

    public IamUserUpdateEvent(@NotNull IamUserDTO source, IamUserDTO before) {
        super(source);
        this.before = before;
    }

    /**
     * 更新用户之后的数据
     * @return
     */
    @NotNull
    @Override
    public IamUserDTO getSource() {
        return (IamUserDTO) super.getSource();
    }

    @Nullable
    public IamUserDTO getBefore() {
        return before;
    }

    public void setBefore(@Nullable IamUserDTO before) {
        this.before = before;
    }
}

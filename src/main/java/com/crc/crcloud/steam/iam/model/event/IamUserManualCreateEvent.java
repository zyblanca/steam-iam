package com.crc.crcloud.steam.iam.model.event;

import cn.hutool.core.lang.Assert;
import com.crc.crcloud.steam.iam.model.dto.IamUserDTO;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 手动用户创建事件
 * <p>将会在设置完角色和所属组织之后发送</p>
 * @author LiuYang
 */
public class IamUserManualCreateEvent extends IamUserCreateEvent {
    /**
     * 明文密码
     */
    private String rawPassword;

    /**
     * @param source 用户信息-持久化之后的
     * @param rawPassword 铭文密码
     */
    public IamUserManualCreateEvent(@NotNull IamUserDTO source, @NotBlank String rawPassword) {
        super(source);
        Assert.notNull(rawPassword);
        this.rawPassword = rawPassword;
    }

    public String getRawPassword() {
        return rawPassword;
    }
}

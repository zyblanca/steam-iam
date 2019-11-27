package com.crc.crcloud.steam.iam.model.event;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import com.crc.crcloud.steam.iam.model.dto.IamUserDTO;
import com.crc.crcloud.steam.iam.model.dto.user.IamUserCreateWithPasswordDTO;

import javax.annotation.Nullable;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 手动用户创建事件
 * <p>将会在设置完角色和所属组织之后发送</p>
 * @author LiuYang
 */
public class IamUserManualCreateEvent extends IamUserCreateEvent {
    /**
     * 带明文密码的额外信息
     */
    @NotEmpty
    private List<IamUserCreateWithPasswordDTO> users;

    /**
     * @param source 用户信息-持久化之后的
     * @param rawPassword 明文密码
     */
    public IamUserManualCreateEvent(@NotNull IamUserDTO source, @Nullable String rawPassword) {
        this(CollUtil.newArrayList(new IamUserCreateWithPasswordDTO(source, rawPassword)));
    }
    /**
     * @param users 用户信息-持久化之后的
     */
    public IamUserManualCreateEvent(@NotEmpty List<IamUserCreateWithPasswordDTO> users) {
        super(Optional.of(users).get().stream().map(IamUserCreateWithPasswordDTO::getUser).collect(Collectors.toList()));
        Assert.notEmpty(users);
    }

    /**
     * @see this#getSource()
     * @return 用户列表-带扩展信息
     */
    public List<IamUserCreateWithPasswordDTO> getUsers() {
        return users;
    }
}

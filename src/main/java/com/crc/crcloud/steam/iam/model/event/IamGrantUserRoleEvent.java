package com.crc.crcloud.steam.iam.model.event;

import cn.hutool.core.lang.Assert;
import com.crc.crcloud.steam.iam.model.dto.IamUserDTO;
import com.crc.crcloud.steam.iam.model.dto.user.IamMemberRoleWithRoleDTO;
import org.springframework.context.ApplicationEvent;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 授权用户角色事件-某人被授予了某些角色
 *
 * @author LiuYang
 * @date 2019/11/25
 */
public class IamGrantUserRoleEvent extends ApplicationEvent {
    @NotEmpty
    private List<IamMemberRoleWithRoleDTO> roles;

    public IamGrantUserRoleEvent(@NotNull IamUserDTO source, @NotEmpty List<IamMemberRoleWithRoleDTO> roles) {
        super(source);
        Assert.notEmpty(roles);
        this.roles = roles;
    }

    @Override
    public IamUserDTO getSource() {
        return (IamUserDTO) super.getSource();
    }

    public List<IamMemberRoleWithRoleDTO> getRoles() {
        return roles;
    }
}

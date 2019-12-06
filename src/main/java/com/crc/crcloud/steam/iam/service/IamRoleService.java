package com.crc.crcloud.steam.iam.service;


import cn.hutool.core.collection.CollUtil;
import com.crc.crcloud.steam.iam.model.dto.IamRoleDTO;
import io.choerodon.core.iam.ResourceLevel;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * @Author
 * @Description
 * @Date 2019-11-12
 */
public interface IamRoleService {
    /**
     * 获取用户角色
     * @param userId 用户编号
     * @param levels 级别,不传递查询所有
     * @return 角色
     */
    @NotNull
    List<IamRoleDTO> getUserRoles(@NotNull Long userId, ResourceLevel... levels);

    /**
     * 获取用户组织层角色
     * @param userId 用户编号
     * @return 角色
     */
    @NotNull
    List<IamRoleDTO> getUserRolesByOrganization(@NotNull Long userId);

    /**
     * 获取角色
     * @param levels 级别,不传递查询所有
     * @return 角色
     */
    @NotNull
    List<IamRoleDTO> getRoles(ResourceLevel... levels);

    /**
     * 获取组织层角色
     * @return 角色
     */
    @NotNull
    List<IamRoleDTO> getRolesByOrganization();

    /**
     * 获取角色
     * @param ids 角色编号
     * @return 角色
     */
    @NotNull
    List<IamRoleDTO> getRoles(@Nullable Set<Long> ids);

    /**
     * 获取角色
     * @param codes 角色编码
     * @return 角色
     */
    @NotNull
    List<IamRoleDTO> getRolesByCode(@Nullable Set<String> codes);

    /**
     * 获取角色
     * @param code 角色编码
     * @return 角色
     */
    default Optional<IamRoleDTO> getRoleByCode(@NotNull String code) {
        return getRolesByCode(CollUtil.newHashSet(code)).stream().findFirst();
    }
}

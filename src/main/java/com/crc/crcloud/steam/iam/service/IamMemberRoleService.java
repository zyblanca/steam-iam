package com.crc.crcloud.steam.iam.service;


import com.crc.crcloud.steam.iam.model.dto.IamMemberRoleDTO;
import io.choerodon.core.iam.ResourceLevel;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;


/**
 * @Author
 * @Description
 * @Date 2019-11-12
 */
public interface IamMemberRoleService {

    /**
     * 授权用户角色到目标资源；（给用户授权在某组织下角色）
     * <p>增量授权</p>
     * <p>资源类型只允许{site,organization,project}</p>
     * <p>会进行匹配，角色类型和资源类型是否匹配，也会校验资源</p>
     * @param userId 用户编号
     * @param roleIds 角色ID
     * @param sourceId 关联资源
     * @param resourceLevel 资源类型
     */
    void grantUserRole(@NotNull Long userId, @NotEmpty Set<Long> roleIds, @NotNull Long sourceId, @NotNull ResourceLevel resourceLevel);

    /**
     * 授权用户角色到目标资源；（给用户授权在某组织下角色）
     * @param userIds 用户编号
     * @param roleIds 角色ID
     * @param sourceId 关联资源
     * @param resourceLevel 资源类型
     * @see this#grantUserRole(Long, Set, Long, ResourceLevel)
     */
    void grantUserRole(@NotNull Set<Long> userIds, @NotEmpty Set<Long> roleIds, @NotNull Long sourceId, @NotNull ResourceLevel resourceLevel);

    /**
     * 查询用户组织下关联关系
     * @param userId 用户编号
     * @param organizationIds 组织编号
     * @return 关联关系集合
     */
    @NotNull
    List<IamMemberRoleDTO> getUserMemberRoleByOrganization(@NotNull Long userId, @NotEmpty Set<Long> organizationIds);

    /**
     * 查询用户组织下关联关系
     * @param userId 用户编号
     * @param sourceType 资源类型
     * @return 关联关系集合
     */
    @NotNull
    List<IamMemberRoleDTO> getUserMemberRoleBySourceType(@NotNull Long userId, @NotNull ResourceLevel sourceType);
}

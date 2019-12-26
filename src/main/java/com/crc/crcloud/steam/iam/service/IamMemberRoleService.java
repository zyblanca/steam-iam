package com.crc.crcloud.steam.iam.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crc.crcloud.steam.iam.model.dto.IamMemberRoleDTO;
import io.choerodon.core.iam.ResourceLevel;

import javax.annotation.Nullable;
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
    List<IamMemberRoleDTO> getUserMemberRoleBySource(@NotNull Long userId, @NotNull ResourceLevel sourceType);

    /**
     * 查询某资源类型下的关联关系
     * <p>资源编号不传递时，只按照资源类型查询</p>
     * <p>资源类型为{@link ResourceLevel#SITE}时，sourceIds不使用</p>
     * @param sourceType 资源类型
     * @param sourceIds 资源编号
     * @return 关联关系集合
     */
    @NotNull
    List<IamMemberRoleDTO> getUserMemberRoleBySource(@NotNull ResourceLevel sourceType, @Nullable Long... sourceIds);

    /**
     * 查询平台级资源类型下的关联关系
     * @see this#getUserMemberRoleBySource(ResourceLevel, Long...)
     * @return 关联关系集合
     */
    List<IamMemberRoleDTO> getSiteUserMemberRoleBySource();

    /**
     * 授权用户平台级角色
     * @param userId 用户编号
     * @param roleIds 角色编号
     * @see this#grantUserRole(Long, Set, Long, ResourceLevel)
     */
    void grantUserSiteRole(@NotNull Long userId, @NotEmpty Set<Long> roleIds);

    /**
     * 获取平台管理员-包括管理员
     * @param page  分页信息
     * @return 用户分页
     */
    @NotNull
    IPage<Long> getSiteAdminUserId(@NotNull Page page);
}

package com.crc.crcloud.steam.iam.service;


import com.crc.crcloud.steam.iam.model.dto.IamRolePermissionDTO;

import javax.annotation.Nullable;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;

/**
 * @Author
 * @Description
 * @Date 2019-11-29
 */
public interface IamRolePermissionService {
    /**
     * 权限关联角色
     * <p>已经关联过的不会重复关联，结果中返回已经持久化过的数据</p>
     * <p>会验证权限和角色是否存在，抛出异常</p>
     *
     * @param permissionId 权限ID
     * @param roleIds      角色编号
     * @return 返回对应结果
     */
    @NotNull
    List<IamRolePermissionDTO> link(@NotNull Long permissionId, @NotEmpty Set<Long> roleIds);

    /**
     * 清除该权限的所有关联角色
     *
     * @param permissionId   权限编号
     * @param excludeRoleIds 需要排除的关联角色
     */
    void clear(@NotNull Long permissionId, @Nullable Set<Long> excludeRoleIds);

    /**
     * 获取错误的层级匹配
     * <p>关联关系中{@link io.choerodon.core.iam.ResourceLevel} 角色和权限不匹配</p>
     *
     * @param roleId 角色编号
     * @return 错误权限层级的关联关系
     */
    @NotNull
    List<IamRolePermissionDTO> selectErrorLevelPermissionByRole(@NotNull Long roleId);

    /**
     * 删除关联关系通过ID
     *
     * @param id 关联关系ID
     */
    void delete(@NotNull Long id);
}

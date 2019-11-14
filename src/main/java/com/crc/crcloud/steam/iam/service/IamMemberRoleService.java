package com.crc.crcloud.steam.iam.service;


import io.choerodon.core.iam.ResourceLevel;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
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
}

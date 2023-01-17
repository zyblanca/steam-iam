package com.crc.crcloud.steam.iam.service;


import com.crc.crcloud.steam.iam.entity.IamUserOrganizationRel;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @Author LiuYang
 * @Description 用户与组织的关系
 * @Date 2019-11-12
 */
public interface IamUserOrganizationRelService {
    /**
     * 用户关联组织
     *
     * @param userId          用户编号
     * @param organizationIds 组织编号
     */
    void link(@NotNull Long userId, @NotEmpty Set<Long> organizationIds);

    /**
     * 获取用户所属组织
     *
     * @param userId 用户编号
     * @return 用户所属组织编号列表，按照关联的时间升序
     */
    @NotNull
    List<IamUserOrganizationRel> getUserOrganizations(@NotNull Long userId);

    /**
     * 获取用户所属组织
     * <p>key:用户ID</p>
     *
     * @param userIds 用户编号集合
     * @return 用户所属组织编号列表，按照关联的时间升序
     */
    Map<Long, List<IamUserOrganizationRel>> getUserOrganizations(@NotNull Set<Long> userIds);
}

package com.crc.crcloud.steam.iam.service;


import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Set;

/**
 * @Author LiuYang
 * @Description 用户与组织的关系
 * @Date 2019-11-12
 */
public interface IamUserOrganizationRelService {
    /**
     * 用户关联组织
     * @param userId 用户编号
     * @param organizationIds 组织编号
     */
    void link(@NotNull Long userId, @NotEmpty Set<Long> organizationIds);


}

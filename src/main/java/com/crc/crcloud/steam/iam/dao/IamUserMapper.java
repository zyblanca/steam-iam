package com.crc.crcloud.steam.iam.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crc.crcloud.steam.iam.entity.IamUser;
import com.crc.crcloud.steam.iam.model.dto.user.SearchDTO;
import org.apache.ibatis.annotations.Param;

import javax.annotation.Nullable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Set;


/**
 * @Author:
 * @Date: 2019-11-12
 * @Description:
 */
public interface IamUserMapper extends BaseMapper<IamUser> {
    /**
     * 填充密码到用户上
     * @param userId 用户编号
     * @param hashPassword 密码
     * @return 影响条数
     */
    Integer fillHashPassword(@NotNull @Param("userId") Long userId, @NotBlank @Param("hashPassword") String hashPassword);

    /**
     * 获取用户的密码
     * @param userId 用户编号
     * @return 密码，可能没有
     */
    @Nullable
    String getHashPassword(@NotNull @Param("userId") Long userId);

    /**
     * 查询组织用户
     * @param page
     * @param searchDTO
     * @return
     */
    IPage<IamUser> pageQueryOrganizationUser(@Param("page") Page<IamUser> page, @Param("organizationIds") Set<Long> organizationIds, @Param("searchDTO") SearchDTO searchDTO);
}

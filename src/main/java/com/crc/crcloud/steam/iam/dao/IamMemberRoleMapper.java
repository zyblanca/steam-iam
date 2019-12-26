package com.crc.crcloud.steam.iam.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crc.crcloud.steam.iam.entity.IamMemberRole;
import com.crc.crcloud.steam.iam.model.dto.IamMemberRoleDTO;
import org.apache.ibatis.annotations.Param;


/**
 * @Author:
 * @Date: 2019-11-12
 * @Description:
 */
public interface IamMemberRoleMapper extends BaseMapper<IamMemberRole> {

    IPage<IamMemberRole> page(Page page, @Param("iamMemberRole") IamMemberRoleDTO iamMemberRoleDTO);


    IPage<Long> getSiteAdminUserId(Page page, @Param("roleId") Long roleId);
}

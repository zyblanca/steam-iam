package com.crc.crcloud.steam.iam.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.crc.crcloud.steam.iam.entity.IamRolePermission;
import org.apache.ibatis.annotations.Param;

import javax.validation.constraints.NotNull;
import java.util.List;


/**
 * @Author:
 * @Date: 2019-11-29
 * @Description:
 */
public interface IamRolePermissionMapper extends BaseMapper<IamRolePermission> {


    List<IamRolePermission> selectErrorLevelPermissionByRole(@NotNull @Param("roleId") Long roleId);
}

package com.crc.crcloud.steam.iam.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crc.crcloud.steam.iam.entity.IamRoleLabel;
import com.crc.crcloud.steam.iam.model.dto.IamRoleLabelDTO;
import org.apache.ibatis.annotations.Param;


/**
 * @Author:
 * @Date: 2019-12-03
 * @Description: 
 */
public interface IamRoleLabelMapper extends BaseMapper<IamRoleLabel> {

    IPage<IamRoleLabel> page(Page page, @Param("iamRoleLabel") IamRoleLabelDTO iamRoleLabelDTO);

}

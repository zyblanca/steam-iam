package com.crc.crcloud.steam.iam.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crc.crcloud.steam.iam.entity.IamUserOrganizationRel;
import com.crc.crcloud.steam.iam.model.dto.IamUserOrganizationRelDTO;
import org.apache.ibatis.annotations.Param;


/**
 * @Author:
 * @Date: 2019-11-12
 * @Description:
 */
public interface IamUserOrganizationRelMapper extends BaseMapper<IamUserOrganizationRel> {

    IPage<IamUserOrganizationRel> page(Page page, @Param("iamUserOrganizationRel") IamUserOrganizationRelDTO iamUserOrganizationRelDTO);

}

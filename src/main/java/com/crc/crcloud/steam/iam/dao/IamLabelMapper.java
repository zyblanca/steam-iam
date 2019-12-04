package com.crc.crcloud.steam.iam.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crc.crcloud.steam.iam.entity.IamLabel;
import com.crc.crcloud.steam.iam.model.dto.IamLabelDTO;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;
import java.util.Set;


/**
 * @Author:
 * @Date: 2019-12-03
 * @Description: 
 */
public interface IamLabelMapper extends BaseMapper<IamLabel> {

    IPage<IamLabel> page(Page page, @Param("iamLabel") IamLabelDTO iamLabelDTO);

    List<IamLabel> selectByRoleIds(@Param("roleIds") Collection<Long> roleIds);

    Set<String> selectLabelNamesInRoleIds(List<Long> roleIds);
}

package com.crc.crcloud.steam.iam.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.crc.crcloud.steam.iam.entity.IamOrganization;
import com.crc.crcloud.steam.iam.model.dto.organization.IamOrganizationWithProjectCountDTO;
import com.crc.crcloud.steam.iam.model.vo.organization.IamOrganizationPageRequestVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * @Author:
 * @Date: 2019-11-12
 * @Description:
 */
public interface IamOrganizationMapper extends BaseMapper<IamOrganization> {
    /**
     * 组织列表分页查询
     * @param page 分页信息
     * @param vo 条件查询
     * @return 分页结果数据
     */
    IPage<IamOrganizationWithProjectCountDTO> page(@Param("page") IPage<IamOrganizationWithProjectCountDTO> page, @Param("vo") IamOrganizationPageRequestVO vo);

    List<IamOrganization> queryAllOrganization(@Param("userId") Long userId,@Param("roleId") Long roleId);

    /**
     * 根据用户编号查询有权限项目的组织列表
     * 1.有权限的组织(有组织层权限)
     * 2.有项目权限的组织(不一定有组织层权限)
     * @param userId
     * @return
     */
    List<IamOrganization> selectProjectOrganizationListByUser(@Param("userId") Long userId,
                                                             @Param("includedDisabled") Boolean includedDisabled);
}

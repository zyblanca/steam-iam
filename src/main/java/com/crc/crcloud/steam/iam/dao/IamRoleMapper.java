package com.crc.crcloud.steam.iam.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.crc.crcloud.steam.iam.entity.IamRole;
import com.crc.crcloud.steam.iam.model.dto.IamMemberRoleDTO;
import org.apache.ibatis.annotations.Param;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;


/**
 * @Author:
 * @Date: 2019-11-12
 * @Description:
 */
public interface IamRoleMapper extends BaseMapper<IamRole> {

    @NotNull
    List<IamRole> getUserRoles(@Param("userId") Long userId, @NotEmpty @Param("levels") Set<String> levels);

    List<IamRole> selectRolesByLabelNameAndType(@Param("name") String name, @Param("type") String type);

    List<IamRole> selectRolesByMemberRole(@Param("memberRole") IamMemberRoleDTO iamMemberRoleDTO);
}

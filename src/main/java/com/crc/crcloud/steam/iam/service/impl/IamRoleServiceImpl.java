package com.crc.crcloud.steam.iam.service.impl;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ArrayUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.crc.crcloud.steam.iam.dao.IamRoleMapper;
import com.crc.crcloud.steam.iam.entity.IamRole;
import com.crc.crcloud.steam.iam.model.dto.IamRoleDTO;
import com.crc.crcloud.steam.iam.service.IamRoleService;
import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.iam.ResourceLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Author
 * @Description
 * @Date 2019-11-12
 */
@Service
public class IamRoleServiceImpl implements IamRoleService {

	@Autowired
	private IamRoleMapper iamRoleMapper;

	@Override
	public @NotNull List<IamRoleDTO> getUserRoles(@NotNull Long userId, ResourceLevel... levels) {
		if (ArrayUtil.isEmpty(levels)) {
			levels = ResourceLevel.values();
		}
		List<IamRole> roles = iamRoleMapper.getUserRoles(userId, CollUtil.newHashSet(levels).stream().map(ResourceLevel::value).collect(Collectors.toSet()));

		return ConvertHelper.convertList(roles, IamRoleDTO.class);
	}

	@Override
	public @NotNull List<IamRoleDTO> getUserRolesByOrganization(@NotNull Long userId) {
		return getUserRoles(userId, ResourceLevel.ORGANIZATION);
	}

	@Override
	public @NotNull List<IamRoleDTO> getRoles(ResourceLevel... levels) {
		LambdaQueryWrapper<IamRole> queryWrapper = Wrappers.<IamRole>lambdaQuery();
		if (ArrayUtil.isNotEmpty(levels)) {
			queryWrapper.in(IamRole::getFdLevel, CollUtil.newHashSet(levels).stream().map(ResourceLevel::value).collect(Collectors.toSet()));
		}
		return ConvertHelper.convertList(iamRoleMapper.selectList(queryWrapper), IamRoleDTO.class);
	}

	@Override
	public @NotNull List<IamRoleDTO> getRolesByOrganization() {
		return getRoles(ResourceLevel.ORGANIZATION);
	}

	@Override
	public @NotNull List<IamRoleDTO> getRoles(@Nullable Set<Long> ids) {
		if (CollUtil.isEmpty(ids)) {
			return new ArrayList<>(0);
		}
		LambdaQueryWrapper<IamRole> queryWrapper = Wrappers.<IamRole>lambdaQuery().in(IamRole::getId, ids);
		return iamRoleMapper.selectList(queryWrapper).stream().map(t -> ConvertHelper.convert(t, IamRoleDTO.class)).collect(Collectors.toList());
	}

	@Override
	public @NotNull List<IamRoleDTO> getRolesByCode(@Nullable Set<String> codes) {
		if (CollUtil.isEmpty(codes)) {
			return new ArrayList<>(0);
		}
		LambdaQueryWrapper<IamRole> queryWrapper = Wrappers.<IamRole>lambdaQuery().in(IamRole::getCode, codes);
		return iamRoleMapper.selectList(queryWrapper).stream().map(t -> ConvertHelper.convert(t, IamRoleDTO.class)).collect(Collectors.toList());
	}
}

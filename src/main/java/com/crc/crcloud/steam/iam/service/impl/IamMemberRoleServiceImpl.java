package com.crc.crcloud.steam.iam.service.impl;


import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.crc.crcloud.steam.iam.common.enums.MemberType;
import com.crc.crcloud.steam.iam.common.exception.IamAppCommException;
import com.crc.crcloud.steam.iam.common.utils.EntityUtil;
import com.crc.crcloud.steam.iam.dao.IamMemberRoleMapper;
import com.crc.crcloud.steam.iam.entity.IamMemberRole;
import com.crc.crcloud.steam.iam.model.dto.IamMemberRoleDTO;
import com.crc.crcloud.steam.iam.model.dto.IamRoleDTO;
import com.crc.crcloud.steam.iam.model.dto.IamUserDTO;
import com.crc.crcloud.steam.iam.model.dto.user.IamMemberRoleWithRoleDTO;
import com.crc.crcloud.steam.iam.model.event.IamGrantUserRoleEvent;
import com.crc.crcloud.steam.iam.service.IamMemberRoleService;
import com.crc.crcloud.steam.iam.service.IamRoleService;
import com.crc.crcloud.steam.iam.service.IamUserService;
import io.choerodon.core.convertor.ApplicationContextHelper;
import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.iam.ResourceLevel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Author
 * @Description
 * @Date 2019-11-12
 */
@Slf4j
@Validated
@Service
public class IamMemberRoleServiceImpl extends ServiceImpl<IamMemberRoleMapper, IamMemberRole> implements IamMemberRoleService {

	@Autowired
	private IamMemberRoleMapper iamMemberRoleMapper;

	@Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
	@Override
	public void grantUserRole(@NotNull Long userId, @NotEmpty Set<Long> roleIds, @NotNull Long sourceId, @NotNull ResourceLevel resourceLevel) {
		if (!CollUtil.newHashSet(ResourceLevel.SITE, ResourceLevel.ORGANIZATION, ResourceLevel.PROJECT).contains(resourceLevel)) {
			throw new IamAppCommException("role.grant.source.type.not.support");
		}
		IamUserService iamUserService = ApplicationContextHelper.getContext().getBean(IamUserService.class);
		IamUserDTO iamUser = iamUserService.getAndThrow(userId);
		//todo 校验资源有效性
		IamRoleService iamRoleService = ApplicationContextHelper.getContext().getBean(IamRoleService.class);

		@NotNull List<IamRoleDTO> userRoles = iamRoleService.getUserRoles(iamUser.getId(), resourceLevel);

		//需要授权的角色
		Set<Long> needGrantRoles = CollUtil.newHashSet(roleIds);
		//从待授权列表中移除已经授权过的角色
		userRoles.forEach(r -> needGrantRoles.remove(r.getId()));
		if (needGrantRoles.isEmpty()) {
			log.warn("没有需要增量授权的角色");
			return;
		}
		@NotNull List<IamRoleDTO> roles = iamRoleService.getRoles(needGrantRoles);
		if (needGrantRoles.size() != roles.size()) {
			throw new IamAppCommException("role.not.exist");
		} else if (!roles.stream().allMatch(t -> Objects.equals(t.getFdLevel(), resourceLevel.value()))) {
			throw new IamAppCommException("role.grant.source.type.not.match");
		}
		log.info("用户[{}]授权{}[{}]角色: {}", iamUser.getLoginName(), resourceLevel, sourceId, roles.stream().map(IamRoleDTO::getName).collect(Collectors.joining(",")));
		List<IamMemberRole> incrementRoleMembers = roles.stream().map(t -> {
			return IamMemberRole.builder()
					.roleId(t.getId())
					.memberId(iamUser.getId()).memberType(MemberType.USER.getValue())
					.sourceId(sourceId).sourceType(resourceLevel.value())
					.build();
		}).collect(Collectors.toList());
		this.saveBatch(incrementRoleMembers);
		//发出授权角色事件,发出的事件都是增量授权的列表
		List<IamMemberRoleWithRoleDTO> rolesEvents = new ArrayList<>(incrementRoleMembers.size());
		for (IamMemberRole incrementRoleMember : incrementRoleMembers) {
			IamRoleDTO one = CollUtil.findOneByField(roles, EntityUtil.getSimpleFieldToCamelCase(IamRoleDTO::getId), incrementRoleMember.getRoleId());
			rolesEvents.add(new IamMemberRoleWithRoleDTO(ConvertHelper.convert(incrementRoleMember, IamMemberRoleDTO.class), one));
		}
		ApplicationContextHelper.getContext().publishEvent(new IamGrantUserRoleEvent(iamUser, rolesEvents));
	}

	@Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
	@Override
	public void grantUserRole(@NotNull Set<Long> userIds, @NotEmpty Set<Long> roleIds, @NotNull Long sourceId, @NotNull ResourceLevel resourceLevel) {
		for (Long userId : userIds) {
			grantUserRole(userId, roleIds, sourceId, resourceLevel);
		}
	}
}

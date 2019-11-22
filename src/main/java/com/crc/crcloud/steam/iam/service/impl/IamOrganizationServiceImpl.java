package com.crc.crcloud.steam.iam.service.impl;


import cn.hutool.core.collection.CollUtil;
import com.crc.crcloud.steam.iam.dao.IamOrganizationMapper;
import com.crc.crcloud.steam.iam.entity.IamOrganization;
import com.crc.crcloud.steam.iam.entity.IamUserOrganizationRel;
import com.crc.crcloud.steam.iam.model.dto.IamOrganizationDTO;
import com.crc.crcloud.steam.iam.service.IamOrganizationService;
import com.crc.crcloud.steam.iam.service.IamUserOrganizationRelService;
import io.choerodon.core.convertor.ConvertHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author
 * @Description
 * @Date 2019-11-12
 */
@Service
public class IamOrganizationServiceImpl implements IamOrganizationService {

	@Autowired
	private IamOrganizationMapper iamOrganizationMapper;
	@Autowired
	private IamUserOrganizationRelService userOrganizationRelService;

	@Override
	public @NotNull List<IamOrganizationDTO> getUserOrganizations(@NotNull Long userId) {
		@NotNull List<IamUserOrganizationRel> userOrganizations = userOrganizationRelService.getUserOrganizations(userId);
		if (CollUtil.isNotEmpty(userOrganizations)) {
			List<Long> organizationIds = userOrganizations.stream().map(IamUserOrganizationRel::getOrganizationId).collect(Collectors.toList());
			return get(organizationIds);
		}
		return new ArrayList<>();
	}

	/**
	 * 通过ID查找，按照查找顺序排序
	 * @param organizationIds 组织编号
	 * @return 组织列表
	 */
	public List<IamOrganizationDTO> get(@Nullable List<Long> organizationIds) {
		List<IamOrganizationDTO> results = new ArrayList<>();
		if (CollUtil.isNotEmpty(organizationIds)) {
			Map<Long, IamOrganizationDTO> organizationMap = iamOrganizationMapper.selectBatchIds(CollUtil.newHashSet(organizationIds)).stream().collect(Collectors.toMap(IamOrganization::getId, t -> ConvertHelper.convert(t, IamOrganizationDTO.class)));
			for (Long organizationId : organizationIds) {
				if (organizationMap.containsKey(organizationId)) {
					results.add(organizationMap.get(organizationId));
				}
			}
		}
		return results;
	}
}

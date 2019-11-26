package com.crc.crcloud.steam.iam.service.impl;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.crc.crcloud.steam.iam.common.config.ChoerodonDevOpsProperties;
import com.crc.crcloud.steam.iam.dao.IamOrganizationMapper;
import com.crc.crcloud.steam.iam.entity.IamOrganization;
import com.crc.crcloud.steam.iam.entity.IamUserOrganizationRel;
import com.crc.crcloud.steam.iam.model.dto.IamOrganizationDTO;
import com.crc.crcloud.steam.iam.model.dto.payload.OrganizationPayload;
import com.crc.crcloud.steam.iam.model.event.IamOrganizationToggleEnableEvent;
import com.crc.crcloud.steam.iam.model.vo.organization.IamOrganizationUpdateRequestVO;
import com.crc.crcloud.steam.iam.service.IamOrganizationService;
import com.crc.crcloud.steam.iam.service.IamUserOrganizationRelService;
import io.choerodon.asgard.saga.annotation.Saga;
import io.choerodon.asgard.saga.producer.StartSagaBuilder;
import io.choerodon.asgard.saga.producer.TransactionalProducer;
import io.choerodon.core.convertor.ApplicationContextHelper;
import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.core.oauth.DetailsHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Nullable;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static com.crc.crcloud.steam.iam.common.utils.SagaTopic.Organization.ORG_UPDATE;

/**
 * @Author
 * @Description
 * @Date 2019-11-12
 */
@Slf4j
@Validated
@Service
public class IamOrganizationServiceImpl implements IamOrganizationService {

	@Autowired
	private IamOrganizationMapper iamOrganizationMapper;
	@Autowired
	private IamUserOrganizationRelService userOrganizationRelService;
	@Autowired
	private ChoerodonDevOpsProperties choerodonDevOpsProperties;
	@Autowired
	private TransactionalProducer producer;

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

	@Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
	@Override
	public IamOrganizationDTO updateBySite(@NotNull @Min(1) Long id, @NotNull @Valid IamOrganizationUpdateRequestVO vo) {
		return updateByLevel(id, vo, startSagaBuilder -> startSagaBuilder.withLevel(ResourceLevel.SITE).withSourceId(0L));
	}

	@Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
	@Override
	public @NotNull IamOrganizationDTO updateByOrganization(@NotNull @Min(1) Long id, @NotNull @Valid IamOrganizationUpdateRequestVO vo) {
		return updateByLevel(id, vo, startSagaBuilder -> startSagaBuilder.withLevel(ResourceLevel.ORGANIZATION).withSourceId(id));
	}

	@Saga(code = ORG_UPDATE, description = "steam-iam更新组织", inputSchemaClass = OrganizationPayload.class)
	private IamOrganizationDTO updateByLevel(@NotNull @Min(1) Long id, @NotNull @Valid IamOrganizationUpdateRequestVO vo, Consumer<StartSagaBuilder> consumer) {
		final IamOrganizationDTO organization = getAndThrow(id);
		IamOrganization entity = IamOrganization.builder()
				.id(id)
				.name(vo.getName())
				.imageUrl(vo.getImageUrl())
				.description(vo.getDescription())
				.build();
		this.iamOrganizationMapper.updateById(entity);
		BeanUtil.copyProperties(entity, organization, CopyOptions.create().ignoreNullValue());
		final String logTitle = StrUtil.format("更新组织[{}|{}]", id, organization.getCode());
		log.info("{};完成", logTitle);
		if (choerodonDevOpsProperties.isMessage()) {
			final OrganizationPayload organizationPayload = new OrganizationPayload();
			BeanUtil.copyProperties(organization, organizationPayload);
			log.info("{};开始发送Saga事件[{code:{}}],内容: {}", logTitle, ORG_UPDATE, JSONUtil.toJsonStr(organizationPayload));
			StartSagaBuilder sagaBuilder = StartSagaBuilder.newBuilder();
			consumer.accept(sagaBuilder);
			producer.apply(sagaBuilder, startSagaBuilder -> {
				startSagaBuilder.withSagaCode(ORG_UPDATE).withPayloadAndSerialize(organizationPayload)
						.withRefType("organization")
						.withRefId(Objects.toString(id));
			});
		}
		if (Objects.nonNull(vo.getIsEnabled())) {
			toggleEnable(id, vo.getIsEnabled(), DetailsHelper.getUserDetails().getUserId());
			organization.setIsEnabled(vo.getIsEnabled());
		}
		return organization;
	}

	@Override
	public Optional<IamOrganizationDTO> get(@NotNull @Min(1) Long id) {
		return Optional.ofNullable(this.iamOrganizationMapper.selectById(id)).map(t -> ConvertHelper.convert(t, IamOrganizationDTO.class));
	}

	@Override
	public void toggleEnable(@NotNull Long id, @NotNull Boolean isEnable, Long userId) {
		final IamOrganizationDTO organization = getAndThrow(id);
		Assert.notNull(isEnable);
		String display = isEnable ? "启用" : "禁用";
		log.info("{}组织[{}|{}]", display, id, organization.getCode());
		if (Objects.equals(organization.getIsEnabled(), isEnable)) {
			log.warn("组织[{}|{}]当前已经是{}状态,不做处理", id, organization.getCode(), display);
			return;
		}
		IamOrganization iamOrganization = IamOrganization.builder().id(organization.getId()).isEnabled(isEnable).build();
		this.iamOrganizationMapper.updateById(iamOrganization);
		BeanUtil.copyProperties(iamOrganization, organization, CopyOptions.create().ignoreNullValue());
		ApplicationContextHelper.getContext().publishEvent(new IamOrganizationToggleEnableEvent(organization, userId));
	}
}

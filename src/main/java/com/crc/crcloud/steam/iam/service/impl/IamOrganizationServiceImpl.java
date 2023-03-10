package com.crc.crcloud.steam.iam.service.impl;


import cn.hutool.core.bean.BeanDesc;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crc.crcloud.steam.iam.common.config.ChoerodonDevOpsProperties;
import com.crc.crcloud.steam.iam.common.exception.IamAppCommException;
import com.crc.crcloud.steam.iam.common.utils.CopyUtil;
import com.crc.crcloud.steam.iam.common.utils.EntityUtil;
import com.crc.crcloud.steam.iam.common.utils.UserDetail;
import com.crc.crcloud.steam.iam.dao.IamOrganizationMapper;
import com.crc.crcloud.steam.iam.dao.IamRoleMapper;
import com.crc.crcloud.steam.iam.entity.IamOrganization;
import com.crc.crcloud.steam.iam.entity.IamRole;
import com.crc.crcloud.steam.iam.entity.IamUserOrganizationRel;
import com.crc.crcloud.steam.iam.model.dto.IamMemberRoleDTO;
import com.crc.crcloud.steam.iam.model.dto.IamOrganizationDTO;
import com.crc.crcloud.steam.iam.model.dto.IamRoleDTO;
import com.crc.crcloud.steam.iam.model.dto.IamUserDTO;
import com.crc.crcloud.steam.iam.model.dto.organization.IamOrganizationWithProjectCountDTO;
import com.crc.crcloud.steam.iam.model.dto.payload.OrganizationPayload;
import com.crc.crcloud.steam.iam.model.event.IamOrganizationToggleEnableEvent;
import com.crc.crcloud.steam.iam.model.vo.IamOrganizationVO;
import com.crc.crcloud.steam.iam.model.vo.organization.IamOrganizationCreateRequestVO;
import com.crc.crcloud.steam.iam.model.vo.organization.IamOrganizationPageRequestVO;
import com.crc.crcloud.steam.iam.model.vo.organization.IamOrganizationUpdateRequestVO;
import com.crc.crcloud.steam.iam.service.*;
import io.choerodon.asgard.saga.annotation.Saga;
import io.choerodon.asgard.saga.producer.StartSagaBuilder;
import io.choerodon.asgard.saga.producer.TransactionalProducer;
import io.choerodon.core.convertor.ApplicationContextHelper;
import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.iam.InitRoleCode;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.core.oauth.DetailsHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
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
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToLongFunction;
import java.util.stream.Collectors;

import static com.crc.crcloud.steam.iam.common.utils.SagaTopic.Organization.ORG_CREATE;
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
    private IamRoleMapper iamRoleMapper;

    @Autowired
    private TransactionalProducer producer;

    @Override
    public @NotNull
    List<IamOrganizationDTO> getUserOrganizations(@NotNull Long userId) {
        @NotNull List<IamUserOrganizationRel> userOrganizations = userOrganizationRelService.getUserOrganizations(userId);
        if (CollUtil.isNotEmpty(userOrganizations)) {
            List<Long> organizationIds = userOrganizations.stream().map(IamUserOrganizationRel::getOrganizationId).collect(Collectors.toList());
            return get(organizationIds);
        }
        return new ArrayList<>();
    }

    /**
     * ??????ID?????????????????????????????????
     *
     * @param organizationIds ????????????
     * @return ????????????
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
    public @NotNull
    IamOrganizationDTO updateByOrganization(@NotNull @Min(1) Long id, @NotNull @Valid IamOrganizationUpdateRequestVO vo) {
        return updateByLevel(id, vo, startSagaBuilder -> startSagaBuilder.withLevel(ResourceLevel.ORGANIZATION).withSourceId(id));
    }

    @Saga(code = ORG_UPDATE, description = "steam-iam????????????", inputSchemaClass = OrganizationPayload.class)
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
        final String logTitle = StrUtil.format("????????????[{}|{}]", id, organization.getCode());
        log.info("{};??????", logTitle);
        if (choerodonDevOpsProperties.isMessage()) {
            final OrganizationPayload organizationPayload = new OrganizationPayload();
            BeanUtil.copyProperties(organization, organizationPayload);
            log.info("{};????????????Saga??????[{code:{}}],??????: {}", logTitle, ORG_UPDATE, JSONUtil.toJsonStr(organizationPayload));
            StartSagaBuilder sagaBuilder = StartSagaBuilder.newBuilder();
            consumer.accept(sagaBuilder);
            sagaBuilder.withSagaCode(ORG_UPDATE);
            producer.apply(sagaBuilder, startSagaBuilder -> {
                startSagaBuilder.withPayloadAndSerialize(organizationPayload)
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

    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    @Override
    public void toggleEnable(@NotNull Long id, @NotNull Boolean isEnable, Long userId) {
        final IamOrganizationDTO organization = getAndThrow(id);
        Assert.notNull(isEnable);
        String display = isEnable ? "??????" : "??????";
        log.info("{}??????[{}|{}]", display, id, organization.getCode());
        if (Objects.equals(organization.getIsEnabled(), isEnable)) {
            log.warn("??????[{}|{}]???????????????{}??????,????????????", id, organization.getCode(), display);
            return;
        }
        IamOrganization iamOrganization = IamOrganization.builder().id(organization.getId()).isEnabled(isEnable).build();
        this.iamOrganizationMapper.updateById(iamOrganization);
        BeanUtil.copyProperties(iamOrganization, organization, CopyOptions.create().ignoreNullValue());
        ApplicationContextHelper.getContext().publishEvent(new IamOrganizationToggleEnableEvent(organization, userId));
    }

    @Saga(code = ORG_CREATE, description = "steam-iam????????????", inputSchemaClass = OrganizationPayload.class)
    public void createOrganizationSaga(IamOrganizationDTO iamOrganizationDTO) {
        Long organizationId = iamOrganizationDTO.getId();
        final String logTitle = StrUtil.format("????????????[{}|{}]", organizationId, iamOrganizationDTO.getCode());
        if (choerodonDevOpsProperties.isMessage()) {
            OrganizationPayload organizationPayload = new OrganizationPayload();
            BeanUtils.copyProperties(iamOrganizationDTO, organizationPayload);
            log.info("{};????????????Saga??????[{code:{}}],??????: {}", logTitle, ORG_CREATE, JSONUtil.toJsonStr(organizationPayload));
            producer.applyAndReturn(StartSagaBuilder.newBuilder()
                            .withSagaCode(ORG_CREATE)
                            .withLevel(ResourceLevel.ORGANIZATION)
                            .withSourceId(organizationId),
                    builder -> {
                        builder.withPayloadAndSerialize(organizationPayload)
                                .withRefType("organization")
                                .withSourceId(organizationId);
                        return organizationPayload;
                    });
        }
    }

    @Override
    public @NotNull
    IPage<IamOrganizationWithProjectCountDTO> page(@NotNull @Valid IamOrganizationPageRequestVO vo) {
        Page<IamOrganizationWithProjectCountDTO> page = new Page<>(vo.getCurrent(), vo.getSize());
        Function<String, String> gbkConvert = orderField -> {
            orderField = StrUtil.toUnderlineCase(orderField);
            return Objects.equals(orderField, EntityUtil.getSimpleField(IamOrganization::getName)) ? StrUtil.format("CONVERT({} USING gbk)", orderField) : orderField;
        };
        if (StrUtil.isNotBlank(vo.getAsc())) {
            page.setAsc(gbkConvert.apply(vo.getAsc()));
        }
        if (StrUtil.isNotBlank(vo.getDesc())) {
            page.setDesc(gbkConvert.apply(vo.getDesc()));
        }
        ArrayList<String> desc = Optional.ofNullable(page.descs()).map(CollUtil::toList).orElseGet(ArrayList::new);
        desc.add(EntityUtil.getSimpleField(IamOrganization::getCreationDate));
        page.setDescs(desc);
        return this.iamOrganizationMapper.page(page, vo);
    }

    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    @Override
    public IamOrganizationDTO create(@NotNull @Valid IamOrganizationCreateRequestVO vo) {
        //??????????????????????????????????????????????????????1?????????????????????????????????.-
        Predicate<String> matchCode = code -> ReUtil.isMatch("^[a-z][a-z0-9-]*$", code);
        //?????????"-??????"
        matchCode = matchCode.and(code -> !StrUtil.endWithAny(code, "-"));
        //????????????????????????"-"
        matchCode = matchCode.and(code -> !StrUtil.containsAny(code, "--"));
        if (matchCode.negate().test(vo.getCode())) {
            throw new IamAppCommException("organization.code.illegal");
        }
        if (this.iamOrganizationMapper.selectCount(Wrappers.<IamOrganization>lambdaQuery().eq(IamOrganization::getCode, vo.getCode())) > 0) {
            throw new IamAppCommException("organization.code.exist");
        }
        IamOrganization entity = new IamOrganization();
        initOrganization(entity);
        entity.setCode(vo.getCode());
        entity.setImageUrl(vo.getImageUrl());
        entity.setName(vo.getName());
        this.iamOrganizationMapper.insert(entity);
        IamOrganizationDTO iamOrganizationDTO = ConvertHelper.convert(entity, IamOrganizationDTO.class);
        createOrganizationSaga(iamOrganizationDTO);
        //????????????????????????????????????????????????????????????
        Optional.ofNullable(entity.getCreatedBy()).ifPresent(userId -> {
            ApplicationContextHelper.getContext().getBean(IamRoleService.class)
                    .getRoleByCode(InitRoleCode.ORGANIZATION_ADMINISTRATOR)
                    .ifPresent(role -> {
                        ApplicationContextHelper.getContext().getBean(IamMemberRoleService.class)
                                .grantUserRole(userId, CollUtil.newHashSet(role.getId()), entity.getId(), ResourceLevel.ORGANIZATION);
                    });
        });
        return iamOrganizationDTO;
    }

    /**
     * ???????????????
     * <p>??????????????????????????????????????????</p>
     *
     * @param entity ????????????
     */
    public void initOrganization(IamOrganization entity) {
        IamOrganization init = IamOrganization.builder()
                .isEnabled(Boolean.TRUE)
                .isRegister(Boolean.FALSE)
                .userId(UserDetail.getUserId())
                .build();
        Map<String, Object> initField = BeanUtil.beanToMap(init, false, true);
        BeanDesc beanDesc = BeanUtil.getBeanDesc(IamOrganization.class);
        initField.forEach((k, v) -> {
            BeanDesc.PropDesc prop = beanDesc.getProp(k);
            Object value = prop.getValue(entity);
            if (Objects.isNull(value)) {
                prop.setValue(entity, v);
            }
        });
    }

    @Override
    public @NotNull
    List<IamOrganizationDTO> getUserAuthOrganizations(@NotNull final Long userId, final boolean includeDisable) {
        final IamUserService iamUserService = ApplicationContextHelper.getContext().getBean(IamUserService.class);
        final IamMemberRoleService iamMemberRoleService = ApplicationContextHelper.getContext().getBean(IamMemberRoleService.class);
        final IamRoleService iamRoleService = ApplicationContextHelper.getContext().getBean(IamRoleService.class);
        final IamProjectService iamProjectService = ApplicationContextHelper.getContext().getBean(IamProjectService.class);
        final IamUserDTO iamUser = iamUserService.getAndThrow(userId);
        @NotNull List<IamRoleDTO> userRoles = iamRoleService.getUserRoles(userId, ResourceLevel.ORGANIZATION, ResourceLevel.PROJECT)
                //????????????????????????????????????
                .stream().filter(role -> includeDisable ? true : role.getIsEnabled()).collect(Collectors.toList());
        final Set<Long> roleIds = userRoles.stream().map(IamRoleDTO::getId).collect(Collectors.toSet());
        @NotNull List<IamMemberRoleDTO> sourceByOrg = iamMemberRoleService.getUserMemberRoleBySource(userId, ResourceLevel.ORGANIZATION);
        //??????????????????????????????????????????????????????
        sourceByOrg = sourceByOrg.stream().filter(t -> roleIds.contains(t.getRoleId())).collect(Collectors.toList());
        @NotNull List<IamMemberRoleDTO> sourceByPro = iamMemberRoleService.getUserMemberRoleBySource(userId, ResourceLevel.PROJECT);
        sourceByPro = sourceByPro.stream().filter(t -> roleIds.contains(t.getRoleId())).collect(Collectors.toList());
        //???????????????ID
        final Set<Long> organizationIds = sourceByOrg.stream().map(IamMemberRoleDTO::getSourceId).collect(Collectors.toSet());
        //???????????????????????????????????????????????????????????????
        iamProjectService.getByIds(sourceByPro.stream().map(IamMemberRoleDTO::getSourceId).collect(Collectors.toSet()))
                .forEach(pro -> {
                    if (includeDisable ? true : pro.getIsEnabled()) {
                        organizationIds.add(pro.getOrganizationId());
                    }
                });
        //???????????????????????????????????????????????????
        ToLongFunction<IamOrganizationDTO> keyExtractor = o -> Optional.ofNullable(o.getCreationDate()).map(Date::getTime).orElse(0L);
        List<IamOrganizationDTO> organizations = getByIds(organizationIds);
        return organizations.stream().filter(t -> includeDisable ? true : t.getIsEnabled()).sorted(Comparator.comparingLong(keyExtractor)).collect(Collectors.toList());
    }

    @Override
    public List<IamOrganizationDTO> getByIds(@Nullable Set<Long> organizationIds) {
        if (CollUtil.isEmpty(organizationIds)) {
            return new ArrayList<>();
        }
        return this.iamOrganizationMapper.selectBatchIds(organizationIds).stream().map(t -> ConvertHelper.convert(t, IamOrganizationDTO.class)).collect(Collectors.toList());
    }

    @Override
    public List<IamOrganizationVO> queryAllOrganization(Long userId) {

        IamRole iamRole = iamRoleMapper.selectOne(Wrappers.<IamRole>lambdaQuery().eq(IamRole::getFdLevel, ResourceLevel.ORGANIZATION).eq(IamRole::getCode, InitRoleCode.ORGANIZATION_ADMINISTRATOR).eq(IamRole::getIsEnabled, true));
        //??????????????????????????????????????????????????????
        List<IamOrganization> organizations = iamOrganizationMapper.queryAllOrganization(userId, iamRole.getId());


        return CopyUtil.copyList(organizations, IamOrganizationVO.class);
    }
}

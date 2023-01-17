package com.crc.crcloud.steam.iam.service.impl;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ArrayUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.crc.crcloud.steam.iam.common.enums.MemberType;
import com.crc.crcloud.steam.iam.common.exception.IamAppCommException;
import com.crc.crcloud.steam.iam.common.utils.EntityUtil;
import com.crc.crcloud.steam.iam.common.utils.PageWrapper;
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
import io.choerodon.core.iam.InitRoleCode;
import io.choerodon.core.iam.ResourceLevel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Nullable;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.*;
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
        @NotNull List<IamMemberRoleDTO> memberRoles = this.getUserMemberRoleByBySource(iamUser.getId(), resourceLevel, sourceId);
        //需要授权的角色
        Set<Long> needGrantRoles = CollUtil.newHashSet(roleIds);
        //从待授权列表中移除已经授权过的角色
        memberRoles.forEach(r -> needGrantRoles.remove(r.getRoleId()));
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

    @Override
    public @NotNull
    List<IamMemberRoleDTO> getUserMemberRoleByOrganization(@NotNull Long userId, @NotEmpty Set<Long> organizationIds) {
        return getUserMemberRoleByBySource(userId, ResourceLevel.ORGANIZATION, ArrayUtil.toArray(organizationIds, Long.class));
    }

    /**
     * 查询用户的角色关联关系
     *
     * @param userId     用户编号
     * @param sourceType 资源级别
     * @param sourceIds  资源编号-只有 {@link ResourceLevel#ORGANIZATION}{@link  ResourceLevel#PROJECT} 不传时查所有
     * @return 关联关系集合
     */
    @NotNull
    private List<IamMemberRoleDTO> getUserMemberRoleByBySource(@NotNull Long userId, @NotNull ResourceLevel sourceType, Long... sourceIds) {
        final Set<ResourceLevel> needSourceIds = CollUtil.newHashSet(ResourceLevel.ORGANIZATION, ResourceLevel.PROJECT);
        LambdaQueryWrapper<IamMemberRole> queryWrapper = Wrappers.<IamMemberRole>lambdaQuery()
                .eq(IamMemberRole::getMemberId, userId)
                .eq(IamMemberRole::getMemberType, ResourceLevel.USER.value())
                .eq(IamMemberRole::getSourceType, sourceType.value());
        if (needSourceIds.contains(sourceType)) {
            List<Long> ids = CollUtil.toList(sourceIds).stream().filter(Objects::nonNull).collect(Collectors.toList());
            if (CollUtil.isNotEmpty(ids)) {
                queryWrapper.in(IamMemberRole::getSourceId, ids);
            }
        }
        return this.iamMemberRoleMapper.selectList(queryWrapper).stream().map(t -> ConvertHelper.convert(t, IamMemberRoleDTO.class)).collect(Collectors.toList());
    }

    @Override
    public @NotNull
    List<IamMemberRoleDTO> getUserMemberRoleBySource(@NotNull Long userId, @NotNull ResourceLevel sourceType) {
        return getUserMemberRoleByBySource(userId, sourceType);
    }

    @Override
    public @NotNull
    List<IamMemberRoleDTO> getUserMemberRoleBySource(@NotNull ResourceLevel sourceType, @Nullable Long... sourceIds) {
        final Set<ResourceLevel> needSourceIds = CollUtil.newHashSet(ResourceLevel.ORGANIZATION, ResourceLevel.PROJECT);
        LambdaQueryWrapper<IamMemberRole> queryWrapper = Wrappers.<IamMemberRole>lambdaQuery()
                .eq(IamMemberRole::getMemberType, ResourceLevel.USER.value())
                .eq(IamMemberRole::getSourceType, sourceType.value());
        if (needSourceIds.contains(sourceType) && ArrayUtil.isNotEmpty(sourceIds)) {
            List<Long> ids = CollUtil.toList(sourceIds).stream().filter(Objects::nonNull).collect(Collectors.toList());
            if (CollUtil.isNotEmpty(ids)) {
                queryWrapper.in(IamMemberRole::getSourceId, ids);
            }
        }
        return this.iamMemberRoleMapper.selectList(queryWrapper).stream().map(t -> ConvertHelper.convert(t, IamMemberRoleDTO.class)).collect(Collectors.toList());
    }

    @Override
    public List<IamMemberRoleDTO> getSiteUserMemberRoleBySource() {
        return getUserMemberRoleBySource(ResourceLevel.SITE);
    }

    @Override
    public void grantUserSiteRole(@NotNull Long userId, @NotEmpty Set<Long> roleIds) {
        this.grantUserRole(userId, roleIds, 0L, ResourceLevel.SITE);
    }

    @Override
    public @NotNull
    IPage<Long> getSiteAdminUserId(@NotNull Page page) {
        IamRoleService iamRoleService = ApplicationContextHelper.getContext().getBean(IamRoleService.class);
        Optional<IamRoleDTO> siteAdminRole = iamRoleService.getRoleByCode(InitRoleCode.SITE_ADMINISTRATOR);
        if (!siteAdminRole.isPresent()) {
            return new Page<>(page.getCurrent(), page.getSize());
        }
        PageWrapper pageWrapper = PageWrapper.instance(page);
        pageWrapper.addDefaultOrderByDesc("rel_date");
        pageWrapper.addGbkFieldConvert(EntityUtil.getSimpleField(IamUserDTO::getRealName));
        return iamMemberRoleMapper.getSiteAdminUserId(pageWrapper, siteAdminRole.get().getId());
    }
}

package com.crc.crcloud.steam.iam.service.impl;


import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.crc.crcloud.steam.iam.common.exception.IamAppCommException;
import com.crc.crcloud.steam.iam.dao.IamRolePermissionMapper;
import com.crc.crcloud.steam.iam.entity.IamRolePermission;
import com.crc.crcloud.steam.iam.model.dto.IamPermissionDTO;
import com.crc.crcloud.steam.iam.model.dto.IamRoleDTO;
import com.crc.crcloud.steam.iam.model.dto.IamRolePermissionDTO;
import com.crc.crcloud.steam.iam.service.IamPermissionService;
import com.crc.crcloud.steam.iam.service.IamRolePermissionService;
import com.crc.crcloud.steam.iam.service.IamRoleService;
import io.choerodon.core.convertor.ApplicationContextHelper;
import io.choerodon.core.convertor.ConvertHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Author
 * @Description
 * @Date 2019-11-29
 */
@Slf4j
@Validated
@Service
public class IamRolePermissionServiceImpl extends ServiceImpl<IamRolePermissionMapper, IamRolePermission> implements IamRolePermissionService {

    @Autowired
    private IamRolePermissionMapper iamRolePermissionMapper;

    @Override
    public @NotNull List<IamRolePermissionDTO> link(@NotNull Long permissionId, @NotEmpty Set<Long> roleIds) {
        final IamPermissionService iamPermissionService = ApplicationContextHelper.getContext().getBean(IamPermissionService.class);
        final IamRoleService iamRoleService = ApplicationContextHelper.getContext().getBean(IamRoleService.class);
        final IamPermissionDTO iamPermission = iamPermissionService.getAndThrow(permissionId);
        //获取到已经关联过的列表
        List<IamRolePermission> iamRolePermissions = iamRolePermissionMapper.selectList(Wrappers.<IamRolePermission>lambdaQuery().eq(IamRolePermission::getPermissionId, iamPermission.getId()));
        //去除掉已经关联过的数据，筛选出需要关联的数据
        Set<Long> needLinkRoleIds = roleIds.stream().filter(t -> iamRolePermissions.stream().noneMatch(rp -> Objects.equals(t, rp.getRoleId()))).collect(Collectors.toSet());
        @NotNull List<IamRoleDTO> roles = iamRoleService.getRoles(needLinkRoleIds);
        if (roles.size() != needLinkRoleIds.size()) {
            throw new IamAppCommException("role.not.exist");
        }
        if (CollUtil.isNotEmpty(roles)) {
            List<IamRolePermission> insertBatch = roles.stream().map(role -> {
                return IamRolePermission.builder().permissionId(iamPermission.getId()).roleId(role.getId()).build();
            }).collect(Collectors.toList());
            this.saveBatch(insertBatch);
            iamRolePermissions.addAll(insertBatch);
        }
        return iamRolePermissions.stream()
                .filter(rp -> roleIds.contains(rp.getRoleId()))
                .map(t -> ConvertHelper.convert(t, IamRolePermissionDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public void clear(@NotNull Long permissionId) {
        LambdaQueryWrapper<IamRolePermission> queryWrapper = Wrappers.<IamRolePermission>lambdaQuery().eq(IamRolePermission::getPermissionId, permissionId);
        this.iamRolePermissionMapper.delete(queryWrapper);
    }

    @Override
    public @NotNull List<IamRolePermissionDTO> selectErrorLevelPermissionByRole(@NotNull Long roleId) {
        List<IamRolePermission> list = iamRolePermissionMapper.selectErrorLevelPermissionByRole(roleId);
        return ConvertHelper.convertList(CollUtil.newArrayList(list), IamRolePermissionDTO.class);
    }

    @Override
    public void delete(@NotNull Long id) {
        iamRolePermissionMapper.deleteById(id);
    }
}

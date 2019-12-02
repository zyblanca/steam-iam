package com.crc.crcloud.steam.iam.service.impl;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.crc.crcloud.steam.iam.common.exception.IamAppCommException;
import com.crc.crcloud.steam.iam.dao.IamPermissionMapper;
import com.crc.crcloud.steam.iam.entity.IamPermission;
import com.crc.crcloud.steam.iam.model.dto.IamPermissionDTO;
import com.crc.crcloud.steam.iam.service.IamPermissionService;
import com.crc.crcloud.steam.iam.service.IamRolePermissionService;
import io.choerodon.core.convertor.ConvertHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @Author
 * @Description
 * @Date 2019-11-29
 */
@Slf4j
@Validated
@Service
public class IamPermissionServiceImpl implements IamPermissionService {

    @Autowired
    private IamPermissionMapper iamPermissionMapper;
    @Autowired
    private IamRolePermissionService iamRolePermissionService;

    @Override
    public List<IamPermissionDTO> getByCodes(List<String> codes) {
        if (CollUtil.isNotEmpty(codes)) {
            return new ArrayList<>();
        }
        LambdaQueryWrapper<IamPermission> queryWrapper = Wrappers.<IamPermission>lambdaQuery().in(IamPermission::getCode, codes);
        return this.iamPermissionMapper.selectList(queryWrapper).stream().map(t -> ConvertHelper.convert(t, IamPermissionDTO.class)).collect(Collectors.toList());
    }

    @Override
    public @NotNull IamPermissionDTO put(@NotNull IamPermissionDTO dto) {
        IamPermission iamPermission = ConvertHelper.convert(dto, IamPermission.class);
        iamPermission.setId(null);
        Assert.notBlank(iamPermission.getCode(), "权限code不能为空");
        Optional<IamPermissionDTO> optional = this.getByCode(iamPermission.getCode());
        if (optional.isPresent()) {
            //更新权限
            iamPermission.setId(optional.get().getId());
            iamPermissionMapper.updateById(iamPermission);
        } else {
            //新增权限
            iamPermissionMapper.insert(iamPermission);
        }
        return ConvertHelper.convert(iamPermission, IamPermissionDTO.class);
    }

    @Override
    public IamPermissionDTO getAndThrow(@NotNull Long id) {
        return get(id)
                .orElseThrow(() -> new IamAppCommException("permission.not.exist"));
    }

    @Override
    public @NotNull List<IamPermissionDTO> getByService(@NotBlank String serviceName) {
        LambdaQueryWrapper<IamPermission> queryWrapper = Wrappers.<IamPermission>lambdaQuery().in(IamPermission::getServiceName, serviceName);
        return this.iamPermissionMapper.selectList(queryWrapper).stream().map(t -> ConvertHelper.convert(t, IamPermissionDTO.class)).collect(Collectors.toList());
    }

    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    @Override
    public boolean delete(@NotNull Long permissionId) {
        iamRolePermissionService.clear(permissionId);
        return this.iamPermissionMapper.deleteById(permissionId) > 0;
    }

    @Override
    public Optional<IamPermissionDTO> get(@NotNull Long permissionId) {
        return Optional.ofNullable(iamPermissionMapper.selectById(permissionId))
                .map(t -> ConvertHelper.convert(t, IamPermissionDTO.class));
    }
}

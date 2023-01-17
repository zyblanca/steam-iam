package com.crc.crcloud.steam.iam.service;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.crc.crcloud.steam.iam.model.dto.IamPermissionDTO;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

/**
 * @Author
 * @Description
 * @Date 2019-11-29
 */
public interface IamPermissionService {
    /**
     * 通过code查询权限
     *
     * @param codes {@link IamPermissionDTO#getCode()}
     * @return 集合
     */
    List<IamPermissionDTO> getByCodes(List<String> codes);

    /**
     * 通过code查询权限
     *
     * @param code {@link IamPermissionDTO#getCode()}
     * @return 集合
     */
    default Optional<IamPermissionDTO> getByCode(String code) {
        if (StrUtil.isBlank(code)) {
            return Optional.empty();
        }
        return getByCodes(CollUtil.newArrayList(code)).stream().findFirst();
    }

    /**
     * 增加权限
     * <p>如果权限已经存在，则更新,通过code码判定</p>
     * <p>暂没做校验</p>
     *
     * @param dto 要新增的权限
     * @return 新增或修改之后的权限
     */
    @NotNull
    IamPermissionDTO put(@NotNull IamPermissionDTO dto);

    /**
     * 通过ID获取权限
     *
     * @param id 编号
     * @return 权限信息
     */
    IamPermissionDTO getAndThrow(@NotNull Long id);

    /**
     * 获取某服务下所有的权限
     *
     * @param serviceName 服务名
     * @return 权限列表
     */
    @NotNull
    List<IamPermissionDTO> getByService(@NotBlank String serviceName);

    /**
     * 删除权限
     * <p>会附带清除关联关系{@link IamRolePermissionService#clear(Long)}</p>
     *
     * @param permissionId 权限编号
     * @return true：删除成功
     */
    boolean delete(@NotNull Long permissionId);

    /**
     * 通过ID查询
     *
     * @param permissionId 编号
     * @return 权限对象
     */
    Optional<IamPermissionDTO> get(@NotNull Long permissionId);
}

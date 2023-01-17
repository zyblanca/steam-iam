package com.crc.crcloud.steam.iam.service.impl;


import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.crc.crcloud.steam.iam.dao.IamUserOrganizationRelMapper;
import com.crc.crcloud.steam.iam.entity.IamUserOrganizationRel;
import com.crc.crcloud.steam.iam.model.dto.IamUserDTO;
import com.crc.crcloud.steam.iam.model.dto.IamUserOrganizationRelDTO;
import com.crc.crcloud.steam.iam.service.IamUserOrganizationRelService;
import com.crc.crcloud.steam.iam.service.IamUserService;
import io.choerodon.core.convertor.ApplicationContextHelper;
import io.choerodon.core.convertor.ConvertHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author
 * @Description
 * @Date 2019-11-12
 */
@Validated
@Slf4j
@Service
public class IamUserOrganizationRelServiceImpl extends ServiceImpl<IamUserOrganizationRelMapper, IamUserOrganizationRel> implements IamUserOrganizationRelService {

    @Autowired
    private IamUserOrganizationRelMapper iamUserOrganizationRelMapper;

    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    @Override
    public void link(@NotNull Long userId, @NotEmpty Set<Long> organizationIds) {
        IamUserService iamUserService = ApplicationContextHelper.getContext().getBean(IamUserService.class);
        IamUserDTO iamUser = iamUserService.getAndThrow(userId);
        //todo 校验组织
        //已经关联的组织
        @NotNull List<IamUserOrganizationRelDTO> relList = getByUserId(iamUser.getId());
        //获取出不存在的组织编号，进行增量关联
        Set<Long> collect = relList.stream().map(IamUserOrganizationRelDTO::getOrganizationId).collect(Collectors.toSet());
        //需要增量的组织
        List<IamUserOrganizationRel> incrementOrganizations = organizationIds.stream()
                .filter(t -> !collect.contains(t))
                .map(organizationId -> IamUserOrganizationRel.builder().organizationId(organizationId).userId(userId).build())
                .collect(Collectors.toList());
        saveBatch(incrementOrganizations);
        log.info("用户[{}]新增所属组织[{}]", iamUser.getLoginName(), CollUtil.join(organizationIds, ","));
    }

    @NotNull
    public List<IamUserOrganizationRelDTO> getByUserId(@NotNull Long userId) {
        List<IamUserOrganizationRel> relList = iamUserOrganizationRelMapper.selectList(Wrappers.<IamUserOrganizationRel>lambdaQuery().eq(IamUserOrganizationRel::getUserId, userId));
        return ConvertHelper.convertList(relList, IamUserOrganizationRelDTO.class);
    }

    @Override
    public @NotNull
    List<IamUserOrganizationRel> getUserOrganizations(@NotNull Long userId) {
        return getUserOrganizations(CollUtil.newHashSet(userId)).getOrDefault(userId, new ArrayList<>(0));
    }

    @Override
    public Map<Long, List<IamUserOrganizationRel>> getUserOrganizations(@NotNull Set<Long> userIds) {
        if (CollUtil.isEmpty(userIds)) {
            return new HashMap<>(0);
        }
        Map<Long, List<IamUserOrganizationRel>> result = new HashMap<>(userIds.size());
        LambdaQueryWrapper<IamUserOrganizationRel> queryWrapper = Wrappers.<IamUserOrganizationRel>lambdaQuery()
                .in(IamUserOrganizationRel::getUserId, userIds)
                .orderByAsc(IamUserOrganizationRel::getId);
        iamUserOrganizationRelMapper.selectList(queryWrapper)
                .forEach(t -> {
                    List<IamUserOrganizationRel> value = result.getOrDefault(t.getUserId(), new ArrayList<>());
                    value.add(t);
                    result.put(t.getUserId(), value);
                });
        return result;
    }
}

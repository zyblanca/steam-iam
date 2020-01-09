package com.crc.crcloud.steam.iam.web;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.crc.crcloud.steam.iam.common.enums.UserOriginEnum;
import com.crc.crcloud.steam.iam.common.utils.EntityUtil;
import com.crc.crcloud.steam.iam.common.utils.ResponseEntity;
import com.crc.crcloud.steam.iam.model.dto.IamMemberRoleDTO;
import com.crc.crcloud.steam.iam.model.dto.IamRoleDTO;
import com.crc.crcloud.steam.iam.model.dto.IamUserDTO;
import com.crc.crcloud.steam.iam.model.vo.IamUserVO;
import com.crc.crcloud.steam.iam.model.vo.user.*;
import com.crc.crcloud.steam.iam.service.IamMemberRoleService;
import com.crc.crcloud.steam.iam.service.IamRoleService;
import com.crc.crcloud.steam.iam.service.IamUserOrganizationRelService;
import com.crc.crcloud.steam.iam.service.IamUserService;
import io.choerodon.core.iam.InitRoleCode;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 组织成员
 */
@RestController
@RequestMapping(value = "/v1/organizations/{organization_id}/user")
public class OrganizationUserController {
    @Autowired
    private IamUserService iamUserService;
    @Autowired
    private IamRoleService iamRoleService;
    @Autowired
    private IamMemberRoleService memberRoleService;
    @Autowired
    private IamUserOrganizationRelService userOrganizationRelService;

    /**
     * 获取的是
     * @param organizationId
     * @param vo
     * @return
     */
    @Permission(level = ResourceLevel.ORGANIZATION, roles = {InitRoleCode.ORGANIZATION_ADMINISTRATOR, InitRoleCode.ORGANIZATION_MEMBER})
    @ApiOperation(value = "组织成员列表", notes = "分页", response = IamOrganizationUserPageResponseVO.class)
    @PostMapping("page")
    public ResponseEntity<IPage<IamOrganizationUserPageResponseVO>> page(@PathVariable("organization_id") Long organizationId
            , @RequestBody @Valid IamOrganizationUserPageRequestVO vo) {
        Optional.ofNullable(vo.getPage()).ifPresent(value -> vo.setCurrent(Convert.toLong(value)));
        Optional.ofNullable(vo.getPageSize()).ifPresent(value -> vo.setSize(Convert.toLong(value)));
        IPage<IamUserVO> pageResult = iamUserService.pageQueryOrganizationUser(organizationId, vo);
        final MultiValueMap<Long, IamRoleDTO> iamUserRoleMap = new LinkedMultiValueMap<>(pageResult.getRecords().size());
        final Map<Long, IamRoleDTO> iamRoleMap = new ConcurrentHashMap<>(2);
        pageResult.getRecords().forEach(t -> {
            @NotNull List<IamMemberRoleDTO> userMemberRoleByOrganization = memberRoleService.getUserMemberRoleByOrganization(t.getId(), CollUtil.newHashSet(organizationId));
            Set<Long> roleIds = userMemberRoleByOrganization.stream().map(IamMemberRoleDTO::getRoleId).filter(roleId -> !iamRoleMap.containsKey(roleId)).collect(Collectors.toSet());
            iamRoleService.getRoles(roleIds).forEach(role -> iamRoleMap.put(role.getId(), role));
            userMemberRoleByOrganization.forEach(userMemberRole -> {
                if (iamRoleMap.containsKey(userMemberRole.getRoleId())) {
                    iamUserRoleMap.add(t.getId(), iamRoleMap.get(userMemberRole.getRoleId()));
                }
            });
        });
        Function<IamUserVO, IamOrganizationUserPageResponseVO> convert = t -> {
            IamOrganizationUserPageResponseVO responseVO = new IamOrganizationUserPageResponseVO();
            BeanUtil.copyProperties(t, responseVO);
            responseVO.setOrigin(t.getIsLdap() ? UserOriginEnum.LDAP.getDesc() : UserOriginEnum.MANUAL.getDesc());
            List<IamRoleDTO> roles = iamUserRoleMap.getOrDefault(t.getId(), new ArrayList<>());
            responseVO.setRoleName(roles.stream().map(IamRoleDTO::getName).collect(Collectors.joining(",")));
            return responseVO;
        };
        return new ResponseEntity<>(pageResult.convert(convert));
    }

    @Permission(level = ResourceLevel.ORGANIZATION, roles = {InitRoleCode.ORGANIZATION_ADMINISTRATOR})
    @ApiOperation(value = "手动创建组织成员")
    @PostMapping
    public ResponseEntity<IamUserSafeVO> createUser(@PathVariable("organization_id") Long organizationId,
                                                    @RequestBody @Valid IamUserCreateRequestVO vo) {
        IamUserDTO userDTO = iamUserService.createUserByManual(vo, CollUtil.newLinkedHashSet(organizationId));
        return new ResponseEntity<>(new IamUserSafeVO(userDTO));
    }

    @ApiOperation("获取所有组织成员")
    @Permission(level = ResourceLevel.ORGANIZATION, roles = {InitRoleCode.ORGANIZATION_ADMINISTRATOR})
    @GetMapping
    public ResponseEntity<List<IamUserSafeVO>> list(@PathVariable("organization_id") Long organizationId) {
        IamOrganizationUserPageRequestVO pageRequestVO = new IamOrganizationUserPageRequestVO();
        pageRequestVO.setSize(999L);
        List<IamUserSafeVO> orgUsers = new ArrayList<>(500);
        AtomicLong page = new AtomicLong();
        IPage<IamUserSafeVO> pageResult;
        pageRequestVO.setAsc(EntityUtil.getSimpleField(IamUserSafeVO::getRealName));
        do {
            pageRequestVO.setCurrent(page.incrementAndGet());
            pageResult = iamUserService.pageQueryOrganizationUser(organizationId, pageRequestVO).convert(IamUserSafeVO::new);
            orgUsers.addAll(pageResult.getRecords());
        } while (pageResult.getCurrent() < pageResult.getPages());
        return new ResponseEntity<>(orgUsers);
    }

    @Permission(level = ResourceLevel.ORGANIZATION, roles = {InitRoleCode.ORGANIZATION_ADMINISTRATOR})
    @ApiOperation("给用户授权角色")
    @PutMapping("grant/role")
    public ResponseEntity grantUserRole(@PathVariable("organization_id") Long organizationId, @RequestBody @Valid GrantUserRoleRequestVO vo) {
        memberRoleService.grantUserRole(vo.getUserIds(), CollUtil.newHashSet(vo.getRoleId()), organizationId, ResourceLevel.ORGANIZATION);
        return new ResponseEntity();
    }

    @ApiOperation(value = "用户关联角色下拉框", notes = "获取组织下没有关联过该角色的用户")
    @Permission(level = ResourceLevel.ORGANIZATION, roles = {InitRoleCode.ORGANIZATION_ADMINISTRATOR})
    @GetMapping("role/{role_id}/negate")
    public ResponseEntity<List<IamUserSafeVO>> negateMemberByRole(@PathVariable("organization_id") Long organizationId
            , @PathVariable("role_id") @Min(1) @NotNull Long roleId) {
        ResponseEntity<List<IamUserSafeVO>> responseEntity = this.list(organizationId);
        List<IamUserSafeVO> negateMember = responseEntity.getData().parallelStream().filter(t -> {
            //过滤掉用户不包含所在角色的用户
            @NotNull List<IamMemberRoleDTO> userMemberRoleByOrganization = memberRoleService.getUserMemberRoleByOrganization(t.getId(), CollUtil.newHashSet(organizationId));
            return userMemberRoleByOrganization.stream().noneMatch(memberRole -> Objects.equals(memberRole.getRoleId(), roleId));
        }).collect(Collectors.toList());
        return new ResponseEntity<>(negateMember);
    }

}

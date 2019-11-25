package com.crc.crcloud.steam.iam.web;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.crc.crcloud.steam.iam.common.enums.UserOriginEnum;
import com.crc.crcloud.steam.iam.common.utils.EntityUtil;
import com.crc.crcloud.steam.iam.common.utils.ResponseEntity;
import com.crc.crcloud.steam.iam.model.dto.IamRoleDTO;
import com.crc.crcloud.steam.iam.model.dto.IamUserDTO;
import com.crc.crcloud.steam.iam.model.vo.IamUserVO;
import com.crc.crcloud.steam.iam.model.vo.user.*;
import com.crc.crcloud.steam.iam.service.IamMemberRoleService;
import com.crc.crcloud.steam.iam.service.IamRoleService;
import com.crc.crcloud.steam.iam.service.IamUserService;
import io.choerodon.core.iam.InitRoleCode;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
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


   // @Permission(level = ResourceLevel.ORGANIZATION, roles = {InitRoleCode.ORGANIZATION_ADMINISTRATOR, InitRoleCode.ORGANIZATION_MEMBER})
   @Permission(permissionLogin = true)
    @ApiOperation(value = "组织成员列表", notes = "分页", response = IamOrganizationUserPageResponseVO.class)
    @PostMapping("page")
    public ResponseEntity<IPage<IamOrganizationUserPageResponseVO>> page(@PathVariable("organization_id") Long organizationId
            , @RequestBody @Valid IamOrganizationUserPageRequestVO vo) {
        if (StrUtil.isAllBlank(vo.getAsc(), vo.getDesc())) {
            vo.setAsc(EntityUtil.getSimpleField(IamUserDTO::getLoginName));
        }
        IPage<IamUserVO> pageResult = iamUserService.pageQueryOrganizationUser(organizationId, vo);
        final Map<Long, List<IamRoleDTO>> iamUserRoleMap = new ConcurrentHashMap<>(pageResult.getRecords().size());
        pageResult.getRecords().parallelStream().forEach(t -> {
            iamUserRoleMap.put(t.getId(), iamRoleService.getUserRolesByOrganization(t.getId()));
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

   // @Permission(level = ResourceLevel.ORGANIZATION, roles = {InitRoleCode.ORGANIZATION_ADMINISTRATOR})
   @Permission(permissionLogin = true)
    @ApiOperation(value = "手动创建组织成员")
    @PostMapping
    public ResponseEntity<IamUserSafeVO> createUser(@PathVariable("organization_id") Long organizationId,
                                                    @RequestBody @Valid IamUserCreateRequestVO vo) {
        IamUserDTO userDTO = iamUserService.createUserByManual(vo, CollUtil.newLinkedHashSet(organizationId));
        return new ResponseEntity<>(new IamUserSafeVO(userDTO));
    }

    @ApiOperation("获取所有组织成员")
   // @Permission(level = ResourceLevel.ORGANIZATION, roles = {InitRoleCode.ORGANIZATION_ADMINISTRATOR})
    @Permission(permissionLogin = true)
    @GetMapping
    public ResponseEntity<List<IamUserSafeVO>> list(@PathVariable("organization_id") Long organizationId) {
        IamOrganizationUserPageRequestVO pageRequestVO = IamOrganizationUserPageRequestVO.builder().pageSize(999).build();
        List<IamUserSafeVO> orgUsers = new ArrayList<>(500);
        AtomicInteger page = new AtomicInteger();
        IPage<IamUserSafeVO> pageResult;
        do {
            pageRequestVO.setPage(page.incrementAndGet());
            pageResult = iamUserService.pageQueryOrganizationUser(organizationId, pageRequestVO).convert(IamUserSafeVO::new);
            orgUsers.addAll(pageResult.getRecords());
        } while (pageResult.getCurrent() < pageResult.getPages());
        return new ResponseEntity<>(orgUsers);
    }

  //  @Permission(level = ResourceLevel.ORGANIZATION, roles = {InitRoleCode.ORGANIZATION_ADMINISTRATOR})
  @Permission(permissionLogin = true)
    @ApiOperation("给用户授权角色")
    @PutMapping("grant/role")
    public ResponseEntity grantUserRole(@PathVariable("organization_id") Long organizationId, @RequestBody @Valid GrantUserRoleRequestVO vo) {
        memberRoleService.grantUserRole(vo.getUserIds(), CollUtil.newHashSet(vo.getRoleId()), organizationId, ResourceLevel.ORGANIZATION);
        return new ResponseEntity();
    }

}

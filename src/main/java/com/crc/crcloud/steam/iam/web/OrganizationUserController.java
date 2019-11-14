package com.crc.crcloud.steam.iam.web;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.crc.crcloud.steam.iam.common.enums.UserOriginEnum;
import com.crc.crcloud.steam.iam.common.utils.ResponseEntity;
import com.crc.crcloud.steam.iam.model.dto.IamRoleDTO;
import com.crc.crcloud.steam.iam.model.dto.IamUserDTO;
import com.crc.crcloud.steam.iam.model.vo.IamUserVO;
import com.crc.crcloud.steam.iam.model.vo.user.IamOrganizationUserPageRequestVO;
import com.crc.crcloud.steam.iam.model.vo.user.IamOrganizationUserPageResponseVO;
import com.crc.crcloud.steam.iam.model.vo.user.IamUserCreateRequestVO;
import com.crc.crcloud.steam.iam.model.vo.user.IamUserSafeVO;
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
import java.util.function.Function;

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

    @Permission(level = ResourceLevel.ORGANIZATION, roles = {InitRoleCode.ORGANIZATION_ADMINISTRATOR, InitRoleCode.ORGANIZATION_MEMBER})
    @ApiOperation(value = "组织成员列表", notes = "分页", response = IamOrganizationUserPageResponseVO.class)
    @PostMapping("page")
    public ResponseEntity<IPage<IamOrganizationUserPageResponseVO>> page(@PathVariable("organization_id") Long organizationId
            , @RequestBody @Valid IamOrganizationUserPageRequestVO vo) {
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
            responseVO.setRoleName(CollUtil.join(roles, ","));
            return responseVO;
        };
        return new ResponseEntity<>(pageResult.convert(convert));
    }

    @Permission(level = ResourceLevel.ORGANIZATION, roles = {InitRoleCode.ORGANIZATION_ADMINISTRATOR})
    @ApiOperation(value = "手动创建组织成员")
    @PostMapping
    public ResponseEntity<IamUserSafeVO> createUser(@PathVariable("organization_id") Long organizationId,
                                                    @RequestBody @Valid IamUserCreateRequestVO vo) {
        IamUserDTO userDTO = iamUserService.createUserByManual(vo, CollUtil.newHashSet(organizationId));
        return new ResponseEntity<>(new IamUserSafeVO(userDTO));
    }

}

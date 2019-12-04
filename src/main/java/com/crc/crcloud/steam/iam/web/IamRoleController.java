package com.crc.crcloud.steam.iam.web;


import com.crc.crcloud.steam.iam.common.utils.ResponseEntity;
import com.crc.crcloud.steam.iam.model.dto.IamRoleDTO;
import com.crc.crcloud.steam.iam.model.vo.IamRoleVO;
import com.crc.crcloud.steam.iam.service.IamRoleService;
import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import java.util.List;


/**
 * @Author:
 * @Date: 2019-11-12
 * @Description:
 */
@Api("")
@Validated
@RestController
@RequestMapping(value = "/v1/roles")
public class IamRoleController {

    @Autowired
    private IamRoleService iamRoleService;

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("获取组织角色列表")
    @GetMapping("organization")
    public ResponseEntity<List<IamRoleVO>> getOrganizationRoles() {
        @NotNull List<IamRoleDTO> rolesByOrganization = iamRoleService.getRolesByOrganization();
        return new ResponseEntity<>(ConvertHelper.convertList(rolesByOrganization, IamRoleVO.class));
    }

}

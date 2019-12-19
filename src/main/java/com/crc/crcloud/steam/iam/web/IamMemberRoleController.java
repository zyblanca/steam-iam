package com.crc.crcloud.steam.iam.web;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crc.crcloud.steam.iam.common.utils.PageUtil;
import com.crc.crcloud.steam.iam.common.utils.ResponseEntity;
import com.crc.crcloud.steam.iam.model.dto.iam.RoleAssignmentSearchDTO;
import com.crc.crcloud.steam.iam.model.dto.iam.UserWithRoleDTO;
import com.crc.crcloud.steam.iam.service.IamMemberRoleService;
import com.crc.crcloud.steam.iam.service.IamUserService;
import io.choerodon.core.iam.InitRoleCode;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;


/**
 * @Author:
 * @Date: 2019-11-12
 * @Description:
 */
@Api("")
@RestController
@RequestMapping(value = "/v1")
public class IamMemberRoleController {

    @Autowired
    private IamMemberRoleService iamMemberRoleService;

    @Autowired
    private IamUserService iamUserService;

    /**
     * 老行云迁移过来,使用老行云的方式
     * 去掉param参数，太浪费性能，作用又不大
     * 用户包含拥有的project层的角色
     *
     * @param pageUtil                分页请求参数，解析url里的param生成
     * @param sourceId                源id，即项目id
     * @param roleAssignmentSearchDTO 查询请求体，无查询条件需要传{}
     * @param doPage                  做不做分页，如果为false，返回一个page对象，context里为所有数据，没有做分页处理
     * @return
     */
    @Permission(level = ResourceLevel.PROJECT, roles = InitRoleCode.PROJECT_OWNER)
    @ApiOperation(value = "项目层查询用户列表以及该用户拥有的角色")
    @PostMapping(value = "/projects/{project_id}/role_members/users/roles")
    public ResponseEntity<IPage<UserWithRoleDTO>> pagingQueryUsersWithProjectLevelRoles(
            @ApiIgnore
                    PageUtil pageUtil,
            @PathVariable(name = "project_id") Long sourceId,
            @RequestBody(required = false) @Valid RoleAssignmentSearchDTO roleAssignmentSearchDTO,
            @RequestParam(defaultValue = "true") boolean doPage) {
        return new ResponseEntity<>(iamUserService.pagingQueryUsersWithProjectLevelRoles(
                pageUtil, roleAssignmentSearchDTO, sourceId, doPage));
    }

    /**
     * 在site层查询用户，用户包含拥有的organization层的角色
     *
     * @param roleAssignmentSearchDTO 搜索条件
     */
    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "组织层查询用户列表以及该用户拥有的角色")
    @PostMapping(value = "/organizations/{organization_id}/role_members/users/roles")
    public ResponseEntity<IPage<UserWithRoleDTO>> pagingQueryUsersWithOrganizationLevelRoles(
            @PathVariable(name = "organization_id") Long sourceId,
            @ApiIgnore PageUtil pageUtil,
            @RequestBody(required = false) @Valid RoleAssignmentSearchDTO roleAssignmentSearchDTO) {
        return new ResponseEntity<>(iamUserService.pagingQueryUsersWithOrganizationLevelRoles(
                pageUtil, roleAssignmentSearchDTO, sourceId));
    }
}

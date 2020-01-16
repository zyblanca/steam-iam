package com.crc.crcloud.steam.iam.web;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.crc.crcloud.steam.iam.common.utils.CopyUtil;
import com.crc.crcloud.steam.iam.common.utils.PageUtil;
import com.crc.crcloud.steam.iam.common.utils.ResponseEntity;
import com.crc.crcloud.steam.iam.entity.IamUser;
import com.crc.crcloud.steam.iam.model.dto.IamUserDTO;
import com.crc.crcloud.steam.iam.model.dto.iam.RoleAssignmentSearchDTO;
import com.crc.crcloud.steam.iam.model.dto.iam.UserWithRoleDTO;
import com.crc.crcloud.steam.iam.model.vo.IamUserVO;
import com.crc.crcloud.steam.iam.service.IamMemberRoleService;
import com.crc.crcloud.steam.iam.service.IamUserService;
import io.choerodon.core.iam.InitRoleCode;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.util.List;


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


    /**
     * 项目绑定用户
     * 给用户授权/重新授权项目权限
     *
     * @param projectId 项目id
     * @param iamUserVO 用户信息
     * @return 绑定结果
     */
    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_OWNER})
    @ApiOperation(value = "项目绑定用户", notes = "项目绑定用户")
    @PostMapping("/projects/{project_id}/iam_user/bind/users")
    public ResponseEntity projectBindUsers(@ApiParam(value = "项目ID", required = true)
                                           @PathVariable(name = "project_id") Long projectId,
                                           @RequestBody IamUserVO iamUserVO) {

        iamUserService.projectBindUsers(projectId, iamUserVO);
        return ResponseEntity.ok();
    }

    /**
     * 回收用户的项目权限
     * 单个用户收回项目权限
     * note： 当前接口不支持批量回收
     *
     * @param projectId 项目id
     * @param userId    人员id
     * @return 状态
     */
    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_OWNER})
    @ApiOperation(value = "项目删除用户权限", notes = "项目删除用户权限")
    @Delete("/projects/{project_id}/iam_user/unbind/users/{user_id}")
    public ResponseEntity projectDeleteUser(@ApiParam(value = "项目ID", required = true)
                                            @PathVariable(name = "project_id") Long projectId,
                                            @ApiParam(value = "人员id", required = true)
                                            @PathVariable(name = "user_id") Long userId) {
        iamUserService.projectUnbindUser(projectId, userId);
        return ResponseEntity.ok();
    }

    /**
     * 获取指定用户的项目级别的权限，仅限普通用户，客户端用户不可使用
     *
     * @param projectId 项目id
     * @param userId    用户id
     * @return 用户角色id
     */
    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_OWNER})
    @ApiOperation(value = "获取指定用户项目级别的权限信息", notes = "获取指定用户项目级别的权限信息")
    @GetMapping("/project/{project_id}/iam_user/role/{user_id}")
    public ResponseEntity<IamUserVO> userProjectRole(@ApiParam(value = "项目ID", required = true)
                                                     @PathVariable(name = "project_id") Long projectId,
                                                     @ApiParam(value = "人员id", required = true)
                                                     @PathVariable(name = "user_id") Long userId) {

        IamUserDTO iamUserDTO = iamUserService.queryProjectRole(projectId, userId);
        IamUserVO iamUserVO = new IamUserVO();
        iamUserVO.setId(userId);
        iamUserVO.setRoleIds(iamUserDTO.getRoleIds());
        return new ResponseEntity<>(iamUserVO);
    }


}

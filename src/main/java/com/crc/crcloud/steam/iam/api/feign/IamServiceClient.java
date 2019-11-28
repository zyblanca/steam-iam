package com.crc.crcloud.steam.iam.api.feign;

import com.crc.crcloud.steam.iam.api.feign.callback.IamServiceClientFallback;
import com.crc.crcloud.steam.iam.entity.IamUser;
import com.crc.crcloud.steam.iam.entity.OauthLdapErrorUser;
import com.crc.crcloud.steam.iam.model.feign.role.MemberRoleDTO;
import com.crc.crcloud.steam.iam.model.feign.role.RoleDTO;
import com.crc.crcloud.steam.iam.model.feign.user.UserDTO;
import io.choerodon.core.iam.InitRoleCode;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.core.validator.ValidList;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.ApiOperation;
import org.springframework.LdapDataEntry;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * @Author: tankang3
 * @Date: 2019-06-25
 * @Description: 查询用户信息
 */
@FeignClient(value = "iam-service", fallback = IamServiceClientFallback.class)
public interface IamServiceClient {

    @ApiOperation(value = "创建用户")
    @PostMapping("/v1/organizations/{organization_id}/users")
    ResponseEntity<UserDTO> create(@PathVariable(name = "organization_id") Long organizationId,
                                   @RequestBody @Validated UserDTO userDTO);

    /**
     * 根据角色code查询角色
     *
     * @return 查询结果
     */
    @ApiOperation(value = "通过code查询角色")
    @GetMapping("/v1/roles")
    ResponseEntity<RoleDTO> queryByCode(@RequestParam("code") String code);

    @Permission(level = ResourceLevel.SITE)
    @ApiOperation(value = "全局层批量分配给用户/客户端角色")
    @PostMapping(value = "/v1/site/role_members")
    ResponseEntity<List<MemberRoleDTO>> createOrUpdateOnSiteLevel(@RequestParam(value = "is_edit", required = false) Boolean isEdit,
                                                                  @RequestParam(name = "member_type", required = false) String memberType,
                                                                  @RequestParam(name = "member_ids") List<Long> memberIds,
                                                                  @RequestBody ValidList<MemberRoleDTO> memberRoleDTOList);

    /**
     * 在organization层分配角色
     */
    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "组织层批量分配给用户角色/客户端")
    @PostMapping(value = "/v1/organizations/{organization_id}/role_members")
    ResponseEntity<List<MemberRoleDTO>> createOrUpdateOnOrganizationLevel(@RequestParam(value = "is_edit", required = false) Boolean isEdit,
                                                                          @PathVariable(name = "organization_id") Long sourceId,
                                                                          @RequestParam(name = "member_type", required = false) String memberType,
                                                                          @RequestParam(name = "member_ids") List<Long> memberIds,
                                                                          @RequestBody ValidList<MemberRoleDTO> memberRoleDTOList);

    /**
     * 在project层分配角色
     */
    @Permission(level = ResourceLevel.PROJECT, roles = InitRoleCode.PROJECT_OWNER)
    @ApiOperation(value = "项目层批量分配给用户/客户端角色")
    @PostMapping(value = "/v1/projects/{project_id}/role_members")
    ResponseEntity<List<MemberRoleDTO>> createOrUpdateOnProjectLevel(@RequestParam(value = "is_edit", required = false) Boolean isEdit,
                                                                     @PathVariable(name = "project_id") Long sourceId,
                                                                     @RequestParam(name = "member_type", required = false) String memberType,
                                                                     @RequestParam(name = "member_ids") List<Long> memberIds,
                                                                     @RequestBody ValidList<MemberRoleDTO> memberRoleDTOList);

    @Permission(permissionWithin = true)
    @ApiOperation(value = "根据email批量查询用户信息列表")
    @PostMapping(value = "/v1/users/emails")
    ResponseEntity<List<UserDTO>> listUsersByEmails(@RequestBody String[] emails);

    @Permission(level = ResourceLevel.ORGANIZATION, permissionLogin = true)
    @ApiOperation(value = "根据用户名查询用户信息")
    @GetMapping(value = "/v1/users")
    ResponseEntity<UserDTO> queryByLoginName(@RequestParam(name = "login_name") String loginName);

    //同步steam中ldap新增用户
    @PostMapping("/organizations/{organization_id}/ldaps/sync_steam_users")
    ResponseEntity<List<OauthLdapErrorUser>> syncSteamUser(@PathVariable("organization_id") Long organizationId,
                                                     @RequestBody List<IamUser> users);
}

package com.crc.crcloud.steam.iam.web;


import com.crc.crcloud.steam.iam.common.exception.IamAppCommException;
import com.crc.crcloud.steam.iam.common.utils.ResponseEntity;
import com.crc.crcloud.steam.iam.model.dto.LdapConnectionDTO;
import com.crc.crcloud.steam.iam.model.vo.OauthLdapVO;
import com.crc.crcloud.steam.iam.service.OauthLdapService;
import io.choerodon.core.iam.InitRoleCode;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;


/**
 * @Author:
 * @Date: 2019-11-12
 * @Description:
 */
@Api("LDAP服务")
@RestController
@RequestMapping(value = "/v1")
public class OauthLdapController {

    @Autowired
    private OauthLdapService oauthLdapService;

    /**
     * 新增ldap配置
     *
     * @param organizationId 组织id
     * @param oauthLdap      ldap配置
     * @return ldap配置
     */
  //  @Permission(level = ResourceLevel.ORGANIZATION)
    @Permission(permissionLogin = true)
    @ApiOperation(value = "创建Ldap")
    @PostMapping(value = "/organizations/{organization_id}/ldaps")
    public ResponseEntity<OauthLdapVO> insert(@ApiParam(value = "组织id", required = true)
                                              @PathVariable(name = "organization_id") Long organizationId,
                                              @ApiParam(value = "ldap配置", required = true)
                                              @RequestBody @Validated OauthLdapVO oauthLdap) {
        oauthLdap.setOrganizationId(organizationId);
        return Optional.ofNullable(oauthLdapService.insert(oauthLdap)).map(ResponseEntity::new)
                .orElseThrow(() -> new IamAppCommException("common.insert.error"));
    }

    /**
     * 修改状态/启用 禁用
     *
     * @param organizationId 组织id
     * @return ldap配置
     */
   // @Permission(level = ResourceLevel.ORGANIZATION)
    @Permission(permissionLogin = true)
    @ApiOperation(value = "启用/禁用,", notes = "启用/禁用isEnabled 0:禁用，1：启用")
    @PutMapping("/organizations/{organization_id}/status")
    public ResponseEntity<OauthLdapVO> delete(@ApiParam(value = "项目ID", required = true)
                                              @PathVariable(name = "organization_id") Long organizationId,
                                              @ApiParam(value = "ldap配置", required = true)
                                              @RequestBody OauthLdapVO oauthLdap) {
        oauthLdap.setOrganizationId(organizationId);

        return new ResponseEntity<>(oauthLdapService.changeStatus(oauthLdap));
    }

    /**
     * 修改ldap配置
     *
     * @param organizationId 组织id
     * @param oauthLdap      ldap配置
     * @return ldap配置
     */
   // @Permission(level = ResourceLevel.ORGANIZATION)
    @Permission(permissionLogin = true)
    @ApiOperation(value = "修改", notes = "修改", response = OauthLdapVO.class)
    @PutMapping(value = "/organizations/{organization_id}/ldaps")
    public ResponseEntity<OauthLdapVO> update(@ApiParam(value = "项目ID", required = true)
                                              @PathVariable(name = "organization_id") Long organizationId,
                                              @ApiParam(value = "ldap配置", required = true)
                                              @RequestBody @Validated OauthLdapVO oauthLdap) {
        oauthLdap.setOrganizationId(organizationId);
        return Optional.ofNullable(oauthLdapService.update(oauthLdap))
                .map(ResponseEntity::new)
                .orElseThrow(() -> new IamAppCommException("common.update.error"));
    }

//    /**
//     * ldap详情
//     *
//     * @param organizationId 组织id
//     * @param id             ldap id
//     * @return ldap配置信息
//     */
//    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
//    @ApiOperation(value = "查询单个信息", notes = "查询单个详情", response = OauthLdapVO.class)
//    @GetMapping("/organizations/{organization_id}/ldaps/{id}")
//    public ResponseEntity<OauthLdapVO> load(@ApiParam(value = "组织id", required = true)
//                                            @PathVariable(name = "organization_id") Long organizationId,
//                                            @ApiParam(value = "ldap id", required = true)
//                                            @PathVariable(name = "id") Long id) {
//        return Optional.ofNullable(oauthLdapService.queryOne(organizationId, id)).map(ResponseEntity::new)
//                .orElseThrow(() -> new IamAppCommException("common.data.null.error"));
//    }

    /**
     * ldap详情
     * 通过组织查询ldap
     *
     * @param organizationId 组织id
     * @return ldap配置信息
     */
   // @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @Permission(permissionLogin = true)
    @ApiOperation(value = "通过组织查询ldap", notes = "通过组织查询ldap", response = OauthLdapVO.class)
    @GetMapping("/organizations/{organization_id}")
    public ResponseEntity<OauthLdapVO> loadByOrg(@ApiParam(value = "组织id", required = true)
                                                 @PathVariable(name = "organization_id") Long organizationId) {
        return new ResponseEntity<>(oauthLdapService.queryOneByOrganizationId(organizationId));
    }


    /**
     * 测试ldap连接
     *
     * @return 是否连接成功
     */
    //@Permission(level = ResourceLevel.ORGANIZATION)
    @Permission(permissionLogin = true)
    @ApiOperation(value = "测试ldap连接", response = LdapConnectionDTO.class)
    @PostMapping("/organizations/{organization_id}/ldaps/{id}/test_connect")
    public ResponseEntity<LdapConnectionDTO> testConnect(@ApiParam(value = "组织id", required = true)
                                                         @PathVariable("organization_id") Long organizationId,
                                                         @ApiParam(value = "ldap id", required = true)
                                                         @PathVariable("id") Long id,
                                                         @RequestBody OauthLdapVO oauthLdapVO) {
        oauthLdapVO.setOrganizationId(organizationId);
        oauthLdapVO.setId(id);
        return new ResponseEntity<>(oauthLdapService.testConnetion(oauthLdapVO));
    }

    /**
     * 同步ldap用户
     */
   // @Permission(level = ResourceLevel.ORGANIZATION)
    @Permission(permissionLogin = true)
    @ApiOperation(value = "同步ldap用户")
    @PostMapping("/organizations/{organization_id}/ldaps/{id}/sync_users")
    public ResponseEntity<Long> syncUsers(@ApiParam(value = "组织id", required = true)
                                          @PathVariable("organization_id") Long organizationId,
                                          @ApiParam(value = "ldap id", required = true)
                                          @PathVariable Long id) {

        return new ResponseEntity<>(oauthLdapService.syncLdapUser(organizationId, id));
    }
}

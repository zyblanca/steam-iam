package com.crc.crcloud.steam.iam.web;


import com.crc.crcloud.steam.iam.common.exception.IamAppCommException;
import com.crc.crcloud.steam.iam.common.utils.ResponseEntity;
import com.crc.crcloud.steam.iam.model.vo.OauthLdapHistoryVO;
import com.crc.crcloud.steam.iam.service.OauthLdapHistoryService;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;


/**
 * @Author:
 * @Date: 2019-11-12
 * @Description:
 */
@Api("")
@RestController
@RequestMapping(value = "/v1/oauth_ldap_history")
public class OauthLdapHistoryController {

    @Autowired
    private OauthLdapHistoryService oauthLdapHistoryService;


    /**
     * 详情
     *
     * @param id
     * @return
     */
    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "查询单个信息", notes = "查询单个详情", response = OauthLdapHistoryVO.class)
    @GetMapping("organizations/{organization_id}/{id}")
    public ResponseEntity<OauthLdapHistoryVO> load(@ApiParam(value = "组织ID", required = true)
                                                   @PathVariable(name = "organization_id") Long organizationId,
                                                   @ApiParam(value = "", required = true)
                                                   @PathVariable(name = "id") Long id) {
        return Optional.ofNullable(oauthLdapHistoryService.queryOne(organizationId, id)).map(ResponseEntity::new)
                .orElseThrow(() -> new IamAppCommException("common.data.null.error"));
    }

}

package com.crc.crcloud.steam.iam.web;



import com.baomidou.mybatisplus.core.metadata.IPage;
import com.crc.crcloud.steam.iam.common.exception.IamAppCommException;
import com.crc.crcloud.steam.iam.common.utils.PageUtil;
import com.crc.crcloud.steam.iam.common.utils.ResponseEntity;
import com.crc.crcloud.steam.iam.model.vo.OauthLdapVO;
import com.crc.crcloud.steam.iam.service.OauthLdapService;
import io.choerodon.core.iam.InitRoleCode;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;


/**
* @Author:
* @Date: 2019-11-12
* @Description: 
*/
@Api("")
@RestController
@RequestMapping(value = "/v1/projects/{project_id}/oauth_ldap")
public class OauthLdapController {

    @Autowired
    private OauthLdapService oauthLdapService;

    /**
    * 新增
    *
    * @param projectId 项目ID
    * @param oauthLdap
    * @return
    */
    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation(value = "新增", notes = "新增", response = OauthLdapVO.class)
    @PostMapping
    public ResponseEntity<OauthLdapVO> insert(@ApiParam(value = "项目ID", required = true)
                                                            @PathVariable(name = "project_id") Long projectId,
                                                            @ApiParam(value = "", required = true)
                                                            @RequestBody OauthLdapVO oauthLdap){
        return Optional.ofNullable(oauthLdapService.insert(projectId,oauthLdap)).map(ResponseEntity::new)
        .orElseThrow(() -> new IamAppCommException("common.insert.error"));
    }

    /**
    * 删除
    * @param projectId 项目ID
    * @param id
    * @return REST状态
    */
    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation(value="删除",notes="删除")
    @DeleteMapping("/{id}")
    public ResponseEntity delete(
                                @ApiParam(value = "项目ID", required = true)
                                @PathVariable(name = "project_id") Long projectId,
                                @ApiParam(value = "", required = true)
                                @PathVariable(name = "id") Long id){

        oauthLdapService.delete(projectId,id);
        return ResponseEntity.ok();
    }

    /**
    * 修改
    *
    * @param projectId 项目ID
    * @param oauthLdap
    * @return
    */
    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation(value="修改",notes="修改",response = OauthLdapVO.class)
    @PutMapping
    public ResponseEntity<OauthLdapVO> update(@ApiParam(value = "项目ID", required = true)
                                                            @PathVariable(name = "project_id") Long projectId,
                                                            @ApiParam(value = "", required = true)
                                                            @RequestBody OauthLdapVO oauthLdap){
        return Optional.ofNullable(oauthLdapService.update(projectId,oauthLdap))
        .map(ResponseEntity::new)
        .orElseThrow(() -> new IamAppCommException("common.update.error"));
    }

    /**
    * 详情
    *
    * @param id
    * @return
    */
    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation(value="查询单个信息",notes = "查询单个详情",response = OauthLdapVO.class)
    @GetMapping("{id}")
    public ResponseEntity<OauthLdapVO> load(@ApiParam(value = "项目ID", required = true)
                                                          @PathVariable(name = "project_id") Long projectId,
                                                          @ApiParam(value = "", required = true)
                                                          @PathVariable(name = "id") Long id){
        return Optional.ofNullable(oauthLdapService.queryOne(projectId,id)).map(ResponseEntity::new)
            .orElseThrow(() -> new IamAppCommException("common.data.null.error"));
    }
    /**
    * 分页查询
    *
    * @param projectId      项目ID
    * @param oauthLdap
    * @param page           分页信息
    * @return
    */
    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation(value = "列表",notes="列表", response = OauthLdapVO.class)
    @GetMapping
    public ResponseEntity<IPage<OauthLdapVO>> page(@ApiParam(value = "项目ID", required = true)
                                                                 @PathVariable(name = "project_id") Long projectId,
                                                                 OauthLdapVO oauthLdap,
                                                                 PageUtil page) {

        return Optional.ofNullable(oauthLdapService.queryPage(oauthLdap, projectId, page)).map(ResponseEntity::new)
                    .orElseThrow(() -> new IamAppCommException("common.search.data.error"));
    }


}

package com.crc.crcloud.steam.iam.web;



import com.baomidou.mybatisplus.core.metadata.IPage;
import com.crc.crcloud.steam.iam.common.exception.IamAppCommException;
import com.crc.crcloud.steam.iam.common.utils.PageUtil;
import com.crc.crcloud.steam.iam.common.utils.ResponseEntity;
import com.crc.crcloud.steam.iam.model.vo.IamOrganizationVO;
import com.crc.crcloud.steam.iam.service.IamOrganizationService;
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
@RequestMapping(value = "/v1/projects/{project_id}/iam_organization")
public class IamOrganizationController {

    @Autowired
    private IamOrganizationService iamOrganizationService;

    /**
    * 新增
    *
    * @param projectId 项目ID
    * @param iamOrganization
    * @return
    */
    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation(value = "新增", notes = "新增", response = IamOrganizationVO.class)
    @PostMapping
    public ResponseEntity<IamOrganizationVO> insert(@ApiParam(value = "项目ID", required = true)
                                                            @PathVariable(name = "project_id") Long projectId,
                                                            @ApiParam(value = "", required = true)
                                                            @RequestBody IamOrganizationVO iamOrganization){
        return Optional.ofNullable(iamOrganizationService.insert(projectId,iamOrganization)).map(ResponseEntity::new)
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

        iamOrganizationService.delete(projectId,id);
        return ResponseEntity.ok();
    }

    /**
    * 修改
    *
    * @param projectId 项目ID
    * @param iamOrganization
    * @return
    */
    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation(value="修改",notes="修改",response = IamOrganizationVO.class)
    @PutMapping
    public ResponseEntity<IamOrganizationVO> update(@ApiParam(value = "项目ID", required = true)
                                                            @PathVariable(name = "project_id") Long projectId,
                                                            @ApiParam(value = "", required = true)
                                                            @RequestBody IamOrganizationVO iamOrganization){
        return Optional.ofNullable(iamOrganizationService.update(projectId,iamOrganization))
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
    @ApiOperation(value="查询单个信息",notes = "查询单个详情",response = IamOrganizationVO.class)
    @GetMapping("{id}")
    public ResponseEntity<IamOrganizationVO> load(@ApiParam(value = "项目ID", required = true)
                                                          @PathVariable(name = "project_id") Long projectId,
                                                          @ApiParam(value = "", required = true)
                                                          @PathVariable(name = "id") Long id){
        return Optional.ofNullable(iamOrganizationService.queryOne(projectId,id)).map(ResponseEntity::new)
            .orElseThrow(() -> new IamAppCommException("common.data.null.error"));
    }
    /**
    * 分页查询
    *
    * @param projectId      项目ID
    * @param iamOrganization
    * @param page           分页信息
    * @return
    */
    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation(value = "列表",notes="列表", response = IamOrganizationVO.class)
    @GetMapping
    public ResponseEntity<IPage<IamOrganizationVO>> page(@ApiParam(value = "项目ID", required = true)
                                                                 @PathVariable(name = "project_id") Long projectId,
                                                                 IamOrganizationVO iamOrganization,
                                                                 PageUtil page) {

        return Optional.ofNullable(iamOrganizationService.queryPage(iamOrganization, projectId, page)).map(ResponseEntity::new)
                    .orElseThrow(() -> new IamAppCommException("common.search.data.error"));
    }


}

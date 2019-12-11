package com.crc.crcloud.steam.iam.web;

import com.crc.crcloud.steam.iam.common.utils.ResponseEntity;
import com.crc.crcloud.steam.iam.entity.IamApplication;
import com.crc.crcloud.steam.iam.model.vo.IamApplicationVO;
import com.crc.crcloud.steam.iam.service.IamApplicationService;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "/v1/organizations/{organization_id}/applications")
public class IamApplicationController {

    @Autowired
    private IamApplicationService iamApplicationService;


    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("创建应用")
    @PostMapping
    public ResponseEntity<IamApplicationVO> createApplication(@PathVariable("organization_id") Long organizationId,
                                                              @RequestBody @Valid IamApplicationVO iamApplicationVO){

        return null;
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(("更新应用"))
    @PostMapping("/{id}")
    public ResponseEntity<IamApplicationVO> updateApplication(@PathVariable("organization_id") Long organizationId,
                                                              @PathVariable("id") Long applicationId,
                                                              @RequestBody @Valid IamApplicationVO iamApplicationVO){

        return null;
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("启动应用")
    @PutMapping("/{id}/enable")
    public ResponseEntity<IamApplicationVO> enableApplication(@PathVariable("id") Long applicationId){

        return null;
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("禁用应用")
    @PutMapping("/{id}/disable")
    public ResponseEntity<IamApplicationVO> disableApplication(@PathVariable("id") Long applicationId){

        return null;
    }
}

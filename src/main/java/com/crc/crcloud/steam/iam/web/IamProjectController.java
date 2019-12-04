package com.crc.crcloud.steam.iam.web;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.crc.crcloud.steam.iam.common.exception.IamAppCommException;
import com.crc.crcloud.steam.iam.common.utils.PageUtil;
import com.crc.crcloud.steam.iam.common.utils.ResponseEntity;
import com.crc.crcloud.steam.iam.model.vo.IamProjectVO;
import com.crc.crcloud.steam.iam.service.IamProjectService;
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
@RequestMapping(value = "/v1/projects")
public class IamProjectController {

    @Autowired
    private IamProjectService iamProjectService;

    /**
     * 新增项目
     * 项目不同步到老行云
     * 项目发起saga事件
     *
     * @param organizationId 组织id
     * @param iamProject
     * @return
     */
    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "新增", notes = "新增", response = IamProjectVO.class)
    @PostMapping("/add_project/{organization_id}")
    public ResponseEntity<IamProjectVO> insert(@ApiParam(value = "组织id", required = true)
                                               @PathVariable(name = "organization_id") Long organizationId,
                                               @RequestBody IamProjectVO iamProject) {

        return new ResponseEntity<>(iamProjectService.insert(organizationId, iamProject));
    }

}

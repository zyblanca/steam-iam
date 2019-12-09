package com.crc.crcloud.steam.iam.web;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.crc.crcloud.steam.iam.common.utils.PageUtil;
import com.crc.crcloud.steam.iam.common.utils.ResponseEntity;
import com.crc.crcloud.steam.iam.model.vo.IamProjectVO;
import com.crc.crcloud.steam.iam.service.IamProjectService;
import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@RequestMapping(value = "/v1/organizations/{organization_id}/projects")
public class OrganizationProjectController {

    private final IamProjectService iamProjectService;

    public OrganizationProjectController(IamProjectService iamProjectService) {
        this.iamProjectService = iamProjectService;
    }

    /**
     * 分页查询项目
     *
     * @param pageUtil 分页请求参数封装对象
     * @return 查询结果
     */
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping
    @ApiOperation(value = "分页查询项目")
    public ResponseEntity<IPage<IamProjectVO>> list(@PathVariable(name = "organization_id") Long organizationId,
                                                    @ApiIgnore
                                                           PageUtil pageUtil,
                                                    @RequestParam(required = false) String name,
                                                    @RequestParam(required = false) String code,
                                                    @RequestParam(required = false) Boolean enabled,
                                                    @RequestParam(required = false) String category) {
        IamProjectVO project = new IamProjectVO();
        project.setOrganizationId(organizationId);
        project.setName(name);
        project.setCode(code);
        project.setIsEnabled(enabled);
        project.setCategory(category);
        return new ResponseEntity<>(iamProjectService.pagingQuery(project, pageUtil));
    }

}

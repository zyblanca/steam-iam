package com.crc.crcloud.steam.iam.web;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crc.crcloud.steam.iam.common.utils.PageUtil;
import com.crc.crcloud.steam.iam.common.utils.ResponseEntity;
import com.crc.crcloud.steam.iam.common.utils.UserDetail;
import com.crc.crcloud.steam.iam.model.dto.IamProjectDTO;
import com.crc.crcloud.steam.iam.model.dto.IamUserDTO;
import com.crc.crcloud.steam.iam.model.vo.IamProjectVO;
import com.crc.crcloud.steam.iam.model.vo.project.IamUserProjectRequestVO;
import com.crc.crcloud.steam.iam.model.vo.project.IamUserProjectResponseVO;
import com.crc.crcloud.steam.iam.service.IamProjectService;
import com.crc.crcloud.steam.iam.service.IamUserService;
import io.choerodon.core.iam.InitRoleCode;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;


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
    @Autowired
    private IamUserService iamUserService;

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
                                               @RequestBody @Validated IamProjectVO iamProject) {

        return new ResponseEntity<>(iamProjectService.insert(organizationId, iamProject));
    }

    /**
     * 修改项目信息
     * 项目code和组织,可用标志不可修改
     * 启用禁用使用额外的接口
     * note：原始行云有两套项目管理
     * 一套为组织级别管理项目 OrganizationProjectController
     * 一套为项目自身管理     ProjectController
     * 现对接 OrganizationProjectController
     */
    @Permission(level = ResourceLevel.PROJECT, roles = InitRoleCode.PROJECT_OWNER)
    @ApiOperation(value = "修改项目")
    @PutMapping(value = "/{project_id}")
    public ResponseEntity<IamProjectVO> update(@PathVariable(name = "project_id") Long id,
                                               @RequestBody @Validated IamProjectVO iamProjectVO) {
        iamProjectVO.setId(id);

        return new ResponseEntity<>(iamProjectService.update(iamProjectVO));
    }

    /**
     * 按照Id查询项目
     * 只有单纯的项目信息，不是详情数据
     * 老行云迁移过来
     *
     * @param id 要查询的项目ID
     * @return 查询到的项目
     */
    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_OWNER})
    @GetMapping(value = "/{project_id}")
    @ApiOperation(value = "通过id查询项目(只是单表数据，不是详情)")
    public ResponseEntity<IamProjectVO> query(@PathVariable(name = "project_id") Long id) {
        return new ResponseEntity<>(iamProjectService.queryProjectById(id));
    }


    /**
     * 查询当前用户所在组织的已授权项目
     * <p>切换完组织之后的项目列表</p>
     */
    @Permission(permissionLogin = true)
    @ApiOperation(value = "查询当前用户授权的项目")
    @PostMapping()
    public ResponseEntity<IPage<IamUserProjectResponseVO>> queryAllProject(
            PageUtil pageUtil, IamUserProjectRequestVO vo) {
        final IamUserDTO iamUser = iamUserService.getAndThrow(UserDetail.getUserId());
        if (Objects.isNull(vo.getOrganizationId())) {
            vo.setOrganizationId(iamUser.getCurrentOrganizationId());
        }
        final Page page = new Page(pageUtil.getCurrent(), pageUtil.getSize(), pageUtil.getTotal(), pageUtil.isSearchCount());
        page.setAsc(pageUtil.ascs());
        page.setDesc(pageUtil.descs());
        PageUtil.sortFieldConvertToUnderlineCase(page);
        IPage<IamProjectDTO> userProjects = iamProjectService.getUserProjects(page, iamUser.getId(), vo.getOrganizationId(), vo.getName());
        Set<Long> userIds = userProjects.getRecords().stream().map(IamProjectDTO::getCreatedBy).filter(Objects::nonNull).collect(Collectors.toSet());
        Map<Long, String> userMap = iamUserService.getUsers(userIds).stream().collect(Collectors.toMap(IamUserDTO::getId, IamUserDTO::getRealName));
        IPage<IamUserProjectResponseVO> pageResult = userProjects.convert(t -> {
            IamUserProjectResponseVO response = new IamUserProjectResponseVO();
            BeanUtil.copyProperties(t, response, CopyOptions.create().ignoreNullValue());
            response.setRealName(userMap.get(t.getCreatedBy()));
            return response;
        });
        return new ResponseEntity<>(pageResult);
    }

    /**
     * 适用于查询指定项目类型，按照创建时间降序排序
     *
     * @param category 项目类型
     * @return 组织与项目的分组信息
     */
    @Permission(permissionLogin = true, permissionWithin = true)
    @ApiOperation(value = "通过项目类型查询所有的项目id")
    @GetMapping("/category")
    public ResponseEntity<List<IamProjectVO>> queryByCategory(@RequestParam(name = "category") String category) {


        return new ResponseEntity<>(iamProjectService.queryByCategory(category));
    }

}

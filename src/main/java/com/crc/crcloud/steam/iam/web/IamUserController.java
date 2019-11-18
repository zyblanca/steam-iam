package com.crc.crcloud.steam.iam.web;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.crc.crcloud.steam.iam.common.utils.PageUtil;
import com.crc.crcloud.steam.iam.common.utils.ResponseEntity;
import com.crc.crcloud.steam.iam.entity.IamUser;
import com.crc.crcloud.steam.iam.model.dto.UserSearchDTO;
import com.crc.crcloud.steam.iam.model.vo.IamUserVO;
import com.crc.crcloud.steam.iam.service.IamUserService;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


/**
 * @Author:
 * @Date: 2019-11-12
 * @Description:
 */
@Api("项目人员管理")
@RestController
@RequestMapping(value = "/v1/projects/{project_id}/iam_user")
public class IamUserController {

    private final IamUserService iamUserService;

    public IamUserController(IamUserService iamUserService) {
        this.iamUserService = iamUserService;
    }

    /**
     * 分页查询指定项目下的成员信息
     *当前只有id loginName realName 三个属性 后续可以根据需要添加
     * @param projectId     项目id
     * @param userSearchDTO 人员查询参数
     * @param page          分页信息
     * @return 人员信息
     */
    //简易权限，后续需要根据实际情况做校验
    @Permission(level = ResourceLevel.PROJECT)
    @ApiOperation(value = "项目人员列表", notes = "项目人员列表", response = IamUserVO.class)
    @GetMapping
    public ResponseEntity<IPage<IamUserVO>> pageProjectUser(@ApiParam(value = "项目ID", required = true)
                                                            @PathVariable(name = "project_id") Long projectId,
                                                            @ApiParam(value = "人员信息")
                                                                    UserSearchDTO userSearchDTO,
                                                            PageUtil page) {


        return new ResponseEntity<>(iamUserService.pageByProject(projectId, userSearchDTO, page));
    }

    /**
     * 项目下的所有人员信息
     * 当前只有id loginName realName 三个属性 后续可以根据需要添加
     *
     * @param projectId     项目id
     * @param userSearchDTO 用户查询条件
     * @return 用户信息
     */
    //简易权限，后续需要根据实际情况做校验
    @Permission(level = ResourceLevel.PROJECT)
    @ApiOperation(value = "项目人员下拉列表", notes = "项目人员下拉列表", response = IamUserVO.class)
    @GetMapping("drop/down")
    public ResponseEntity<List<IamUserVO>> projectDropDownUser(@ApiParam(value = "项目ID", required = true)
                                                               @PathVariable(name = "project_id") Long projectId,
                                                               @ApiParam(value = "人员信息")
                                                                       UserSearchDTO userSearchDTO) {
        return new ResponseEntity<>(iamUserService.projectDropDownUser(projectId, userSearchDTO));
    }

    /**
     * 组织下面未被项目选择的人员下拉
     * 当前只有id loginName realName 三个属性 后续可以根据需要添加
     *
     * @param projectId     项目id
     * @param userSearchDTO 用户查询条件
     * @return 用户信息
     */
    //简易权限，后续需要根据实际情况做校验
    @Permission(level = ResourceLevel.PROJECT)
    @ApiOperation(value = "组织下面未被项目选择的人员下拉", notes = "组织下面未被项目选择的人员下拉", response = IamUserVO.class)
    @GetMapping("/unselect")
    public ResponseEntity<List<IamUserVO>> projectUnselectUser(@ApiParam(value = "项目ID", required = true)
                                                               @PathVariable(name = "project_id") Long projectId,
                                                               @ApiParam(value = "人员信息")
                                                                       UserSearchDTO userSearchDTO) {
        return new ResponseEntity<>(iamUserService.projectUnselectUser(projectId, userSearchDTO));
    }
}

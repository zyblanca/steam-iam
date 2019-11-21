package com.crc.crcloud.steam.iam.web;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.crc.crcloud.steam.iam.common.utils.PageUtil;
import com.crc.crcloud.steam.iam.common.utils.ResponseEntity;
import com.crc.crcloud.steam.iam.model.dto.UserSearchDTO;
import com.crc.crcloud.steam.iam.model.vo.IamUserVO;
import com.crc.crcloud.steam.iam.service.IamUserService;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 用户相关
 *
 * @author hand-196
 */
@Api("")
@RestController
@RequestMapping(value = "/v1")
public class IamUserController {

    private final IamUserService iamUserService;

    public IamUserController(IamUserService iamUserService) {
        this.iamUserService = iamUserService;
    }

    /**
     * 分页查询指定项目下的成员信息
     * 当前只有id loginName realName 三个属性 后续可以根据需要添加
     *
     * @param projectId     项目id
     * @param userSearchDTO 人员查询参数
     * @param page          分页信息
     * @return 人员信息
     */
    //简易权限，后续需要根据实际情况做校验
    @Permission(level = ResourceLevel.PROJECT)
    @ApiOperation(value = "项目人员列表", notes = "项目人员列表", response = IamUserVO.class)
    @GetMapping("/projects/{project_id}/iam_user")
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
    @GetMapping("/projects/{project_id}/iam_user/drop/down")
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
    @GetMapping("/projects/{project_id}/iam_user/unselect")
    public ResponseEntity<List<IamUserVO>> projectUnselectUser(@ApiParam(value = "项目ID", required = true)
                                                               @PathVariable(name = "project_id") Long projectId,
                                                               @ApiParam(value = "人员信息")
                                                                       UserSearchDTO userSearchDTO) {
        return new ResponseEntity<>(iamUserService.projectUnselectUser(projectId, userSearchDTO));
    }

    /**
     * 项目绑定用户
     *
     * @param projectId 项目id
     * @param userIds   用户id
     * @return 绑定结果
     */
    //简易权限，后续需要根据实际情况做校验
    @Permission(level = ResourceLevel.PROJECT)
    @ApiOperation(value = "项目绑定用户", notes = "项目绑定用户")
    @PostMapping("/projects/{project_id}/iam_user/bind/users")
    public ResponseEntity projectBindUsers(@ApiParam(value = "项目ID", required = true)
                                           @PathVariable(name = "project_id") Long projectId,
                                           @ApiParam(value = "人员信息数组id")
                                                   List<Long> userIds) {
        iamUserService.projectBindUsers(projectId, userIds);
        return ResponseEntity.ok();
    }

    /**
     * 内部端口，不对外使用
     * 通过用户id集合，查询用户信息
     * 包含用户id loginName email realName四个属性
     *
     * @param ids         用户id集合
     * @param onlyEnabled 是否排除无效用户
     * @return 用户信息
     */
    @ApiOperation(value = "通过给定的id数组获取用户信息")
    @PostMapping("/users/ids")
    @Permission(permissionWithin = true)
    public ResponseEntity<List<IamUserVO>> listUserByIds(@RequestBody List<Long> ids,
                                                         @RequestParam(value = "only_enabled", defaultValue = "true", required = false) Boolean onlyEnabled) {
        return new ResponseEntity<>(iamUserService.listUserByIds(ids, onlyEnabled));
    }


}

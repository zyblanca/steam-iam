package com.crc.crcloud.steam.iam.web;


import cn.hutool.cache.Cache;
import cn.hutool.cache.impl.LRUCache;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.crc.crcloud.steam.iam.common.utils.PageUtil;
import com.crc.crcloud.steam.iam.common.utils.ResponseEntity;
import com.crc.crcloud.steam.iam.model.dto.*;
import com.crc.crcloud.steam.iam.model.vo.IamOrganizationVO;
import com.crc.crcloud.steam.iam.model.vo.IamProjectVO;
import com.crc.crcloud.steam.iam.model.vo.IamUserVO;
import com.crc.crcloud.steam.iam.model.vo.organization.IamUserCurrentOrganizationUpdateRequestVO;
import com.crc.crcloud.steam.iam.model.vo.organization.IamUserOrganizationsResponseVO;
import com.crc.crcloud.steam.iam.service.IamMemberRoleService;
import com.crc.crcloud.steam.iam.service.IamOrganizationService;
import com.crc.crcloud.steam.iam.service.IamRoleService;
import com.crc.crcloud.steam.iam.service.IamUserService;
import io.choerodon.core.iam.InitRoleCode;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.stream.Collectors;


/**
 * 用户相关
 *
 * @author hand-196
 */
@Slf4j
@Validated
@Api("")
@RestController
@RequestMapping(value = "/v1")
public class IamUserController {

    private final IamUserService iamUserService;
    @Autowired
    private IamRoleService iamRoleService;
    @Autowired
    private IamMemberRoleService iamMemberRoleService;
    @Autowired
    private IamOrganizationService organizationService;

    public IamUserController(IamUserService iamUserService) {
        this.iamUserService = iamUserService;
    }

    /**
     * 用户是否为对应level管理员，缓存10s
     */
    private final Cache<String, Boolean> isOrganizationAdminCache = new LRUCache<>(1000, DateUnit.SECOND.getMillis() * 10);
    private final BiFunction<ResourceLevel, Object, String> getCacheKey = (level, keyId) -> StrUtil.format("{}#{}", level, keyId.toString());

    /**
     * 分页查询指定项目下的成员信息
     * 当前只有id loginName realName email四个属性 后续可以根据需要添加
     *
     * @param projectId     项目id
     * @param userSearchDTO 人员查询参数
     * @param page          分页信息
     * @return 人员信息
     */
    //简易权限，后续需要根据实际情况做校验
    @Permission(level = ResourceLevel.PROJECT,roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation(value = "项目人员列表", notes = "项目人员列表", response = IamUserVO.class)
    @GetMapping("/projects/{project_id}/users")
    public ResponseEntity<IPage<IamUserVO>> pageProjectUser(@ApiParam(value = "项目ID", required = true)
                                                            @PathVariable(name = "project_id") Long projectId,
                                                             UserSearchDTO userSearchDTO,
                                                            PageUtil page) {


        return new ResponseEntity<>(iamUserService.pageByProject(projectId, userSearchDTO, page));
    }

    /**
     * 查询指定项目下的所有成员信息
     * 当前只有id loginName realName email 四个属性 后续可以根据需要添加
     *
     * @param projectId     项目id
     * @param userSearchDTO 人员查询参数
     * @return 人员信息
     */
    //简易权限，后续需要根据实际情况做校验
    @Permission(level = ResourceLevel.PROJECT,roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation(value = "项目人员列表，无分页", notes = "项目人员列表，无分页", response = IamUserVO.class)
    @GetMapping("/projects/{project_id}/list/users")
    public ResponseEntity<List<IamUserVO>> listProjectUser(@ApiParam(value = "项目ID", required = true)
                                                           @PathVariable(name = "project_id") Long projectId,
                                                           UserSearchDTO userSearchDTO) {


        return new ResponseEntity<>(iamUserService.listByProject(projectId, userSearchDTO));
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
    @Permission(level = ResourceLevel.PROJECT,roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation(value = "项目人员下拉列表", notes = "项目人员下拉列表", response = IamUserVO.class)
    @GetMapping("/projects/{project_id}/iam_user/drop/down")
    public ResponseEntity<List<IamUserVO>> projectDropDownUser(@ApiParam(value = "项目ID", required = true)
                                                               @PathVariable(name = "project_id") Long projectId,
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
    @Permission(level = ResourceLevel.PROJECT,roles = {InitRoleCode.PROJECT_OWNER})
    @ApiOperation(value = "组织下面未被项目选择的人员下拉", notes = "组织下面未被项目选择的人员下拉", response = IamUserVO.class)
    @GetMapping("/projects/{project_id}/iam_user/unselect")
    public ResponseEntity<List<IamUserVO>> projectUnselectUser(@ApiParam(value = "项目ID", required = true)
                                                               @PathVariable(name = "project_id") Long projectId,
                                                               UserSearchDTO userSearchDTO) {
        return new ResponseEntity<>(iamUserService.projectUnselectUser(projectId, userSearchDTO));
    }


    /**
     * 项目绑定用户
     *
     * @param projectId 项目id
     * @param iamUserVO 用户信息
     * @return 绑定结果
     */
    //简易权限，后续需要根据实际情况做校验
    @Permission(level = ResourceLevel.PROJECT,roles = { InitRoleCode.PROJECT_OWNER})
    @ApiOperation(value = "项目绑定用户", notes = "项目绑定用户")
    @PostMapping("/projects/{project_id}/iam_user/bind/users")
    public ResponseEntity projectBindUsers(@ApiParam(value = "项目ID", required = true)
                                           @PathVariable(name = "project_id") Long projectId,
                                           @RequestBody IamUserVO iamUserVO) {

        iamUserService.projectBindUsers(projectId, iamUserVO);
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

    @Permission(level = ResourceLevel.SITE, permissionLogin = true)
    @ApiOperation(value = "查询当前用户信息")
    @GetMapping(value = "/users/self")
    public ResponseEntity<IamUserVO> querySelf() {
        return new ResponseEntity<>(iamUserService.querySelf());
    }


    @Permission(level = ResourceLevel.SITE, permissionLogin = true)
    @ApiOperation("查询用户是否为平台管理员")
    @GetMapping("users/site/admin")
    public ResponseEntity<Boolean> isSiteAdmin(@RequestParam(value = "user_id", required = false) Long userId) {
        /*
         *为了减少使用方处理异常场景,对参数异常采用返回False参数
         */
        final AtomicBoolean isAdmin = new AtomicBoolean(false);
        Predicate<Long> validatedId = Objects::nonNull;
        //不为空并且大于0
        validatedId = validatedId.and(id -> NumberUtil.isGreater(BigDecimal.valueOf(id), BigDecimal.ZERO));
        if (validatedId.test(userId)) {
            final String cacheKey = getCacheKey.apply(ResourceLevel.SITE, userId.toString());
            Boolean admin = isOrganizationAdminCache.get(cacheKey, () -> {
                try {
                    IamUserDTO iamUser = iamUserService.getAndThrow(userId);
                    @NotNull List<IamRoleDTO> roles = iamRoleService.getUserRoles(userId, ResourceLevel.SITE);
                    return Optional.ofNullable(iamUser.getIsAdmin()).orElse(Boolean.FALSE) || CollUtil.newArrayList(roles).stream().anyMatch(role -> Objects.equals(role.getCode(), InitRoleCode.SITE_ADMINISTRATOR));
                } catch (Exception ex) {
                    log.error(" getUserRoles({},{}) error: {}", userId, ResourceLevel.SITE, ex.getMessage(), ex);
                }
                return false;
            });
            isAdmin.set(Optional.ofNullable(admin).orElse(Boolean.FALSE));
        }
        return new ResponseEntity<>(isAdmin.get());
    }

    /**
     * 根据用户查询所有组织并且用户角色为组织管理员
     *
     * @param userId 用户编号
     * @return Boolean
     */
    @Permission(level = ResourceLevel.ORGANIZATION, permissionLogin = true)
    @ApiOperation(value = "查询用户在该组织是否为组织管理员")
    @GetMapping("users/organization/admin")
    public ResponseEntity<Boolean> isOrganizationAdmin(
            @RequestParam(value = "user_id", required = false) Long userId
            , @ApiParam("组织编号") @RequestParam(value = "organization_id", required = false) Long organizationId) {
        /*
         *为了减少使用方处理异常场景,对参数异常采用返回False参数
         */
        final AtomicBoolean isAdmin = new AtomicBoolean(false);
        Predicate<Long> validatedId = Objects::nonNull;
        //不为空并且大于0
        validatedId = validatedId.and(id -> NumberUtil.isGreater(BigDecimal.valueOf(id), BigDecimal.ZERO));
        if (validatedId.test(userId) && validatedId.test(organizationId)) {
            final String cacheKey = getCacheKey.apply(ResourceLevel.ORGANIZATION, userId + "#" + organizationId);
            Boolean admin = isOrganizationAdminCache.get(cacheKey, () -> {
                try {
                    @NotNull List<IamMemberRoleDTO> userMemberRoleByOrganization = iamMemberRoleService.getUserMemberRoleByOrganization(userId, CollUtil.newHashSet(organizationId));
                    @NotNull List<IamRoleDTO> roles = iamRoleService.getRoles(userMemberRoleByOrganization.stream().map(IamMemberRoleDTO::getRoleId).collect(Collectors.toSet()));
                    return CollUtil.newArrayList(roles).stream().anyMatch(role -> Objects.equals(role.getCode(), InitRoleCode.ORGANIZATION_ADMINISTRATOR));
                } catch (Exception ex) {
                    log.error(" getUserMemberRoleByOrganization({},{}) error: {}", organizationId, userId, ex.getMessage(), ex);
                }
                return false;
            });
            isAdmin.set(Optional.ofNullable(admin).orElse(Boolean.FALSE));
        }
        return new ResponseEntity<>(isAdmin.get());
    }

    @Permission(level = ResourceLevel.ORGANIZATION, permissionLogin = true)
    @ApiOperation(value = "获取用户已授权的组织列表", notes = "包括只授权了项目，但是没有授权组织,也需要被包含进来")
    @GetMapping("users/{user_id}/organizations")
    public ResponseEntity<IamUserOrganizationsResponseVO> getUserOrganizations(@PathVariable(value = "user_id") Long userId) {
        final IamUserDTO iamUser = iamUserService.getAndThrow(userId);
        List<IamOrganizationDTO> organizations = organizationService.getUserAuthOrganizations(userId, false);
        List<IamUserOrganizationsResponseVO.IamUserOrganizationResponse> userOrganizations = organizations.stream().map(IamUserOrganizationsResponseVO::instance).collect(Collectors.toList());
        IamUserOrganizationsResponseVO.IamUserOrganizationsResponseVOBuilder responseBuilder = IamUserOrganizationsResponseVO.builder()
                .organizationList(userOrganizations);
        //处理当前组织
        Optional.ofNullable(iamUser.getCurrentOrganizationId()).flatMap(id -> {
            return userOrganizations.stream().filter(t -> Objects.equals(t.getId(), id)).findFirst();
        }).ifPresent(t -> {
            responseBuilder.currentOrganization(t.getId()).currentOrganizationName(t.getName());
            t.setCurrent(BigDecimal.ONE.toPlainString());
        });
        return new ResponseEntity<>(responseBuilder.build());
    }

    @Permission(level = ResourceLevel.ORGANIZATION, permissionLogin = true)
    @ApiOperation(value = "记录用户当前组织")
    @PutMapping("users/{user_id}/current_organization")
    public ResponseEntity<Boolean> updateUserCurrentOrganization(@PathVariable(value = "user_id") Long userId, @RequestBody @Valid IamUserCurrentOrganizationUpdateRequestVO vo) {
        final IamUserDTO iamUser = iamUserService.getAndThrow(userId);
        if (!Objects.equals(iamUser.getCurrentOrganizationId(), vo.getCurrentOrganizationId())) {
            iamUserService.updateUserCurrentOrganization(userId, vo.getCurrentOrganizationId());
        }
        return new ResponseEntity<>(Boolean.TRUE);
    }

    /**
     * 根据用户查询所有组织并且用户角色为组织管理员
     *
     * @param userId
     * @return
     * @deprecated 已废弃
     */
    @Deprecated
    @Permission(permissionWithin = true)
    @ApiOperation(value = "查询用户所在的所有组织，并前用户角色是组织管理员")
    @GetMapping("/users/query_organizations")
    public ResponseEntity<List<IamOrganizationVO>> queryOrganizations(
            @RequestParam("user_id") Long userId) {
        return new ResponseEntity<>(organizationService.queryAllOrganization(userId));
    }

    @Permission(permissionWithin = true)
    @ApiOperation("得到所有用户id")
    @GetMapping("/users/ids")
    public ResponseEntity<Long[]> getUserIds() {
        return new ResponseEntity<>(iamUserService.listUserIds());
    }

    /**
     * 分页查询所有的用户
     *
     * @param pageUtil 分页信息
     * @return 分页的用户
     */
    @Permission(permissionLogin = true, permissionWithin = true)
    @ApiOperation(value = "分页模糊查询用户列表")
    @GetMapping("users/all")
    public ResponseEntity<IPage<IamUserVO>> pagingQueryUsers(
            PageUtil pageUtil,
            @RequestParam(required = false, name = "realName") String realName) {
        IamUserDTO userDTO = new IamUserDTO();
        userDTO.setRealName(realName);
        return new ResponseEntity<>(iamUserService.pagingQueryUsers(pageUtil, userDTO));
    }


    /**
     * 怀疑为临时解决方案，由老行云iam迁移过来
     * 不做逻辑修改
     * <p>左上角下拉框项目列表</p>
     *
     * @param id               用户编号
     * @param includedDisabled 是否包含禁用
     */
    @Permission(level = ResourceLevel.ORGANIZATION, permissionLogin = true)
    @ApiOperation(value = "新行云查询用户所在项目列表")
    @GetMapping(value = "users/{id}/projects/new")
    public ResponseEntity<List<IamProjectVO>> queryProjectsNew(@PathVariable Long id,
                                                               @RequestParam(required = false, name = "included_disabled")
                                                                       boolean includedDisabled
            , @RequestParam(value = "organization_id", required = false) Long organizationId) {
        IamUserDTO iamUser = iamUserService.getAndThrow(id);
        organizationId = Optional.ofNullable(organizationId).orElse(iamUser.getCurrentOrganizationId());
        if (Objects.isNull(organizationId)) {
            log.warn("当前不存在组织编号,返回空项目列表");
            return new ResponseEntity<>(new ArrayList<>());
        }
        return new ResponseEntity<>(iamUserService.queryProjectsNew(id, organizationId, includedDisabled));
    }
}

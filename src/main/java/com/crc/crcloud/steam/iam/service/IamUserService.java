package com.crc.crcloud.steam.iam.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.crc.crcloud.steam.iam.common.utils.PageUtil;
import com.crc.crcloud.steam.iam.entity.IamUser;
import com.crc.crcloud.steam.iam.model.dto.IamUserDTO;
import com.crc.crcloud.steam.iam.model.dto.UserSearchDTO;
import com.crc.crcloud.steam.iam.model.dto.iam.RoleAssignmentSearchDTO;
import com.crc.crcloud.steam.iam.model.dto.iam.UserWithRoleDTO;
import com.crc.crcloud.steam.iam.model.vo.IamProjectVO;
import com.crc.crcloud.steam.iam.model.vo.IamUserVO;
import com.crc.crcloud.steam.iam.model.vo.user.IamOrganizationUserPageRequestVO;
import com.crc.crcloud.steam.iam.model.vo.user.IamUserCreateRequestVO;

import javax.annotation.Nullable;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * @Author LiuYang
 * @Description 用户
 * @Date 2019-11-12
 */
public interface IamUserService {
    /**
     * 手动添加组织成员
     *
     * @param vo              用户信息
     * @param organizationIds 所在组织
     * @return 用户
     */
    @NotNull
    IamUserDTO createUserByManual(@Valid IamUserCreateRequestVO vo, @NotEmpty Set<Long> organizationIds);

    /**
     * 获取用户-如果用户不存在抛出异常
     *
     * @param userId 用户编号
     * @return 用户信息
     */
    IamUserDTO getAndThrow(@NotNull Long userId);

    /**
     * 分页查询组织成员
     * <p>复合条件用户都会查询出来，不区分是否禁用开启等情况</p>
     *
     * @param organizationId 组织ID
     * @param vo             查询属性与分页参数
     * @return 分页数据
     */
    IPage<IamUserVO> pageQueryOrganizationUser(@NotNull Long organizationId, @Valid IamOrganizationUserPageRequestVO vo);

    /**
     * 分页查询指定项目下的成员信息
     *
     * @param projectId     项目id
     * @param userSearchDTO 人员查询参数
     * @param page          分页信息
     * @return 人员信息
     */
    IPage<IamUserVO> pageByProject(Long projectId, UserSearchDTO userSearchDTO, PageUtil page);

    /**
     * 查询项目下的人员信息，不分页，适用下拉
     *
     * @param projectId     项目id
     * @param userSearchDTO 查询条件
     * @return 用户信息
     */
    List<IamUserVO> projectDropDownUser(Long projectId, UserSearchDTO userSearchDTO);

    /**
     * 获取当前项目属于组织下未被规划的人员
     *
     * @param projectId     项目id
     * @param userSearchDTO 查询条件
     * @return 人员信息
     */
    List<IamUserVO> projectUnselectUser(Long projectId, UserSearchDTO userSearchDTO);

    /**
     * 项目绑定用户
     *
     * @param projectId 项目id
     * @param iamUserVO 绑定参数
     */
    void projectBindUsers(Long projectId, IamUserVO iamUserVO);

    /**
     * 内部端口，不对外使用
     * 通过用户id集合，查询用户信息
     * 包含用户id loginName email realName四个属性
     *
     * @param ids         用户id集合
     * @param onlyEnabled 是否排除无效用户
     * @return 用户信息
     */
    List<IamUserVO> listUserByIds(List<Long> ids, Boolean onlyEnabled);

    /**
     * 获取用户的hash密码
     * <p>用户不存在时 return {@link Optional#empty()}</p>
     *
     * @param userId 用户ID
     * @return 如果密码为空串，也返回{@link Optional#empty()}
     */
    Optional<String> getHashPassword(@NotNull Long userId);

    /**
     * 修改用户当前组织编号记录
     * {@link IamUserDTO#getCurrentOrganizationId()}
     *
     * @param userId                用户编号
     * @param currentOrganizationId 当前组织编号
     */
    void updateUserCurrentOrganization(@NotNull Long userId, @NotNull Long currentOrganizationId);

    IPage<UserWithRoleDTO> pagingQueryUsersWithProjectLevelRoles(PageUtil pageUtil, RoleAssignmentSearchDTO roleAssignmentSearchDTO, Long sourceId, boolean doPage);

    IamUserVO querySelf();

    Long[] listUserIds();

    IPage<IamUserVO> pagingQueryUsers(PageUtil pageUtil, IamUserDTO userDTO);

    /**
     * 页面左上角用户项目列表
     * <p>如果是管理员，则显示该项目下所有应用</p>
     *
     * @param id               用户编号
     * @param includedDisabled 是否包含禁用项目 已废弃
     * @param organizationId 组织编号
     * @return 项目列表
     */
    List<IamProjectVO> queryProjectsNew(Long id, @NotNull Long organizationId, boolean includedDisabled);

    /**
     * 查询项目下的人员信息
     *
     * @param projectId
     * @param userSearchDTO
     * @return
     */
    List<IamUserVO> listByProject(Long projectId, UserSearchDTO userSearchDTO);


    IPage<UserWithRoleDTO> pagingQueryUsersWithOrganizationLevelRoles(PageUtil pageUtil, RoleAssignmentSearchDTO roleAssignmentSearchDTO, Long sourceId);

    /**
     * 获取用户通过用户编号
     * <p>查询所有用户，只要用户编号存在，后续逻辑删除除外</p>
     * @param ids 用户编号
     * @return 用户集合
     */
    @NotNull
    List<IamUserDTO> getUsers(@Nullable Set<Long> ids);

    /**
     * 获取admin用户
     * <p>{@link IamUser#getIsAdmin()}=true</p>
     * <p>查询所有用户，只要用户编号存在，后续逻辑删除除外</p>
     * @return 用户集合
     */
    @NotNull
    List<IamUserDTO> getAdminUsers();

    /**
     * 通过登录名查询
     * @param loginName 登录名
     * @return 用户信息
     */
    Optional<IamUserDTO> getByLoginName(@NotBlank String loginName);

    /**
     * 通过邮箱查询
     * @param email 邮箱
     * @return 用户信息
     */
    Optional<IamUserDTO> getByEmail(@NotBlank String email);

}

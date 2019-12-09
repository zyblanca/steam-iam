package com.crc.crcloud.steam.iam.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crc.crcloud.steam.iam.common.utils.PageUtil;
import com.crc.crcloud.steam.iam.entity.IamUser;
import com.crc.crcloud.steam.iam.model.dto.IamRoleDTO;
import com.crc.crcloud.steam.iam.model.dto.UserMatchLdapDTO;
import com.crc.crcloud.steam.iam.model.dto.UserSearchDTO;
import com.crc.crcloud.steam.iam.model.dto.iam.RoleAssignmentSearchDTO;
import com.crc.crcloud.steam.iam.model.dto.user.SearchDTO;
import org.apache.ibatis.annotations.Param;

import javax.annotation.Nullable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.List;
import java.util.Set;


/**
 * 涉及人员信息，取值的时候尽量取够用
 * 涉及机密的信息，该加密的加密
 *
 * @Author:
 * @Date: 2019-11-12
 * @Description:
 */
public interface IamUserMapper extends BaseMapper<IamUser> {
    /**
     * 填充密码到用户上
     *
     * @param userId       用户编号
     * @param hashPassword 密码
     * @return 影响条数
     */
    Integer fillHashPassword(@NotNull @Param("userId") Long userId, @NotBlank @Param("hashPassword") String hashPassword);

    /**
     * 获取用户的密码
     *
     * @param userId 用户编号
     * @return 密码，可能没有
     */
    @Nullable
    String getHashPassword(@NotNull @Param("userId") Long userId);

    /**
     * 查询组织用户
     *
     * @param page
     * @param searchDTO
     * @return
     */
    IPage<IamUser> pageQueryOrganizationUser(@Param("page") Page<IamUser> page, @Param("organizationIds") Set<Long> organizationIds, @Param("searchDTO") SearchDTO searchDTO);

    /**
     * 通过项目查询用户
     *
     * @param page       分页信息
     * @param userSearch 用户查询条件
     * @return 用户信息
     */
    IPage<IamUser> pageByProject(PageUtil page, @Param("userSearch") UserSearchDTO userSearch);

    /**
     * 通过项目查询用户
     * 不分页，结果集尽量少，下拉使用
     * @param userSearch 用户查询条件
     * @return 用户信息
     */
    List<IamUser> projectDropDownUser( @Param("userSearch")UserSearchDTO userSearch);
    /**
     * 查询组织下未被当前项目选择的人
     * 不分页，结果集尽量少，下拉使用
     * @param userSearch 用户查询条件
     * @return 用户信息
     */
    List<IamUser> projectUnselectUser(@Param("userSearch")UserSearchDTO userSearch);

    /**
     * 查询用户的ldap比对信息
     * 包含登入名 是否ldap账户，所属机构信息
     * @param loginNames 登入名集合
     * @return 用户信息
     */
    List<UserMatchLdapDTO> selectUserMatchLdapByLoginName(@Param("loginNames") Collection<String> loginNames);
    /**
     * 查询用户的ldap比对信息
     * 包含登入名 邮箱信息
     * @param emails 邮箱集合
     * @return 用户信息
     */
    List<UserMatchLdapDTO> selectEmailUserByEmail(@Param("emails")Set<String> emails);

    /**
     * 通过ldap修改用户信息
     * @param iamUser
     * @return
     */
    int updateLdapUser(@Param("iamUser") IamUser iamUser);

    /**
     * ldap初始化密码
     * @param userIds 用户id
     * @param password 用户密码
     * @return
     */
    int batchUpdateLdapPassword(@Param("userIds") Set<Long> userIds,@Param("password") String password);

    int selectCountUsers(@Param("roleAssignmentSearchDTO")
                                 RoleAssignmentSearchDTO roleAssignmentSearchDTO,
                         @Param("sourceId") Long sourceId,
                         @Param("sourceType") String sourceType);

    List<IamUser> selectUserByOption( @Param("roleAssignmentSearchDTO") RoleAssignmentSearchDTO roleAssignmentSearchDTO,
                                      @Param("sourceId") Long sourceId,
                                      @Param("sourceType") String sourceType,
                                      @Param("start") Long start,
                                      @Param("size") Long size);


    List<IamRoleDTO> selectUserWithRolesByOption(@Param("sourceId")Long sourceId,@Param("sourceType")  String sourceType, @Param("userIds") List<Long> userIds);

    Long[] selectAllIds();
}

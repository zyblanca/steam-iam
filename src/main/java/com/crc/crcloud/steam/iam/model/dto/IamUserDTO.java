package com.crc.crcloud.steam.iam.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * @Author:
 * @Date: 2019-11-12
 * @Description:
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IamUserDTO {

    /**
     *
     */
    private Long id;

    /**
     * 用户名
     */
    private String loginName;

    /**
     * 电子邮箱地址
     */
    private String email;

    /**
     * 用户当前使用的组织
     */
    private Long currentOrganizationId;

    /**
     * hash后的用户密码
     */
    private String hashPassword;

    /**
     * 用户真实姓名
     */
    private String realName;


    private String roleName;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 国际电话区号。
     */
    private String internationalTelCode;

    /**
     * 用户头像地址
     */
    private String imageUrl;

    /**
     * 用户二进制头像
     */
    private String profilePhoto;

    /**
     * 语言
     */
    private String language;

    /**
     * 时区
     */
    private String timeZone;

    /**
     * 上一次密码更新时间
     */
    private Date lastPasswordUpdatedAt;

    /**
     * 上一次登陆时间
     */
    private Date lastLoginAt;

    /**
     * 用户是否启用。1启用，0未启用
     */
    private Boolean isEnabled;

    /**
     * 是否锁定账户
     */
    private Boolean isLocked;

    /**
     * 是否是ldap来源。1是，0不是
     */
    private Boolean isLdap;

    /**
     * 是否为管理员用户。1表示是，0表示不是
     */
    private Boolean isAdmin;

    /**
     * 锁定账户截止时间
     */
    private Date lockedUntilAt;

    /**
     * 密码输错累积次数
     */
    private Integer passwordAttempt;

    /**
     *
     */
    private Long objectVersionNumber;

    /**
     *
     */
    private Long createdBy;

    /**
     *
     */
    private Date creationDate;

    /**
     *
     */
    private Long lastUpdatedBy;

    /**
     *
     */
    private Date lastUpdateDate;

    private List<Long> roleIds;
}

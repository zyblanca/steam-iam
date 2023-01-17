package com.crc.crcloud.steam.iam.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Author:
 * @Date: 2019-11-12
 * @Description:
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("iam_user")
public class IamUser {

    /**
     *
     */
    @TableId(type = IdType.AUTO)
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
     * 操作密码有单独的接口
     */
    @Deprecated
    @TableField(exist = false)
    private String hashPassword;

    /**
     * 用户真实姓名
     */
    private String realName;

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
    @TableField(fill = FieldFill.INSERT)
    private Long createdBy;

    /**
     *
     */
    @TableField(fill = FieldFill.INSERT)
    private Date creationDate;

    /**
     *
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long lastUpdatedBy;

    /**
     *
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date lastUpdateDate;


}

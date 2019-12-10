package com.crc.crcloud.steam.iam.model.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
public class IamUserVO {


    @ApiModelProperty("")
    private Long id;


    @ApiModelProperty("用户名")
    private String loginName;


    @ApiModelProperty("电子邮箱地址")
    private String email;


    @ApiModelProperty("用户当前使用的组织")
    private Long currentOrganizationId;


    @ApiModelProperty("hash后的用户密码")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String hashPassword;


    @ApiModelProperty("用户真实姓名")
    private String realName;


    @ApiModelProperty("手机号")
    private String phone;


    @ApiModelProperty("国际电话区号。")
    private String internationalTelCode;


    @ApiModelProperty("用户头像地址")
    private String imageUrl;


    @ApiModelProperty("用户二进制头像")
    private String profilePhoto;


    @ApiModelProperty("语言")
    private String language;


    @ApiModelProperty("时区")
    private String timeZone;


    @ApiModelProperty("上一次密码更新时间")
    private Date lastPasswordUpdatedAt;


    @ApiModelProperty("上一次登陆时间")
    private Date lastLoginAt;


    @ApiModelProperty("用户是否启用。1启用，0未启用")
    private Boolean isEnabled;


    @ApiModelProperty("是否锁定账户")
    private Boolean isLocked;


    @ApiModelProperty("是否是ldap来源。1是，0不是")
    private Boolean isLdap;


    @ApiModelProperty("是否为管理员用户。1表示是，0表示不是")
    private Boolean isAdmin;


    @ApiModelProperty("锁定账户截止时间")
    private Date lockedUntilAt;


    @ApiModelProperty("密码输错累积次数")
    private Integer passwordAttempt;


    @ApiModelProperty("")
    private Long objectVersionNumber;


    @ApiModelProperty("")
    private Long createdBy;


    @ApiModelProperty("")
    private Date creationDate;


    @ApiModelProperty("")
    private Long lastUpdatedBy;


    @ApiModelProperty("")
    private Date lastUpdateDate;
    @ApiModelProperty("用户id数组")
    private List<Long> userIds;
    @ApiModelProperty("人员归属组织信息")
    private List<IamOrganizationVO> organizations;



}

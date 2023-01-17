package com.crc.crcloud.steam.iam.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.lang.Integer;
import java.lang.Long;
import java.lang.Byte;

/**
 * @Author:
 * @Date: 2019-11-12
 * @Description:
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel("")
public class OauthLdapErrorUserVO {


    @ApiModelProperty("")
    private Long id;


    @ApiModelProperty("ldap同步历史id")
    private Long ldapHistoryId;


    @ApiModelProperty("ldap对象的唯一标识，可以根据此标识到ldap server查询详细信息")
    private String uuid;


    @ApiModelProperty("用户登录名")
    private String loginName;


    @ApiModelProperty("用户邮箱")
    private String email;


    @ApiModelProperty("真实姓名")
    private String realName;


    @ApiModelProperty("手机号")
    private String phone;


    @ApiModelProperty("失败原因")
    private String cause;


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


}

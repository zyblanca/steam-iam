package com.crc.crcloud.steam.iam.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * @Author:
 * @Date: 2019-11-12
 * @Description:
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OauthLdapVO {


    @ApiModelProperty("主键")
    private Long id;


    @ApiModelProperty("ldap的名称，取组织名称")
    private String name;


    @ApiModelProperty("组织id")
    private Long organizationId;


    @ApiModelProperty("ldap服务器地址")
    @NotEmpty(message = "ldap.host.empty")
    private String serverAddress;


    @ApiModelProperty("LDAP端口")
    @NotEmpty(message = "ldap.port.empty")
    private String port;


    @ApiModelProperty("管理员登录名")
    @NotEmpty(message = "ldap.account.empty")
    private String account;


    @ApiModelProperty("管理员密码")
    @NotEmpty(message = "ldap.password.empty")
    private String ldapPassword;


    @ApiModelProperty("使用ssl加密传输方式，0：不使用，1：使用")
    private Long useSsl;


    @ApiModelProperty("是否启用，0：不启用，1：启用")
    private Long isEnabled;


    @ApiModelProperty("基准DN")
    @NotEmpty(message = "ldap.base.dn.empty")
    private String baseDn;


    @ApiModelProperty("目录类型")
    @NotEmpty(message = "ldap.directory.type.empty")
    private String directoryType;


    @ApiModelProperty("对象类型")
    @NotEmpty(message = "ldap.object.class.empty")
    private String objectClass;


    @ApiModelProperty("同步用户自定义过滤条件，格式以'('开始以')'结束")
    @Pattern(regexp = "\\(.*\\)", message = "ldap.custom.filter.match")
    private String customFilter;


    @ApiModelProperty("同步用户每次发送saga的用户数量")
    @NotNull(message = "ldap.batch.size.null")
    @Min(value = 1, message = "ldap.batch.size.min")
    private Integer sagaBatchSize;


    @ApiModelProperty("ldap服务器连接超时时间，单位为秒，默认值为10秒")
    @NotNull(message = "ldap.time.out.null")
    @Min(value = 1, message = "ldap.time.out.min")
    private Integer connectionTimeout;


    @ApiModelProperty("ldap中唯一标识对象的字段")
    @NotEmpty(message = "ldap.uuidField.empty")
    private String uuidField;


    @ApiModelProperty("登入名对应的字段名")
    @NotEmpty(message = "ldap.login.name.empty")
    private String loginNameField;


    @ApiModelProperty("用户名属性")
    @NotEmpty(message = "ldap.real.name.empty")
    private String realNameField;


    @ApiModelProperty("邮箱属性")
    @NotEmpty(message = "ldap.mail.empty")
    private String emailField;


    @ApiModelProperty("手机号属性")
    private String phoneField;


    @ApiModelProperty("")
    private Long objectVersionNumber;


}

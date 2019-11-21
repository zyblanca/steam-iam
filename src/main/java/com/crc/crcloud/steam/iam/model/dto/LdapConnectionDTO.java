package com.crc.crcloud.steam.iam.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LdapConnectionDTO {
    @ApiModelProperty(value = "基础连接是否成功")
    private Boolean canConnectServer;
    @ApiModelProperty(value = "LDAP登录是否成功")
    private Boolean canLogin;
    @ApiModelProperty(value = "用户属性校验是否成功")
    private Boolean matchAttribute;
    @ApiModelProperty(value = "登录名属性是否成功")
    private Boolean loginNameField;
    @ApiModelProperty(value = "用户名属性是否成功")
    private Boolean realNameField;
    @ApiModelProperty(value = "手机号属性是否成功")
    private Boolean phoneField;
    @ApiModelProperty(value = "邮箱属性是否成功")
    private Boolean emailField;
    @ApiModelProperty(value = "uuid属性校验是否成功")
    private Boolean uuidField;

}

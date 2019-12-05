package com.crc.crcloud.steam.iam.model.dto.iam;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDTO {


    @ApiModelProperty(value = "主键ID/非必填")
    private Long id;

    @ApiModelProperty(value = "组织ID/非必填")
    private Long organizationId;

    @ApiModelProperty(value = "组织名称/非必填")
    private String organizationName;

    // 只用于返回该数据，不读入
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String organizationCode;

    @ApiModelProperty(value = "登录名/必填")
    private String loginName;

    @ApiModelProperty(value = "邮箱/必填")

    private String email;

    @ApiModelProperty(value = "用户名/必填")

    private String realName;

    @ApiModelProperty(value = "手机号/非必填")

    private String phone;

    @ApiModelProperty(value = "国际电话区号/非必填")
    private String internationalTelCode;

    @ApiModelProperty(value = "头像/非必填")
    private String imageUrl;

    @ApiModelProperty(value = "语言/非必填")
    private String language;

    @ApiModelProperty(value = "时区/非必填")
    private String timeZone;

    @ApiModelProperty(value = "是否被锁定/非必填")
    private Boolean locked;

    @ApiModelProperty(value = "是否是LDAP用户/非必填")
    private Boolean ldap;

    @ApiModelProperty(value = "是否启用/非必填")
    private Boolean enabled;

    @ApiModelProperty(value = "密码")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @ApiModelProperty(value = "是否是ROOT用户/非必填")
    private Boolean admin;
    @ApiModelProperty(value = "乐观锁版本号")
    private Long objectVersionNumber;

    @JsonIgnore
    private String param;

}

package com.crc.crcloud.steam.iam.model.vo.site;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

/**
 * LDAP用户信息
 * @author LiuYang
 * @date 2019/12/16
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SiteLdapUserResponseVO {
    @NotNull
    @ApiModelProperty("用户名")
    private Long id;

    @NotNull
    @ApiModelProperty("登录名")
    private String loginName;

    @NotNull
    @ApiModelProperty("用户名")
    private String realName;

    @NotNull
    @ApiModelProperty("邮箱")
    private String email;

    @Nullable
    @ApiModelProperty("电话号码")
    private String phoneNumber;

    @ApiModelProperty("公司")
    private String company;
    @ApiModelProperty("部门")
    private String department;
    @ApiModelProperty("职务")
    private String position;


}

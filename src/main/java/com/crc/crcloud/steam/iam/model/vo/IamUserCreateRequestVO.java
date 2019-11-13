package com.crc.crcloud.steam.iam.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

/**
 * 创建用户
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IamUserCreateRequestVO {
    @Length(max = 40, message = "user.loginName.length")
    @NotBlank
    @ApiModelProperty("登录名")
    private String loginName;
    @NotBlank
    @Length(max = 40)
    @ApiModelProperty("用户名")
    private String realName;

    @NotBlank(message = "user.email.format")
    @Email(message = "user.email.format")
    @ApiModelProperty("电子邮箱地址")
    private String email;

    @NotBlank
    @Length(min = 6, max = 40)
    @ApiModelProperty("密码")
    private String password;

    @ApiModelProperty("角色")
    private String role;
}

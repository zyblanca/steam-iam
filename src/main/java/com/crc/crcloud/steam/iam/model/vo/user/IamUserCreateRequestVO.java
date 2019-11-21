package com.crc.crcloud.steam.iam.model.vo.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Set;

/**
 * 创建用户
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IamUserCreateRequestVO {
    /**
     * 登录名只能由字母、数字、"-"、"_"、"."组成，且不能以“-”开头，不能以"."、 “.git"或者“.atom”结尾
     */
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

    @NotNull
    @NotEmpty
    @ApiModelProperty("角色编号")
    private Set<Long> roleIds;
}

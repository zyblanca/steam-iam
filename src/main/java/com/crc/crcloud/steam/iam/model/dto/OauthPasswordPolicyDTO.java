package com.crc.crcloud.steam.iam.model.dto;


import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OauthPasswordPolicyDTO {
    @ApiModelProperty(value = "主键ID/非必填")
    private Long id;

    @ApiModelProperty(value = "密码策略编码/必填")
    @NotNull(message = "error.passwordPolicy.code.null")
    private String code;

    @ApiModelProperty(value = "密码策略名称/必填")
    @NotNull(message = "error.passwordPolicy.name.null")
    private String name;

    @ApiModelProperty(value = "组织ID/必填")
    @NotNull(message = "error.passwordPolicy.organizationId.null")
    private Long organizationId;

    @ApiModelProperty(value = "新用户默认密码/非必填")
    private String originalPassword;

    @ApiModelProperty(value = "最小密码长度/非必填")
    @Range(min = 0, message = "error.minLength")
    private Integer minLength;

    @ApiModelProperty(value = "最大密码长度/非必填")
    @Range(min = 0, message = "error.maxLength")
    private Integer maxLength;

    @ApiModelProperty(value = "输错多少次后开启锁定/非必填")
    private Integer maxErrorTime;

    @ApiModelProperty(value = "最少数字数/非必填")
    @Range(min = 0, message = "error.digitsCount")
    private Integer digitsCount;

    @ApiModelProperty(value = "最少小写字母数/非必填")
    @Range(min = 0, message = "error.lowercaseCount")
    private Integer lowercaseCount;

    @ApiModelProperty(value = "最少大写字母数/非必填")
    @Range(min = 0, message = "error.uppercaseCount")
    private Integer uppercaseCount;

    @ApiModelProperty(value = "最少特殊字符数/非必填")
    @Range(min = 0, message = "error.specialCharCount")
    private Integer specialCharCount;

    @ApiModelProperty(value = "是否允许与登录名相同/非必填")
    private Boolean notUsername;

    @ApiModelProperty(value = "密码正则/非必填")
    private String regularExpression;

    @ApiModelProperty(value = "最大近期密码数/非必填")
    @Range(min = 0, message = "error.notRecentCount")
    private Integer notRecentCount;

    @ApiModelProperty(value = "是否开启密码安全策略/非必填")
    private Boolean enablePassword;

    @ApiModelProperty(value = "是否开启登录安全策略/非必填")
    private Boolean enableSecurity;

    @ApiModelProperty(value = "是否开启锁定/非必填")
    private Boolean enableLock;

    @ApiModelProperty(value = "锁定时长/非必填")
    private Integer lockedExpireTime;

    @ApiModelProperty(value = "是否开启验证码/非必填")
    private Boolean enableCaptcha;

    @ApiModelProperty(value = "输错多少次后开启验证码/非必填")
    private Integer maxCheckCaptcha;

    @ApiModelProperty(value = "乐观锁版本号")
    private Long objectVersionNumber;

    private Long createdBy;
    private Date creationDate;
    private Long lastUpdatedBy;
    private Date lastUpdateDate;
}

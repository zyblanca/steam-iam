package com.crc.crcloud.steam.iam.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("oauth_password_policy")
public class OauthPasswordPolicy {
    private Long id;
    private String code;
    private String name;
    private Long organizationId;
    private String originalPassword;
    private Integer minLength;
    private Integer maxLength;
    private Integer maxErrorTime;
    private Integer digitsCount;
    private Integer lowercaseCount;
    private Integer uppercaseCount;
    private Integer specialCharCount;
    private Boolean notUsername;
    private String regularExpression;
    private Integer notRecentCount;
    private Boolean enablePassword;
    private Boolean enableSecurity;
    private Boolean enableLock;
    private Integer lockedExpireTime;
    private Boolean enableCaptcha;
    private Integer maxCheckCaptcha;
    private Long objectVersionNumber;

    @TableField(fill = FieldFill.INSERT)
    private Long createdBy;

    @TableField(fill = FieldFill.INSERT)
    private Date creationDate;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long lastUpdatedBy;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date lastUpdateDate;
}

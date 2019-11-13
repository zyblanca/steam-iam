package com.crc.crcloud.steam.iam.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.annotation.Nullable;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IamOrganizationUserPageRequestVO {
    @NotNull
    @Min(1)
    private Integer page;
    @NotNull
    @Min(1)
    private Integer pageSize;

    @Nullable
    @ApiModelProperty("登录名")
    private String loginName;

    @Nullable
    @ApiModelProperty("用户名")
    private String realName;

    @Nullable
    @ApiModelProperty("角色")
    private String roleName;
}

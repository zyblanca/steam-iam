package com.crc.crcloud.steam.iam.model.dto.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.annotation.Nullable;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchDTO {
    @Nullable
    @ApiModelProperty("登录名搜索")
    private String loginName;
    @Nullable
    @ApiModelProperty("用户名搜索")
    private String realName;

    @Nullable
    @ApiModelProperty("角色")
    private Set<Long> roleIds;

    @Nullable
    @ApiModelProperty("是否LDAP用户")
    private Boolean isLdap;

    @ApiModelProperty("角色排序")
    private String orderByRoleName;
}

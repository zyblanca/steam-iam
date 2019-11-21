package com.crc.crcloud.steam.iam.model.vo.user;

import com.crc.crcloud.steam.iam.common.enums.UserOriginEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.annotation.Nullable;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Set;

/**
 * @author hand-196
 */
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
    @ApiModelProperty("登录名搜索")
    private String loginName;
    @Nullable
    @ApiModelProperty("用户名搜索")
    private String realName;

    @Nullable
    @ApiModelProperty("角色")
    private Set<Long> roleIds;

    /**
     * @see UserOriginEnum#getValue()
     */
    @Nullable
    @ApiModelProperty("用户来源筛选")
    private Set<String> origins;
}

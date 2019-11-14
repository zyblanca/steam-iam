package com.crc.crcloud.steam.iam.model.vo.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.annotation.Nullable;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Set;

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
    @ApiModelProperty("搜索")
    private String keywords;

    @Nullable
    @ApiModelProperty("角色")
    private Set<Long> roleIds;
}

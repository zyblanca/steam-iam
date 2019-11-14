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
    @ApiModelProperty("搜索")
    private Set<String> keywords;

    @Nullable
    @ApiModelProperty("角色")
    private Set<Long> roleIds;
}

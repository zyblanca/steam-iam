package com.crc.crcloud.steam.iam.model.vo.project;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

/**
 *
 * @author LiuYang
 * @date 2019/12/24
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IamUserProjectRequestVO {
    @NotNull
    private Long organizationId;

    @ApiModelProperty("名称搜索")
    @Nullable
    private String name;
}

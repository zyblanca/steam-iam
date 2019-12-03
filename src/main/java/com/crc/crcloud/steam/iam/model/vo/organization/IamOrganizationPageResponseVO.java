package com.crc.crcloud.steam.iam.model.vo.organization;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 *
 * @author LiuYang
 * @date 2019/12/3
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IamOrganizationPageResponseVO {
    @ApiModelProperty("")
    private Long id;

    @ApiModelProperty("组织名")
    private String name;

    @ApiModelProperty("组织编码")
    private String code;

    @NotNull
    @ApiModelProperty("项目数量")
    private Integer projectCount;

    @ApiModelProperty("是否启用。1启用，0未启用")
    private Boolean isEnabled;
}

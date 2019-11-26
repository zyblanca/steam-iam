package com.crc.crcloud.steam.iam.model.feign.role;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * @author LiuYang
 * @date 2019/11/25
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberRoleDTO {
    @ApiModelProperty(value = "主键ID/非必填")
    private Long id;
    @ApiModelProperty(value = "角色ID/必填")
    @NotNull(message = "error.role.id.null")
    private Long roleId;
    @ApiModelProperty(value = "成员ID/必填")
    @NotNull(message = "error.member.id.null")
    private Long memberId;
    @ApiModelProperty(value = "成员类型/必填/默认：user")
    private String memberType;
    @ApiModelProperty(value = "资源ID/必填")
    @NotNull(message = "error.source.id.null")
    private Long sourceId;
    @ApiModelProperty(value = "来源类型（project/organization）/非必填")
    private String sourceType;
}

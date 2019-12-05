package com.crc.crcloud.steam.iam.model.dto.iam;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RolePermissionDTO {
    @ApiModelProperty(value = "主键ID")
    private Long id;
    @ApiModelProperty(value = "角色ID/必填")
    private Long roleId;
    @ApiModelProperty(value = "权限ID/必填")
    private Long permissionId;

}

package com.crc.crcloud.steam.iam.model.dto.iam;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoleAssignmentSearchDTO {

    @ApiModelProperty(value = "登录名")
    private String loginName;

    @ApiModelProperty(value = "角色名")
    private String roleName;

    @ApiModelProperty(value = "用户名")
    private String realName;

//    @ApiModelProperty(value = "参数")
//    private String[] param;

}

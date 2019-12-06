package com.crc.crcloud.steam.iam.model.dto.iam;

import com.crc.crcloud.steam.iam.model.feign.role.RoleDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserWithRoleDTO extends UserDTO {

    @ApiModelProperty(value = "角色列表")
    private List<RoleDTO> roles;

}

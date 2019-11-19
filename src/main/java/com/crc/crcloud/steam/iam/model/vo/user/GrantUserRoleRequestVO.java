package com.crc.crcloud.steam.iam.model.vo.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Set;

/**
 * 授权用户角色
 * @author hand-196
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GrantUserRoleRequestVO {

    @ApiModelProperty("用户ID")
    @NotEmpty
    private Set<Long> userIds;

    @NotNull
    @Min(1)
    @ApiModelProperty("角色ID")
    private Long roleId;
}

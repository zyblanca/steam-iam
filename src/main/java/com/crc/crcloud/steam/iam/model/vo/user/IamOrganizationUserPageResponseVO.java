package com.crc.crcloud.steam.iam.model.vo.user;

import com.crc.crcloud.steam.iam.common.enums.UserOriginEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 组织成员响应
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IamOrganizationUserPageResponseVO {
    @ApiModelProperty("登录名")
    private String loginName;
    @ApiModelProperty("用户名")
    private String realName;
    @ApiModelProperty("角色")
    private String roleName;
    /**
     * @see UserOriginEnum#getDesc()
     */
    @ApiModelProperty("来源")
    private String origin;
}

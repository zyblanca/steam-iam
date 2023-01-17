package com.crc.crcloud.steam.iam.model.feign.role;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * iam-server.RoleDTO
 *
 * @author LiuYang
 * @date 2019/11/25
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleDTO {
    private Long id;
    private String name;
    private String code;
    private String description;
    private String level;
    private Boolean enabled;
    private Boolean modified;
    private Boolean enableForbidden;
    private Boolean builtIn;
    private Boolean assignable;
}

package com.crc.crcloud.steam.iam.model.dto.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author:
 * @Date: 2019-11-25
 * @Description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserEventPayload {
    /**
     * 用户 id
     */
    private Long id;
    /**
     * 用户名称
     */
    private String name;
    /**
     * 用户登录名称
     */
    private String username;
    /**
     * 邮箱
     */
    private String email;
    /**
     * 该用户创建人
     */
    private Long fromUserId;
    /**
     * 组织 id
     */
    private Long organizationId;
    /**
     * 是否是 ldap 用户
     */
    private Boolean ldap;
}

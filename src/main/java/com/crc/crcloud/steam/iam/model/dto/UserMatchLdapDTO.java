package com.crc.crcloud.steam.iam.model.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * 用于比对ldap用户与系统已存在用户
 */
@Getter
@Setter
public class UserMatchLdapDTO {

    private Long id;

    private String loginName;

    private String email;

    private Boolean isLdap = Boolean.FALSE;

    private String organizationIds;

    private Set<Long> organization = new HashSet<>();

    public void setOrganizationIds(String organizationIds) {
        if (StringUtils.hasText(organizationIds)) {
            for (String id : organizationIds.split(",")) {
                organization.add(Long.valueOf(id));
            }
        }
        this.organizationIds = organizationIds;
    }
}

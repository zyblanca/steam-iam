package com.crc.crcloud.steam.iam.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * ldap类型
 */
@AllArgsConstructor
@Getter
public enum LdapTypeEnum {
    OPEN_LDAP("OpenLDAP"),

    MICROSOFT_ACTIVE_DIRECTORY("Microsoft Active Directory");

    private final String value;


}

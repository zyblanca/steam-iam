package com.crc.crcloud.steam.iam.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * ldap类型
 *
 * @author LiuChun
 */
@SuppressWarnings("AlibabaEnumConstantsMustHaveComment")
@AllArgsConstructor
@Getter
public enum LdapTypeEnum implements IBaseEnum<String> {
    OPEN_LDAP("OpenLDAP"),

    MICROSOFT_ACTIVE_DIRECTORY("Microsoft Active Directory");

    private final String value;


    @Override
    public String getDesc() {
        return this.name();
    }
}

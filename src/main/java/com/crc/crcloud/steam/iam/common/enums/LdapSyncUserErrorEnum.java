package com.crc.crcloud.steam.iam.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * ldap 同步异常类型
 */
@Getter
@AllArgsConstructor
public enum LdapSyncUserErrorEnum {

    UUID_NOT_FIND("uuid对应属性不存在"),
    REAL_NAME_NOT_FIND("用户名属性不存在"),
    LOGIN_NAME__NOT_FIND("登入名属性不存在"),
    EMAIL_NOT_FIND("邮箱属性不存在"),
    PHONE_NOT_FIND("手机号属性不存在"),
    SAME_LOGIN_DIFF_LDAP("相同用户名，不同的ldap配置"),
    LOGIN_NAME_EXIST_NOT_LDAP("用户名已被非ldap用户占用");

    private String msg;

}

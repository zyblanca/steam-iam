package com.crc.crcloud.steam.iam.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * ldap 同步异常类型
 */
@Getter
@AllArgsConstructor
public enum LdapSyncUserErrorEnum {

    UUID_NOT_FOUND("uuid对应属性不存在"),
    UUID_GET_FAILURE("获取uuid失败"),
    REAL_NAME_NOT_FOUND("用户名属性不存在"),
    REAL_NAME_GET_FAILURE("获取用户名属性失败"),
    LOGIN_NAME_NOT_FOUND("登入名属性不存在"),
    LOGIN_NAME_GET_FAILURE("获取登入名失败"),
    EMAIL_NOT_FOUND("邮箱属性不存在"),
    EMAIL_GET_FAILURE("获取邮箱失败"),
    EMAIL_EXIST_FOUND("邮箱已经存在于其他账户"),
    PHONE_NOT_FOUND("手机号属性不存在"),
    PHONE_GET_FAILURE("获取手机号失败"),
    SAME_LOGIN_DIFF_LDAP("相同用户名，不同的ldap配置"),
    LOGIN_NAME_EXIST_NOT_LDAP("用户名已被非ldap用户占用");

    private String msg;

}

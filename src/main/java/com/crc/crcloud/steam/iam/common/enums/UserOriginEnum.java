package com.crc.crcloud.steam.iam.common.enums;

/**
 * 用户来源枚举
 */
public enum UserOriginEnum implements IBaseEnum<String> {
    LDAP("LDAP"),
    MANUAL("手动添加"),
    ;
    //待确认
    private String value;
    private String desc;

    UserOriginEnum(String desc) {
        this.desc = desc;
    }

    @Override
    public String getDesc() {
        return this.name();
    }

    @Override
    public String getValue() {
        return desc;
    }
}

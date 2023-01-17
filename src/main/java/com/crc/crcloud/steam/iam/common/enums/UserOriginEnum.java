package com.crc.crcloud.steam.iam.common.enums;

/**
 * 用户来源枚举
 *
 * @author hand-196
 */
public enum UserOriginEnum implements IBaseEnum<String> {
    /**
     * LDAP添加
     */
    LDAP("LDAP"),
    /**
     * 手动添加
     */
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
        return desc;
    }

    @Override
    public String getValue() {
        return this.name();
    }
}

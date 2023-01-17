package com.crc.crcloud.steam.iam.common.enums;

import com.crc.crcloud.steam.iam.entity.IamMemberRole;

/**
 * 成员类型
 *
 * @see IamMemberRole#getMemberType()
 */
public enum MemberType implements IBaseEnum<String> {
    CLIENT("client", "客户端"),
    USER("user", "用户"),
    ;
    private String value;
    private String desc;

    MemberType(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    @Override
    public String getDesc() {
        return desc;
    }

    @Override
    public String getValue() {
        return value;
    }
}

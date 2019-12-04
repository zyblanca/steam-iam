package com.crc.crcloud.steam.iam.common.enums;

import java.util.Arrays;

/**
 * 应用的分类
 */
public enum  ApplicationType {
    /**
     * 开发应用
     */
    DEVELOPMENT("开发应用", "normal"),

    /**
     * 测试应用
     */
    TEST("测试应用", "test");

    private String value;
    private String code;

    ApplicationType(String value, String code) {
        this.value = value;
        this.code = code;
    }

    public String value() {
        return value;
    }

    public String code() {
        return code;
    }

    public static boolean matchCode(String code) {
        /*for (ApplicationType applicationType : ApplicationType.values()) {
            if (applicationType.code.equals(code)) {
                return true;
            }
        }
        return false;*/
        return Arrays.stream(ApplicationType.values()).anyMatch(app -> app.code.equals(code));
    }

}

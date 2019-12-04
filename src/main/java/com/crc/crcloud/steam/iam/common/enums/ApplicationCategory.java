package com.crc.crcloud.steam.iam.common.enums;

import java.util.Arrays;

/**
 * 应用被划分为哪些类别
 */
public enum ApplicationCategory {

    /**
     * 应用
     */
    APPLICATION("普通应用", "application"),

    /**
     * 组合应用
     */
    COMBINATION("组合应用", "combination-application");

    private String value;
    private String code;

    ApplicationCategory(String value, String code) {
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
/*        for (ApplicationCategory applicationCategory : ApplicationCategory.values()) {
            if (applicationCategory.code.equals(code)) {
                return true;
            }
        }
        return false;*/
        // 任意一个匹配到就返回 true ，否则就返回 false
        return Arrays.stream(ApplicationCategory.values()).anyMatch(app -> app.code.equals(code));
    }
}

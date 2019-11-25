package com.crc.crcloud.steam.iam.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 角色来源
 * @see io.choerodon.core.iam.ResourceLevel
 */
@Getter
@AllArgsConstructor
public enum MemberRoleSourceTypeEnum {

    SITE("site", ""),
    USER("user", ""),
    PROJECT("project", "项目"),
    ORGANIZATION("organization", "组织");

    private String sourceType;

    private String msg;

}

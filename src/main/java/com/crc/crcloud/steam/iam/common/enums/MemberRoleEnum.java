package com.crc.crcloud.steam.iam.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 当前角色写死，使用配置
 * 后续动态配置的时候，删除/作废该枚举
 */
@Getter
@AllArgsConstructor
public enum  MemberRoleEnum {
    ORGANIZATION_ADMINISTRATOR(2L,"role/organization/default/administrator","组织管理员","organization"),
    PROJECT_ADMINISTRATOR(3L,"role/project/default/administrator","项目管理员","project"),
    PROJECT_MEMBER(5L,"role/project/default/project-member","项目成员","project"),
    PROJECT_OWNER(6L,"role/project/default/project-owner","项目所有者","project"),
    ORGANIZATION_MEMBER(7L,"role/organization/default/organization-member","组织成员","organization"),
    ;



    private Long roleId;

    private String code;

    private String name;

    private String fdLevel;
}

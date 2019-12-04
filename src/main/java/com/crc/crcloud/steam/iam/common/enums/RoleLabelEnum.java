package com.crc.crcloud.steam.iam.common.enums;

/**
 * @author superlee
 */
public enum RoleLabelEnum {

    PROJECT_DEPLOY_ADMIN("project.deploy.admin"),

    PROJECT_OWNER("project.owner"),

    ORGANIZATION_OWNER("organization.owner");

    private final String value;

    RoleLabelEnum(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}

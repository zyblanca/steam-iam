package com.crc.crcloud.steam.iam.model.dto.payload;

/**
 * @author liuyang
 */
public class OrganizationEventPayload {

    private Long organizationId;

    public OrganizationEventPayload(Long organizationId) {
        this.organizationId = organizationId;
    }

    public OrganizationEventPayload() {
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }
}

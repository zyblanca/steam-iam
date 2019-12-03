package com.crc.crcloud.steam.iam.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicationSearchDTO {

    private Long organizationId;
    private String name;
    private String code;
    private String applicationType;
    private String applicationCategory;
    private Boolean enabled;
    private String projectName;
    private String param;

}

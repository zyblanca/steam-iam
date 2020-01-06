package com.crc.crcloud.steam.iam.model.dto.payload;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class ProjectEventPayload {

    private Long projectId;
    private String projectCode;
    private String projectName;
    private String projectCategory;
    private String organizationCode;
    private String organizationName;
    private String userName;
    private Long userId;
    private String imageUrl;
    private Long organizationId;
    private Set<String> roleLabels;
    //新行云新增同步信息
    private String type;
    private String category;
}

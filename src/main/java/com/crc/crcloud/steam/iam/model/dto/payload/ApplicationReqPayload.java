package com.crc.crcloud.steam.iam.model.dto.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApplicationReqPayload {

    private Long id;
    private String name;
    private String code;
    private Long projectId;
    private String type;
    private Long applicationTemplateId;
    private Long harborConfigId;
    private Long chartConfigId;
    private List<Long> userIds;
    private Boolean isSkipCheckPermission;

}

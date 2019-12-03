package com.crc.crcloud.steam.iam.model.dto.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApplicationPayload {

    private Long steamProjectId;
    private String applicationCode;
    private String applicationName;
    private Integer status;

}

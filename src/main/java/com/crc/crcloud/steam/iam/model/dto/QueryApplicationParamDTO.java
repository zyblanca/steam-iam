package com.crc.crcloud.steam.iam.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class QueryApplicationParamDTO {
    @ApiModelProperty(value = "组织code")
    private String code;
    @ApiModelProperty(value = "行云项目Id")
    private Long steamProjectId;
    @ApiModelProperty(value = "行云组织ID")
    private Long organizationId;
    @ApiModelProperty(value = "应用名称")
    private String name;

}

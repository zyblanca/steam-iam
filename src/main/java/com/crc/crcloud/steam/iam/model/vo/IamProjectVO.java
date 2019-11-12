package com.crc.crcloud.steam.iam.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Author:
 * @Date: 2019-11-12
 * @Description:
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IamProjectVO {


    @ApiModelProperty("")
    private Long id;


    @ApiModelProperty("项目名")
    private String name;


    @ApiModelProperty("项目编码")
    private String code;


    @ApiModelProperty("项目描述")
    private String description;


    @ApiModelProperty("组织编号")
    private Long organizationId;


    @ApiModelProperty("是否启用。1启用，0未启用")
    private Byte isEnabled;


    @ApiModelProperty("项目图标url")
    private String imageUrl;


    @ApiModelProperty("")
    private Long objectVersionNumber;


    @ApiModelProperty("")
    private Long createdBy;


    @ApiModelProperty("")
    private Date creationDate;


    @ApiModelProperty("")
    private Long lastUpdatedBy;


    @ApiModelProperty("")
    private Date lastUpdateDate;


    @ApiModelProperty("项目类型")
    private String type;


    @ApiModelProperty("项目类别：agile(敏捷项目),program(普通项目组),analytical(分析型项目群)")
    private String category;


}

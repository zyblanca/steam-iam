package com.crc.crcloud.steam.iam.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
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
    @NotEmpty(message = "project.name.empty")
    @Size(max = 40, min = 1, message = "project.name.size")
    @Pattern(regexp = "^[-—\\.\\w\\s\\u4e00-\\u9fa5]{1,40}$", message = "project.name.pattern")
    private String name;


    @ApiModelProperty("项目编码")
    @NotEmpty(message = "project.code.name")
    @Size(min = 1, max = 40, message = "project.code.size")
    @Pattern(regexp = "^[-—\\.\\w\\s\\u4e00-\\u9fa5]{1,40}$", message = "project.code.pattern")
    private String code;


    @ApiModelProperty("项目描述")
    private String description;


    @ApiModelProperty("组织编号")
    private Long organizationId;


    @ApiModelProperty("是否启用")
    private Boolean isEnabled;

    @ApiModelProperty("兼容老行云，新行云勿用此字段")
    private Boolean enabled;


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

    @Deprecated
    //老行云使用，不需要赋值，只要有这个字段就可以
    private String typeName;

    @ApiModelProperty("项目类别：agile(敏捷项目),program(普通项目组),analytical(分析型项目群)")
    @NotEmpty(message = "project.category.empty")
    private String category;

    private Long userId;

    private String realName;


}

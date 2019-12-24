package com.crc.crcloud.steam.iam.model.vo.project;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 切换组织之后用户所属组织下项目列表
 * @author LiuYang
 * @date 2019/12/24
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IamUserProjectResponseVO {

    @ApiModelProperty("项目编号")
    private Long id;

    @ApiModelProperty("项目名")
    private String name;

    @ApiModelProperty("组织编号")
    private Long organizationId;

    @ApiModelProperty("项目编码")
    private String code;

    @ApiModelProperty("项目类别：agile(敏捷项目),program(普通项目组),analytical(分析型项目群)")
    private String category;

    @ApiModelProperty("创建人名字")
    private String realName;

    private Long createdBy;

    private Date creationDate;

    private Long lastUpdatedBy;

    private Date lastUpdateDate;
}

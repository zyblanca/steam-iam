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
@ApiModel("")
public class IamRoleVO {


    @ApiModelProperty("")
    private Long id;


    @ApiModelProperty("角色名")
    private String name;


    @ApiModelProperty("角色编码")
    private String code;


    @ApiModelProperty("角色描述full description")
    private String description;


    @ApiModelProperty("角色级别")
    private String fdLevel;


    @ApiModelProperty("是否启用。1启用，0未启用")
    private Boolean isEnabled;


    @ApiModelProperty("是否可以修改。1表示可以，0不可以")
    private Boolean isModified;


    @ApiModelProperty("是否可以被禁用")
    private Boolean isEnableForbidden;


    @ApiModelProperty("是否内置。1表示是，0表示不是")
    private Boolean isBuiltIn;


    @ApiModelProperty("是否禁止在更高的层次上分配，禁止project role在organization上分配。1表示可以，0表示不可以")
    private Boolean isAssignable;


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


}

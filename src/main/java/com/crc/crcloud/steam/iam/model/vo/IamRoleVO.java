package com.crc.crcloud.steam.iam.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.lang.Integer;
import java.lang.Long;
import java.lang.Byte;

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
public class IamRoleVO{


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
        private Byte isEnabled;


        @ApiModelProperty("是否可以修改。1表示可以，0不可以")
        private Byte isModified;


        @ApiModelProperty("是否可以被禁用")
        private Byte isEnableForbidden;


        @ApiModelProperty("是否内置。1表示是，0表示不是")
        private Byte isBuiltIn;


        @ApiModelProperty("是否禁止在更高的层次上分配，禁止project role在organization上分配。1表示可以，0表示不可以")
        private Byte isAssignable;


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

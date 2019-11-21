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
public class IamUserOrganizationRelVO{


        @ApiModelProperty("")
        private Long id;


        @ApiModelProperty("用户编号")
        private Long userId;


        @ApiModelProperty("组织编号")
        private Long organizationId;


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

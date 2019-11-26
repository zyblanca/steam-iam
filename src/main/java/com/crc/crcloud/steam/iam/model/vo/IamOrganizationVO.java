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
public class IamOrganizationVO{


        @ApiModelProperty("")
        private Long id;


        @ApiModelProperty("组织名")
        private String name;


        @ApiModelProperty("组织编码")
        private String code;


        @ApiModelProperty("组织描述")
        private String description;


        @ApiModelProperty("是否启用。1启用，0未启用")
        private Boolean isEnabled;


        @ApiModelProperty("是否为注册组织。1.是，0不是")
        private Boolean isRegister;


        @ApiModelProperty("创建用户的编号")
        private Long userId;


        @ApiModelProperty("组织的地址")
        private String address;


        @ApiModelProperty("组织图标url")
        private String imageUrl;


        @ApiModelProperty("组织规模。0：0-30,1：30-100,2：100")
        private Integer scale;


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

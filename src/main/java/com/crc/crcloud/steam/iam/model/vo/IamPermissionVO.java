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
 * @Date: 2019-11-29
 * @Description:
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel("")
public class IamPermissionVO{


        @ApiModelProperty("")
        private Long id;


        @ApiModelProperty("权限的标识")
        private String code;


        @ApiModelProperty("权限对应的api路径")
        private String path;


        @ApiModelProperty("请求的http方法")
        private String method;


        @ApiModelProperty("权限的层级")
        private String fdLevel;


        @ApiModelProperty("权限描述")
        private String description;


        @ApiModelProperty("权限对应的方法名")
        private String action;


        @ApiModelProperty("权限资源类型")
        private String fdResource;


        @ApiModelProperty("是否公开的权限")
        private Boolean publicAccess;


        @ApiModelProperty("是否需要登录才能访问的权限")
        private Boolean loginAccess;


        @ApiModelProperty("权限所在的服务名称")
        private String serviceName;


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


        @ApiModelProperty("是否为内部接口")
        private Boolean isWithin;



}

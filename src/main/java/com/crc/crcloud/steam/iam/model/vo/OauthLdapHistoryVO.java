package com.crc.crcloud.steam.iam.model.vo;

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
public class OauthLdapHistoryVO {


    @ApiModelProperty("")
    private Long id;


    @ApiModelProperty("ldap id")
    private Long ldapId;


    @ApiModelProperty("同步用户新增数量")
    private Integer newUserCount;


    @ApiModelProperty("同步用户更新数量")
    private Integer updateUserCount;


    @ApiModelProperty("同步用户失败数量")
    private Integer errorUserCount;


    @ApiModelProperty("同步开始时间")
    private Date syncBeginTime;


    @ApiModelProperty("同步结束时间")
    private Date syncEndTime;


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

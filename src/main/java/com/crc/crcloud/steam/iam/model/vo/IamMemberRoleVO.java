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
public class IamMemberRoleVO {


    @ApiModelProperty("")
    private Long id;


    @ApiModelProperty("角色id")
    private Long roleId;


    @ApiModelProperty("成员id,可以是userid,clientid等，与member_type对应")
    private Long memberId;


    @ApiModelProperty("成员类型，默认为user")
    private String memberType;


    @ApiModelProperty("创建该记录的源id，可以是projectid,也可以是organizarionid等")
    private Long sourceId;


    @ApiModelProperty("创建该记录的源类型，sit/organization/project/user等")
    private String sourceType;


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

package com.crc.crcloud.steam.iam.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@TableName("iam_application")
public class IamApplication {
    @TableId
    private Long id;

    private Long organizationId;

    private Long projectId;

    private String name;

    private String code;

//    @Column(name = "is_enabled")
    @TableField("is_enabled")
    private Boolean enabled;

    private String applicationCategory;

    private String applicationType;

//    @Transient
    @TableField(exist = false)
    private Integer appCount;
//    @Transient
    @TableField(exist = false)
    private String projectName;
//    @Transient
    @TableField(exist = false)
    private String projectCode;
//    @Transient
    @TableField(exist = false)
    private String imageUrl;

    @ApiModelProperty(hidden = true)
    private Long objectVersionNumber;
    @TableField(fill= FieldFill.INSERT)
    @ApiModelProperty(hidden = true)
    private Date creationDate;
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(hidden = true)
    private Long createdBy;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @ApiModelProperty(hidden = true)
    private Date lastUpdateDate;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @ApiModelProperty(hidden = true)
    private Long lastUpdatedBy;

}

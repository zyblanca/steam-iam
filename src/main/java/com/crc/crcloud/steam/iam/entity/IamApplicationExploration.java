package com.crc.crcloud.steam.iam.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("iam_application_exploration")
public class IamApplicationExploration {
//    @Id
//    @GeneratedValue
    @TableId
    private Long id;
    private Long applicationId;
    private String path;
    private Long rootId;
    private Long parentId;
    private String hashcode;
//    @Column(name = "is_enabled")
    @TableField("is_enabled")
    private Boolean enabled;

//    @Transient
    @TableField(exist = false)
    private String applicationName;
//    @Transient
    @TableField(exist = false)
    private String applicationCode;
//    @Transient
    @TableField(exist = false)
    private String applicationCategory;
//    @Transient
    @TableField(exist = false)
    private String applicationType;
//    @Transient
    @TableField(exist = false)
    private Boolean applicationEnabled;
//    @Transient
    @TableField(exist = false)
    private Long projectId;
//    @Transient
    @TableField(exist = false)
    private String projectCode;
//    @Transient
    @TableField(exist = false)
    private String projectName;
//    @Transient
    @TableField(exist = false)
    private String projectImageUrl;

    @ApiModelProperty(hidden = true)
    private Date creationDate;
    @ApiModelProperty(hidden = true)
    private Long createdBy;
    @ApiModelProperty(hidden = true)
    private Date lastUpdateDate;
    @ApiModelProperty(hidden = true)
    private Long lastUpdatedBy;
    @ApiModelProperty(hidden = true)
    private Long objectVersionNumber;
}

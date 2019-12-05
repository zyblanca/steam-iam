package com.crc.crcloud.steam.iam.model.dto;

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
public class IamApplicationDTO {

    private Long id;

    private Long organizationId;

    private Long projectId;

    private String name;

    private String code;

    private Boolean enabled;

    private String applicationCategory;

    private String applicationType;

    private Integer appCount;

    private String projectName;

    private String projectCode;

    private String imageUrl;

    private Long objectVersionNumber;

    private Date creationDate;

    private Long createdBy;

    private Date lastUpdateDate;

    private Long lastUpdatedBy;

}

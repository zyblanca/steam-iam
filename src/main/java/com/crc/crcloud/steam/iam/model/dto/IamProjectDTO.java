package com.crc.crcloud.steam.iam.model.dto;

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
public class IamProjectDTO {

    /**
    *
    */
    private Long id;

    /**
    * 项目名
    */
    private String name;

    /**
    * 项目编码
    */
    private String code;

    /**
    * 项目描述
    */
    private String description;

    /**
    * 组织编号
    */
    private Long organizationId;
    /**
     * 组织编号
     */
    private String organizationName;

    /**
    * 是否启用。1启用，0未启用
    */
    private Boolean isEnabled;




    /**
    * 项目图标url
    */
    private String imageUrl;

    /**
    *
    */
    private Long objectVersionNumber;

    /**
    *
    */
    private Long createdBy;

    /**
    *
    */
    private Date creationDate;

    /**
    *
    */
    private Long lastUpdatedBy;

    /**
    *
    */
    private Date lastUpdateDate;

    /**
    * 项目类型
    */
    private String type;

    /**
    * 项目类别：agile(敏捷项目),program(普通项目组),analytical(分析型项目群)
    */
    private String category;


    private Long userId;

    private String realName;

}

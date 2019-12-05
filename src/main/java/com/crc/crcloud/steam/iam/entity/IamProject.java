package com.crc.crcloud.steam.iam.entity;

import com.baomidou.mybatisplus.annotation.*;
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
@TableName("iam_project")
public class IamProject {

        /**
        *
        */
        @TableId(type = IdType.AUTO)
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
        @TableField(fill=FieldFill.INSERT)
        private Long createdBy;

        /**
        *
        */
        @TableField(fill=FieldFill.INSERT)
        private Date creationDate;

        /**
        *
        */
        @TableField(fill=FieldFill.INSERT_UPDATE)
        private Long lastUpdatedBy;

        /**
        *
        */
        @TableField(fill=FieldFill.INSERT_UPDATE)
        private Date lastUpdateDate;

        /**
        * 项目类型
        */
        private String type;

        /**
        * 项目类别：agile(敏捷项目),program(普通项目组),analytical(分析型项目群)
        */
        private String category;


}

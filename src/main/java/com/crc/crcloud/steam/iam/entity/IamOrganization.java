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
@TableName("iam_organization")
public class IamOrganization {

        /**
         *
         */
        @TableId(type = IdType.AUTO)
        private Long id;

        /**
         * 组织名
         */
        private String name;

        /**
         * 组织编码
         */
        private String code;

        /**
         * 组织描述
         */
        private String description;

        /**
         * 是否启用。1启用，0未启用
         */
        private Boolean isEnabled;

        /**
         * 是否为注册组织。1.是，0不是
         */
        private Boolean isRegister;

        /**
         * 创建用户的编号
         */
        private Long userId;

        /**
         * 组织的地址
         */
        private String address;

        /**
         * 组织图标url
         */
        private String imageUrl;

        /**
         * 组织规模。0：0-30,1：30-100,2：100
         */
        private Integer scale;

        /**
         *
         */
        private Long objectVersionNumber;

        /**
         *
         */
        @TableField(fill = FieldFill.INSERT)
        private Long createdBy;

        /**
         *
         */
        @TableField(fill = FieldFill.INSERT)
        private Date creationDate;

        /**
         *
         */
        @TableField(fill = FieldFill.INSERT_UPDATE)
        private Long lastUpdatedBy;

        /**
         *
         */
        @TableField(fill = FieldFill.INSERT_UPDATE)
        private Date lastUpdateDate;


}

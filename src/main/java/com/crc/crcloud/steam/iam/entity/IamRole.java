package com.crc.crcloud.steam.iam.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;
import lombok.Data;
import java.util.Date;
import java.lang.Integer;
import java.lang.Long;
import java.lang.Byte;

/**
 * @Author:
 * @Date: 2019-11-12
 * @Description: 
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("iam_role")
public class IamRole {

        /**
        * 
        */
        @TableId(type = IdType.AUTO)
        private Long id;

        /**
        * 角色名
        */
        private String name;

        /**
        * 角色编码
        */
        private String code;

        /**
        * 角色描述full description
        */
        private String description;

        /**
        * 角色级别
        */
        private String fdLevel;

        /**
        * 是否启用。1启用，0未启用
        */
        private Byte isEnabled;

        /**
        * 是否可以修改。1表示可以，0不可以
        */
        private Byte isModified;

        /**
        * 是否可以被禁用
        */
        private Byte isEnableForbidden;

        /**
        * 是否内置。1表示是，0表示不是
        */
        private Byte isBuiltIn;

        /**
        * 是否禁止在更高的层次上分配，禁止project role在organization上分配。1表示可以，0表示不可以
        */
        private Byte isAssignable;

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


}

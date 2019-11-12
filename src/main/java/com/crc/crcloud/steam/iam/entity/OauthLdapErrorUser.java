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
@TableName("oauth_ldap_error_user")
public class OauthLdapErrorUser {

        /**
        * 
        */
        @TableId(type = IdType.AUTO)
        private Long id;

        /**
        * ldap同步历史id
        */
        private Long ldapHistoryId;

        /**
        * ldap对象的唯一标识，可以根据此标识到ldap server查询详细信息
        */
        private String uuid;

        /**
        * 用户登录名
        */
        private String loginName;

        /**
        * 用户邮箱
        */
        private String email;

        /**
        * 真实姓名
        */
        private String realName;

        /**
        * 手机号
        */
        private String phone;

        /**
        * 失败原因
        */
        private String cause;

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

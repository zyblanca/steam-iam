package com.crc.crcloud.steam.iam.model.dto;

import java.util.Date;
import java.lang.Integer;
import java.lang.Long;
import java.lang.Byte;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;
import lombok.Data;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * @Author:
 * @Date: 2019-11-12
 * @Description: 
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OauthLdapDTO {

    /**
    * 
    */
    private Long id;

    /**
    * ldap的名称
    */
    private String name;

    /**
    * 组织id
    */
    private Long organizationId;

    /**
    * ldap服务器地址
    */
    private String serverAddress;

    /**
    * 端口号
    */
    private String port;

    /**
    * 
    */
    private String account;

    /**
    * ldap登陆密码
    */
    private String ldapPassword;

    /**
    * 使用ssl加密传输方式，默认情况为不使用
    */
    private Long useSsl;

    /**
    * 是否启用，默认为启用
    */
    private Long isEnabled;

    /**
    * 基础dn
    */
    private String baseDn;

    /**
    * 
    */
    private String directoryType;

    /**
    * 对象类型
    */
    private String objectClass;

    /**
    * 同步用户的自定义过滤配置
    */
    private String customFilter;

    /**
    * 同步用户每次发送saga的用户数量
    */
    private Integer sagaBatchSize;

    /**
    * ldap服务器连接超时时间，单位为秒，默认值为10秒
    */
    private Integer connectionTimeout;

    /**
    * ldap中唯一标识对象的字段
    */
    private String uuidField;

    /**
    * login_name对应的字段名
    */
    private String loginNameField;

    /**
    * real_name对应的字段名
    */
    private String realNameField;

    /**
    * email对应的字段名
    */
    private String emailField;

    /**
    * phone对应的字段名
    */
    private String phoneField;

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


}

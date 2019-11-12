package com.crc.crcloud.steam.iam.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
@ApiModel("")
public class OauthLdapVO{


        @ApiModelProperty("")
        private Long id;


        @ApiModelProperty("ldap的名称")
        private String name;


        @ApiModelProperty("组织id")
        private Long organizationId;


        @ApiModelProperty("ldap服务器地址")
        private String serverAddress;


        @ApiModelProperty("端口号")
        private String port;


        @ApiModelProperty("")
        private String account;


        @ApiModelProperty("ldap登陆密码")
        private String ldapPassword;


        @ApiModelProperty("使用ssl加密传输方式，默认情况为不使用")
        private Long useSsl;


        @ApiModelProperty("是否启用，默认为启用")
        private Long isEnabled;


        @ApiModelProperty("基础dn")
        private String baseDn;


        @ApiModelProperty("")
        private String directoryType;


        @ApiModelProperty("对象类型")
        private String objectClass;


        @ApiModelProperty("同步用户的自定义过滤配置")
        private String customFilter;


        @ApiModelProperty("同步用户每次发送saga的用户数量")
        private Integer sagaBatchSize;


        @ApiModelProperty("ldap服务器连接超时时间，单位为秒，默认值为10秒")
        private Integer connectionTimeout;


        @ApiModelProperty("ldap中唯一标识对象的字段")
        private String uuidField;


        @ApiModelProperty("login_name对应的字段名")
        private String loginNameField;


        @ApiModelProperty("real_name对应的字段名")
        private String realNameField;


        @ApiModelProperty("email对应的字段名")
        private String emailField;


        @ApiModelProperty("phone对应的字段名")
        private String phoneField;


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

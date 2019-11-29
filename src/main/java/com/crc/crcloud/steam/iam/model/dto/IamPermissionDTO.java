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
 * @Date: 2019-11-29
 * @Description: 
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IamPermissionDTO {

    /**
    * 
    */
    private Long id;

    /**
    * 权限的标识
    */
    private String code;

    /**
    * 权限对应的api路径
    */
    private String path;

    /**
    * 请求的http方法
    */
    private String method;

    /**
    * 权限的层级
    */
    private String fdLevel;

    /**
    * 权限描述
    */
    private String description;

    /**
    * 权限对应的方法名
    */
    private String action;

    /**
    * 权限资源类型
    */
    private String fdResource;

    /**
    * 是否公开的权限
    */
    private Byte publicAccess;

    /**
    * 是否需要登录才能访问的权限
    */
    private Byte loginAccess;

    /**
    * 权限所在的服务名称
    */
    private String serviceName;

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
    * 是否为内部接口
    */
    private Byte isWithin;


}

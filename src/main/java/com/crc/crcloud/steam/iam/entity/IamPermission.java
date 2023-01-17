package com.crc.crcloud.steam.iam.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Author:
 * @Date: 2019-11-29
 * @Description:
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("iam_permission")
public class IamPermission {

    /**
     *
     */
    @TableId(type = IdType.AUTO)
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
    private Boolean publicAccess;

    /**
     * 是否需要登录才能访问的权限
     */
    private Boolean loginAccess;

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

    /**
     * 是否为内部接口
     */
    private Boolean isWithin;


}

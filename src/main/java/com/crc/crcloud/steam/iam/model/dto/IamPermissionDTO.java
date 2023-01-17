package com.crc.crcloud.steam.iam.model.dto;

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
    private Boolean isWithin;

    @Override
    public String toString() {
        return "IamPermissionDTO{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", path='" + path + '\'' +
                ", method='" + method + '\'' +
                ", fdLevel='" + fdLevel + '\'' +
                ", description='" + description + '\'' +
                ", action='" + action + '\'' +
                ", fdResource='" + fdResource + '\'' +
                ", publicAccess=" + publicAccess +
                ", loginAccess=" + loginAccess +
                ", serviceName='" + serviceName + '\'' +
                ", isWithin=" + isWithin +
                '}';
    }
}

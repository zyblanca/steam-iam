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
public class IamRoleDTO {

    /**
    *
    */
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
    private Boolean isEnabled;

    /**
    * 是否可以修改。1表示可以，0不可以
    */
    private Boolean isModified;

    /**
    * 是否可以被禁用
    */
    private Boolean isEnableForbidden;

    /**
    * 是否内置。1表示是，0表示不是
    */
    private Boolean isBuiltIn;

    /**
    * 是否禁止在更高的层次上分配，禁止project role在organization上分配。1表示可以，0表示不可以
    */
    private Boolean isAssignable;

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

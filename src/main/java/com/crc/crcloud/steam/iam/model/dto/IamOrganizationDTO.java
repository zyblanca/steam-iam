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
public class IamOrganizationDTO {

    /**
    * 
    */
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
    private Byte isEnabled;

    /**
    * 是否为注册组织。1.是，0不是
    */
    private Byte isRegister;

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
    private Byte scale;

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

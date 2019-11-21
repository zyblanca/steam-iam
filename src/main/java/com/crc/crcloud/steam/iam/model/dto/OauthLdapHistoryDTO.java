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
public class OauthLdapHistoryDTO {

    /**
    * 
    */
    private Long id;

    /**
    * ldap id
    */
    private Long ldapId;

    /**
    * 同步用户新增数量
    */
    private Integer newUserCount;

    /**
    * 同步用户更新数量
    */
    private Integer updateUserCount;

    /**
    * 同步用户失败数量
    */
    private Integer errorUserCount;

    /**
    * 同步开始时间
    */
    private Date syncBeginTime;

    /**
    * 同步结束时间
    */
    private Date syncEndTime;

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

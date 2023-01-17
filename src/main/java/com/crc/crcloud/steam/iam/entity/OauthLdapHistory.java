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
@TableName("oauth_ldap_history")
public class OauthLdapHistory {

    /**
     *
     */
    @TableId(type = IdType.AUTO)
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


}

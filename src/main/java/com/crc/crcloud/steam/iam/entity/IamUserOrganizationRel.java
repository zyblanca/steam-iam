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
@TableName("iam_user_organization_rel")
public class IamUserOrganizationRel {

    /**
     *
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户编号
     */
    private Long userId;

    /**
     * 组织编号
     */
    private Long organizationId;

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

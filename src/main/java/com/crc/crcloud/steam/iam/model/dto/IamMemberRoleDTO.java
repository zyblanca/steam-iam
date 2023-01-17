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
public class IamMemberRoleDTO {

    /**
     *
     */
    private Long id;

    /**
     * 角色id
     */
    private Long roleId;

    /**
     * 成员id,可以是userid,clientid等，与member_type对应
     */
    private Long memberId;

    /**
     * 成员类型，默认为user
     */
    private String memberType;

    /**
     * 创建该记录的源id，可以是projectid,也可以是organizarionid等
     */
    private Long sourceId;

    /**
     * 创建该记录的源类型，sit/organization/project/user等
     */
    private String sourceType;

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

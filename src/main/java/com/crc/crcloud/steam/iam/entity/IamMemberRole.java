package com.crc.crcloud.steam.iam.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.crc.crcloud.steam.iam.common.enums.MemberType;
import io.choerodon.core.iam.ResourceLevel;
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
@TableName("iam_member_role")
public class IamMemberRole {

        /**
         *
        */
        @TableId(type = IdType.AUTO)
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
         * @see MemberType#getValue()
         */
        private String memberType;

        /**
        * 创建该记录的源id，可以是projectid,也可以是organizarionid等
        */
        private Long sourceId;

        /**
        * 创建该记录的源类型，sit/organization/project/user等
         * @see ResourceLevel#value()
         */
        private String sourceType;

        /**
         *
        */
        private Long objectVersionNumber;

        /**
         *
        */
        @TableField(fill=FieldFill.INSERT)
        private Long createdBy;

        /**
         *
        */
        @TableField(fill=FieldFill.INSERT)
        private Date creationDate;

        /**
         *
        */
        @TableField(fill=FieldFill.INSERT_UPDATE)
        private Long lastUpdatedBy;

        /**
         *
        */
        @TableField(fill=FieldFill.INSERT_UPDATE)
        private Date lastUpdateDate;


}

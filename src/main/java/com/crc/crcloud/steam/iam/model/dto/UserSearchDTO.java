package com.crc.crcloud.steam.iam.model.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * 人员查询条件
 */
@Getter
@Setter
public class UserSearchDTO {


    private String loginName;

    private String realName;

    private Long projectId;

    private String memberSourceType;

    private String memberType;

    private Long organizationId;

    private Long id;

    private String email;

    private String param;
}

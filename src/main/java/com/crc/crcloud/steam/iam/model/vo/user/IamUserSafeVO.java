package com.crc.crcloud.steam.iam.model.vo.user;

import cn.hutool.core.bean.BeanUtil;
import com.crc.crcloud.steam.iam.model.dto.IamUserDTO;
import com.crc.crcloud.steam.iam.model.vo.IamUserVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * 可以安全返回的用户数据信息
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IamUserSafeVO {

    public IamUserSafeVO(@NotNull IamUserVO vo) {
        BeanUtil.copyProperties(vo, this);
    }

    public IamUserSafeVO(@NotNull IamUserDTO dto) {
        BeanUtil.copyProperties(dto, this);
    }

    @ApiModelProperty("")
    private Long id;


    @ApiModelProperty("用户名")
    private String loginName;


    @ApiModelProperty("电子邮箱地址")
    private String email;

    @ApiModelProperty("用户真实姓名")
    private String realName;


    @ApiModelProperty("手机号")
    private String phone;


    @ApiModelProperty("国际电话区号。")
    private String internationalTelCode;


    @ApiModelProperty("用户头像地址")
    private String imageUrl;


    @ApiModelProperty("用户二进制头像")
    private String profilePhoto;


    @ApiModelProperty("语言")
    private String language;


    @ApiModelProperty("时区")
    private String timeZone;

    @ApiModelProperty("用户是否启用。1启用，0未启用")
    private Boolean isEnabled;


    @ApiModelProperty("是否锁定账户")
    private Boolean isLocked;


    @ApiModelProperty("是否是ldap来源。1是，0不是")
    private Boolean isLdap;


    @ApiModelProperty("是否为管理员用户。1表示是，0表示不是")
    private Byte isAdmin;

    @ApiModelProperty("")
    private Long objectVersionNumber;


    @ApiModelProperty("")
    private Long createdBy;


    @ApiModelProperty("")
    private Date creationDate;


    @ApiModelProperty("")
    private Long lastUpdatedBy;


    @ApiModelProperty("")
    private Date lastUpdateDate;
}

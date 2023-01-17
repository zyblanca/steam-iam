package com.crc.crcloud.steam.iam.model.vo.site;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.crc.crcloud.steam.iam.model.dto.IamUserDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * 平台管理用户列表
 *
 * @author LiuYang
 * @date 2019/12/16
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SiteManagerUserResponseVO {
    @NotNull
    @ApiModelProperty("登录名")
    private String loginName;

    @NotNull
    @ApiModelProperty("用户名")
    private String realName;

    public static SiteManagerUserResponseVO instance(IamUserDTO iamUser) {
        SiteManagerUserResponseVO entity = new SiteManagerUserResponseVO();
        BeanUtil.copyProperties(iamUser, entity, CopyOptions.create().ignoreNullValue());
        return entity;
    }
}

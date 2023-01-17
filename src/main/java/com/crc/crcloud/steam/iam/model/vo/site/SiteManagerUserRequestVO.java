package com.crc.crcloud.steam.iam.model.vo.site;

import com.crc.crcloud.steam.iam.model.vo.PageRequestVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.annotation.Nullable;

/**
 * 平台管理用户列表
 *
 * @author LiuYang
 * @date 2019/12/16
 */
@ToString
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SiteManagerUserRequestVO extends PageRequestVO {
    @Nullable
    @ApiModelProperty("正序字段")
    private String asc;

    @Nullable
    @ApiModelProperty("倒序字段")
    private String desc;

}

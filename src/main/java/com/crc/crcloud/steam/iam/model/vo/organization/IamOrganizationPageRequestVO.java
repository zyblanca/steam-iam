package com.crc.crcloud.steam.iam.model.vo.organization;

import com.crc.crcloud.steam.iam.model.vo.PageRequestVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.annotation.Nullable;
import java.util.Set;

/**
 *
 * @author LiuYang
 * @date 2019/12/3
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IamOrganizationPageRequestVO extends PageRequestVO {
    @Nullable
    @ApiModelProperty("组织名,模糊搜索")
    private String name;

    @Nullable
    @ApiModelProperty("组织编码,模糊搜索")
    private String code;

    @Nullable
    @ApiModelProperty("禁用启用状态")
    private Set<Boolean> isEnables;

    @Nullable
    @ApiModelProperty("正序字段")
    private String asc;

    @Nullable
    @ApiModelProperty("倒序字段")
    private String desc;
}

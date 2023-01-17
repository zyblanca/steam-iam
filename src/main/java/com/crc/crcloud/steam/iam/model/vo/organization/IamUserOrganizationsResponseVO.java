package com.crc.crcloud.steam.iam.model.vo.organization;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.crc.crcloud.steam.iam.model.dto.IamOrganizationDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

/**
 * 用户所授权组织列表
 *
 * @author LiuYang
 * @date 2019/12/5
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IamUserOrganizationsResponseVO {
    @NotNull
    @ApiModelProperty("组织列表")
    private List<IamUserOrganizationResponse> organizationList;

    @Nullable
    @ApiModelProperty("当前组织编号")
    private Long currentOrganization;

    @Nullable
    @ApiModelProperty("当前组织名称")
    private String currentOrganizationName;

    public static IamUserOrganizationResponse instance(IamOrganizationDTO iamOrganization) {
        IamUserOrganizationResponse entity = new IamUserOrganizationResponse();
        BeanUtil.copyProperties(iamOrganization, entity, CopyOptions.create().ignoreNullValue());
        entity.setCurrent(BigDecimal.ZERO.toPlainString());
        return entity;
    }

    @Data
    public static class IamUserOrganizationResponse {
        IamUserOrganizationResponse() {
        }

        @ApiModelProperty("组织编号")
        private Long id;
        @ApiModelProperty("组织名称")
        private String name;
        @ApiModelProperty("组织编码")
        private String code;

        @ApiModelProperty("是否为当前：1是当前组织,反之0")
        private String current;

        @ApiModelProperty("是否启用;true启用")
        private Boolean isEnabled;

        @ApiModelProperty("组织图标")
        private String imageUrl;
    }
}

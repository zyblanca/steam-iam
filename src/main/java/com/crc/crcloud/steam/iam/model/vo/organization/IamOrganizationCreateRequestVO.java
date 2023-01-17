package com.crc.crcloud.steam.iam.model.vo.organization;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

/**
 * 组织创建请求参数
 *
 * @author LiuYang
 * @date 2019/12/4
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IamOrganizationCreateRequestVO {
    @NotBlank
    @Length(max = 30, message = "organization.name.length")
    @ApiModelProperty("组织名")
    private String name;
    /**
     * 最多15个字符，编码只能由小写字母、数字、"-"组成，且以小写字母开头，不能以"-"结尾且不能连续出现两个"-"
     */
    @NotBlank
    @Length(max = 15, message = "organization.code.length")
    @ApiModelProperty("组织编码")
    private String code;

    @NotBlank
    @ApiModelProperty(value = "组织图标URL", hidden = true)
    private String imageUrl;
}

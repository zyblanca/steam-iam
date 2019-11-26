package com.crc.crcloud.steam.iam.model.vo.organization;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.annotation.Nullable;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.Optional;

/**
 * 修改组织请求参数
 * @author LiuYang
 * @date 2019/11/26
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IamOrganizationUpdateRequestVO {

    @ApiModelProperty(value = "组织名/必填")
    @NotEmpty(message = "error.organization.name.empty")
    @Size(min = 1, max = 32, message = "error.organization.name.size")
    private String name;

    @ApiModelProperty(value = "乐观锁版本号")
    private Long objectVersionNumber;

    @ApiModelProperty(value = "是否启用/非必填,此属性为了保证旧接口兼容性,请使用isEnabled")
    private Boolean enabled;

    @ApiModelProperty(value = "组织图标url,增量更新")
    private String imageUrl;

    @ApiModelProperty("是否启用,和enabled优先取isEnabled,都没设置时,不修改此值")
    private Boolean isEnabled;

    @Nullable
    @ApiModelProperty("描述,增量更新")
    private String description;


    @Nullable
    public Boolean getIsEnabled() {
        Boolean value = Optional.ofNullable(isEnabled).orElse(enabled);
        setEnabled(value);
        setIsEnabled(value);
        return value;
    }

    @Nullable
    public Boolean getEnabled() {
        return getIsEnabled();
    }

}

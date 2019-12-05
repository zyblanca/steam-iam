package com.crc.crcloud.steam.iam.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IamApplicationVO {

    private static final String CODE_REGULAR_EXPRESSION = "^[a-z]([-a-z0-9]*[a-z0-9])?$";

    private Long id;

    private Long organizationId;

    private Long projectId;

    @Length(min = 1, max = 20, message = "error.application.name.length")
    @NotEmpty(message = "error.application.name.empty")
    private String name;

    //@Pattern(regexp = CODE_REGULAR_EXPRESSION, message = "error.application.code.illegal")
    @NotEmpty(message = "error.application.code.empty")
    private String code;

    private Boolean enabled;

    @NotEmpty(message = "error.application.applicationCategory.empty")
    private String applicationCategory;

    @NotEmpty(message = "error.application.applicationType.empty")
    private String applicationType;

    private Integer appCount;

    private Long objectVersionNumber;

    private String param;

    @ApiModelProperty(value = "发送saga事件，标记从哪里调用的")
    private String from;

    private String projectName;

    private String projectCode;

    private String imageUrl;

}

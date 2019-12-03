package com.crc.crcloud.steam.iam.model.dto.organization;

import com.crc.crcloud.steam.iam.model.dto.IamOrganizationDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.ToString;

/**
 * 组织信息带项目个数
 * @author LiuYang
 * @date 2019/12/3
 */
@ToString
public class IamOrganizationWithProjectCountDTO extends IamOrganizationDTO {

    @ApiModelProperty("项目数量")
    private Integer projectCount;

    public Integer getProjectCount() {
        return projectCount;
    }

    public void setProjectCount(Integer projectCount) {
        this.projectCount = projectCount;
    }
}

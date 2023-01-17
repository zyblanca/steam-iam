package com.crc.crcloud.steam.iam.model.vo.organization;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * @author LiuYang
 * @date 2019/12/5
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IamUserCurrentOrganizationUpdateRequestVO {
    @Min(1)
    @NotNull
    private Long currentOrganizationId;
}

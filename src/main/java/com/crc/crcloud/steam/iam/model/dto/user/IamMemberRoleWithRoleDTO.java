package com.crc.crcloud.steam.iam.model.dto.user;

import com.crc.crcloud.steam.iam.model.dto.IamMemberRoleDTO;
import com.crc.crcloud.steam.iam.model.dto.IamRoleDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * @author LiuYang
 * @date 2019/11/25
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IamMemberRoleWithRoleDTO {
    @NotNull
    private IamMemberRoleDTO iamMemberRole;
    @NotNull
    private IamRoleDTO role;
}

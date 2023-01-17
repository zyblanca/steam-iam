package com.crc.crcloud.steam.iam.model.dto.user;

import com.crc.crcloud.steam.iam.model.dto.IamUserDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

/**
 * @author LiuYang
 * @date 2019/11/27
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IamUserCreateWithPasswordDTO {
    @NotNull
    private IamUserDTO user;
    /**
     * 明文密码
     */
    @Nullable
    private String rawPassword;
}

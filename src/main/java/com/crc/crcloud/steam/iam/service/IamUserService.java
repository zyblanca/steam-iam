package com.crc.crcloud.steam.iam.service;


import com.crc.crcloud.steam.iam.model.dto.IamUserDTO;
import com.crc.crcloud.steam.iam.model.vo.user.IamUserCreateRequestVO;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Set;

/**
 * @Author LiuYang
 * @Description 用户
 * @Date 2019-11-12
 */
public interface IamUserService {
    /**
     * 手动添加组织成员
     * @param vo 用户信息
     * @param organizationIds 所在组织
     * @return 用户
     */
    @NotNull
    IamUserDTO createUserByManual(@Valid IamUserCreateRequestVO vo, @NotEmpty Set<Long> organizationIds);
}

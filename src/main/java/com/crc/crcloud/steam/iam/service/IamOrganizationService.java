package com.crc.crcloud.steam.iam.service;


import com.crc.crcloud.steam.iam.model.dto.IamOrganizationDTO;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @Author
 * @Description
 * @Date 2019-11-12
 */
public interface IamOrganizationService {
    /**
     * 获取用户所属组织
     * @param userId 用户编号
     * @return 用户所属组织列表，按照关联的时间升序
     */
    @NotNull
    List<IamOrganizationDTO> getUserOrganizations(@NotNull Long userId);

}

package com.crc.crcloud.steam.iam.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.crc.crcloud.steam.iam.common.utils.PageUtil;
import com.crc.crcloud.steam.iam.entity.IamUser;
import com.crc.crcloud.steam.iam.model.dto.IamUserDTO;
import com.crc.crcloud.steam.iam.model.vo.IamUserVO;
import com.crc.crcloud.steam.iam.model.vo.user.IamOrganizationUserPageRequestVO;
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
     *
     * @param vo              用户信息
     * @param organizationIds 所在组织
     * @return 用户
     */
    @NotNull
    IamUserDTO createUserByManual(@Valid IamUserCreateRequestVO vo, @NotEmpty Set<Long> organizationIds);

    IamUserDTO getAndThrow(@NotNull Long userId);

    /**
     * 分页查询组织成员
     * <p>复合条件用户都会查询出来，不区分是否禁用开启等情况</p>
     *
     * @param organizationId 组织ID
     * @param vo             查询属性与分页参数
     * @return 分页数据
     */
    IPage<IamUserVO> pageQueryOrganizationUser(@NotNull Long organizationId, @Valid IamOrganizationUserPageRequestVO vo);

    /**
     * 分页查询指定项目下的成员信息
     *
     * @param projectId 项目id
     * @param iamUserVO 人员查询参数
     * @param page      分页信息
     * @return 人员信息
     */
    IPage<IamUserVO> pageByProject(Long projectId, IamUserVO iamUserVO, PageUtil page);
}

package com.crc.crcloud.steam.iam.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.crc.crcloud.steam.iam.common.config.ChoerodonDevOpsProperties;
import com.crc.crcloud.steam.iam.common.exception.IamAppCommException;
import com.crc.crcloud.steam.iam.model.dto.IamOrganizationDTO;
import com.crc.crcloud.steam.iam.model.dto.organization.IamOrganizationWithProjectCountDTO;
import com.crc.crcloud.steam.iam.model.vo.organization.IamOrganizationCreateRequestVO;
import com.crc.crcloud.steam.iam.model.vo.organization.IamOrganizationPageRequestVO;
import com.crc.crcloud.steam.iam.model.vo.organization.IamOrganizationUpdateRequestVO;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

/**
 * @Author
 * @Description
 * @Date 2019-11-12
 */
public interface IamOrganizationService {
    /**
     * 组织code校验正则
     */
    String CODE_REGULAR_EXPRESSION
            = "^[a-z](([a-z0-9]|-(?!-))*[a-z0-9])*$";

    /**
     * 获取用户所属组织
     * @param userId 用户编号
     * @return 用户所属组织列表，按照关联的时间升序
     */
    @NotNull
    List<IamOrganizationDTO> getUserOrganizations(@NotNull Long userId);

    /**
     * 全局层修改组织
     * <p>修改后会根据配置{@link ChoerodonDevOpsProperties#isMessage()}来决定是否发送saga事件</p>
     * @param id 组织编号
     * @param vo 修改属性参数
     * @return 修改后的组织
     */
    @NotNull
    IamOrganizationDTO updateBySite(@NotNull @Min(1) Long id, @NotNull @Valid IamOrganizationUpdateRequestVO vo);

    /**
     * 组织层修改组织
     * <p>修改后会根据配置{@link ChoerodonDevOpsProperties#isMessage()}来决定是否发送saga事件</p>
     * @param id 组织编号
     * @param vo 修改属性参数
     * @return 修改后的组织
     */
    @NotNull
    IamOrganizationDTO updateByOrganization(@NotNull @Min(1) Long id, @NotNull @Valid IamOrganizationUpdateRequestVO vo);

    /**
     * 查找组织-不存在则报错
     * @param id 组织编号
     * @return 组织
     */
    Optional<IamOrganizationDTO> get(@NotNull @Min(1) Long id);

    /**
     * 查找组织-不存在则报错
     * @param id 组织编号
     * @return 组织
     */
    default IamOrganizationDTO getAndThrow(@NotNull @Min(1) Long id) {
        return get(id).orElseThrow(() -> new IamAppCommException("organization.data.empty"));
    }

    /**
     * 禁用启用组织
     * <p>会发出事件{@link com.crc.crcloud.steam.iam.model.event.IamOrganizationToggleEnableEvent}</p>
     * <p>warn:如果当前已经是对应要处理的状态，则不会进行更新，也不会发出上述事件</p>
     * @param id 组织编号
     * @param userId 操作人,主要用作消息通知
     * @param isEnable true:启用组织，false禁用组织
     */
    void toggleEnable(@NotNull Long id, @NotNull Boolean isEnable, Long userId);

    /**
     * 分页查询
     * @param vo 查询条件
     * @return 数据结果
     */
    @NotNull
    IPage<IamOrganizationWithProjectCountDTO> page(@NotNull @Valid IamOrganizationPageRequestVO vo);

    /**
     * 创建组织
     * @param vo
     * @return
     */
    IamOrganizationDTO create(@NotNull @Valid IamOrganizationCreateRequestVO vo);
}

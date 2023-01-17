package com.crc.crcloud.steam.iam.service;


import com.crc.crcloud.steam.iam.model.vo.IamApplicationVO;

public interface IamApplicationService {


    /**
     * 启用应用
     *
     * @param applicationId
     * @return
     */
    IamApplicationVO enableApplication(Long applicationId);

    /**
     * 禁用应用
     *
     * @param applicationId
     * @return
     */
    IamApplicationVO disableApplication(Long applicationId);

    /**
     * 创建应用
     *
     * @param iamApplicationVO
     * @return
     */
    IamApplicationVO createApplication(IamApplicationVO iamApplicationVO);

    /**
     * 更改应用
     *
     * @param iamApplicationVO
     * @return
     */
    IamApplicationVO updateApplication(IamApplicationVO iamApplicationVO);

    /**
     * 删除steam-iam服务应用
     *
     * @param organizationId 组织id
     * @param steamProjectId 项目id
     * @param code           应用code
     * @return
     */
    void deleteApplication(Long organizationId, Long steamProjectId, String code);
}

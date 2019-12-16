package com.crc.crcloud.steam.iam.service;


import com.crc.crcloud.steam.iam.model.vo.IamApplicationVO;

public interface IamApplicationService {


    /**
     * 启用应用
     * @param applicationId
     * @return
     */
    IamApplicationVO enableApplication(Long applicationId);

    /**
     * 禁用应用
     * @param applicationId
     * @return
     */
    IamApplicationVO disableApplication(Long applicationId);

    /**
     * 创建应用
     * @param iamApplicationVO
     * @return
     */
    IamApplicationVO createApplication(IamApplicationVO iamApplicationVO);
}

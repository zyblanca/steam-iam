package com.crc.crcloud.steam.iam.service;

import com.crc.crcloud.steam.iam.model.dto.payload.ApplicationPayload;

/**
 * 持续集成应用业务服务类
 *
 * @author XIAXINYU3
 * @date 2019.7.31
 */
public interface SteamCiApplicationService {
    /**
     * 处理应用名称
     *
     * @param payload 应用实体类
     */
    void processName(ApplicationPayload payload);

    /**
     * 处理应用状态
     *
     * @param payload 应用实体类
     */
    void processStatus(ApplicationPayload payload);
}

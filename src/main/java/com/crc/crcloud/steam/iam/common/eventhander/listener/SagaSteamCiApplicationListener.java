package com.crc.crcloud.steam.iam.common.eventhander.listener;


import com.crc.crcloud.steam.iam.common.utils.JsonUtils;
import com.crc.crcloud.steam.iam.model.dto.payload.ApplicationPayload;
import com.crc.crcloud.steam.iam.service.SteamCiApplicationService;
import io.choerodon.asgard.saga.annotation.SagaTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SagaSteamCiApplicationListener {

    private static final String APPLICATION_STATUS_SAGA_CODE = "steam-ci-application-status-sync-service";
    private static final String APPLICATION_NAME_SAGA_CODE = "steam-ci-application-name-sync-service";

    @Autowired
    private SteamCiApplicationService steamCiApplicationService;

    @SagaTask(code = "steam-iam-ServiceApplicationStatusSync", description = "CI应用 状态 同步服务",
            sagaCode = APPLICATION_STATUS_SAGA_CODE,
            maxRetryCount = 1, seq = 2)
    public void processStatus(String data) {
        log.info("CI应用状态同步服务请求参数：{}", data);
        ApplicationPayload payload = JsonUtils.parse(data, ApplicationPayload.class);
        steamCiApplicationService.processStatus(payload);
        log.info("完成CI应用状态同步服务");
    }

    @SagaTask(code = "steam-iam-ServiceApplicationNameSync", description = "CI应用 名称 同步服务",
            sagaCode = APPLICATION_NAME_SAGA_CODE,
            maxRetryCount = 1, seq = 2)
    public void processName(String data) {
        log.info("CI应用名称同步服务请求参数：{}", data);
        ApplicationPayload payload = JsonUtils.parse(data, ApplicationPayload.class);
        steamCiApplicationService.processName(payload);
        log.info("完成CI应用名称同步服务");
    }
}

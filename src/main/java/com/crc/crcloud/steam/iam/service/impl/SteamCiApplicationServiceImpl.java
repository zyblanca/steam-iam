package com.crc.crcloud.steam.iam.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.crc.crcloud.steam.iam.dao.IamApplicationMapper;
import com.crc.crcloud.steam.iam.entity.IamApplication;
import com.crc.crcloud.steam.iam.model.dto.payload.ApplicationPayload;
import com.crc.crcloud.steam.iam.service.SteamCiApplicationService;
import io.choerodon.core.exception.CommonException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Slf4j
@Service
public class SteamCiApplicationServiceImpl implements SteamCiApplicationService {

    private static final Integer APPLICATION_ENABLE = 1;

    @Autowired
    private IamApplicationMapper applicationMapper;

    @Override
    public void processName(ApplicationPayload payload) {
        IamApplication rawApplication = convert2Application(payload);
        // 根据 projectId 和 code 查询
        IamApplication legacyApplication = applicationMapper.selectOne(new QueryWrapper(rawApplication));
        if (Objects.isNull(legacyApplication)) {
            throw new CommonException(String.format("找不到应用, applicationCode=%s, projectId=%d", payload.getApplicationCode(), payload.getSteamProjectId()));
        }
        log.info("更新应用名称，applicationId={}，originalApplicationName={}, updatedApplicationName={}", legacyApplication.getId(), legacyApplication.getName(), payload.getApplicationName());
        applicationMapper.updateApplicationName(legacyApplication.getId(), payload.getApplicationName());
    }

    @Override
    public void processStatus(ApplicationPayload payload) {
        IamApplication raeApplication = convert2Application(payload);
        // 根据 projectId 和 code 查询
        IamApplication legacyApplication = applicationMapper.selectOne(new QueryWrapper<>(raeApplication));
        if (Objects.isNull(legacyApplication)) {
            throw new CommonException(String.format("找不到应用, applicationCode=%s, projectId=%d", payload.getApplicationCode(), payload.getSteamProjectId()));
        }
        boolean active = payload.getStatus().intValue() == APPLICATION_ENABLE.intValue();
        log.info("更新应用状态，applicationId={}，oldStatus={}, updatedStatus={}", legacyApplication.getId(), legacyApplication.getEnabled(), active);
        applicationMapper.updateApplicationEnabled(legacyApplication.getId(), payload.getStatus());
    }

    /**
     * 将 ApplicationPayload 转化为 IamApplication
     * @param payload 更新对象
     * @return 返回的 IamApplication 只有 projectId 和 code
     */
    private IamApplication convert2Application(ApplicationPayload payload){
        return IamApplication.builder()
                .projectId(payload.getSteamProjectId())
                .code(payload.getApplicationCode())
                .build();
    }

}

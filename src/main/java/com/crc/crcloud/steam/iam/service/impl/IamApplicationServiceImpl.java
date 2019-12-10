package com.crc.crcloud.steam.iam.service.impl;

import com.crc.crcloud.steam.iam.common.exception.IamAppCommException;
import com.crc.crcloud.steam.iam.dao.IamApplicationExplorationMapper;
import com.crc.crcloud.steam.iam.dao.IamApplicationMapper;
import com.crc.crcloud.steam.iam.entity.IamApplication;
import com.crc.crcloud.steam.iam.service.IamApplicationService;
import io.choerodon.asgard.saga.producer.StartSagaBuilder;
import io.choerodon.asgard.saga.producer.TransactionalProducer;
import io.choerodon.core.iam.ResourceLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IamApplicationServiceImpl implements IamApplicationService {

    @Autowired
    private IamApplicationMapper iamApplicationMapper;
    @Autowired
    private IamApplicationExplorationMapper iamApplicationExplorationMapper;
    @Autowired
    private TransactionalProducer producer;


    /**
     * 发送 Saga 事务
     * @param iamApplication
     * @param sagaCode
     * @return IamApplication
     */
    private IamApplication sendSagaEvent(IamApplication iamApplication, String sagaCode){
        return producer.applyAndReturn(
                StartSagaBuilder
                        .newBuilder()
                        .withLevel(ResourceLevel.ORGANIZATION)
                        .withRefType("application")
                        .withSagaCode(sagaCode),
                builder -> {
                    IamApplication application = getAndUpdate(iamApplication);
                    builder
                            .withPayloadAndSerialize(application)
                            .withRefId(String.valueOf(application.getId()))
                            .withSourceId(application.getOrganizationId());
                    return application;
                });
    }

    /**
     * 更新并且获得更新后应用数据
     * @param iamApplication
     * @return
     */
    private IamApplication getAndUpdate(IamApplication iamApplication){
        if (1 != iamApplicationMapper.updateById(iamApplication)){
            throw new IamAppCommException("error.application.update");
        }
        return iamApplicationMapper.selectById(iamApplication.getId());
    }

}

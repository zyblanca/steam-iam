package com.crc.crcloud.steam.iam.service.impl;

import com.crc.crcloud.steam.iam.common.config.ChoerodonDevOpsProperties;
import com.crc.crcloud.steam.iam.common.enums.ApplicationCategory;
import com.crc.crcloud.steam.iam.common.exception.IamAppCommException;
import com.crc.crcloud.steam.iam.common.utils.AssertHelper;
import com.crc.crcloud.steam.iam.dao.IamApplicationExplorationMapper;
import com.crc.crcloud.steam.iam.dao.IamApplicationMapper;
import com.crc.crcloud.steam.iam.entity.IamApplication;
import com.crc.crcloud.steam.iam.model.dto.IamApplicationDTO;
import com.crc.crcloud.steam.iam.model.vo.IamApplicationVO;
import com.crc.crcloud.steam.iam.service.IamApplicationService;
import io.choerodon.asgard.saga.annotation.Saga;
import io.choerodon.asgard.saga.producer.StartSagaBuilder;
import io.choerodon.asgard.saga.producer.TransactionalProducer;
import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.iam.ResourceLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.crc.crcloud.steam.iam.common.utils.SagaTopic.Application.APP_DISABLE;
import static com.crc.crcloud.steam.iam.common.utils.SagaTopic.Application.APP_ENABLE;

@Service
public class IamApplicationServiceImpl implements IamApplicationService {

    private static final Long PROJECT_DOES_NOT_EXIST_ID = 0L;

    @Autowired
    private IamApplicationMapper iamApplicationMapper;
    @Autowired
    private IamApplicationExplorationMapper iamApplicationExplorationMapper;
    @Autowired
    private TransactionalProducer producer;
    @Autowired
    private AssertHelper assertHelper;
    @Autowired
    private ChoerodonDevOpsProperties choerodonDevOpsProperties;

    @Saga(code = APP_ENABLE, description = "steam-iam 启用应用", inputSchemaClass = IamApplication.class)
    @Override
    public IamApplicationVO enableApplication(Long applicationId) {
        return toggleStatus(applicationId, true, APP_ENABLE);
    }

    @Saga(code = APP_DISABLE, description = "steam-iam 禁用应用", inputSchemaClass = IamApplication.class)
    @Override
    public IamApplicationVO disableApplication(Long applicationId) {
        return toggleStatus(applicationId, false, APP_DISABLE);
    }

    private IamApplicationVO toggleStatus(Long applicationId, Boolean status, String sagaCode) {
        IamApplication iamApplication = assertHelper.applicationNotExisted(applicationId);
        iamApplication.setEnabled(status);
        String combination = ApplicationCategory.COMBINATION.code();
        boolean sendSagaEvent =
                (!combination.equals(iamApplication.getApplicationCategory())
                        && !PROJECT_DOES_NOT_EXIST_ID.equals(iamApplication.getProjectId())
                        && choerodonDevOpsProperties.isMessage());
        if (sendSagaEvent){
            IamApplication update = updateAndGet(iamApplication);
            sendSagaEvent(update, sagaCode);
            return entityToVo(iamApplication);
        } else {
            IamApplication update = updateAndGet(iamApplication);
            return entityToVo(update);
        }
    }

    private IamApplicationVO entityToVo(IamApplication iamApplication){
        IamApplicationDTO dto = ConvertHelper.convert(iamApplication, IamApplicationDTO.class);
        return ConvertHelper.convert(dto, IamApplicationVO.class);
    }


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
                    builder
                            .withPayloadAndSerialize(iamApplication)
                            .withRefId(String.valueOf(iamApplication.getId()))
                            .withSourceId(iamApplication.getOrganizationId());
                    return iamApplication;
                });
    }

    /**
     * 更新并且获得更新后应用数据
     * @param iamApplication
     * @return
     */
    private IamApplication updateAndGet(IamApplication iamApplication){
        if (1 != iamApplicationMapper.updateById(iamApplication)){
            throw new IamAppCommException("error.application.update");
        }
        return iamApplicationMapper.selectById(iamApplication.getId());
    }

}

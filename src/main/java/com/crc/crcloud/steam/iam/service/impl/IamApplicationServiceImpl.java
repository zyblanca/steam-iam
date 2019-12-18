package com.crc.crcloud.steam.iam.service.impl;

import com.crc.crcloud.steam.iam.common.config.ChoerodonDevOpsProperties;
import com.crc.crcloud.steam.iam.common.enums.ApplicationCategory;
import com.crc.crcloud.steam.iam.common.enums.ApplicationType;
import com.crc.crcloud.steam.iam.common.exception.IamAppCommException;
import com.crc.crcloud.steam.iam.common.utils.AssertHelper;
import com.crc.crcloud.steam.iam.dao.IamApplicationExplorationMapper;
import com.crc.crcloud.steam.iam.dao.IamApplicationMapper;
import com.crc.crcloud.steam.iam.entity.IamApplication;
import com.crc.crcloud.steam.iam.entity.IamApplicationExploration;
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
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;


import java.util.Objects;

import static com.crc.crcloud.steam.iam.common.utils.SagaTopic.Application.*;

@Service
public class IamApplicationServiceImpl implements IamApplicationService {

    private static final Long PROJECT_DOES_NOT_EXIST_ID = 0L;
    private static final String SEPARATOR = "/";

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

    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    @Saga(code = APP_CREATE, description = "steam-iam 创建应用", inputSchemaClass = IamApplication.class)
    @Override
    public IamApplicationVO createApplication(IamApplicationVO iamApplicationVO) {
        assertHelper.organizationNotExisted(iamApplicationVO.getOrganizationId());
        validate(iamApplicationVO);
        if (Objects.isNull(iamApplicationVO.getProjectId())){
            iamApplicationVO.setProjectId(PROJECT_DOES_NOT_EXIST_ID);
        }
        IamApplication iamApplication = voToEntity(iamApplicationVO);
        Long projectId = iamApplication.getProjectId();
        if (!Objects.equals(PROJECT_DOES_NOT_EXIST_ID, projectId)) {
            assertHelper.projectNotExisted(projectId);
        }

        String combination = ApplicationCategory.COMBINATION.code();
        boolean sendSagaEvent =
                (!combination.equals(iamApplication.getApplicationCategory())
                        && !PROJECT_DOES_NOT_EXIST_ID.equals(projectId)
                        && choerodonDevOpsProperties.isMessage());
        IamApplication returnEntity;
        if (sendSagaEvent) {
            returnEntity = insertAndGet(iamApplication);
            insertExploration(returnEntity.getId());
            sendSagaEvent(returnEntity, APP_CREATE);

        } else {
            returnEntity = insertAndGet(iamApplication);
            insertExploration(returnEntity.getId());
        }
        returnEntity.setObjectVersionNumber(1L);
        return entityToVo(returnEntity);
    }

    @Saga(code = APP_UPDATE, description = "steam-iam 更新应用", inputSchemaClass = IamApplication.class)
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    @Override
    public IamApplicationVO updateApplication(IamApplicationVO iamApplicationVO) {
        Long originProjectId =
                ObjectUtils.isEmpty(iamApplicationVO.getProjectId()) ? PROJECT_DOES_NOT_EXIST_ID : iamApplicationVO.getProjectId();
        iamApplicationVO.setProjectId(originProjectId);
        validate(iamApplicationVO);
        IamApplication before = assertHelper.applicationNotExisted(iamApplicationVO.getId());
        Long targetProjectId = before.getProjectId();
        preUpdate(iamApplicationVO, before);
        IamApplication iamApplication = voToEntity(iamApplicationVO);
        String combination = ApplicationCategory.COMBINATION.code();
        IamApplication result = null;
        if (choerodonDevOpsProperties.isMessage() && !combination.equals(iamApplication.getApplicationCategory())){
            if ( PROJECT_DOES_NOT_EXIST_ID.equals(targetProjectId)){
                if (!PROJECT_DOES_NOT_EXIST_ID.equals(originProjectId)){
                    result = updateAndGet(iamApplication);
                    sendSagaEvent(iamApplication, APP_UPDATE);
                } else {
                    result = updateAndGet(iamApplication);
                }
            } else {
                result = updateAndGet(iamApplication);
                sendSagaEvent(iamApplication, APP_UPDATE);
            }
        } else {
            result = updateAndGet(iamApplication);
        }
        return entityToVo(result);
    }

    /**
     * 更新前预操作 —— 将 organizationId applicationCategory code 设置为空，调用 updateById 不会被更新
     * @param vo
     * @param before
     */
    private void preUpdate(IamApplicationVO vo, IamApplication before) {
        if (!PROJECT_DOES_NOT_EXIST_ID.equals(before.getProjectId())) {
            // 为空情况，调用 updateById 不会被更新
            vo.setProjectId(null);
        } else if (!PROJECT_DOES_NOT_EXIST_ID.equals(vo.getProjectId())){
                assertHelper.projectNotExisted(vo.getProjectId());
        }
        vo.setOrganizationId(null);
        vo.setApplicationCategory(null);
        vo.setCode(null);
        assertHelper.objectVersionNumberNotNull(vo.getObjectVersionNumber());
    }

    private void insertExploration(Long applicationId) {
        IamApplicationExploration example = new IamApplicationExploration();
        example.setApplicationId(applicationId);
        String path = generatePath(applicationId);
        example.setPath(path);
        example.setApplicationEnabled(true);
        example.setRootId(applicationId);
        example.setHashcode(String.valueOf(path.hashCode()));
        iamApplicationExplorationMapper.insert(example);
    }

    private String generatePath(Long applicationId) {
        StringBuilder builder = new StringBuilder();
        return builder.append(SEPARATOR).append(applicationId).append(SEPARATOR).toString();
    }

    private IamApplication insertAndGet(IamApplication iamApplication) {
        if (1 != iamApplicationMapper.insert(iamApplication)){
            throw  new IamAppCommException("error.steam-iam.application.insert");
        }
        return iamApplication;
    }

    /**
     * 检验 创建对象 的 category 和 type 是否符合规范
     * @param iamApplicationVO
     */
    private void validate(IamApplicationVO iamApplicationVO) {
        String applicationCategory = iamApplicationVO.getApplicationCategory();
        String applicationType = iamApplicationVO.getApplicationType();
        if (!ApplicationCategory.matchCode(applicationCategory)){
            throw new IamAppCommException("error.steam-iam.application.applicationCategory.illegal");
        }
        if (!ApplicationType.matchCode(applicationType)) {
            throw new IamAppCommException("error.steam-iam.application.applicationType.illegal");
        }
    }

    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    @Saga(code = APP_ENABLE, description = "steam-iam 启用应用", inputSchemaClass = IamApplication.class)
    @Override
    public IamApplicationVO enableApplication(Long applicationId) {
        return toggleStatus(applicationId, true, APP_ENABLE);
    }

    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
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

    private IamApplication voToEntity(IamApplicationVO iamApplicationVO){
        IamApplicationDTO dto = ConvertHelper.convert(iamApplicationVO, IamApplicationDTO.class);
        return ConvertHelper.convert(dto, IamApplication.class);
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
                        .withSourceId(iamApplication.getOrganizationId())
                        .withSagaCode(sagaCode),
                builder -> {
                    builder
                            .withPayloadAndSerialize(iamApplication)
                            .withRefId(String.valueOf(iamApplication.getId()))
                            .withRefType("application");
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

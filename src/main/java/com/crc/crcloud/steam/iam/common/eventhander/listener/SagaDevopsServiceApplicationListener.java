package com.crc.crcloud.steam.iam.common.eventhander.listener;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.crc.crcloud.steam.iam.common.exception.IamAppCommException;
import com.crc.crcloud.steam.iam.common.enums.ApplicationCategory;
import com.crc.crcloud.steam.iam.common.enums.ApplicationType;
import com.crc.crcloud.steam.iam.common.utils.AssertHelper;
import com.crc.crcloud.steam.iam.dao.IamApplicationExplorationMapper;
import com.crc.crcloud.steam.iam.dao.IamApplicationMapper;
import com.crc.crcloud.steam.iam.entity.IamApplication;
import com.crc.crcloud.steam.iam.entity.IamApplicationExploration;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.choerodon.asgard.saga.annotation.SagaTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Component
public class SagaDevopsServiceApplicationListener {

    private static final String SUCCESSFUL = "successful";
    private static final String FAILED = "failed";
    private static final String SEPARATOR = "/";
    private static final String APP_SYNC = "devops-sync-application";
    private static final String IAM_SYNC_APP = "iam-sync-application";

    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private AssertHelper assertHelper;
    @Autowired
    private IamApplicationMapper applicationMapper;
    @Autowired
    private IamApplicationExplorationMapper applicationExplorationMapper;

    /*@SagaTask(code = IAM_SYNC_APP, description = "iam接受devops-service同步application集合事件",
            sagaCode = APP_SYNC,
            seq = 1)*/
    public void syncApplications(String data) throws IOException {

        List<IamApplication> applications = objectMapper.readValue(data, new TypeReference<List<IamApplication>>() {
        });
        log.info("steam-iam开始同步应用，总共[{}]", applications.size());
        if (CollUtil.isEmpty(applications)){
            log.warn("steam-iam同步应用程序时未收到任何应用程序");
            return;
        }
        HashMap<String, Integer> statisticsMap = CollUtil.newHashMap(2);
        statisticsMap.put(SUCCESSFUL, 0);
        statisticsMap.put(FAILED, 0);
        applications.forEach(app -> {
            int successful = statisticsMap.get(SUCCESSFUL);
            int failed = statisticsMap.get(FAILED);
            if (isIllegal(app)) {
                statisticsMap.put(FAILED, ++failed);
                return;
            }
            try {
                applicationMapper.insert(app);
                long appId = app.getId();
                IamApplicationExploration example = new IamApplicationExploration();
                example.setApplicationId(appId);
                String path = SEPARATOR + appId + SEPARATOR;
                example.setPath(path);
                example.setRootId(appId);
                example.setHashcode(String.valueOf(path.hashCode()));
                example.setEnabled(true);
                applicationExplorationMapper.insert(example);
                statisticsMap.put(SUCCESSFUL, ++successful);
            } catch (Exception e) {
                statisticsMap.put(FAILED, ++failed);
                log.error("insert application into db failed, application: {}, exception: {} ", app, e);
            }
        });
        log.info("syncing applications has done, successful: {}, failed: {}", statisticsMap.get(SUCCESSFUL), statisticsMap.get(FAILED));

    }

    private Boolean isIllegal(IamApplication app){
        Long organizationId = app.getOrganizationId();
        if (ObjectUtils.isEmpty(organizationId)) {
            log.error("illegal application because of organization id is empty, application: {}", app);
        } else {
            try {
                assertHelper.organizationNotExisted(organizationId);
            } catch (IamAppCommException e) {
                log.error("illegal application because of organization does not existed, application: {}", app);
                return true;
            }
        }

        Long projectId = app.getProjectId();
        if (ObjectUtils.isEmpty(projectId)) {
            log.error("illegal application because of project id is empty, application: {}", app);
        } else {
            try {
                assertHelper.projectNotExisted(projectId);
            } catch (IamAppCommException e) {
                log.error("illegal application because of project does not existed, application: {}", app);
                return true;
            }
        }

        String name = app.getName();
        if (StringUtils.isEmpty(name)) {
            log.error("illegal application because of name is empty, application: {}", app);
            return true;
        }

        String code = app.getCode();
        if (StringUtils.isEmpty(code)) {
            log.error("illegal application because of code is empty, application: {}", app);
            return true;
        }

        if (!ApplicationType.matchCode(app.getApplicationType())) {
            log.error("illegal application because of type is illegal, application: {}", app);
            return true;
        }

        IamApplication example = new IamApplication();
        example.setName(name);
        example.setOrganizationId(organizationId);
        example.setProjectId(projectId);
        if (!applicationMapper.selectList(new QueryWrapper<>(example)).isEmpty()) {
            log.error("illegal application because of name is duplicated, application: {}", app);
            return true;
        }

        example.setName(null);
        example.setCode(code);
        if (!applicationMapper.selectList(new QueryWrapper<>(example)).isEmpty()) {
            log.error("illegal application because of code is duplicated, application: {}", app);
            return true;
        }

        if (ObjectUtils.isEmpty(app.getEnabled())) {
            log.warn("the enabled of application is null, so set default value true, application: {}", app);
            app.setEnabled(true);
        }
        app.setApplicationCategory(ApplicationCategory.APPLICATION.code());
        return false;
    }

}

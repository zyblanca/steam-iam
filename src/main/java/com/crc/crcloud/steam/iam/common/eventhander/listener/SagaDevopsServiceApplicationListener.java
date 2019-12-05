package com.crc.crcloud.steam.iam.common.eventhander.listener;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.crc.crcloud.steam.iam.common.exception.IamAppCommException;
import com.crc.crcloud.steam.iam.common.enums.ApplicationCategory;
import com.crc.crcloud.steam.iam.common.enums.ApplicationType;
import com.crc.crcloud.steam.iam.common.utils.AssertHelper;
import com.crc.crcloud.steam.iam.common.utils.CollectionUtils;
import com.crc.crcloud.steam.iam.dao.IamApplicationExplorationMapper;
import com.crc.crcloud.steam.iam.dao.IamApplicationMapper;
import com.crc.crcloud.steam.iam.dao.IamLabelMapper;
import com.crc.crcloud.steam.iam.dao.IamMemberRoleMapper;
import com.crc.crcloud.steam.iam.entity.IamApplication;
import com.crc.crcloud.steam.iam.entity.IamApplicationExploration;
import com.crc.crcloud.steam.iam.entity.IamMemberRole;
import com.crc.crcloud.steam.iam.model.dto.payload.UserMemberEventPayload;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.choerodon.asgard.saga.annotation.SagaTask;
import io.choerodon.asgard.saga.producer.StartSagaBuilder;
import io.choerodon.asgard.saga.producer.TransactionalProducer;
import io.choerodon.core.iam.ResourceLevel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.crc.crcloud.steam.iam.common.utils.SagaTopic.MemberRole.MEMBER_ROLE_UPDATE;
import static com.crc.crcloud.steam.iam.common.utils.SagaTopic.Organization.ORG_UPDATE;

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
    private TransactionalProducer producer;
    @Autowired
    private AssertHelper assertHelper;
    @Autowired
    private IamApplicationMapper applicationMapper;
    @Autowired
    private IamApplicationExplorationMapper applicationExplorationMapper;
    @Autowired
    private IamMemberRoleMapper iamMemberRoleMapper;
    @Autowired
    private IamLabelMapper iamLabelMapper;

    /*@SagaTask(code = IAM_SYNC_APP, description = "iam 接受 devops-service 同步 application 集合事件",
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

    /*@SagaTask(code = MEMBER_ROLE_UPDATE, description = "iam接收devops平滑升级事件",
            sagaCode = "devops-upgrade-0.9",
            seq = 1)*/
    public void assignRolesOnProject(String data) {
        IamMemberRole iamMemberRole = new IamMemberRole();
        iamMemberRole.setSourceType(ResourceLevel.PROJECT.value());
        iamMemberRole.setMemberType("user");
        List<IamMemberRole> iamMemberRoleList = iamMemberRoleMapper.selectList(new QueryWrapper<>(iamMemberRole));
        Map<HashMap<Long, Long>, List<IamMemberRole>> map = iamMemberRoleList.stream().collect(Collectors.groupingBy(m -> {
            HashMap<Long, Long> hashMap = new HashMap<>();
            hashMap.put(m.getSourceId(), m.getMemberId());
            return hashMap;
        }));
        List<UserMemberEventPayload> userMemberEventPayloadList = new ArrayList<>();
        for (Map.Entry<HashMap<Long, Long>, List<IamMemberRole>> entry : map.entrySet()) {
            UserMemberEventPayload payload = new UserMemberEventPayload();
            List<IamMemberRole> memberRoles = entry.getValue();
            Long sourceId = null;
            Long userId = null;
            List<Long> roleIds = new ArrayList<>();
            for (IamMemberRole memberRole : memberRoles) {
                sourceId = memberRole.getSourceId();
                userId = memberRole.getMemberId();
                roleIds.add(memberRole.getRoleId());
            }
            payload.setResourceId(sourceId);
            payload.setResourceType("project");
            payload.setUserId(userId);
            if (CollUtil.isNotEmpty(roleIds)){
                payload.setRoleLabels(iamLabelMapper.selectLabelNamesInRoleIds(roleIds));
            }
            userMemberEventPayloadList.add(payload);
        }
        List<List<UserMemberEventPayload>> lists = CollectionUtils.subList(userMemberEventPayloadList, 1000);
        lists.forEach(list -> {
            try {
                String input = objectMapper.writeValueAsString(list);
                String refIds = list.stream().map(t -> t.getUserId() + "").collect(Collectors.joining(","));
                log.info("steam-iam开始发送Saga事件[{code:{}}],内容: {}", ORG_UPDATE, input);
                producer.apply(StartSagaBuilder.newBuilder()
                        .withLevel(ResourceLevel.PROJECT)
                        .withSagaCode(MEMBER_ROLE_UPDATE),
                        builder -> {
                        builder.withPayloadAndSerialize(list)
                                .withRefType("users")
                                .withRefId(refIds);
                });
            } catch (Exception e) {
                throw new IamAppCommException("error.iRoleMemberServiceImpl.updateMemberRole.event");
            }
        });
    }

}
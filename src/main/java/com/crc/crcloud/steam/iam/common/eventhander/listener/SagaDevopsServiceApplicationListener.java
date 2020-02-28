package com.crc.crcloud.steam.iam.common.eventhander.listener;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
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
import com.crc.crcloud.steam.iam.model.dto.QueryApplicationParamDTO;
import com.crc.crcloud.steam.iam.model.dto.payload.ApplicationReqPayload;
import com.crc.crcloud.steam.iam.model.dto.payload.UserMemberEventPayload;
import com.crc.crcloud.steam.iam.model.vo.IamApplicationVO;
import com.crc.crcloud.steam.iam.model.vo.IamProjectVO;
import com.crc.crcloud.steam.iam.service.IamApplicationService;
import com.crc.crcloud.steam.iam.service.IamProjectService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.choerodon.asgard.saga.annotation.SagaTask;
import io.choerodon.asgard.saga.producer.StartSagaBuilder;
import io.choerodon.asgard.saga.producer.TransactionalProducer;
import io.choerodon.core.convertor.ApplicationContextHelper;
import io.choerodon.core.iam.ResourceLevel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static com.crc.crcloud.steam.iam.common.utils.SagaTopic.MemberRole.MEMBER_ROLE_UPDATE;
import static com.crc.crcloud.steam.iam.common.utils.SagaTopic.Organization.ORG_UPDATE;

@Slf4j
@Component
public class SagaDevopsServiceApplicationListener {

    private static final String APPLICATION = "application";
    private static final String SUCCESSFUL = "successful";
    private static final String FAILED = "failed";
    private static final String SEPARATOR = "/";
    private static final String APP_SYNC = "devops-sync-application";
    @Value("${spring.application.name}")
    private String applicationName;

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
    @Autowired
    IamApplicationService iamApplicationService;

    @SagaTask(code = "steam-iam-sync-application", description = "iam 接受 devops-service 同步 application 集合事件",
            sagaCode = APP_SYNC,
            seq = 1)
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
                log.error("illegal application because of organization does not existed, application: {}，{}", app,e.getCode());
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
                log.error("illegal application because of project does not existed, application: {},{}", app,e.getCode());
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

    @SagaTask(code = MEMBER_ROLE_UPDATE, description = "iam接收devops平滑升级事件",
            sagaCode = "devops-upgrade-0.9",
            seq = 1)
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
                log.warn(e.getMessage());
                throw new IamAppCommException("error.iRoleMemberServiceImpl.updateMemberRole.event");
            }
        });
    }

    @SagaTask(code = "steamIamCreateApplication",
            description = "steam-iam 同步创建应用",
            sagaCode = "devops-ci-create-application",
            maxRetryCount = 3,
            seq = 1)
    public void createApplicationSagaTask(String data) throws IOException {
        ApplicationReqPayload applicationReqPayload = objectMapper.readValue(data, ApplicationReqPayload.class);
        // 通过 projectId 组织
        IamProjectService iamProjectService = ApplicationContextHelper.getContext().getBean(IamProjectService.class);
        IamProjectVO iamProjectVO = iamProjectService.queryOne(applicationReqPayload.getProjectId());
        if (Objects.isNull(iamProjectVO)) {
            throw new IamAppCommException("error.steam-iam.iamProject.isNull");
        }
        Long organizationId = iamProjectVO.getOrganizationId();

        IamApplicationVO iamApplicationVO = new IamApplicationVO();
        iamApplicationVO.setApplicationCategory(APPLICATION);
        iamApplicationVO.setApplicationType(applicationReqPayload.getType());
        iamApplicationVO.setCode(applicationReqPayload.getCode());
        iamApplicationVO.setName(applicationReqPayload.getName());
        iamApplicationVO.setEnabled(true);
        iamApplicationVO.setOrganizationId(organizationId);
        iamApplicationVO.setProjectId(iamProjectVO.getId());
        iamApplicationVO.setFrom(applicationName);

        IamApplicationService iamApplicationService = ApplicationContextHelper.getContext().getBean(IamApplicationService.class);
        iamApplicationService.createApplication(iamApplicationVO);
    }

    @SagaTask(code = "deleteApplicationSagaTask",
            description = "删除steam-iam应用",
            sagaCode = "steam-ci-delete-application",
            maxRetryCount = 3,
            seq = 2)
    public void deleteApplicationSagaTask(String data) {
        try {
            QueryApplicationParamDTO queryApplicationParamDTO = JSON.parseObject(data, QueryApplicationParamDTO.class);
            iamApplicationService.deleteApplication(queryApplicationParamDTO.getOrganizationId(),
                    queryApplicationParamDTO.getSteamProjectId(), queryApplicationParamDTO.getCode());
            log.info("删除devops应用成功");
        } catch (Exception e) {
            log.info("删除devops-service应用失败", e.getMessage());
        }
    }

}

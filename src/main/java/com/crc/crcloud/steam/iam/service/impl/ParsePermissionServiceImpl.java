package com.crc.crcloud.steam.iam.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.crc.crcloud.steam.iam.model.dto.IamPermissionDTO;
import com.crc.crcloud.steam.iam.model.dto.IamRoleDTO;
import com.crc.crcloud.steam.iam.model.dto.IamRolePermissionDTO;
import com.crc.crcloud.steam.iam.service.IamPermissionService;
import com.crc.crcloud.steam.iam.service.IamRolePermissionService;
import com.crc.crcloud.steam.iam.service.IamRoleService;
import com.crc.crcloud.steam.iam.service.ParsePermissionService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.InitRoleCode;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.core.swagger.PermissionData;
import io.choerodon.core.swagger.SwaggerExtraData;
import io.choerodon.eureka.event.EurekaEventPayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.client.RestTemplate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

import static io.choerodon.core.iam.InitRoleCode.SITE_ADMINISTRATOR;

/**
 * @author LiuYang
 * @date 2019/11/29
 */
@Validated
@Service
public class ParsePermissionServiceImpl implements ParsePermissionService {
    private final Logger logger = LoggerFactory.getLogger(ParsePermissionServiceImpl.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private IamPermissionService iamPermissionService;

    @Autowired
    private IamRolePermissionService iamRolePermissionService;

    private RestTemplate restTemplate = new RestTemplate();

    @Autowired
    private IamRoleService iamRoleService;

    @Value("${choerodon.cleanPermission:false}")
    private boolean cleanPermission;

    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public ParsePermissionServiceImpl() {
    }

    private void fetchSwaggerJsonByIp(final EurekaEventPayload payload) {
        ResponseEntity<String> response = restTemplate.getForEntity("http://" + payload.getInstanceAddress() + "/v2/choerodon/api-docs",
                String.class);
        if (response.getStatusCode() == HttpStatus.OK) {
            payload.setApiData(response.getBody());
        } else {
            throw new CommonException("fetch swagger error, statusCode is not 2XX, serviceId: " + payload.getId());
        }
    }

    //    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    @Override
    public void parser(EurekaEventPayload payload) {
        try {
            logger.info("parser {}", payload);
            fetchSwaggerJsonByIp(payload);
            String serviceName = payload.getAppName();
            String json = payload.getApiData();
            logger.info("receive service: {} message, version: {}, ip: {}", serviceName, payload.getVersion(), payload.getInstanceAddress());
            if (!StringUtils.isEmpty(serviceName) && !StringUtils.isEmpty(json)) {
                JsonNode node = objectMapper.readTree(json);
                Iterator<Map.Entry<String, JsonNode>> pathIterator = node.get("paths").fields();
                List<String> permissionCodes = new ArrayList<>();
                while (pathIterator.hasNext()) {
                    Map.Entry<String, JsonNode> pathNode = pathIterator.next();
                    Iterator<Map.Entry<String, JsonNode>> methodIterator = pathNode.getValue().fields();
                    parserMethod(methodIterator, pathNode, serviceName, permissionCodes);
                }
                logger.info("cleanPermission : {}", cleanPermission);
                if (cleanPermission) {
                    deleteDeprecatedPermission(permissionCodes, serviceName);
                    //??????role_permission???????????????????????????????????????????????????????????????
                    cleanRolePermission();
                }
            }
        } catch (IOException e) {
            throw new CommonException("error.parsePermissionService.parse.IOException", e);
        }
    }

    /**
     * ????????????????????????????????????
     *
     * @param permissionCodes ????????????????????????????????????code
     * @param serviceName     ?????????
     */
    private void deleteDeprecatedPermission(List<String> permissionCodes, String serviceName) {
        @NotNull List<IamPermissionDTO> servicePermissions = iamPermissionService.getByService(serviceName);
        AtomicInteger deprecatedCount = new AtomicInteger();
        for (IamPermissionDTO permission : servicePermissions) {
            if (!permissionCodes.contains(permission.getCode())) {
                iamPermissionService.delete(permission.getId());
                logger.info("@@@ service {} delete deprecated permission {}", serviceName, permission.getCode());
                deprecatedCount.incrementAndGet();
            }
        }
        logger.info("service {} delete deprecated permission, total {}", serviceName, deprecatedCount.get());
    }

    /**
     * ?????????????????????
     * <p>??????role_permission???????????????????????????????????????????????????????????????</p>
     */
    private void cleanRolePermission() {
        @NotNull List<IamRoleDTO> roles = iamRoleService.getRoles();
        AtomicInteger cleanCount = new AtomicInteger();
        for (IamRoleDTO role : roles) {
            @NotNull List<IamRolePermissionDTO> iamRolePermissions = iamRolePermissionService.selectErrorLevelPermissionByRole(role.getId());
            for (IamRolePermissionDTO iamRolePermission : iamRolePermissions) {
                Optional<IamPermissionDTO> permissionOpt = iamPermissionService.get(iamRolePermission.getPermissionId());
                iamRolePermissionService.delete(iamRolePermission.getId());
                permissionOpt.ifPresent(permission -> {
                    logger.warn("delete error role_permission, role id: {}, code: {}, level: {} ## permission id: {}, code:{}, level: {} ## because of resource level not the same",
                            role.getId(), role.getCode(), role.getFdLevel(), permission.getId(), permission.getCode(), permission.getFdLevel());
                });
            }
        }
        logger.info("clean error role_permission finished, total: {}", cleanCount.get());
    }

    /**
     * ??????????????????????????????????????????
     *
     * @param methodIterator ????????????
     * @param pathNode       ??????
     * @param serviceName    ?????????
     */
    private void parserMethod(Iterator<Map.Entry<String, JsonNode>> methodIterator,
                              Map.Entry<String, JsonNode> pathNode, String serviceName,
                              List<String> permissionCode) {
        while (methodIterator.hasNext()) {
            Map.Entry<String, JsonNode> methodNode = methodIterator.next();
            JsonNode tags = methodNode.getValue().get("tags");
            String resourceCode = processResourceCode(tags);
            try {
                JsonNode extraDataNode = methodNode.getValue().get("description");
                if (resourceCode == null || extraDataNode == null) {
                    continue;
                }
                SwaggerExtraData extraData = objectMapper.readValue(extraDataNode.asText(), SwaggerExtraData.class);
                permissionCode.add(processPermission(extraData, pathNode.getKey(), methodNode, serviceName, resourceCode));
            } catch (IOException e) {
                logger.info("extraData read failed.", e);
            }
        }
    }

    private String processPermission(SwaggerExtraData extraData, String path, Map.Entry<String, JsonNode> methodNode,
                                     String serviceName, String resourceCode) {
        String method = methodNode.getKey();
        String description = methodNode.getValue().get("summary").asText();
        PermissionData permission = extraData.getPermission();
        String action = permission.getAction();
        String code = serviceName + "." + resourceCode + "." + action;
        IamPermissionDTO newPermission = IamPermissionDTO.builder()
                .code(code).path(path).method(method)
                .fdLevel(permission.getPermissionLevel())
                .description(description)
                .action(action)
                .fdResource(resourceCode)
                .publicAccess(permission.isPermissionPublic())
                .loginAccess(permission.isPermissionLogin())
                .isWithin(permission.isPermissionWithin())
                .serviceName(serviceName)
                .build();
        newPermission = this.iamPermissionService.put(newPermission);
        logger.debug("processPermission[{}]: {}", newPermission.getCode(), newPermission);
        /**
         * ?????????permission level????????????????????????????????????
         * level=site -> SITE_ADMINISTRATOR
         * level=organization -> ORGANIZATION_ADMINISTRATOR
         * level=project -> PROJECT_ADMINISTRATOR,PROJECT_OWNER
         */
        MultiValueMap<ResourceLevel, String> levelRole = new LinkedMultiValueMap<>(3);
        levelRole.add(ResourceLevel.SITE, InitRoleCode.SITE_ADMINISTRATOR);
        levelRole.add(ResourceLevel.ORGANIZATION, InitRoleCode.ORGANIZATION_ADMINISTRATOR);
        levelRole.add(ResourceLevel.PROJECT, InitRoleCode.PROJECT_ADMINISTRATOR);
        levelRole.add(ResourceLevel.PROJECT, InitRoleCode.PROJECT_OWNER);
        final Set<String> roleSet = new HashSet<>(Arrays.asList(permission.getRoles()));
        final String level = newPermission.getFdLevel();
        final Long permissionId = newPermission.getId();

        Arrays.stream(ResourceLevel.values()).filter(t -> Objects.equals(t.value(), level)).findFirst().ifPresent(t -> {
            if (levelRole.containsKey(t)) {
                //noinspection ConstantConditions
                roleSet.addAll(levelRole.get(t));
            }
        });
        logger.info("permission[{}|{}] link roles: [{}]", permissionId, code, CollUtil.join(roleSet, ","));
        //????????????????????????????????????????????????????????????
        iamRolePermissionService.clear(permissionId, iamRoleService.getRolesByCode(roleSet).stream().map(IamRoleDTO::getId).collect(Collectors.toSet()));
        if (!roleSet.isEmpty()) {
            processRolePermission(roleSet, permissionId, level);
        }
        return code;
    }

    private String processResourceCode(JsonNode tags) {
        String resourceCode = null;
        boolean illegal = true;
        List<String> illegalTags = new ArrayList<>();
        for (int i = 0; i < tags.size(); i++) {
            String tag = tags.get(i).asText();
            //??????choerodon-eureka????????????-endpoint?????????tag???
            if (tag.endsWith("-controller")) {
                illegal = false;
                resourceCode = tag.substring(0, tag.length() - "-controller".length());
            } else if (tag.endsWith("-endpoint")) {
                illegal = false;
                resourceCode = tag.substring(0, tag.length() - "-endpoint".length());
            } else {
                illegalTags.add(tag);
            }
        }
        if (illegal) {
            logger.warn("skip the controller/endpoint because of the illegal tags {}, please ensure the controller is end with ##Controller## or ##EndPoint##", illegalTags);
        }
        return resourceCode;
    }

    /**
     * ??????????????????????????????
     *
     * @param roleCodes    ????????????
     * @param permissionId ????????????
     * @param level        ??????
     */
    private void processRolePermission(@NotEmpty Set<String> roleCodes, @NotNull Long permissionId, @NotBlank String level) {
        final Map<String, IamRoleDTO> codeRoleMap = iamRoleService.getRolesByCode(roleCodes).stream().collect(Collectors.toMap(IamRoleDTO::getCode, Function.identity()));
        //?????????????????????ID
        final Set<Long> needLinkRoleIds = CollUtil.newHashSet();
        for (String roleCode : roleCodes) {
            final IamRoleDTO role = codeRoleMap.get(roleCode);
            if (Objects.isNull(role)) {
                //?????????code????????????????????????????????????????????????code?????????
                logger.error("can not find the role, role code is : {}", roleCode);
            } else if (!Objects.equals(level, role.getFdLevel())) {
                logger.error("init role level does not match the permission level, permission id: {}, level: {}, @@ role code: {}, level: {}",
                        permissionId, level, role.getCode(), role.getFdLevel());
            } else {
                needLinkRoleIds.add(role.getId());
            }
        }
        if (CollUtil.isNotEmpty(needLinkRoleIds)) {
            iamRolePermissionService.link(permissionId, needLinkRoleIds);
        }
    }

    @Deprecated
    private IamRoleDTO getRoleByLevel(Map<String, IamRoleDTO> initRoleMap, String level) {
        if (ResourceLevel.SITE.value().equals(level)) {
            return initRoleMap.get(SITE_ADMINISTRATOR);
        }
        if (ResourceLevel.ORGANIZATION.value().equals(level)) {
            return initRoleMap.get(InitRoleCode.ORGANIZATION_ADMINISTRATOR);
        }
        if (ResourceLevel.PROJECT.value().equals(level)) {
            return initRoleMap.get(InitRoleCode.PROJECT_ADMINISTRATOR);
        }
        return null;
    }

    /**
     * ??????{@link InitRoleCode#values()}?????????????????????
     *
     * @return key {@link InitRoleCode} ??????,????????????
     */
    @Deprecated
    private Map<String, IamRoleDTO> queryInitRoleByCode() {
        Set<String> codes = CollUtil.newHashSet(InitRoleCode.values());
        Map<String, IamRoleDTO> roleMap = iamRoleService.getRoles().stream().filter(t -> codes.contains(t.getCode())).collect(Collectors.toMap(IamRoleDTO::getCode, Function.identity()));
        for (String code : codes) {
            if (!roleMap.containsKey(code)) {
                logger.warn("init roles do not exist, code: {}", code);
            }
        }
        return roleMap;
    }

}

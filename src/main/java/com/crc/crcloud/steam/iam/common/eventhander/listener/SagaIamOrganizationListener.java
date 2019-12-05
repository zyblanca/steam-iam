package com.crc.crcloud.steam.iam.common.eventhander.listener;

import com.crc.crcloud.steam.iam.model.dto.IamOrganizationDTO;
import com.crc.crcloud.steam.iam.model.dto.OauthLdapDTO;
import com.crc.crcloud.steam.iam.model.dto.OauthPasswordPolicyDTO;
import com.crc.crcloud.steam.iam.model.dto.payload.OrganizationPayload;
import com.crc.crcloud.steam.iam.service.IamOrganizationService;
import com.crc.crcloud.steam.iam.service.OauthLdapService;
import com.crc.crcloud.steam.iam.service.OauthPasswordPolicyService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.choerodon.asgard.saga.annotation.SagaTask;
import io.choerodon.core.ldap.DirectoryType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.crc.crcloud.steam.iam.common.utils.SagaTopic.Organization.ORG_CREATE;
import static com.crc.crcloud.steam.iam.common.utils.SagaTopic.Organization.TASK_ORG_CREATE;

@Slf4j
@Component
public class SagaIamOrganizationListener {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Value("${lock.expireTime:3600}")
    private Integer lockedExpireTime;
    @Value("${max.checkCaptcha:3}")
    private Integer maxCheckCaptcha;
    @Value("${max.errorTime:5}")
    private Integer maxErrorTime;

    @Autowired
    private IamOrganizationService iamOrganizationService;
    @Autowired
    private OauthLdapService oauthLdapService;
    @Autowired
    private OauthPasswordPolicyService oauthPasswordPolicyService;

    /*@SagaTask(code = TASK_ORG_CREATE, sagaCode = ORG_CREATE, description = "iam接收org服务创建组织事件",
            seq = 1 )*/
    public OrganizationPayload sagaTaskCreateEventHandle(String data) throws IOException {
        OrganizationPayload organizationEventPayload = objectMapper.readValue(data, OrganizationPayload.class);
        log.info("steam-iam-sagaTask create the organization trigger task, payload: {}", organizationEventPayload);
        Long orgId = organizationEventPayload.getId();
        IamOrganizationDTO iamOrganizationDTO = iamOrganizationService.getAndThrow(orgId);
//        sagaTaskCreateLdap(orgId, iamOrganizationDTO.getName());
        sagaTaskCreatePasswordPolicy(orgId, iamOrganizationDTO.getCode(), iamOrganizationDTO.getName());
        return organizationEventPayload;
    }

    private void sagaTaskCreateLdap(Long orgId, String name) {
        try {
            log.info("steam-iam-sagaTask begin create ldap of organization {} ", orgId);
            OauthLdapDTO ldapDTO = new OauthLdapDTO();
            ldapDTO.setOrganizationId(orgId);
            ldapDTO.setName(name);
            ldapDTO.setServerAddress("");
            ldapDTO.setPort("389");
            ldapDTO.setDirectoryType(DirectoryType.OPEN_LDAP.value());
//            ldapDTO.setEnabled(true);
            ldapDTO.setIsEnabled(1L);
//            ldapDTO.setUseSSL(false);
            ldapDTO.setUseSsl(0L);
            ldapDTO.setObjectClass("person");
            ldapDTO.setSagaBatchSize(500);
            ldapDTO.setConnectionTimeout(10);
            ldapDTO.setAccount("test");
//            ldapDTO.setPassword("test");
            ldapDTO.setLdapPassword("test");
            ldapDTO.setUuidField("entryUUID");
            oauthLdapService.sagaTaskCreateLdap(orgId, ldapDTO);
        } catch (Exception e) {
            log.error("steam-iam-SagaTask create ldap error of organization, organizationId: {}, exception: {}", orgId, e);
        }
    }

    private void sagaTaskCreatePasswordPolicy(Long orgId, String code, String name) {
        try {
            log.info("steam-iam-SagaTask begin create password policy of organization {} ", orgId);
            OauthPasswordPolicyDTO passwordPolicyDTO = new OauthPasswordPolicyDTO();
            passwordPolicyDTO.setOrganizationId(orgId);
            passwordPolicyDTO.setCode(code);
            passwordPolicyDTO.setName(name);
            passwordPolicyDTO.setMaxCheckCaptcha(maxCheckCaptcha);
            passwordPolicyDTO.setMaxErrorTime(maxErrorTime);
            passwordPolicyDTO.setLockedExpireTime(lockedExpireTime);
            //默认开启登陆安全策略，设置为
            passwordPolicyDTO.setEnableSecurity(true);
            passwordPolicyDTO.setEnableCaptcha(true);
            passwordPolicyDTO.setMaxCheckCaptcha(3);
            passwordPolicyDTO.setEnableLock(true);
            passwordPolicyDTO.setMaxErrorTime(5);
            passwordPolicyDTO.setLockedExpireTime(600);
            oauthPasswordPolicyService.sagaCreatePasswordPolicy(orgId, passwordPolicyDTO);
        } catch (Exception e) {
            log.error("steam-iam-SagaTask create password policy error of organizationId: {}, exception: {}", orgId, e);
        }
    }


}

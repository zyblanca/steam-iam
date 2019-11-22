package com.crc.crcloud.steam.iam.job;

import com.alibaba.fastjson.JSON;
import com.crc.crcloud.steam.iam.service.LdapService;
import com.crc.crcloud.steam.iam.service.OauthLdapService;
import io.choerodon.asgard.schedule.annotation.JobParam;
import io.choerodon.asgard.schedule.annotation.JobTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
public class LdapSyncUserJob {
    @Autowired
    private OauthLdapService oauthLdapService;

    @JobTask(code = "ldap_sync_user_job", description = "定时同步ldap用户信息")
    public Map<String, Object> ldapSyncUser(Map<String, Object> param) {
        log.info("定时触发同步ldap用户信息,参数{}", JSON.toJSONString(param));

        oauthLdapService.jobForSyncLdapUser();

        log.info("定时触发同步ldap用户信息结束,参数", JSON.toJSONString(param));
        return param;
    }
}

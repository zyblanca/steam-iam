package com.crc.crcloud.steam.iam.common.utils;

import com.crc.crcloud.steam.iam.common.enums.LdapTypeEnum;
import com.crc.crcloud.steam.iam.model.dto.OauthLdapDTO;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * ldap 工具类
 */
public class LdapUtil {


    private static final String LDAP_TIME_OUT = "com.sun.jndi.ldap.connect.timeout";

    /**
     * 获取ldap连接配置
     *
     * @param oauthLdapDTO      配置参数
     * @param anonymousReadOnly 匿名访问,查看基本信息，不需要账户
     * @return ldap连接模板
     */
    public static LdapTemplate getLdapTemplate(OauthLdapDTO oauthLdapDTO, boolean anonymousReadOnly) {
        LdapContextSource contextSource = new LdapContextSource();
        //匿名登入不需要账户信息
        if (anonymousReadOnly) {
            contextSource.setAnonymousReadOnly(anonymousReadOnly);
        } else {
            contextSource.setUserDn(oauthLdapDTO.getAccount());
            contextSource.setPassword(oauthLdapDTO.getLdapPassword());
        }
        //基本信息设置
        contextSource.setBase(oauthLdapDTO.getBaseDn());
        contextSource.setUrl(oauthLdapDTO.getServerAddress() + ":" + oauthLdapDTO.getPort());
        contextSource.setPooled(false);
        //设置超时时间
        Map<String, Object> environment = new HashMap<>(1);
        //不设置时间默认为10秒
        int connectionTimeout = Objects.isNull(oauthLdapDTO.getConnectionTimeout()) ? 10 : oauthLdapDTO.getConnectionTimeout();
        //设置ldap服务器连接超时时间为10s
        environment.put(LDAP_TIME_OUT, String.valueOf(connectionTimeout * 1000));
        contextSource.setBaseEnvironmentProperties(environment);

        //必须设置
        contextSource.afterPropertiesSet();


        LdapTemplate ldapTemplate = new LdapTemplate(contextSource);
        if (Objects.equals(LdapTypeEnum.MICROSOFT_ACTIVE_DIRECTORY.getValue(), oauthLdapDTO.getDirectoryType())) {
            ldapTemplate.setIgnorePartialResultException(true);
        }
        return ldapTemplate;
    }


}

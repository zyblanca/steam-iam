package com.crc.crcloud.steam.iam.service.impl;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.crc.crcloud.steam.iam.common.enums.LdapTypeEnum;
import com.crc.crcloud.steam.iam.common.utils.CopyUtil;
import com.crc.crcloud.steam.iam.common.utils.LdapUtil;
import com.crc.crcloud.steam.iam.dao.OauthLdapMapper;
import com.crc.crcloud.steam.iam.entity.OauthLdap;
import com.crc.crcloud.steam.iam.model.dto.LdapConnectionDTO;
import com.crc.crcloud.steam.iam.model.dto.OauthLdapDTO;
import com.crc.crcloud.steam.iam.service.LdapService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.AuthenticationException;
import org.springframework.ldap.InvalidNameException;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.AbstractContextMapper;
import org.springframework.ldap.filter.AndFilter;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.ldap.filter.Filter;
import org.springframework.ldap.query.SearchScope;
import org.springframework.stereotype.Service;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.springframework.ldap.query.LdapQueryBuilder.query;

@Service
@Slf4j
public class LdapServiceImpl implements LdapService {
    @Autowired
    private OauthLdapMapper oauthLdapMapper;

    private static final String OBJECT_CLASS = "objectclass";

    @Override
    public LdapConnectionDTO validAccount(OauthLdapDTO oauthLdapDTO) {

        LdapConnectionDTO connection = testConnection(oauthLdapDTO);
        //连接成功，登入失败，则使用数据库配置管理账户进行登入校验
        if (connection.getCanConnectServer() && !connection.getCanLogin()) {
            OauthLdap oauthLdap = oauthLdapMapper.selectById(oauthLdapDTO.getId());
            //如果测试账户与管理员账户一致，不进行校验，直接返回
            if (!Objects.equals(oauthLdap.getAccount(), oauthLdap.getAccount())) {
                return connection;
            }
            //寻找userDn
            String userDn = findUserDn(CopyUtil.copy(oauthLdap, OauthLdapDTO.class), oauthLdapDTO.getAccount());
            //没有找到userDn，返回
            if (Objects.isNull(userDn)) return connection;
            //找到对应的userDn重新校验登入
            oauthLdapDTO.setAccount(userDn);
            connection = testConnection(oauthLdapDTO);
        }
        return connection;
    }

    //寻找对应账户的userdn
    private String findUserDn(OauthLdapDTO oauthLdapDTO, String account) {
        try {
            LdapTemplate ldapTemplate = LdapUtil.getLdapTemplate(oauthLdapDTO, Boolean.FALSE);
            AndFilter andFilter = getAndFilterByObjectClass(oauthLdapDTO);
            //uuid=指定账户
            andFilter.and(new EqualsFilter(oauthLdapDTO.getUuidField(), account));
            List<String> userdns = ldapTemplate.search(query().searchScope(SearchScope.SUBTREE).countLimit(1).filter(andFilter), new AbstractContextMapper<String>() {
                @Override
                protected String doMapFromContext(DirContextOperations ctx) {
                    return ctx.getNameInNamespace();
                }
            });
            if (!CollectionUtils.isEmpty(userdns)) {
                return userdns.get(0);
            }
        } catch (Exception e) {
            log.warn("ldap通过管理员查询指定账户失败{},{}，管理员账户{}，{}，查询账户{}", oauthLdapDTO.getServerAddress(), oauthLdapDTO.getPort(), oauthLdapDTO.getAccount(), oauthLdapDTO.getBaseDn(), account, e);
        }
        return null;
    }

    @Override
    public LdapConnectionDTO testConnection(OauthLdapDTO oauthLdapDTO) {
        log.info("测试连接ldap，连接信息为{},{},{},{}", oauthLdapDTO.getServerAddress(), oauthLdapDTO.getPort(), oauthLdapDTO.getAccount(), oauthLdapDTO.getBaseDn());
        LdapConnectionDTO ldapConnectionDTO = new LdapConnectionDTO();
        //初始化状态
        initLdapStatus(ldapConnectionDTO);
        try {
            LdapTemplate ldapTemplate = LdapUtil.getLdapTemplate(oauthLdapDTO, Boolean.FALSE);
            AndFilter andFilter = getAndFilterByObjectClass(oauthLdapDTO);
            //查询级别当前节点以及子节点
            //限制100主要为了对比属性，可能会有偏差
            //todo 当前无法知道实际ldap实际的权限规则，普通用户是否有查询权限等，默认当做有权限，后续如果发现普通用户无权限在做处理
            List<Attributes> attributes = ldapTemplate.search(query().searchScope(SearchScope.SUBTREE).countLimit(100).filter(andFilter), new AttributesMapper<Attributes>() {
                @Override
                public Attributes mapFromAttributes(Attributes attributes) throws NamingException {
                    return attributes;
                }
            });
            ldapConnectionDTO.setCanLogin(Boolean.TRUE);
            ldapConnectionDTO.setCanConnectServer(Boolean.TRUE);
            matchAttributes(oauthLdapDTO, ldapConnectionDTO, attributes);
            //命名异常或者权限异常，都是在登入成功的前提下出现的
        } catch (InvalidNameException | AuthenticationException e) {
            if (e.getRootCause() instanceof javax.naming.InvalidNameException
                    || e.getRootCause() instanceof javax.naming.AuthenticationException) {
                ldapConnectionDTO.setCanConnectServer(true);
            }
            log.warn("测试连接ldap，账户失败，连接信息为{},{},{},{}", oauthLdapDTO.getServerAddress(), oauthLdapDTO.getPort(), oauthLdapDTO.getAccount(), oauthLdapDTO.getBaseDn(), e);
        } catch (Exception e) {
            log.warn("测试连接ldap，失败，连接信息为{},{},{},{}", oauthLdapDTO.getServerAddress(), oauthLdapDTO.getPort(), oauthLdapDTO.getAccount(), oauthLdapDTO.getBaseDn(), e);
        }
        return ldapConnectionDTO;
    }
    //对比属性
    private void matchAttributes(OauthLdapDTO oauthLdapDTO, LdapConnectionDTO ldapConnectionDTO, List<Attributes> attributes) {

    }

    //设置用户对象过滤条件
    private AndFilter getAndFilterByObjectClass(OauthLdapDTO oauthLdapDTO) {
        String objectClass = oauthLdapDTO.getObjectClass();
        String[] arr = objectClass.split(",");
        AndFilter andFilter = new AndFilter();
        for (String str : arr) {
            andFilter.and(new EqualsFilter(OBJECT_CLASS, str));
        }
        return andFilter;
    }

    //初始默认值
    private void initLdapStatus(LdapConnectionDTO ldapConnectionDTO) {
        ldapConnectionDTO.setCanConnectServer(Boolean.FALSE);
        ldapConnectionDTO.setCanLogin(Boolean.FALSE);
        ldapConnectionDTO.setMatchAttribute(Boolean.FALSE);

    }
}

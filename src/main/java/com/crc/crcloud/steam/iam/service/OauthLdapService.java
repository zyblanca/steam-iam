package com.crc.crcloud.steam.iam.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crc.crcloud.steam.iam.model.vo.OauthLdapVO;


/**
 * @Author
 * @Description
 * @Date 2019-11-12
 */
public interface OauthLdapService {


    /**
     * 新增ldap配置
     *
     * @param oauthLdap ldap配置
     * @return ldap配置
     */
    OauthLdapVO insert(OauthLdapVO oauthLdap);

    /**
     * 启用/禁用ldap配置
     *
     * @param oauthLdap ldap配置
     * @return ldap配置
     */
    OauthLdapVO changeStatus(OauthLdapVO oauthLdap);


    /**
     * 更新ldap配置
     *
     * @param oauthLdap ldap配置
     * @return ldap配置
     */
    OauthLdapVO update(OauthLdapVO oauthLdap);

    /**
     * 查询单个详情
     *
     * @param organizationId 组织id
     * @param id             ldap id
     * @return ldap配置信息
     */
    OauthLdapVO queryOne(Long organizationId, Long id);


}

package com.crc.crcloud.steam.iam.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crc.crcloud.steam.iam.common.utils.ResponseEntity;
import com.crc.crcloud.steam.iam.model.dto.LdapConnectionDTO;
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

    /**
     * 测试ldap连接
     *
     * @param oauthLdapVO ldap配置
     * @return 测试结果
     */
    LdapConnectionDTO testConnetion(OauthLdapVO oauthLdapVO);

    /**
     * 同步用户信息
     *
     * @param organizationId 组织id
     * @param id             ldap配置id
     * @return 同步记录id
     */
    Long syncLdapUser(Long organizationId, Long id);

    /**
     * 查询组织下的ldap配置
     * @param organizationId
     * @return
     */
    OauthLdapVO queryOneByOrganizationId(Long organizationId);


    /**
     * 定时同步所有ldap用户信息
     */
    void jobForSyncLdapUser();
}

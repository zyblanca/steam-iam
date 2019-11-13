package com.crc.crcloud.steam.iam.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crc.crcloud.steam.iam .model.vo.OauthLdapVO;



/**
 * @Author
 * @Description 
 * @Date 2019-11-12
 */
public interface OauthLdapService {


    /**
     * 新增ldap配置
     * @param oauthLdap ldap配置
     * @return ldap配置
     */
    OauthLdapVO insert(OauthLdapVO oauthLdap);

    /**
     * 启用/禁用ldap配置
     * @param oauthLdap ldap配置
     * @return ldap配置
     */
    OauthLdapVO changeStatus(OauthLdapVO oauthLdap);


    /**
    * 更新
    * @param projectId  项目ID
    * @param oauthLdap
    * @return
    */
    OauthLdapVO  update(Long projectId, OauthLdapVO oauthLdap);

    /**
     *
     * 查询单个详情
     * @param projectId  项目ID
     * @param id
     * @return
     */
    OauthLdapVO queryOne(Long projectId, Long id);




}

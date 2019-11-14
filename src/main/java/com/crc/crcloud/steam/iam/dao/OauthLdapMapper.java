package com.crc.crcloud.steam.iam.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.crc.crcloud.steam.iam.entity.OauthLdap;
import org.apache.ibatis.annotations.Param;


/**
 * @Author:
 * @Date: 2019-11-12
 * @Description:
 */
public interface OauthLdapMapper extends BaseMapper<OauthLdap> {

    /**
     * 修改ldap状态
     * @param oauthLdap ldap配置
     * @return 修改数量
     */
    int changeStatus(@Param("oauthLdap") OauthLdap oauthLdap);

    /**
     * 修改ldap数据
     * @param oauthLdap ldap配置
     * @return  修改数量
     */
    int updateLdapData(@Param("oauthLdap")OauthLdap oauthLdap);
}

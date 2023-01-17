package com.crc.crcloud.steam.iam.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.crc.crcloud.steam.iam.entity.OauthLdap;
import com.crc.crcloud.steam.iam.model.dto.OauthLdapDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;


/**
 * @Author:
 * @Date: 2019-11-12
 * @Description:
 */
public interface OauthLdapMapper extends BaseMapper<OauthLdap> {

    /**
     * 修改ldap状态
     *
     * @param oauthLdap ldap配置
     * @return 修改数量
     */
    int changeStatus(@Param("oauthLdap") OauthLdap oauthLdap);

    /**
     * 修改ldap数据
     *
     * @param oauthLdap ldap配置
     * @return 修改数量
     */
    int updateLdapData(@Param("oauthLdap") OauthLdap oauthLdap);

    /**
     * 用户找到组织，组织找到ldap配置
     * 找到的ldap配置与现有的ldap配置进行对比
     *
     * @param ids
     * @param oauthLdap
     * @return
     */
    List<Long> matchLdapByUserIdAndLdap(@Param("ids") List<Long> ids, @Param("oauthLdap") OauthLdapDTO oauthLdap);
}

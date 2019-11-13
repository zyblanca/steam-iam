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


    int changeStatus(@Param("oauthLdap") OauthLdap oauthLdap);
}

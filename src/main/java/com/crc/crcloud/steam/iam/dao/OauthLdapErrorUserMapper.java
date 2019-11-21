package com.crc.crcloud.steam.iam.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crc.crcloud.steam.iam.entity.OauthLdapErrorUser;
import com.crc.crcloud.steam.iam.model.dto.OauthLdapErrorUserDTO;
import org.apache.ibatis.annotations.Param;


/**
 * @Author:
 * @Date: 2019-11-12
 * @Description: 
 */
public interface OauthLdapErrorUserMapper extends BaseMapper<OauthLdapErrorUser> {

    IPage<OauthLdapErrorUser> page(Page page, @Param("oauthLdapErrorUser") OauthLdapErrorUserDTO oauthLdapErrorUserDTO);

}

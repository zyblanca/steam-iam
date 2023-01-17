package com.crc.crcloud.steam.iam.service;

import com.crc.crcloud.steam.iam.entity.IamUser;
import com.crc.crcloud.steam.iam.entity.OauthLdap;
import com.crc.crcloud.steam.iam.entity.OauthLdapErrorUser;
import com.crc.crcloud.steam.iam.entity.OauthLdapHistory;
import com.crc.crcloud.steam.iam.model.dto.LdapConnectionDTO;
import com.crc.crcloud.steam.iam.model.dto.OauthLdapDTO;
import com.crc.crcloud.steam.iam.model.vo.OauthLdapVO;
import org.springframework.ldap.filter.Filter;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * ldap 操作相关
 */
public interface LdapService {

    /**
     * 验证账户
     * 1.尝试登入，连接失败退出，连接成功登入失败进入后续
     * 2.使用数据库配置的管理员账户，登入。登入成功失败退出，登入成功后续。
     * 3.管理员账户搜索测试连接的账户，存在，获取
     *
     * @param oauthLdapDTO ldap配置
     * @note 原始版本为 1登入失败 使用匿名方式获取账户的地址，当前版本不使用匿名方式，使用管理员获取
     * 管理员获取失败则失败
     * @retur 测试结果
     */
    LdapConnectionDTO validAccount(OauthLdapDTO oauthLdapDTO);


    /**
     * 验证账户是否可以连接
     * 属性是否匹配
     *
     * @param oauthLdapDTO ldap配置
     * @return 验证结果
     */
    LdapConnectionDTO testConnection(@NotNull OauthLdapDTO oauthLdapDTO);

    /**
     * 同步ldap用户信息
     *
     * @param oauthLdapDTO
     * @param oauthLdapHistory
     */
    void syncLdapUser(OauthLdapDTO oauthLdapDTO, OauthLdapHistory oauthLdapHistory);

    /**
     * 检验ldap是否可以运行同步
     * 历史记录不存在，或者最后一条记录已经完成 可以运行同步
     * 最后一条记录未执行完毕，但是时间超过一个小时 ，可以运行同步
     * 最后一条记录未执行完毕，时间未超过一个小时，不允许重开一个同步
     *
     * @param ldapId
     * @return
     */
    Long checkLast(Long ldapId);

    /**
     * 内部使用，便于事务控制
     *
     * @param id
     * @param insertUser
     * @param organizationId
     * @return
     */
    List<OauthLdapErrorUser> insertLdapUser(Long id, List<IamUser> insertUser, Long organizationId);
}

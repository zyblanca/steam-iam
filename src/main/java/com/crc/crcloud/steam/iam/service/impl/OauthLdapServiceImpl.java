package com.crc.crcloud.steam.iam.service.impl;


import com.crc.crcloud.steam.iam.common.exception.IamAppCommException;
import com.crc.crcloud.steam.iam.common.utils.CopyUtil;
import com.crc.crcloud.steam.iam.common.utils.UserDetail;
import com.crc.crcloud.steam.iam.dao.IamOrganizationMapper;
import com.crc.crcloud.steam.iam.dao.OauthLdapMapper;
import com.crc.crcloud.steam.iam.entity.IamOrganization;
import com.crc.crcloud.steam.iam.entity.OauthLdap;
import com.crc.crcloud.steam.iam.model.vo.OauthLdapVO;
import com.crc.crcloud.steam.iam.service.OauthLdapService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Objects;

/**
 * @Author liuchun
 * @Description ldap服务
 * @Date 2019-11-12
 */
@Service
@Slf4j
public class OauthLdapServiceImpl implements OauthLdapService {

    @Autowired
    private OauthLdapMapper oauthLdapMapper;
    @Autowired
    private IamOrganizationMapper iamOrganizationMapper;

    /**
     * 新增ldap配置
     *
     * @param oauthLdapVO ldap配置
     * @return ldap配置
     */
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    @Override
    public OauthLdapVO insert(OauthLdapVO oauthLdapVO) {

        initLdapData(oauthLdapVO);
        //保存
        OauthLdap oauthLdap = CopyUtil.copy(oauthLdapVO, OauthLdap.class);
        oauthLdapMapper.insert(oauthLdap);
        return queryOne(oauthLdap.getOrganizationId(), oauthLdap.getId());
    }

    /**
     * 启用/禁用ldap配置
     *
     * @param oauthLdapVO ldap配置
     * @return ldap配置
     */
    @Override
    public OauthLdapVO changeStatus(OauthLdapVO oauthLdapVO) {
        if (Objects.isNull(oauthLdapVO.getId())) {
            throw new IamAppCommException("ldap.id.null");
        }
        if (Objects.isNull(oauthLdapVO.getIsEnabled())) {
            throw new IamAppCommException("ldap.enabled.null");
        }
        OauthLdap oauthLdap = oauthLdapMapper.selectById(oauthLdapVO.getId());
        //ladp不存在或者指定的ldap不属于该组织
        if (Objects.isNull(oauthLdap) || !Objects.equals(oauthLdapVO.getOrganizationId(), oauthLdap.getOrganizationId())) {
            throw new IamAppCommException("ldap.data.null");
        }
        oauthLdap.setIsEnabled(oauthLdap.getIsEnabled());
        oauthLdap.setLastUpdatedBy(UserDetail.getUserId());
        //修改状态
        oauthLdapMapper.changeStatus(oauthLdap);
        log.info("用户{}，修改ldap：{}，状态为:{}", UserDetail.getUserId(), oauthLdapVO.getId(), oauthLdapVO.getIsEnabled());
        return queryOne(oauthLdap.getOrganizationId(), oauthLdap.getId());
    }

    /**
     * 更新ldap配置
     *
     * @param oauthLdapVO ldap配置
     * @return ldap配置
     */
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    @Override
    public OauthLdapVO update(OauthLdapVO oauthLdapVO) {
        if (Objects.isNull(oauthLdapVO.getId())) {
            throw new IamAppCommException("ldap.id.null");
        }
        initLdapData(oauthLdapVO);
        //保存
        OauthLdap oauthLdap = CopyUtil.copy(oauthLdapVO, OauthLdap.class);
        oauthLdap.setLastUpdatedBy(UserDetail.getUserId());
        oauthLdap.setLastUpdateDate(new Date());
        oauthLdapMapper.updateLdapData(oauthLdap);
        return queryOne(oauthLdapVO.getOrganizationId(), oauthLdapVO.getId());
    }

    /**
     * 查询单个详情
     *
     * @param organizationId 组织id
     * @param id             ldap id
     * @return ldap配置信息
     */
    @Override
    public OauthLdapVO queryOne(Long organizationId, Long id) {
        getOrThrow(organizationId);
        OauthLdap data = oauthLdapMapper.selectById(id);
        if (Objects.isNull(data) || !Objects.equals(organizationId, data.getOrganizationId())) {
            throw new IamAppCommException("ldap.data.null");
        }
        return CopyUtil.copy(data, OauthLdapVO.class);
    }


    //获取组织信息，校验组织是否存在
    private IamOrganization getOrThrow(Long organizationId) {
        IamOrganization iamOrganization = iamOrganizationMapper.selectById(organizationId);
        if (Objects.isNull(iamOrganization)) {
            throw new IamAppCommException("organization.data.empty");
        }
        return iamOrganization;
    }

    //初始化数据
    private void initLdapData(OauthLdapVO oauthLdapVO) {
        //组织名称即为ldap名称
        IamOrganization iamOrganization = getOrThrow(oauthLdapVO.getOrganizationId());
        oauthLdapVO.setName(iamOrganization.getName());
        //没有设置SSL默认不使用
        if (Objects.isNull(oauthLdapVO.getUseSsl())) {
            oauthLdapVO.setUseSsl(0L);
        }
        //默认启用状态
        if (Objects.isNull(oauthLdapVO.getIsEnabled())) {
            oauthLdapVO.setIsEnabled(1L);
        }

    }

}

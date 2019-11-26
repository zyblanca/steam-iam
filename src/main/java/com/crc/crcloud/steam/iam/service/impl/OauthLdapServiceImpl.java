package com.crc.crcloud.steam.iam.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.crc.crcloud.steam.iam.common.exception.IamAppCommException;
import com.crc.crcloud.steam.iam.common.utils.CopyUtil;
import com.crc.crcloud.steam.iam.common.utils.UserDetail;
import com.crc.crcloud.steam.iam.dao.IamOrganizationMapper;
import com.crc.crcloud.steam.iam.dao.OauthLdapHistoryMapper;
import com.crc.crcloud.steam.iam.dao.OauthLdapMapper;
import com.crc.crcloud.steam.iam.entity.IamOrganization;
import com.crc.crcloud.steam.iam.entity.OauthLdap;
import com.crc.crcloud.steam.iam.entity.OauthLdapHistory;
import com.crc.crcloud.steam.iam.model.dto.LdapConnectionDTO;
import com.crc.crcloud.steam.iam.model.dto.OauthLdapDTO;
import com.crc.crcloud.steam.iam.model.vo.OauthLdapVO;
import com.crc.crcloud.steam.iam.service.LdapService;
import com.crc.crcloud.steam.iam.service.OauthLdapService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
    @Autowired
    private LdapService ldapService;
    @Autowired
    private OauthLdapHistoryMapper oauthLdapHistoryMapper;

    /**
     * 新增ldap配置
     *
     * @param oauthLdapVO ldap配置
     * @return ldap配置
     */
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    @Override
    public OauthLdapVO insert(OauthLdapVO oauthLdapVO) {
        if (Objects.nonNull(queryOneByOrganizationId(oauthLdapVO.getOrganizationId()))) {
            throw new IamAppCommException("ldap.data.exist");
        }
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


    @Override
    public LdapConnectionDTO testConnetion(OauthLdapVO oauthLdapVO) {
        if (Objects.isNull(oauthLdapVO.getAccount()) || Objects.isNull(oauthLdapVO.getLdapPassword())) {
            throw new IamAppCommException("comm.param.error");
        }
        //验证信息
        OauthLdapVO oauthLdap = queryOne(oauthLdapVO.getOrganizationId(), oauthLdapVO.getId());
        oauthLdap.setAccount(oauthLdapVO.getAccount());
        oauthLdap.setLdapPassword(oauthLdapVO.getLdapPassword());
        return ldapService.validAccount(CopyUtil.copy(oauthLdap, OauthLdapDTO.class));
    }

    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public Long syncLdapUser(Long organizationId, Long id) {
        //检验最后一次执行时间
        Long historyId = ldapService.checkLast(id);
        if (Objects.nonNull(historyId)) return historyId;
        OauthLdapDTO oauthLdapDTO = CopyUtil.copy(queryOne(organizationId, id), OauthLdapDTO.class);
        //先测试连接
        LdapConnectionDTO ldapConnectionDTO = ldapService.testConnection(oauthLdapDTO);
        //连接不通
        if (!ldapConnectionDTO.getCanConnectServer()) {
            throw new IamAppCommException("ldap.connection.false");
        }
        if (!ldapConnectionDTO.getCanLogin()) {
            throw new IamAppCommException("ldap.login.false");
        }
        if (!ldapConnectionDTO.getMatchAttribute()) {
            throw new IamAppCommException("ldap.attribute.false");
        }
        //记录操作信息
        OauthLdapHistory oauthLdapHistory = initHistory(id);
//        oauthLdapHistoryMapper.insert(oauthLdapHistory);
        //调用同步操作
        ldapService.syncLdapUser(oauthLdapDTO, oauthLdapHistory);
        return oauthLdapHistory.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public void jobForSyncLdapUser() {
        //查询所有有效的ldap配置
        LambdaQueryWrapper<OauthLdap> query = Wrappers.<OauthLdap>lambdaQuery().eq(OauthLdap::getIsEnabled, Boolean.TRUE);
        List<OauthLdap> oauthLdaps = oauthLdapMapper.selectList(query);
        log.info("ldap定时同步用户，共有配置{}", oauthLdaps.size());
        //兼容历史数据，历史数据部分必填信息缺失
        List<OauthLdap> availableLdap = oauthLdaps.stream().filter(v -> validLdap(v)).collect(Collectors.toList());
        log.info("ldap定时同步用户，有效配置{}", availableLdap.size());
        List<OauthLdapDTO> ldapJobs = availableLdap.stream().map(v -> CopyUtil.copy(v, OauthLdapDTO.class)).filter(v -> {
            LdapConnectionDTO ldapConnectionDTO = ldapService.testConnection(v);
            return ldapConnectionDTO.getCanConnectServer() && ldapConnectionDTO.getCanLogin() && ldapConnectionDTO.getMatchAttribute();
        }).collect(Collectors.toList());
        log.info("ldap定时同步用户，可连接，可登入，属性可匹配的配置{}", ldapJobs.size());

        for (OauthLdapDTO oauthLdapDTO : ldapJobs) {

            //调用同步操作
            ldapService.syncLdapUser(oauthLdapDTO, initHistory(oauthLdapDTO.getId()));
        }


    }


    private OauthLdapHistory initHistory(Long ldapId) {
        //记录操作信息
        OauthLdapHistory oauthLdapHistory = new OauthLdapHistory();
        oauthLdapHistory.setLdapId(ldapId);
        oauthLdapHistory.setSyncBeginTime(new Date());
        oauthLdapHistory.setErrorUserCount(0);
        oauthLdapHistory.setNewUserCount(0);
        oauthLdapHistory.setUpdateUserCount(0);

        oauthLdapHistoryMapper.insert(oauthLdapHistory);
        return oauthLdapHistory;
    }


    //校验必填信息是否出现空值
    private boolean validLdap(OauthLdap oauthLdap) {
        return StringUtils.hasText(oauthLdap.getAccount()) &&
                StringUtils.hasText(oauthLdap.getBaseDn()) &&
                StringUtils.hasText(oauthLdap.getDirectoryType()) &&
                StringUtils.hasText(oauthLdap.getEmailField()) &&
                StringUtils.hasText(oauthLdap.getLdapPassword()) &&
                StringUtils.hasText(oauthLdap.getLoginNameField()) &&
                StringUtils.hasText(oauthLdap.getObjectClass()) &&
                StringUtils.hasText(oauthLdap.getPort()) &&
                StringUtils.hasText(oauthLdap.getRealNameField()) &&
                StringUtils.hasText(oauthLdap.getServerAddress()) &&
                StringUtils.hasText(oauthLdap.getUuidField()) &&
                Objects.nonNull(oauthLdap.getOrganizationId()) &&
                Objects.nonNull(oauthLdap.getConnectionTimeout()) &&
                Objects.nonNull(oauthLdap.getSagaBatchSize()) &&
                Objects.nonNull(oauthLdap.getUseSsl());

    }

    @Override
    public OauthLdapVO queryOneByOrganizationId(Long organizationId) {
        OauthLdap oauthLdap = oauthLdapMapper.selectOne(Wrappers.<OauthLdap>lambdaQuery().eq(OauthLdap::getOrganizationId, organizationId));
        if (Objects.isNull(oauthLdap)) return null;
        return CopyUtil.copy(oauthLdap, OauthLdapVO.class);
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
        if(StringUtils.isEmpty(oauthLdapVO.getPhoneField())){
            oauthLdapVO.setPhoneField(null);
        }

    }

}

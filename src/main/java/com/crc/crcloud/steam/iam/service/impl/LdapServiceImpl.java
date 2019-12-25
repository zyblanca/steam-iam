package com.crc.crcloud.steam.iam.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.crc.crcloud.steam.iam.api.feign.IamServiceClient;
import com.crc.crcloud.steam.iam.common.enums.LdapSyncUserErrorEnum;
import com.crc.crcloud.steam.iam.common.utils.CommonCollectionUtils;
import com.crc.crcloud.steam.iam.common.utils.CopyUtil;
import com.crc.crcloud.steam.iam.common.utils.LdapUtil;
import com.crc.crcloud.steam.iam.dao.*;
import com.crc.crcloud.steam.iam.entity.*;
import com.crc.crcloud.steam.iam.model.dto.IamUserDTO;
import com.crc.crcloud.steam.iam.model.dto.LdapConnectionDTO;
import com.crc.crcloud.steam.iam.model.dto.OauthLdapDTO;
import com.crc.crcloud.steam.iam.model.dto.UserMatchLdapDTO;
import com.crc.crcloud.steam.iam.model.event.IamUserLdapBatchCreateEvent;
import com.crc.crcloud.steam.iam.service.IamMemberRoleService;
import com.crc.crcloud.steam.iam.service.IamRoleService;
import com.crc.crcloud.steam.iam.service.LdapService;
import io.choerodon.core.iam.InitRoleCode;
import io.choerodon.core.iam.ResourceLevel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ldap.AuthenticationException;
import org.springframework.ldap.InvalidNameException;
import org.springframework.ldap.control.PagedResultsDirContextProcessor;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.LdapOperations;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.AbstractContextMapper;
import org.springframework.ldap.core.support.LdapOperationsCallback;
import org.springframework.ldap.core.support.SingleContextSource;
import org.springframework.ldap.filter.AndFilter;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.ldap.filter.HardcodedFilter;
import org.springframework.ldap.query.SearchScope;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import java.beans.Transient;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static org.springframework.ldap.query.LdapQueryBuilder.query;

@Service
@Slf4j
public class LdapServiceImpl implements LdapService {
    @Autowired
    private OauthLdapMapper oauthLdapMapper;
    @Autowired
    private OauthLdapHistoryMapper oauthLdapHistoryMapper;
    @Autowired
    private IamUserOrganizationRelMapper iamUserOrganizationRelMapper;
    @Autowired
    private IamUserMapper iamUserMapper;
    @Autowired
    private OauthLdapErrorUserMapper oauthLdapErrorUserMapper;

    private static final String OBJECT_CLASS = "objectclass";
    @Autowired
    private IamServiceClient iamServiceClient;
    @Autowired
    private IamRoleMapper iamRoleMapper;
    @Autowired
    private IamMemberRoleService iamMemberRoleService;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Override
    public LdapConnectionDTO validAccount(OauthLdapDTO oauthLdapDTO) {

        LdapConnectionDTO connection = testConnection(oauthLdapDTO);
        //连接成功，登入失败，则使用数据库配置管理账户进行登入校验
        if (connection.getCanConnectServer() && !connection.getCanLogin()) {
            OauthLdap oauthLdap = oauthLdapMapper.selectById(oauthLdapDTO.getId());
            //如果测试账户与管理员账户一致，不进行校验，直接返回
            if (Objects.equals(oauthLdapDTO.getAccount(), oauthLdap.getAccount())) {
                return connection;
            }
            //寻找userDn
            String userDn = findUserDn(CopyUtil.copy(oauthLdap, OauthLdapDTO.class), oauthLdapDTO.getAccount());
            //没有找到userDn，返回
            if (Objects.isNull(userDn)) return connection;
            //找到对应的userDn重新校验登入
            oauthLdapDTO.setAccount(userDn);
            connection = testConnection(oauthLdapDTO);
        }
        return connection;
    }

    //寻找对应账户的userdn
    private String findUserDn(OauthLdapDTO oauthLdapDTO, String account) {
        try {
            LdapTemplate ldapTemplate = LdapUtil.getLdapTemplate(oauthLdapDTO, Boolean.FALSE);
            AndFilter andFilter = getAndFilterByObjectClass(oauthLdapDTO);
            //uuid=指定账户
            andFilter.and(new EqualsFilter(oauthLdapDTO.getUuidField(), account));
            List<String> userdns = ldapTemplate.search(query().searchScope(SearchScope.SUBTREE).countLimit(1).filter(andFilter), new AbstractContextMapper<String>() {
                @Override
                protected String doMapFromContext(DirContextOperations ctx) {
                    return ctx.getNameInNamespace();
                }
            });
            if (!CollectionUtils.isEmpty(userdns)) {
                return userdns.get(0);
            }
        } catch (Exception e) {
            log.warn("ldap通过管理员查询指定账户失败{},{}，管理员账户{}，{}，查询账户{}", oauthLdapDTO.getServerAddress(), oauthLdapDTO.getPort(), oauthLdapDTO.getAccount(), oauthLdapDTO.getBaseDn(), account, e);
        }
        return null;
    }

    @Override
    public LdapConnectionDTO testConnection(OauthLdapDTO oauthLdapDTO) {
        log.info("测试连接ldap，连接信息为{},{},{},{}", oauthLdapDTO.getServerAddress(), oauthLdapDTO.getPort(), oauthLdapDTO.getAccount(), oauthLdapDTO.getBaseDn());
        LdapConnectionDTO ldapConnectionDTO = new LdapConnectionDTO();
        //初始化状态
        initLdapStatus(ldapConnectionDTO);
        try {
            LdapTemplate ldapTemplate = LdapUtil.getLdapTemplate(oauthLdapDTO, Boolean.FALSE);
            AndFilter andFilter = getAndFilterByObjectClass(oauthLdapDTO);
            //查询级别当前节点以及子节点
            //限制100主要为了对比属性，可能会有偏差
            //todo 当前无法知道实际ldap实际的权限规则，普通用户是否有查询权限等，默认当做有权限，后续如果发现普通用户无权限在做处理
            List<Attributes> attributes = ldapTemplate.search(query().searchScope(SearchScope.SUBTREE).countLimit(100).filter(andFilter), new AttributesMapper<Attributes>() {
                @Override
                public Attributes mapFromAttributes(Attributes attributes) throws NamingException {
                    return attributes;
                }
            });
            ldapConnectionDTO.setCanLogin(Boolean.TRUE);
            ldapConnectionDTO.setCanConnectServer(Boolean.TRUE);
            //匹配属性
            matchAttributes(oauthLdapDTO, ldapConnectionDTO, attributes);
            //命名异常或者权限异常，都是在登入成功的前提下出现的
        } catch (InvalidNameException | AuthenticationException e) {
            if (e.getRootCause() instanceof javax.naming.InvalidNameException
                    || e.getRootCause() instanceof javax.naming.AuthenticationException) {
                ldapConnectionDTO.setCanConnectServer(true);
            }
            log.warn("测试连接ldap，账户失败，连接信息为{},{},{},{}", oauthLdapDTO.getServerAddress(), oauthLdapDTO.getPort(), oauthLdapDTO.getAccount(), oauthLdapDTO.getBaseDn(), e);
        } catch (Exception e) {
            log.warn("测试连接ldap，失败，连接信息为{},{},{},{}", oauthLdapDTO.getServerAddress(), oauthLdapDTO.getPort(), oauthLdapDTO.getAccount(), oauthLdapDTO.getBaseDn(), e);
        }
        return ldapConnectionDTO;
    }

    @Override
    @Async("ldap-executor")
    public void syncLdapUser(OauthLdapDTO oauthLdapDTO, OauthLdapHistory oauthLdapHistory) {
        //初始化记录
        try {
            AndFilter andFilter = getAndFilterByObjectClass(oauthLdapDTO);
            //存在过滤条件则使用
            if (StringUtils.hasText(oauthLdapDTO.getCustomFilter())) {
                andFilter.and(new HardcodedFilter(oauthLdapDTO.getCustomFilter()));
            }
            LdapTemplate ldapTemplate = LdapUtil.getLdapTemplate(oauthLdapDTO, Boolean.FALSE);
            //分页
            PagedResultsDirContextProcessor processor = new PagedResultsDirContextProcessor(oauthLdapDTO.getSagaBatchSize());
            //查找域
            SearchControls searchControls = new SearchControls();
            searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
            SingleContextSource.doWithSingleContext(
                    ldapTemplate.getContextSource(), new LdapOperationsCallback<List<IamUserDTO>>() {

                        @Override
                        public List<IamUserDTO> doWithLdapOperations(LdapOperations operations) {
                            int page = 1;//记录操作信息
                            AttributesMapper attributeMapper = new AttributesMapper() {
                                @Override
                                public Object mapFromAttributes(Attributes attributes) throws NamingException {
                                    return attributes;
                                }
                            };
                            do {
                                List<Attributes> attributesList = operations.search("", andFilter.toString(), searchControls, attributeMapper, processor);
                                log.info("ldap同步用户，ldap id{},当前页{},每页{},当前查询数量{}", oauthLdapDTO.getId(), page, oauthLdapDTO.getSagaBatchSize(), attributesList.size());
                                if (attributesList.isEmpty()) break;
                                //普通用户，第一步的时候只能代表属性正确，不代表完整性正确
                                List<IamUser> normalUser = new ArrayList<>();
                                //错误的用户
                                List<OauthLdapErrorUser> errorUsers = new ArrayList<>();
                                //属性转对象
                                transformationToUser(oauthLdapHistory, attributesList, normalUser, errorUsers, oauthLdapDTO);
                                //对象对比并且进行插入或者更新
                                operationUsers(oauthLdapHistory, normalUser, errorUsers, oauthLdapDTO);
                                //help gc
                                normalUser = null;
                                errorUsers = null;
                                page++;
                            } while (processor.hasMore());
                            return null;
                        }
                    }

            );
        } catch (Exception e) {
            log.warn("同步用户异常===》", e);
        }

        oauthLdapHistory.setSyncEndTime(new Date());
        oauthLdapHistoryMapper.updateById(oauthLdapHistory);


    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Long checkLast(Long ldapId) {

        OauthLdapHistory oauthLdapHistory = oauthLdapHistoryMapper.selectLastByLdapId(ldapId);
        if (Objects.isNull(oauthLdapHistory) || Objects.nonNull(oauthLdapHistory.getSyncEndTime())) return null;
        if (Objects.isNull(oauthLdapHistory.getSyncBeginTime())) {
            oauthLdapHistory.setSyncBeginTime(new Date());
            oauthLdapHistory.setSyncEndTime(new Date());
            oauthLdapHistoryMapper.updateById(oauthLdapHistory);
            return null;
        }
        //如果当前时间-开始时间大于一个小时，重新触发执行
        if (System.currentTimeMillis() - oauthLdapHistory.getSyncBeginTime().getTime() > 60 * 60 * 1000) {
            oauthLdapHistory.setSyncEndTime(new Date());
            oauthLdapHistoryMapper.updateById(oauthLdapHistory);
            return null;
        }
        return oauthLdapHistory.getId();
    }


    /**
     * 比对人员信息
     * 1.ldap邮箱数据库中存在，并且ldap邮箱所属的用户名与数据库中该邮箱的用户名不一致，属于异常用户
     * 2.ldap登入名和邮箱数据库都不存在，进行插入用户，并且绑定组织
     * 3.ldap登入名数据库中存在，如果不是ldap账号算错误用户
     * 4.ldap登入名数据库存在，获取该数据库该用户所属的组织（一个人可以挂靠多个组织）;
     * 4.1 当前ldap配置所属的组织，属于数据库该用户的组织之一，无条件进行更新操作
     * 4.2 当前ldap配置所属的组织，不属于数据库该用户的组织之一，进行比对当前ldap配置和该用户的所有组织的ldap配置
     * 4.2.1 当前的ldap配置跟该用户任意一个组织的ldap配置一致，进行绑定组织，修改用户信息
     * 4.2.2 当前的ldap配置跟该用户没有任意一个组织的ldap配置一致，算异常用户
     *
     * @param oauthLdapHistory
     * @param normalUser
     * @param errorUsers
     */
    private void operationUsers(OauthLdapHistory oauthLdapHistory, List<IamUser> normalUser, List<OauthLdapErrorUser> errorUsers, OauthLdapDTO oauthLdapDTO) {
        //需要插入的用户
        List<IamUser> insertUser = new ArrayList<>();
        //需要修改的用户
        List<IamUser> updateUser = new ArrayList<>();
        //需要对比用户ldap信息的
        List<IamUser> matchLdapUser = new ArrayList<>();
        //登入名
        Set<String> loginNames = new HashSet<>();
        //邮箱
        Set<String> emails = new HashSet<>();
        for (IamUser user : normalUser) {
            loginNames.add(user.getLoginName());
            emails.add(user.getEmail());
        }
        //部分数据库无法支持大批量in操作，分批in也能提高效率
        List<Set<String>> subNameSet = CommonCollectionUtils.subSet(loginNames, 999);
        List<Set<String>> subEmailSet = CommonCollectionUtils.subSet(emails, 999);
        Map<String, UserMatchLdapDTO> userIsLdapMap = new HashMap<>();
        Map<String, String> emailUser = new HashMap<>();
        //获取人员
        subNameSet.forEach(set -> userIsLdapMap.putAll(getUserLdapMap(set)));
        //获取邮箱与人员信息
        subEmailSet.forEach(set -> emailUser.putAll(getEmailUser(set)));
        int initErrorSize = errorUsers.size();
        UserMatchLdapDTO userMatchLdapDTO;
        String loginName;
        //普通用户匹配
        for (IamUser user : normalUser) {
            //邮箱已经存在，但是与ldap的用户名不一致
            if (Objects.nonNull(loginName = emailUser.get(user.getEmail())) && !Objects.equals(loginName, user.getLoginName())) {
                userToErrorUser(oauthLdapHistory, user, errorUsers, LdapSyncUserErrorEnum.EMAIL_EXIST_FOUND.getMsg());
                log.warn("ldap比对用户信息失败，邮箱{}用户{}，邮箱已被用户占用{}", user.getEmail(), user.getLoginName(), loginName);
                continue;
            }
            //用户名不存在，直接进行新增
            if (Objects.isNull(userMatchLdapDTO = userIsLdapMap.get(user.getLoginName()))) {
                insertUser.add(user);
                continue;
            }
            //匹配上的用户绑定id
            user.setId(userMatchLdapDTO.getId());
            //命中的用户非ldap用户
            if (!userMatchLdapDTO.getIsLdap()) {
                userToErrorUser(oauthLdapHistory, user, errorUsers, LdapSyncUserErrorEnum.LOGIN_NAME_EXIST_NOT_LDAP.getMsg());
                log.warn("ldap比对用户信息失败，用户{}已存在相同的用户，且该用户非ldap用户", user.getEmail(), user.getLoginName(), loginName);
                continue;
            }
            //当前匹配到的用户属于当前组织的进行更新操作
            if (userMatchLdapDTO.getOrganization().contains(oauthLdapDTO.getOrganizationId())) {
                updateUser.add(user);
                continue;
            }
            //上诉都不匹配后，走匹配ldap选项
            matchLdapUser.add(user);
        }
        //特殊用户匹配，用户名相同，不属于已绑定的组织，判断已绑定的组织是否跟当前的ldap配置一致
        List<IamUserOrganizationRel> bandUser = matchLdapInfo(matchLdapUser, oauthLdapDTO, oauthLdapHistory, errorUsers, updateUser);

        oauthLdapHistory.setErrorUserCount(oauthLdapHistory.getErrorUserCount() + errorUsers.size() - initErrorSize);
        oauthLdapHistory.setNewUserCount(oauthLdapHistory.getNewUserCount() + insertUser.size());
        oauthLdapHistory.setUpdateUserCount(oauthLdapHistory.getUpdateUserCount() + updateUser.size());
        List<OauthLdapErrorUser> insertError = insertLdapUser(oauthLdapHistory.getId(), insertUser, oauthLdapDTO.getOrganizationId());
        oauthLdapHistory.setNewUserCount(oauthLdapHistory.getNewUserCount() - insertError.size());
        oauthLdapHistory.setErrorUserCount(oauthLdapHistory.getErrorUserCount() + insertError.size());
        errorUsers.addAll(insertError);
        updateLdapUser(updateUser);
        bandLdapUser(bandUser);
        insertErrorUser(errorUsers);
    }

    private void insertErrorUser(List<OauthLdapErrorUser> errorUsers) {
        if (CollectionUtils.isEmpty(errorUsers)) return;
        errorUsers.forEach(v -> oauthLdapErrorUserMapper.insert(v));
    }

    //绑定用户
    private void bandLdapUser(List<IamUserOrganizationRel> bandUser) {
        if (CollectionUtils.isEmpty(bandUser)) return;
        bandUser.forEach(v -> iamUserOrganizationRelMapper.insert(v));
    }

    //修改用户
    private void updateLdapUser(List<IamUser> updateUser) {
        //TODO 当前版本不做ldap人员修改功能
        //后续看情况修改
//        if (CollectionUtils.isEmpty(updateUser)) return;
//        updateUser.forEach(v -> iamUserMapper.updateLdapUser(v));

    }

    //插入用户
    private List<OauthLdapErrorUser> insertLdapUser(Long historyId, List<IamUser> insertUser, Long organizationId) {
        if (CollectionUtils.isEmpty(insertUser)) return new ArrayList<>();

        //默认都是成功的
        for (IamUser v : insertUser) {
            iamUserMapper.insert(v);
            IamUserOrganizationRel iamUserOrganizationRel = new IamUserOrganizationRel();
            iamUserOrganizationRel.setUserId(v.getId());
            iamUserOrganizationRel.setOrganizationId(organizationId);
            iamUserOrganizationRelMapper.insert(iamUserOrganizationRel);
        }
        Set<Long> insertIds = insertUser.stream().map(IamUser::getId).collect(Collectors.toSet());
        //临时步骤 往老行云同步用户信息
        log.info("ldap同步用户，记录{},同步用户数量{}", historyId, insertUser.size());
        ResponseEntity<List<OauthLdapErrorUser>> errorUserResp = null;
        try {
            errorUserResp = iamServiceClient.syncSteamUser(organizationId, insertUser);
            if (!Objects.equals(errorUserResp.getStatusCode(), HttpStatus.OK)) {
                log.warn("同步用户老行云异常{}", errorUserResp);
                return operationErrorUser(organizationId, historyId, insertUser);
            }
        } catch (Exception e) {
            log.warn("同步用户老行云异常{}", e);
            return operationErrorUser(organizationId, historyId, insertUser);

        }
        List<OauthLdapErrorUser> errorUsers = errorUserResp.getBody();
        List<Long> errorIds = new ArrayList<>();
        //删除错误数据
        if (!CollectionUtils.isEmpty(errorUsers)) {
            for (OauthLdapErrorUser eUser : errorUsers) {
                if (!insertIds.contains(eUser.getId())) {
                    log.warn("ldap同步数据严重警告===发送插入的数据id与返回的数据id不一致{},返回{}", insertIds, eUser.getId());
                    continue;
                }
                eUser.setUuid("--");
                eUser.setLdapHistoryId(historyId);
                errorIds.add(eUser.getId());
                eUser.setId(null);
            }
            if (!CollectionUtils.isEmpty(errorIds)) {
                iamUserMapper.deleteBatchIds(errorIds);
                iamUserOrganizationRelMapper.delete(Wrappers.<IamUserOrganizationRel>lambdaQuery()
                        .in(IamUserOrganizationRel::getUserId, errorIds));
            }
        }
        if (errorIds.size() == insertIds.size()) return errorUsers;
        //删除无效的用户，有效用户进行授权
        if (!CollectionUtils.isEmpty(errorIds)) {
            insertIds.removeAll(errorIds);
        }
        //如果存在有效用户
        if (!CollectionUtils.isEmpty(insertIds)) {
            //获取组织成员权限
            IamRole iamRole = iamRoleMapper.selectOne(Wrappers.<IamRole>lambdaQuery()
                    .eq(IamRole::getFdLevel, ResourceLevel.ORGANIZATION.value())
                    .eq(IamRole::getCode, InitRoleCode.ORGANIZATION_MEMBER));
            if (Objects.isNull(iamRole)) {
                log.warn("查询组织成员权限失败{},{}", ResourceLevel.ORGANIZATION.value(), InitRoleCode.ORGANIZATION_MEMBER);
                return errorUsers;
            }
            Set<Long> roleIds = new HashSet<>();
            roleIds.add(iamRole.getId());
            iamMemberRoleService.grantUserRole(new HashSet<>(insertIds), roleIds, organizationId, ResourceLevel.ORGANIZATION);
        }
        //发起用户创建saga服务
        applicationEventPublisher.publishEvent(new IamUserLdapBatchCreateEvent(organizationId, insertUser.stream().filter(v -> insertIds.contains(v.getId())).collect(Collectors.toList())));
        //密码字段需要单独处理
        iamUserMapper.batchUpdateLdapPassword(insertIds, "ldap users do not have password");
        return errorUsers;
    }

    //调用老行云报错，修改
    private List<OauthLdapErrorUser> operationErrorUser(Long organizationId, Long historyId, List<IamUser> insertUser) {
        //删除错误的数据
        insertUser.forEach(v -> {
            iamUserMapper.deleteById(v.getId());
            iamUserOrganizationRelMapper.delete(Wrappers.<IamUserOrganizationRel>lambdaQuery()
                    .eq(IamUserOrganizationRel::getUserId, v.getId())
                    .eq(IamUserOrganizationRel::getOrganizationId, organizationId));
        });
        //错误数据返回
        return
                insertUser.stream().map(v -> OauthLdapErrorUser.builder().cause(LdapSyncUserErrorEnum.SYNC_STEAM_USER_ERROR.getMsg())
                        .email(v.getEmail())
                        .loginName(v.getLoginName())
                        .phone(v.getPhone())
                        .realName(v.getRealName())
                        .uuid("--")
                        .ldapHistoryId(historyId).build()
                ).collect(Collectors.toList());
    }


    //用户ldap匹配
    private List<IamUserOrganizationRel> matchLdapInfo(List<IamUser> matchLdapUser, OauthLdapDTO oauthLdapDTO, OauthLdapHistory oauthLdapHistory, List<OauthLdapErrorUser> errorUsers, List<IamUser> updateUser) {
        List<IamUserOrganizationRel> bandUser = new ArrayList<>();
        if (CollectionUtils.isEmpty(matchLdapUser)) return bandUser;
        List<Long> ids = matchLdapUser.stream().map(IamUser::getId).collect(Collectors.toList());
        //防止 in太多
        List<List<Long>> pageIds = CommonCollectionUtils.subList(ids, 999);
        Set<Long> sameLdapUserIds = new HashSet<>();
        pageIds.forEach(v -> sameLdapUserIds.addAll(oauthLdapMapper.matchLdapByUserIdAndLdap(v, oauthLdapDTO)));
        //遍历用户信息
        for (IamUser user : matchLdapUser) {
            //匹配到相同的ldap，进行绑定操作
            if (sameLdapUserIds.contains(user.getId())) {
                IamUserOrganizationRel iamUserOrganizationRel = new IamUserOrganizationRel();
                iamUserOrganizationRel.setOrganizationId(oauthLdapDTO.getOrganizationId());
                iamUserOrganizationRel.setUserId(user.getId());
                //修改用户
                updateUser.add(user);
                //绑定用户
                bandUser.add(iamUserOrganizationRel);
                //为找到相同的用户属于异常用户
            } else {
                userToErrorUser(oauthLdapHistory, user, errorUsers, LdapSyncUserErrorEnum.SAME_LOGIN_DIFF_LDAP.getMsg());
                log.warn("ldap匹配，ldap id：{}，获取的用户{} 已经存在，存在用户的ldap与当前ldap配置不一致", oauthLdapDTO.getId(), user.getLoginName());
            }
        }
        return bandUser;
    }

    private void userToErrorUser(OauthLdapHistory oauthLdapHistory, IamUser user, List<OauthLdapErrorUser> errorUsers, String msg) {
        OauthLdapErrorUser oauthLdapErrorUser = new OauthLdapErrorUser();
        oauthLdapErrorUser.setUuid("--");//别对阶段没有传递过来，使用默认值
        oauthLdapErrorUser.setRealName(user.getRealName());
        oauthLdapErrorUser.setPhone(user.getPhone());
        oauthLdapErrorUser.setLoginName(user.getLoginName());
        oauthLdapErrorUser.setLdapHistoryId(oauthLdapHistory.getId());
        oauthLdapErrorUser.setEmail(user.getEmail());
        oauthLdapErrorUser.setCause(msg);
        errorUsers.add(oauthLdapErrorUser);
    }

    //邮箱用户名关系
    private Map<String, String> getEmailUser(Set<String> emails) {
        List<UserMatchLdapDTO> emailUser = iamUserMapper.selectEmailUserByEmail(emails);
        return emailUser.stream().collect(Collectors.toMap(UserMatchLdapDTO::getEmail, UserMatchLdapDTO::getLoginName));
    }

    //通过登入账号获取用户信息
    private Map<String, UserMatchLdapDTO> getUserLdapMap(Set<String> loginNames) {
        List<UserMatchLdapDTO> userMatchLdapDTO = iamUserMapper.selectUserMatchLdapByLoginName(loginNames);
        return userMatchLdapDTO.stream().collect(Collectors.toMap(UserMatchLdapDTO::getLoginName, Function.identity()));
    }


    /**
     * LDAP 属性转用户对象
     *
     * @param history        同步记录
     * @param attributesList ldap属性
     * @param normalUser     正常用户信息
     * @param errorUsers     异常用户信息
     * @param oauthLdapDTO   ldap 配置信息
     */
    private void transformationToUser(OauthLdapHistory history, List<Attributes> attributesList, List<IamUser> normalUser, List<OauthLdapErrorUser> errorUsers, OauthLdapDTO oauthLdapDTO) {
        Long orgId = oauthLdapDTO.getId();
        Long historyId = history.getId();
        IamUser iamUser;
        //此处可以设置成枚举或者集合对象遍历
        //当前考虑性能问题，暂不做改变，如果性能达标后续可以修改，更好的去维护使用
        //属性定义
        Attribute uuidAttribute;
        Attribute loginNameAttribute;
        Attribute realNameAttribute;
        Attribute emailAttribute;
        Attribute phoneAttribute;
        //字段对照
        String uuidField = oauthLdapDTO.getUuidField();
        String loginNameField = oauthLdapDTO.getLoginNameField();
        String realNameField = oauthLdapDTO.getRealNameField();
        String emailField = oauthLdapDTO.getEmailField();
        String phoneField = oauthLdapDTO.getPhoneField();
        boolean phoneExist = Objects.nonNull(phoneField);
        //错误数量
        int errorCount = 0;
        for (Attributes attributes : attributesList) {
            String uuid = null;
            String loginName = null;
            String realName = null;
            String email = null;
            String phone = null;
            //获取属性
            uuidAttribute = attributes.get(uuidField);
            loginNameAttribute = attributes.get(loginNameField);
            realNameAttribute = attributes.get(realNameField);
            emailAttribute = attributes.get(emailField);
            if (phoneExist) {
                phoneAttribute = attributes.get(phoneField);
            } else {
                phoneAttribute = null;
            }
            //获取字段
            //uuid错误表为必填，uuid错误的不记录数据库
            if (Objects.isNull(uuidAttribute)) {
                log.warn("同步记录{},UUID属性获取为空，获取key为{},属性为{}", historyId, uuidField, attributes);
                errorCount++;
                continue;
            }

            try {
                uuid = uuidAttribute.get().toString();
            } catch (NamingException e) {
                log.warn("同步记录{},UUID属性获取失败，获取key为{},属性为{}", historyId, uuidField, attributes);
                errorCount++;
                continue;
            }
            //登入名
            if (Objects.isNull(loginNameAttribute)) {
                log.warn("同步记录{},loginName属性获取为空，获取key为{},属性为{}", historyId, loginNameField, attributes);
                errorCount++;
                errorUsers.add(errorUser(historyId, uuid, loginName, realName, email, phone, LdapSyncUserErrorEnum.LOGIN_NAME_NOT_FOUND.getMsg()));
                continue;
            }
            try {
                loginName = loginNameAttribute.get().toString();
            } catch (NamingException e) {
                log.warn("同步记录{},loginName属性获取失败，获取key为{},属性为{}", historyId, loginNameField, attributes);
                errorCount++;
                errorUsers.add(errorUser(historyId, uuid, loginName, realName, email, phone, LdapSyncUserErrorEnum.LOGIN_NAME_GET_FAILURE.getMsg()));
                continue;
            }
            //真实名称
            if (Objects.isNull(realNameAttribute)) {
                log.warn("同步记录{},realName属性获取为空，获取key为{},属性为{}", historyId, realNameField, attributes);
                errorCount++;
                errorUsers.add(errorUser(historyId, uuid, loginName, realName, email, phone, LdapSyncUserErrorEnum.REAL_NAME_NOT_FOUND.getMsg()));
                continue;
            }

            try {
                realName = realNameAttribute.get().toString();
            } catch (NamingException e) {
                log.warn("同步记录{},realName属性获取失败，获取key为{},属性为{}", historyId, realNameField, attributes);
                errorCount++;
                errorUsers.add(errorUser(historyId, uuid, loginName, realName, email, phone, LdapSyncUserErrorEnum.REAL_NAME_GET_FAILURE.getMsg()));
                continue;
            }
            //email
            if (Objects.isNull(emailAttribute)) {
                log.warn("同步记录{},email属性获取为空，获取key为{},属性为{}", historyId, emailField, attributes);
                errorCount++;
                errorUsers.add(errorUser(historyId, uuid, loginName, realName, email, phone, LdapSyncUserErrorEnum.EMAIL_NOT_FOUND.getMsg()));
                continue;
            }

            try {
                email = emailAttribute.get().toString();
            } catch (NamingException e) {
                log.warn("同步记录{},email属性获取失败，获取key为{},属性为{}", historyId, emailField, attributes);
                errorCount++;
                continue;
            }
            //电话号码可以为空
            try {
                if (Objects.nonNull(phoneAttribute)) {
                    phone = uuidAttribute.get().toString();
                }
            } catch (NamingException e) {
                log.warn("同步记录{},phone属性获取失败，获取key为{},属性为{}", historyId, phoneField, attributes);
            }
            iamUser = new IamUser();
            iamUser.setEmail(email);
            iamUser.setIsAdmin(Boolean.FALSE);
            iamUser.setIsEnabled(Boolean.TRUE);
            iamUser.setIsLdap(Boolean.TRUE);
            iamUser.setIsLocked(Boolean.FALSE);
            iamUser.setLanguage("zh_CN");
            iamUser.setLastPasswordUpdatedAt(new Date());
            iamUser.setLoginName(loginName);
            iamUser.setPhone(phone);
            iamUser.setRealName(realName);
            iamUser.setTimeZone("CTT");
            iamUser.setHashPassword("ldap users do not have password");
            normalUser.add(iamUser);
        }
        history.setErrorUserCount(history.getErrorUserCount() + errorCount);
    }

    private OauthLdapErrorUser errorUser(Long historyId, String uuid, String loginName, String realName, String email, String phone, String cause) {
        OauthLdapErrorUser oauthLdapErrorUser = new OauthLdapErrorUser();
        oauthLdapErrorUser.setCause(cause);
        oauthLdapErrorUser.setEmail(email);
        oauthLdapErrorUser.setLdapHistoryId(historyId);
        oauthLdapErrorUser.setLoginName(loginName);
        oauthLdapErrorUser.setPhone(phone);
        oauthLdapErrorUser.setRealName(realName);
        oauthLdapErrorUser.setUuid(uuid);
        return oauthLdapErrorUser;
    }


    //对比属性
    private void matchAttributes(OauthLdapDTO oauthLdapDTO, LdapConnectionDTO ldapConnectionDTO, List<Attributes> attributesList) {
        Set<String> key = new HashSet<>();
        //抽样的所有属性字段
        for (Attributes attributes : attributesList) {
            NamingEnumeration<String> attributesIDs = attributes.getIDs();
            while (attributesIDs != null && attributesIDs.hasMoreElements()) {
                key.add(attributesIDs.nextElement());
            }
        }
        //默认匹配
        ldapConnectionDTO.setMatchAttribute(Boolean.TRUE);
        //不做枚举，变动性不可测
        ldapConnectionDTO.setUuidField(matchs(key, oauthLdapDTO.getUuidField(), ldapConnectionDTO));
        ldapConnectionDTO.setEmailField(matchs(key, oauthLdapDTO.getEmailField(), ldapConnectionDTO));
        ldapConnectionDTO.setLoginNameField(matchs(key, oauthLdapDTO.getLoginNameField(), ldapConnectionDTO));
        ldapConnectionDTO.setRealNameField(matchs(key, oauthLdapDTO.getRealNameField(), ldapConnectionDTO));
        ldapConnectionDTO.setPhoneField(matchs(key, oauthLdapDTO.getPhoneField(), ldapConnectionDTO));
    }

    //匹配属性
    private Boolean matchs(Set<String> key, String field, LdapConnectionDTO ldapConnectionDTO) {
        //属性不存在，不匹配
        if (StringUtils.isEmpty(field)) return null;
        Boolean b = key.contains(field);
        //只要有一个属性不存在，则匹配失败
        if (!b) ldapConnectionDTO.setMatchAttribute(Boolean.FALSE);
        return b;
    }


    //设置用户对象过滤条件
    private AndFilter getAndFilterByObjectClass(OauthLdapDTO oauthLdapDTO) {
        String objectClass = oauthLdapDTO.getObjectClass();
        String[] arr = objectClass.split(",");
        AndFilter andFilter = new AndFilter();
        for (String str : arr) {
            andFilter.and(new EqualsFilter(OBJECT_CLASS, str));
        }
        return andFilter;
    }

    //初始默认值
    private void initLdapStatus(LdapConnectionDTO ldapConnectionDTO) {
        ldapConnectionDTO.setCanConnectServer(Boolean.FALSE);
        ldapConnectionDTO.setCanLogin(Boolean.FALSE);
        ldapConnectionDTO.setMatchAttribute(Boolean.FALSE);

    }
}

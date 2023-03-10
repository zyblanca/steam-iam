package com.crc.crcloud.steam.iam.service.impl;


import cn.hutool.core.bean.BeanDesc;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crc.crcloud.steam.iam.common.enums.MemberType;
import com.crc.crcloud.steam.iam.common.enums.UserOriginEnum;
import com.crc.crcloud.steam.iam.common.exception.IamAppCommException;
import com.crc.crcloud.steam.iam.common.utils.*;
import com.crc.crcloud.steam.iam.dao.*;
import com.crc.crcloud.steam.iam.entity.*;
import com.crc.crcloud.steam.iam.model.dto.*;
import com.crc.crcloud.steam.iam.entity.*;
import com.crc.crcloud.steam.iam.model.dto.IamProjectDTO;
import com.crc.crcloud.steam.iam.model.dto.IamRoleDTO;
import com.crc.crcloud.steam.iam.model.dto.IamUserDTO;
import com.crc.crcloud.steam.iam.model.dto.UserSearchDTO;
import com.crc.crcloud.steam.iam.model.dto.iam.RoleAssignmentSearchDTO;
import com.crc.crcloud.steam.iam.model.dto.iam.UserWithRoleDTO;
import com.crc.crcloud.steam.iam.model.dto.payload.UserMemberEventPayload;
import com.crc.crcloud.steam.iam.model.dto.user.SearchDTO;
import com.crc.crcloud.steam.iam.model.event.IamUnbindUserRoleEvent;
import com.crc.crcloud.steam.iam.model.event.IamUpdateUserRoleEvent;
import com.crc.crcloud.steam.iam.model.event.IamUserManualCreateEvent;
import com.crc.crcloud.steam.iam.model.feign.role.RoleDTO;
import com.crc.crcloud.steam.iam.model.vo.IamOrganizationVO;
import com.crc.crcloud.steam.iam.model.vo.IamProjectVO;
import com.crc.crcloud.steam.iam.model.vo.IamUserVO;
import com.crc.crcloud.steam.iam.model.vo.user.IamOrganizationUserPageRequestVO;
import com.crc.crcloud.steam.iam.model.vo.user.IamUserCreateRequestVO;
import com.crc.crcloud.steam.iam.service.IamMemberRoleService;
import com.crc.crcloud.steam.iam.service.IamOrganizationService;
import com.crc.crcloud.steam.iam.service.IamUserOrganizationRelService;
import com.crc.crcloud.steam.iam.service.IamUserService;
import io.choerodon.core.convertor.ApplicationContextHelper;
import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.InitRoleCode;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Nullable;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @Author
 * @Description
 * @Date 2019-11-12
 */
@Validated
@Slf4j
@Service
public class IamUserServiceImpl implements IamUserService {

    @Autowired
    private IamUserMapper iamUserMapper;
    @Autowired
    private IamUserOrganizationRelService iamUserOrganizationRelService;
    @Autowired
    private IamMemberRoleService iamMemberRoleService;
    @Autowired
    private IamMemberRoleMapper iamMemberRoleMapper;
    @Autowired
    private IamProjectMapper iamProjectMapper;
    @Autowired
    private IamRoleMapper iamRoleMapper;
    @Autowired
    private IamUserOrganizationRelMapper iamUserOrganizationRelMapper;
    @Autowired
    private IamOrganizationMapper iamOrganizationMapper;
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;
    /**
     * ????????????
     */
    private final BCryptPasswordEncoder ENCODER = new BCryptPasswordEncoder();

    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    @Override
    public @NotNull
    IamUserDTO createUserByManual(@Valid IamUserCreateRequestVO vo, @NotEmpty Set<Long> organizationIds) {
        @NotNull IamUserDTO user = createUserByManual(vo);
        log.info("??????????????????[{}],??????????????????[{}]", vo.getRealName(), CollUtil.join(organizationIds, ","));
        iamUserOrganizationRelService.link(user.getId(), organizationIds);
        iamUserOrganizationRelService.getUserOrganizations(user.getId()).stream().findFirst().ifPresent(t -> {
            this.updateUserCurrentOrganization(t.getUserId(), t.getOrganizationId());
        });
        ApplicationContextHelper.getContext().publishEvent(new IamUserManualCreateEvent(user, vo.getPassword()));
        log.info("??????????????????[{}],??????????????????[{}]", vo.getRealName(), CollUtil.join(vo.getRoleIds(), ","));
        for (Long organizationId : organizationIds) {
            iamMemberRoleService.grantUserRole(user.getId(), vo.getRoleIds(), organizationId, ResourceLevel.ORGANIZATION);
        }
        return user;
    }

    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public @NotNull
    IamUserDTO createUserByManual(@Valid IamUserCreateRequestVO vo) {
        //??????????????????????????????????????????????????????2?????????????????????????????????.-
        Predicate<String> matchLoginName = loginName -> ReUtil.isMatch("^[a-zA-Z0-9._][a-zA-Z0-9_.-]+$", loginName);
        //?????????. .git .atom ??????
        matchLoginName = matchLoginName.and(loginName -> !StrUtil.endWithAny(loginName, ".", ".git", ".atom"));
        if (matchLoginName.negate().test(vo.getLoginName())) {
            throw new IamAppCommException("user.loginName.content");
        }
        if (getByLoginName(vo.getLoginName()).isPresent()) {
            throw new IamAppCommException("user.loginName.exist");
        }
        if (getByEmail(vo.getEmail()).isPresent()) {
            throw new IamAppCommException("user.email.exist");
        }
        IamUser entity = new IamUser();
        BeanUtil.copyProperties(vo, entity);
        initUser(entity);
        iamUserMapper.insert(entity);
        iamUserMapper.fillHashPassword(entity.getId(), ENCODER.encode(vo.getPassword()));
        log.info("??????????????????[{}]", vo.getRealName());
        return ConvertHelper.convert(entity, IamUserDTO.class);
    }

    /**
     * ??????????????????????????????,???????????????null?????????????????????
     * <p>???????????????{@link IamUser#getIsAdmin()} ()}???false</p>
     * <p>??????????????????{@link IamUser#getInternationalTelCode()} ()}???+86</p>
     * <p>??????{@link IamUser#getLanguage()}???zh_CN</p>
     * <p>??????{@link IamUser#getTimeZone()}???CTT</p>
     * <p>????????????{@link IamUser#getIsEnabled()}???true</p>
     * <p>??????????????????{@link IamUser#getIsLocked()}???false</p>
     * <p>??????ldap??????{@link IamUser#getIsLdap()}???false</p>
     *
     * @param entity ??????
     */
    private void initUser(@NotNull IamUser entity) {
        IamUser init = IamUser.builder()
                .isAdmin(false)
                .internationalTelCode("+86")
                .language("zh_CN")
                .timeZone("CTT")
                .isEnabled(true)
                .isLocked(false)
                .isLdap(false)
                .build();
        Map<String, Object> initField = BeanUtil.beanToMap(init, false, true);
        BeanDesc beanDesc = BeanUtil.getBeanDesc(IamUser.class);
        initField.forEach((k, v) -> {
            BeanDesc.PropDesc prop = beanDesc.getProp(k);
            Object value = prop.getValue(entity);
            if (Objects.isNull(value)) {
                prop.setValue(entity, v);
            }
        });
    }

    @Override
    public Optional<IamUserDTO> getByLoginName(@NotBlank String loginName) {
        return getOne(t -> t.eq(IamUser::getLoginName, loginName));
    }

    @Override
    public Optional<IamUserDTO> getByEmail(@NotBlank String email) {
        return getOne(t -> t.eq(IamUser::getEmail, email));
    }

    @Override
    public Boolean projectOwner(Long projectId) {

        Long userId = UserDetail.getUserId();
        IamMemberRoleDTO iamMemberRoleDTO = new IamMemberRoleDTO();
        iamMemberRoleDTO.setMemberId(userId);
        iamMemberRoleDTO.setMemberType("user");
        iamMemberRoleDTO.setSourceType("project");
        iamMemberRoleDTO.setSourceId(projectId);
        //???????????????????????????????????????
        List<IamRole> roles = iamRoleMapper.selectRolesByMemberRole(iamMemberRoleDTO);
        if (CollectionUtils.isEmpty(roles)) {
            return Boolean.FALSE;
        }

        return roles.stream().anyMatch(v -> Objects.equals(v.getCode(), InitRoleCode.PROJECT_OWNER));
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void projectUnbindUser(@NotNull Long projectId, @NotNull Long userId) {
        //???????????????????????????
        List<IamMemberRole> iamMemberRoles = iamMemberRoleMapper.selectList(Wrappers.<IamMemberRole>lambdaQuery()
                .eq(IamMemberRole::getSourceId, projectId)
                .eq(IamMemberRole::getMemberId, userId)
                .eq(IamMemberRole::getMemberType, MemberType.USER.getValue())
                .eq(IamMemberRole::getSourceType, ResourceLevel.PROJECT.value())
        );
        if (CollectionUtils.isEmpty(iamMemberRoles)) {
            return;
        }

        //??????????????????
        iamMemberRoleMapper.deleteBatchIds(iamMemberRoles.stream().map(IamMemberRole::getId).collect(Collectors.toList()));
        //???????????????????????????????????????????????????
        checkHasOwner(projectId);

        //????????????
        IamUser iamUser = iamUserMapper.selectById(userId);
        //??????saga????????????
        List<UserMemberEventPayload> userMemberEventPayloads = new ArrayList<>();
        UserMemberEventPayload userMemberEventPayload = new UserMemberEventPayload();
        userMemberEventPayload.setUsername(iamUser.getLoginName());
        userMemberEventPayload.setUserId(userId);
        userMemberEventPayload.setResourceId(projectId);
        userMemberEventPayload.setResourceType(ResourceLevel.PROJECT.value());
        userMemberEventPayloads.add(userMemberEventPayload);
        IamUnbindUserRoleEvent event = new IamUnbindUserRoleEvent(userMemberEventPayloads, ResourceLevel.PROJECT, projectId, userMemberEventPayloads);
        //??????????????????
        applicationEventPublisher.publishEvent(event);


    }

    //?????????????????????????????????????????????
    private void checkHasOwner(Long projectId) {
        //?????????????????????
        IamRole iamRole = iamRoleMapper.selectOne(Wrappers.<IamRole>lambdaQuery()
                .eq(IamRole::getCode, InitRoleCode.PROJECT_OWNER)
                .eq(IamRole::getFdLevel, ResourceLevel.PROJECT.value())
                .eq(IamRole::getIsEnabled, Boolean.TRUE));
        //??????????????????????????????????????????
        int count = iamMemberRoleMapper.selectCount(Wrappers.<IamMemberRole>lambdaQuery()
                .eq(IamMemberRole::getMemberType, MemberType.USER.getValue())
                .eq(IamMemberRole::getRoleId, iamRole.getId())
                .eq(IamMemberRole::getSourceId, projectId)
                .eq(IamMemberRole::getSourceType, ResourceLevel.PROJECT.value()));
        if (0 == count) {
            throw new IamAppCommException("project.owner.exist");
        }

    }

    @Override
    public IamUserDTO queryProjectRole(@NotNull Long projectId, @NotNull Long userId) {
        IamUser iamUser = iamUserMapper.selectById(userId);
        if (Objects.isNull(iamUser)) {
            throw new IamAppCommException("user.not.exist");
        }

        IamUserDTO iamUserDTO = new IamUserDTO();
        BeanUtil.copyProperties(iamUser, iamUserDTO);
        //???????????????????????????
        List<IamMemberRole> iamMemberRoles = iamMemberRoleMapper.selectList(Wrappers.<IamMemberRole>lambdaQuery()
                .eq(IamMemberRole::getSourceId, projectId)
                .eq(IamMemberRole::getMemberId, userId)
                .eq(IamMemberRole::getMemberType, MemberType.USER.getValue())
                .eq(IamMemberRole::getSourceType, ResourceLevel.PROJECT.value())
        );

        iamUserDTO.setRoleIds(iamMemberRoles.stream().map(IamMemberRole::getRoleId).collect(Collectors.toList()));
        return iamUserDTO;
    }

    /**
     * ???????????????????????????
     *
     * @param consumer ??????
     * @return ??????????????????????????????
     */
    private Optional<IamUserDTO> getOne(@NotNull Consumer<LambdaQueryWrapper<IamUser>> consumer) {
        LambdaQueryWrapper<IamUser> queryWrapper = Wrappers.<IamUser>lambdaQuery();
        consumer.accept(queryWrapper);
        return iamUserMapper.selectList(queryWrapper)
                .stream()
                .findFirst()
                .map(t -> ConvertHelper.convert(t, IamUserDTO.class));
    }

    @Override
    public IamUserDTO getAndThrow(@NotNull Long userId) {
        return getOne(t -> t.eq(IamUser::getId, userId)).orElseThrow(() -> new IamAppCommException("user.not.exist"));
    }

    @Override
    public IPage<IamUserVO> pageQueryOrganizationUser(@NotNull Long organizationId, @Valid IamOrganizationUserPageRequestVO vo) {
        SearchDTO searchDTO = new SearchDTO();
        BeanUtil.copyProperties(vo, searchDTO);
        // ????????????
        if (Objects.nonNull(vo.getOrigins()) && CollUtil.isNotEmpty(vo.getOrigins())) {
            UserOriginEnum[] userOriginEnums = UserOriginEnum.values();
            if (vo.getOrigins().stream().noneMatch(t -> Arrays.stream(userOriginEnums).anyMatch(origin -> origin.equalsValue(t)))) {
                log.warn("????????????????????????;[{}]", CollUtil.join(vo.getOrigins(), ""));
                return new Page<>(vo.getCurrent(), vo.getSize());
            }
            Set<UserOriginEnum> collect = Arrays.stream(userOriginEnums).filter(t -> vo.getOrigins().contains(t.getValue())).collect(Collectors.toSet());
            if (collect.size() == 1) {
                switch (CollUtil.getFirst(collect)) {
                    case LDAP:
                        searchDTO.setIsLdap(Boolean.TRUE);
                        break;
                    case MANUAL:
                        searchDTO.setIsLdap(Boolean.FALSE);
                        break;
                    default:
                }
            }
        }
        //??????????????????
        final Page<IamUser> page = new Page<>(vo.getCurrent(), vo.getSize());
        page.setAsc(vo.getAsc());
        page.setDesc(vo.getDesc());
        PageWrapper<IamUser> pageWrapper = PageWrapper.instance(page);
        pageWrapper.addGbkFieldConvert(IamUser::getRealName);
        pageWrapper.addSortFieldConvert(origin -> EntityUtil.getSimpleField(IamUser::getIsLdap), "origin");
        //roleName-> imr.role_id
        pageWrapper.addSortFieldConvert(t -> EntityUtil.getSimpleField(IamMemberRole::getRoleId), "roleName");
        pageWrapper.addTableAliasSortFieldConvert("imr", "roleName");
        //creationDate->any_value(imr.creation_date)
        pageWrapper.addDefaultOrderByDesc(IamUser::getCreationDate);
        pageWrapper.addTableAliasSortFieldConvert("imr", IamUser::getCreationDate);
        pageWrapper.addSortFieldConvert(t -> StrUtil.format("ANY_VALUE({})", t), IamUser::getCreationDate);
        pageWrapper.addSortFieldConvert(t -> StrUtil.format("MAX({})", t), IamUser::getCreationDate);
        IPage<IamUser> pageResult = iamUserMapper.pageQueryOrganizationUser(pageWrapper, CollUtil.newHashSet(organizationId), searchDTO);
        return pageResult.convert(t -> CopyUtil.copy(t, IamUserVO.class));
    }

    @Override
    public IPage<IamUserVO> pageByProject(Long projectId, UserSearchDTO userSearchDTO, PageUtil page) {

        userSearchDTO.setProjectId(projectId);
        userSearchDTO.setMemberSourceType(ResourceLevel.PROJECT.value());
        userSearchDTO.setMemberType(MemberType.USER.getValue());
        //?????????????????????
        IPage<IamUserDTO> userPage = iamUserMapper.pageByProject(page, userSearchDTO);

        IPage<IamUserVO> result = new Page<>();
        result.setTotal(userPage.getTotal());
        result.setSize(userPage.getSize());
        result.setRecords(CopyUtil.copyList(userPage.getRecords(), IamUserVO.class));
        return result;
    }

    @Override
    public List<IamUserVO> projectDropDownUser(Long projectId, UserSearchDTO userSearchDTO) {
        userSearchDTO.setProjectId(projectId);
        userSearchDTO.setMemberSourceType(ResourceLevel.PROJECT.value());
        userSearchDTO.setMemberType(MemberType.USER.getValue());
        //?????????????????????
        List<IamUser> users = iamUserMapper.projectDropDownUser(userSearchDTO);

        return CopyUtil.copyList(users, IamUserVO.class);
    }

    @Override
    public List<IamUserVO> projectUnselectUser(Long projectId, UserSearchDTO userSearchDTO) {
        IamProject project = getAndThrowProject(projectId);
        userSearchDTO.setProjectId(projectId);
        userSearchDTO.setMemberSourceType(ResourceLevel.PROJECT.value());
        userSearchDTO.setMemberType(MemberType.USER.getValue());
        userSearchDTO.setOrganizationId(project.getOrganizationId());
        //?????????????????????????????????????????????
        List<IamUser> users = iamUserMapper.projectUnselectUser(userSearchDTO);

        return CopyUtil.copyList(users, IamUserVO.class);
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void projectBindUsers(Long projectId, IamUserVO iamUserVO) {
        List<Long> userIds, roleIds;
        if (CollectionUtils.isEmpty(userIds = iamUserVO.getUserIds()) || CollectionUtils.isEmpty(roleIds = iamUserVO.getRoleIds())) {
            throw new IamAppCommException("comm.param.error");
        }
        //??????????????????????????????????????????????????????????????????
        checkProjectOwner(projectId, roleIds, userIds);
        //????????????
        Set<Long> newUser = new HashSet<>(userIds.size());
        //????????????
        Set<Long> allUser = new HashSet<>(userIds);
        for (Long userId : allUser) {
            //??????????????????
            List<IamMemberRole> iamMemberRoles = iamMemberRoleMapper.selectList(Wrappers.<IamMemberRole>lambdaQuery()
                    .eq(IamMemberRole::getSourceId, projectId)
                    .eq(IamMemberRole::getMemberId, userId)
                    .eq(IamMemberRole::getMemberType, MemberType.USER.getValue())
                    .eq(IamMemberRole::getSourceType, ResourceLevel.PROJECT.value())
            );
            //????????????????????????????????????
            if (CollectionUtils.isEmpty(iamMemberRoles)) {
                newUser.add(userId);
                continue;
            }
            //????????????????????????????????????
            updateUserProjectRole(projectId, userId, roleIds, iamMemberRoles);

        }

        //????????????????????????????????????
        if (CollectionUtils.isEmpty(newUser)) {
            return;
        }
        //??????????????????
        iamMemberRoleService.grantUserRole(newUser, new HashSet<>(roleIds), projectId, ResourceLevel.PROJECT);

    }

    //?????????????????????????????????????????????
    private void checkProjectOwner(Long projectId, List<Long> roleIds, List<Long> userIds) {
        //?????????????????????
        IamRole iamRole = iamRoleMapper.selectOne(Wrappers.<IamRole>lambdaQuery()
                .eq(IamRole::getCode, InitRoleCode.PROJECT_OWNER)
                .eq(IamRole::getFdLevel, ResourceLevel.PROJECT.value())
                .eq(IamRole::getIsEnabled, Boolean.TRUE));
        //?????????????????????????????????????????????????????????
        if (roleIds.contains(iamRole.getId())) {
            return;
        }
        //?????????????????? ???????????????????????????
        int count = iamMemberRoleMapper.selectCount(Wrappers.<IamMemberRole>lambdaQuery()
                .eq(IamMemberRole::getMemberType, MemberType.USER.getValue())
                .eq(IamMemberRole::getRoleId, iamRole.getId())
                .eq(IamMemberRole::getSourceId, projectId)
                .eq(IamMemberRole::getSourceType, ResourceLevel.PROJECT.value())
                .notIn(IamMemberRole::getMemberId, userIds));

        if (count == 0) {
            throw new IamAppCommException("project.owner.exist");
        }


    }

    //?????????????????????
    //???????????????????????????????????????????????????????????????
    private void updateUserProjectRole(Long projectId, Long userId, List<Long> roleIds, List<IamMemberRole> oldIamMemberRoles) {
        //???????????????????????????
        List<Long> needDelete = oldIamMemberRoles.stream().
                filter(v -> !roleIds.contains(v.getRoleId()))
                .map(IamMemberRole::getId)
                .collect(Collectors.toList());
        List<Long> oldRoles = oldIamMemberRoles.stream().map(IamMemberRole::getRoleId).collect(Collectors.toList());
        //???????????????????????????
        List<Long> needInsert = roleIds.stream().filter(v -> !oldRoles.contains(v)).collect(Collectors.toList());
        //??????????????????????????????????????????????????????????????????
        if (CollectionUtils.isEmpty(needDelete) && CollectionUtils.isEmpty(needInsert)) {
            return;
        }
        //???????????????????????????
        if (!CollectionUtils.isEmpty(needDelete)) {
            iamMemberRoleMapper.deleteBatchIds(needDelete);
        }
        //???????????????
        if (!CollectionUtils.isEmpty(needInsert)) {
            for (Long roleId : needInsert) {
                IamMemberRole iamMemberRole = new IamMemberRole();
                iamMemberRole.setMemberType(MemberType.USER.getValue());
                iamMemberRole.setSourceType(ResourceLevel.PROJECT.value());
                iamMemberRole.setMemberId(userId);
                iamMemberRole.setRoleId(roleId);
                iamMemberRole.setSourceId(projectId);
                iamMemberRoleMapper.insert(iamMemberRole);
            }
        }
        //??????saga??????????????????
        List<UserMemberEventPayload> userMemberEventPayloads = new ArrayList<>();
        IamUser iamUser = iamUserMapper.selectById(userId);
        UserMemberEventPayload userMemberEventPayload = new UserMemberEventPayload();
        userMemberEventPayload.setResourceType(ResourceLevel.PROJECT.value());
        userMemberEventPayload.setResourceId(projectId);
        userMemberEventPayload.setUserId(userId);
        userMemberEventPayload.setUsername(iamUser.getLoginName());
        userMemberEventPayloads.add(userMemberEventPayload);
        //??????saga
        IamUpdateUserRoleEvent iamUpdateUserRoleEvent = new IamUpdateUserRoleEvent(projectId, ResourceLevel.PROJECT, roleIds, userMemberEventPayloads, userMemberEventPayloads);
        applicationEventPublisher.publishEvent(iamUpdateUserRoleEvent);

    }

    @Override
    public List<IamUserVO> listUserByIds(List<Long> ids, Boolean onlyEnabled) {
        if (CollectionUtils.isEmpty(ids)) return new ArrayList<>();
        LambdaQueryWrapper<IamUser> query = Wrappers.<IamUser>lambdaQuery().in(IamUser::getId, ids);
        if (onlyEnabled) {
            query.eq(IamUser::getIsEnabled, onlyEnabled);
        }
        List<IamUser> iamUsers = iamUserMapper.selectList(query);
        if (CollectionUtils.isEmpty(iamUsers)) return new ArrayList<>();
        //?????????????????????????????? id ?????????  ????????? ??????
        return iamUsers.stream().map(v -> IamUserVO.builder().id(v.getId())
                .loginName(v.getLoginName()).realName(v.getRealName()).email(v.getEmail()).build()).collect(Collectors.toList());
    }

    private IamProject getAndThrowProject(Long projectId) {
        IamProject iamProject = iamProjectMapper.selectById(projectId);
        if (Objects.isNull(iamProject))
            throw new IamAppCommException("project.data.null");
        return iamProject;
    }

    @Override
    public Optional<String> getHashPassword(@NotNull Long userId) {
        String hashPassword = iamUserMapper.getHashPassword(userId);
        return Optional.ofNullable(hashPassword).filter(StrUtil::isNotBlank);
    }

    @Override
    public void updateUserCurrentOrganization(@NotNull Long userId, @NotNull Long currentOrganizationId) {
        Assert.notNull(userId);
        ApplicationContextHelper.getContext().getBean(IamOrganizationService.class).get(currentOrganizationId)
                .ifPresent(org -> iamUserMapper.updateById(IamUser.builder().id(userId).currentOrganizationId(currentOrganizationId).build()));
    }

    @Override
    public IPage<UserWithRoleDTO> pagingQueryUsersWithProjectLevelRoles(PageUtil pageUtil, RoleAssignmentSearchDTO roleAssignmentSearchDTO, Long sourceId, boolean doPage) {
        List<UserWithRoleDTO> result;
        long total;
        long size;
        if (doPage) {
            long page = pageUtil.getCurrent() < 1 ? 1L : pageUtil.getCurrent();
            size = pageUtil.getSize() < 1 ? 10L : pageUtil.getSize();
            long start = page * size;
            total = iamUserMapper.selectCountUsers(roleAssignmentSearchDTO, sourceId, ResourceLevel.PROJECT.value());
            if (total > 0) {
                result = getUserRoleData(roleAssignmentSearchDTO, sourceId, ResourceLevel.PROJECT.value(), start, size);
            } else {
                result = new ArrayList<>();
            }
        } else {
            result = getUserRoleData(roleAssignmentSearchDTO, sourceId, ResourceLevel.PROJECT.value(), null, null);
            size = result.size();
            total = result.size();
        }

        IPage<UserWithRoleDTO> page = new Page<>();
        page.setRecords(result);
        page.setSize(size);
        page.setTotal(total);
        return page;
    }

    @Override
    public IamUserVO querySelf() {
        Long userId = UserDetail.getUserId();
        IamUser user = iamUserMapper.selectById(userId);
        user.setHashPassword(null);
        IamUserVO iamUserVO = CopyUtil.copy(user, IamUserVO.class);

        List<IamUserOrganizationRel> rels = iamUserOrganizationRelMapper.selectList(Wrappers.<IamUserOrganizationRel>lambdaQuery().eq(IamUserOrganizationRel::getUserId, userId));
        if (CollectionUtils.isEmpty(rels)) return iamUserVO;
        List<IamOrganization> organizations = iamOrganizationMapper.selectBatchIds(rels.stream().map(IamUserOrganizationRel::getOrganizationId).collect(Collectors.toList()));
        List<IamOrganizationVO> organizationVOS = new ArrayList<>();
        for (IamOrganization organization : organizations) {
            organizationVOS.add(IamOrganizationVO.builder().name(organization.getName())
                    .code(organization.getCode()).id(organization.getId()).build());
        }
        iamUserVO.setOrganizations(organizationVOS);
        return iamUserVO;
    }

    @Override
    public Long[] listUserIds() {
        return iamUserMapper.selectAllIds();
    }

    @Override
    public IPage<IamUserVO> pagingQueryUsers(PageUtil pageUtil, IamUserDTO userDTO) {

        return CopyUtil.copyPage(iamUserMapper.pagingQueryUsers(pageUtil, userDTO), IamUserVO.class);
    }

    @Override
    public List<IamProjectVO> queryProjectsNew(Long id, @NotNull Long organizationId, boolean includedDisabled) {
        CustomUserDetails customUserDetails = checkLoginUser(id);
        boolean isAdmin = customUserDetails.getAdmin() == null ? false : customUserDetails.getAdmin();
        //superAdmin????????????
        if (isAdmin) {
            LambdaQueryWrapper<IamProject> queryWrapper = Wrappers.<IamProject>lambdaQuery()
                    .eq(IamProject::getOrganizationId, organizationId)
                    .eq(IamProject::getIsEnabled, true)
                    .orderByDesc(IamProject::getCreationDate);
            return CopyUtil.copyList(iamProjectMapper.selectList(queryWrapper), IamProjectVO.class);
        } else {
            IamProjectDTO project = new IamProjectDTO();
            if (!includedDisabled) {
                project.setIsEnabled(true);
            }
            //?????????????????????????????????,??????????????????????????????,??????????????????????????????
            checkCurrentOrganization(id);

            return CopyUtil.copyList(iamProjectMapper
                    .selectProjectsByUserIdAndCurrentOrgId(id, project, organizationId), IamProjectVO.class);
        }
    }

    @Override
    public List<IamUserVO> listByProject(Long projectId, UserSearchDTO userSearchDTO) {
        userSearchDTO.setProjectId(projectId);
        userSearchDTO.setMemberSourceType(ResourceLevel.PROJECT.value());
        userSearchDTO.setMemberType(MemberType.USER.getValue());
        //?????????????????????
        List<IamUser> users = iamUserMapper.listByProject(userSearchDTO);


        return CopyUtil.copyList(users, IamUserVO.class);
    }

    @Override
    public IPage<UserWithRoleDTO> pagingQueryUsersWithOrganizationLevelRoles(PageUtil pageUtil, RoleAssignmentSearchDTO roleAssignmentSearchDTO, Long sourceId) {
        List<UserWithRoleDTO> result;
        long page = pageUtil.getCurrent() < 1 ? 1L : pageUtil.getCurrent();
        long size = pageUtil.getSize() < 1 ? 10L : pageUtil.getSize();
        long start = page * size;
        long total = iamUserMapper.selectCountUsers(roleAssignmentSearchDTO, sourceId, ResourceLevel.ORGANIZATION.value());
        if (total > 0) {
            result = getUserRoleData(roleAssignmentSearchDTO, sourceId, ResourceLevel.ORGANIZATION.value(), start, size);
        } else {
            result = new ArrayList<>();
        }

        IPage<UserWithRoleDTO> pageData = new Page<>();
        pageData.setRecords(result);
        pageData.setSize(size);
        pageData.setTotal(total);
        return pageData;
    }

    /**
     * 1.??????????????????????????????
     * 2.?????????????????????????????????
     *
     * @param userId
     * @return
     */
    private Long checkCurrentOrganization(Long userId) {
        // ?????????????????????????????????
        Long currentOrganization = iamUserMapper.selectById(userId).getCurrentOrganizationId();
        // ????????????????????????????????????(1.??????????????? 2.????????????????????????)
        List<IamOrganization> organizationDOS = iamOrganizationMapper.selectProjectOrganizationListByUser(userId, false);

        // ??????????????????
        if (organizationDOS.size() <= 0) {
            return null;
        }

        // ???????????????
        for (IamOrganization organizationDO : organizationDOS) {
            if (organizationDO.getId().equals(currentOrganization)) {
                return currentOrganization;
            }
        }

        // ??????????????????,????????????????????????,???????????????????????????????????????
        Long orgId = organizationDOS.get(0).getId();
        this.updateUserCurrentOrganization(userId, orgId);
        return orgId;
    }

    private CustomUserDetails checkLoginUser(Long id) {
        CustomUserDetails customUserDetails = DetailsHelper.getUserDetails();
        if (customUserDetails == null) {
            throw new CommonException("user.not.login");
        }
        if (!id.equals(customUserDetails.getUserId())) {
            throw new CommonException("user.id.not.equals");
        }
        return customUserDetails;
    }

    private List<UserWithRoleDTO> getUserRoleData(RoleAssignmentSearchDTO roleAssignmentSearchDTO, Long sourceId, String value, Long start, Long size) {
        List<UserWithRoleDTO> result = new ArrayList<>();
        //????????????
        List<IamUser> users = iamUserMapper.selectUserByOption(roleAssignmentSearchDTO, sourceId, value, start, size);
        if (CollectionUtils.isEmpty(users)) return new ArrayList<>();
        //????????????????????????
        List<Long> userIds = users.stream().map(IamUser::getId).collect(Collectors.toList());
        List<IamRoleDTO> roles = iamUserMapper.selectUserWithRolesByOption(sourceId, value, userIds);
        Map<Long, List<IamRoleDTO>> map = CopyUtil.listToMapList(roles, IamRoleDTO::getUserId);
        //????????????
        UserWithRoleDTO userWithRoleDTO;
        List<RoleDTO> roleDTOS;
        RoleDTO roleDTO;
        List<IamRoleDTO> iamRoleDTOS;

        for (IamUser user : users) {
            userWithRoleDTO = new UserWithRoleDTO();
            userWithRoleDTO.setLoginName(user.getLoginName());
            userWithRoleDTO.setEmail(user.getEmail());
            userWithRoleDTO.setRealName(user.getRealName());
            userWithRoleDTO.setEnabled(user.getIsEnabled());
            userWithRoleDTO.setId(user.getId());
            result.add(userWithRoleDTO);
            if (CollectionUtils.isEmpty(iamRoleDTOS = map.get(user.getId()))) {
                continue;
            }
            roleDTOS = new ArrayList<>();
            userWithRoleDTO.setRoles(roleDTOS);

            for (IamRoleDTO iamRole : iamRoleDTOS) {
                roleDTO = new RoleDTO();
                roleDTO.setId(iamRole.getId());
                roleDTO.setName(iamRole.getName());
                roleDTO.setCode(iamRole.getCode());
                roleDTO.setBuiltIn(iamRole.getIsBuiltIn());
                roleDTO.setEnabled(iamRole.getIsEnabled());
                roleDTOS.add(roleDTO);
            }

        }
        return result;

    }

    @Override
    public @NotNull
    List<IamUserDTO> getUsers(@Nullable Set<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return new ArrayList<>();
        }
        return this.iamUserMapper.selectBatchIds(ids).stream().map(t -> ConvertHelper.convert(t, IamUserDTO.class)).collect(Collectors.toList());
    }

    @Override
    public @NotNull
    List<IamUserDTO> getAdminUsers() {
        LambdaQueryWrapper<IamUser> queryWrapper = Wrappers.<IamUser>lambdaQuery().eq(IamUser::getIsAdmin, true);
        return iamUserMapper.selectList(queryWrapper)
                .stream().map(t -> ConvertHelper.convert(t, IamUserDTO.class)).collect(Collectors.toList());
    }

}

package com.crc.crcloud.steam.iam.service.impl;


import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crc.crcloud.steam.iam.common.enums.RoleLabelEnum;
import com.crc.crcloud.steam.iam.common.exception.IamAppCommException;
import com.crc.crcloud.steam.iam.common.utils.*;
import com.crc.crcloud.steam.iam.dao.IamLabelMapper;
import com.crc.crcloud.steam.iam.dao.IamOrganizationMapper;
import com.crc.crcloud.steam.iam.dao.IamProjectMapper;
import com.crc.crcloud.steam.iam.dao.IamRoleMapper;
import com.crc.crcloud.steam.iam.entity.*;
import com.crc.crcloud.steam.iam.model.dto.IamProjectDTO;
import com.crc.crcloud.steam.iam.model.dto.payload.ProjectEventPayload;
import com.crc.crcloud.steam.iam.model.event.IamProjectCreateEvent;
import com.crc.crcloud.steam.iam.model.event.IamProjectUpdateEvent;
import com.crc.crcloud.steam.iam.model.vo.IamProjectVO;
import com.crc.crcloud.steam.iam.service.IamMemberRoleService;
import com.crc.crcloud.steam.iam.service.IamProjectService;
import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.iam.InitRoleCode;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author
 * @Description
 * @Date 2019-11-12
 */
@Service
public class IamProjectServiceImpl implements IamProjectService {

    @Autowired
    private IamProjectMapper iamProjectMapper;
    @Autowired
    private IamOrganizationMapper iamOrganizationMapper;
    @Autowired
    private IamRoleMapper iamRoleMapper;
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;
    @Autowired
    private IamLabelMapper iamLabelMapper;
    @Autowired
    private IamMemberRoleService iamMemberRoleService;


    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    @Override
    public IamProjectVO insert(Long organizationId, IamProjectVO iamProjectVO) {
        IamOrganization organization = getOrThrow(organizationId);
        iamProjectVO.setOrganizationId(organizationId);
        //??????????????????????????????
        validDuplicate(iamProjectVO);

        IamProject iamProject = new IamProject();
        BeanUtils.copyProperties(iamProjectVO, iamProject);
        iamProject.setOrganizationId(organizationId);
        iamProject.setIsEnabled(Boolean.TRUE);
        //????????????
        iamProjectMapper.insert(iamProject);
        //??????????????? ??????saga??????
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
            @Override
            public void afterCommit() {
                //??????saga??????
                applicationEventPublisher.publishEvent(new IamProjectCreateEvent(intiParam(iamProject, organization)));
                //????????????????????????????????????
                grantCreateMember(iamProject);
            }
        });


        return CopyUtil.copy(iamProject, IamProjectVO.class);
    }

    //??????saga????????????
    private ProjectEventPayload intiParam(IamProject iamProject, IamOrganization organization) {

        ProjectEventPayload projectEventPayload = new ProjectEventPayload();
        CustomUserDetails customUserDetails = DetailsHelper.getUserDetails();
        //????????????
        projectEventPayload.setUserId(customUserDetails.getUserId());
        projectEventPayload.setUserName(customUserDetails.getUsername());
        //??????label
        projectEventPayload.setRoleLabels(initProjectRoleLabels());
        //??????????????????
        projectEventPayload.setProjectId(iamProject.getId());
        projectEventPayload.setProjectCode(iamProject.getCode());
        projectEventPayload.setProjectCategory(iamProject.getCategory());
        projectEventPayload.setProjectName(iamProject.getName());
        projectEventPayload.setImageUrl(iamProject.getImageUrl());
        projectEventPayload.setCategory(iamProject.getCategory());
        projectEventPayload.setType(iamProject.getType());
        //????????????
        projectEventPayload.setOrganizationCode(organization.getCode());
        projectEventPayload.setOrganizationId(organization.getId());
        projectEventPayload.setOrganizationName(organization.getName());
        return projectEventPayload;
    }

    //??????????????????
    private Set<String> initProjectRoleLabels() {
        List<IamRole> roles = iamRoleMapper.selectRolesByLabelNameAndType(RoleLabelEnum.PROJECT_OWNER.value(), "role");
        if (CollectionUtils.isEmpty(roles)) return new HashSet<>();
        List<IamLabel> labels = iamLabelMapper.selectByRoleIds(roles.stream().map(IamRole::getId).collect(Collectors.toList()));
        if (CollectionUtils.isEmpty(labels)) return new HashSet<>();

        return labels.stream().map(IamLabel::getName).collect(Collectors.toSet());

    }

    //????????????????????????????????????
    //?????????????????????????????????????????????????????????????????????????????????
    //todo ????????????????????????????????? ????????????????????????
    private void grantCreateMember(IamProject iamProject) {
        //????????????????????????
        IamRole iamRole = iamRoleMapper.selectOne(Wrappers.<IamRole>lambdaQuery()
                .eq(IamRole::getFdLevel, ResourceLevel.PROJECT.value())
                .eq(IamRole::getCode, InitRoleCode.PROJECT_OWNER));
        Set<Long> roleIds = new HashSet<>();
        roleIds.add(iamRole.getId());
        iamMemberRoleService.grantUserRole(UserDetail.getUserId(), roleIds, iamProject.getId(), ResourceLevel.PROJECT);
    }

    private void validDuplicate(IamProjectVO iamProjectVO) {
        //??????????????????
//        IamProject iamProject = iamProjectMapper.selectOne(Wrappers.<IamProject>lambdaQuery().eq(IamProject::getName, iamProjectVO.getName()));
//        if (Objects.nonNull(iamProject)) {
//            throw new IamAppCommException("project.name.duplicated");
//        }
        IamProject iamProject = iamProjectMapper.selectOne(Wrappers.<IamProject>lambdaQuery()
                .eq(IamProject::getCode, iamProjectVO.getCode())
                .eq(IamProject::getOrganizationId, iamProjectVO.getOrganizationId()));
        if (Objects.nonNull(iamProject)) {
            throw new IamAppCommException("project.code.duplicated");
        }

    }

    private IamOrganization getOrThrow(Long organizationId) {
        IamOrganization organization = iamOrganizationMapper.selectById(organizationId);
        if (Objects.isNull(organization)) {
            throw new IamAppCommException("organization.data.null");
        }
        return organization;
    }

    /**
     * ??????
     *
     * @param projectId ??????ID
     * @param id
     */
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    @Override
    public void delete(Long projectId, Long id) {
        //??????????????????projectId??????????????????????????????projectId???????????? ??????????????????????????????????????????
        iamProjectMapper.deleteById(id);
    }


    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    @Override
    public IamProjectVO update(IamProjectVO iamProjectVO) {
        IamProjectDTO iamProjectDTO = CopyUtil.copy(iamProjectVO, IamProjectDTO.class);
        iamProjectDTO.setLastUpdateDate(new Date());
        iamProjectDTO.setLastUpdatedBy(UserDetail.getUserId());
        //??????????????????
        iamProjectMapper.updateBySql(iamProjectDTO);
        //??????saga??????
        applicationEventPublisher.publishEvent(new IamProjectUpdateEvent(intiUpdateParam(iamProjectDTO)));

        return queryOne(iamProjectVO.getId());
    }

    //????????????saga??????
    private ProjectEventPayload intiUpdateParam(IamProjectDTO iamProjectDTO) {
        ProjectEventPayload projectEventPayload = new ProjectEventPayload();
        IamProject project = iamProjectMapper.selectById(iamProjectDTO.getId());
        IamOrganization organization = iamOrganizationMapper.selectById(project.getOrganizationId());
        CustomUserDetails customUserDetails = DetailsHelper.getUserDetails();
        //????????????
        projectEventPayload.setUserName(customUserDetails.getUsername());
        projectEventPayload.setUserId(customUserDetails.getUserId());
        //???????????????
        projectEventPayload.setOrganizationName(organization.getName());
        projectEventPayload.setOrganizationId(organization.getId());
        projectEventPayload.setOrganizationCode(organization.getCode());
        projectEventPayload.setProjectId(project.getId());
        projectEventPayload.setProjectCode(project.getCode());
        //?????????????????????
        projectEventPayload.setProjectName(iamProjectDTO.getName());
        projectEventPayload.setImageUrl(iamProjectDTO.getImageUrl());
        return projectEventPayload;
    }


    /**
     * ??????????????????
     *
     * @param projectId ??????ID
     * @return
     */
    @Override
    public IamProjectVO queryOne(Long projectId) {
        IamProject data = iamProjectMapper.selectById(projectId);
        if (Objects.isNull(data)) {
            throw new IamAppCommException("common.data.null.error");
        }

        return CopyUtil.copy(data, IamProjectVO.class);
    }

    /**
     * ????????????
     *
     * @param iamProjectVO
     * @param projectId    ??????ID
     * @param page         ????????????
     * @return
     */
    @Override
    public IPage<IamProjectVO> queryPage(IamProjectVO iamProjectVO, Long projectId, Page page) {

        IamProjectDTO iamProjectDTO = ConvertHelper.convert(iamProjectVO, IamProjectDTO.class);


        //??????
        IPage<IamProject> pageResult = iamProjectMapper.page(page, iamProjectDTO);
        IPage<IamProjectVO> result = new Page<>();
        if (Objects.isNull(pageResult) || pageResult.getTotal() == 0) {
            return result;
        }

        result.setSize(pageResult.getSize());
        result.setTotal(pageResult.getTotal());
        List<IamProjectDTO> recordsDTO = ConvertHelper.convertList(pageResult.getRecords(), IamProjectDTO.class);
        List<IamProjectVO> recordsVO = ConvertHelper.convertList(recordsDTO, IamProjectVO.class);
        result.setRecords(recordsVO);
        return result;
    }

    @Override
    public @NotNull
    List<IamProjectDTO> getByIds(@Nullable Set<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return new ArrayList<>();
        }
        return iamProjectMapper.selectBatchIds(ids).stream().map(t -> ConvertHelper.convert(t, IamProjectDTO.class)).collect(Collectors.toList());
    }

    @Override
    public IPage<IamProjectVO> pagingQuery(IamProjectVO projectVO, PageUtil pageUtil) {
        IamProjectDTO iamProjectDTO = CopyUtil.copy(projectVO, IamProjectDTO.class);
        IPage<IamProject> projects = iamProjectMapper.page(pageUtil, iamProjectDTO);

        IPage<IamProjectVO> result = new Page<>();
        result.setTotal(projects.getTotal());
        result.setSize(projects.getSize());
        if (CollectionUtils.isEmpty(projects.getRecords())) return result;
        List<IamProjectVO> projectList = new ArrayList<>();
        for (IamProject project : projects.getRecords()) {
            IamProjectVO iamProjectVO = new IamProjectVO();
            BeanUtils.copyProperties(project, iamProjectVO);
            //?????????????????????
            iamProjectVO.setEnabled(project.getIsEnabled());
            projectList.add(iamProjectVO);
        }
        result.setRecords(projectList);
        return result;
    }

    @Override
    public IamProjectVO queryProjectById(Long id) {
        IamProject iamProject = iamProjectMapper.selectById(id);
        if (Objects.isNull(iamProject)) {
            throw new IamAppCommException("project.data.null");
        }
        IamProjectVO iamProjectVO = CopyUtil.copy(iamProject, IamProjectVO.class);
        //???????????????
        iamProjectVO.setEnabled(iamProject.getIsEnabled());
        return iamProjectVO;
    }

    @Override
    public List<IamProjectVO> queryByCategory(String category) {

        List<IamProjectDTO> projects = iamProjectMapper.queryByCategory(category);
        return CopyUtil.copyList(projects, IamProjectVO.class);
    }

    @Override
    public void check(IamProjectVO projectVO) {
        Boolean checkCode = !StringUtils.isEmpty(projectVO.getCode());
        if (!checkCode) {
            throw new IamAppCommException("project.code.name");
        } else {
            checkCode(projectVO);
        }
    }

    private void checkCode(IamProjectVO iamProjectVO) {
        Boolean createCheck = StringUtils.isEmpty(iamProjectVO.getId());
        IamProject project = new IamProject();
        project.setOrganizationId(iamProjectVO.getOrganizationId());
        project.setCode(iamProjectVO.getCode());
        IamProject oldProject = iamProjectMapper.selectOne(Wrappers.<IamProject>lambdaQuery()
                .eq(IamProject::getOrganizationId, project.getOrganizationId())
                .eq(IamProject::getCode, project.getCode()));
        if (createCheck) {
            if (Objects.nonNull(oldProject)) {
                throw new IamAppCommException("project.code.duplicated");
            }
        } else {
            Long id = iamProjectVO.getId();

            Boolean existed = Objects.nonNull(oldProject) && !id.equals(oldProject.getId());
            if (existed) {
                throw new IamAppCommException("project.code.duplicated");
            }
        }
    }

    @Override
    public Optional<IamProjectDTO> get(@NotNull Long projectId) {
        return Optional.ofNullable(this.iamProjectMapper.selectById(projectId)).map(t -> ConvertHelper.convert(t, IamProjectDTO.class));
    }

    @Override
    public IPage<IamProjectDTO> getUserProjects(Page page, @NotNull Long userId, @NotNull Long organizationId, @Nullable String searchName) {
        //noinspection unchecked
        PageWrapper<IamProject> pageWrapper = PageWrapper.instance(page);
        pageWrapper.addGbkFieldConvert(IamProject::getName, IamProject::getDescription);
        pageWrapper.addGbkFieldConvert(EntityUtil.getSimpleField(IamUser::getRealName));
        pageWrapper.addDefaultOrderByDesc(IamProject::getCreationDate);
        IPage<IamProject> projectPage = iamProjectMapper.getUserProjects(pageWrapper, userId, organizationId, searchName);
        return projectPage.convert(t -> ConvertHelper.convert(t, IamProjectDTO.class));
    }
}

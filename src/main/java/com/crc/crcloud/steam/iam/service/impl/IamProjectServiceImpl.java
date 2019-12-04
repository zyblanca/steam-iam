package com.crc.crcloud.steam.iam.service.impl;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crc.crcloud.steam.iam.common.enums.RoleLabelEnum;
import com.crc.crcloud.steam.iam.common.exception.IamAppCommException;
import com.crc.crcloud.steam.iam.common.utils.CopyUtil;
import com.crc.crcloud.steam.iam.common.utils.UserDetail;
import com.crc.crcloud.steam.iam.dao.IamLabelMapper;
import com.crc.crcloud.steam.iam.dao.IamOrganizationMapper;
import com.crc.crcloud.steam.iam.dao.IamProjectMapper;
import com.crc.crcloud.steam.iam.dao.IamRoleMapper;
import com.crc.crcloud.steam.iam.entity.IamLabel;
import com.crc.crcloud.steam.iam.entity.IamOrganization;
import com.crc.crcloud.steam.iam.entity.IamProject;
import com.crc.crcloud.steam.iam.entity.IamRole;
import com.crc.crcloud.steam.iam.model.dto.IamProjectDTO;
import com.crc.crcloud.steam.iam.model.dto.payload.ProjectEventPayload;
import com.crc.crcloud.steam.iam.model.event.IamProjectCreateEvent;
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
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.CollectionUtils;
import sun.awt.CustomCursor;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
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
        //验证 项目名称，项目编码是否存在
        validDuplicate(iamProjectVO);

        IamProject iamProject = new IamProject();
        BeanUtils.copyProperties(iamProjectVO, iamProject);
        iamProject.setOrganizationId(organizationId);
        iamProject.setIsEnabled((byte) 1);
        //创建项目
        iamProjectMapper.insert(iamProject);
        //发起saga事件
        applicationEventPublisher.publishEvent(new IamProjectCreateEvent(intiParam(iamProject, organization)));


        //创建人授予项目拥有者权限
        grantCreatMember(iamProject);

        return CopyUtil.copy(iamProject, IamProjectVO.class);
    }

    //生成saga参数信息
    private ProjectEventPayload intiParam(IamProject iamProject, IamOrganization organization) {

        ProjectEventPayload projectEventPayload = new ProjectEventPayload();
        CustomUserDetails customUserDetails = DetailsHelper.getUserDetails();
        //用户信息
        projectEventPayload.setUserId(customUserDetails.getUserId());
        projectEventPayload.setUserName(customUserDetails.getUsername());
        //设置label
        projectEventPayload.setRoleLabels(initProjectRoleLabels());
        //项目基本信息
        projectEventPayload.setProjectId(iamProject.getId());
        projectEventPayload.setProjectCode(iamProject.getCode());
        projectEventPayload.setProjectCategory(iamProject.getCategory());
        projectEventPayload.setProjectName(iamProject.getName());
        projectEventPayload.setImageUrl(iamProject.getImageUrl());
        //组织信息
        projectEventPayload.setOrganizationCode(organization.getCode());
        projectEventPayload.setOrganizationId(organization.getId());
        projectEventPayload.setOrganizationName(organization.getName());
        return projectEventPayload;
    }

    //获取标签权限
    private Set<String> initProjectRoleLabels() {
        List<IamRole> roles = iamRoleMapper.selectRolesByLabelNameAndType(RoleLabelEnum.PROJECT_OWNER.value(), "role");
        if (CollectionUtils.isEmpty(roles)) return new HashSet<>();
        List<IamLabel> labels = iamLabelMapper.selectByRoleIds(roles.stream().map(IamRole::getId).collect(Collectors.toList()));
        if (CollectionUtils.isEmpty(labels)) return new HashSet<>();

        return labels.stream().map(IamLabel::getName).collect(Collectors.toSet());

    }

    //项目创建者授予拥有者权限
    //原始行云使用标签动态获取权限，当前只授予项目拥有者权限
    //todo 当前项目权限查询写死的 后续需要动态增加
    private void grantCreatMember(IamProject iamProject) {
        //获取组织成员权限
        IamRole iamRole = iamRoleMapper.selectOne(Wrappers.<IamRole>lambdaQuery()
                .eq(IamRole::getFdLevel, ResourceLevel.PROJECT.value())
                .eq(IamRole::getCode, InitRoleCode.PROJECT_OWNER));
        Set<Long> roleIds = new HashSet<>();
        roleIds.add(iamRole.getId());
        iamMemberRoleService.grantUserRole(UserDetail.getUserId(), roleIds, iamProject.getId(), ResourceLevel.PROJECT);
    }

    private void validDuplicate(IamProjectVO iamProjectVO) {
        IamProject iamProject = iamProjectMapper.selectOne(Wrappers.<IamProject>lambdaQuery().eq(IamProject::getName, iamProjectVO.getName()));
        if (Objects.nonNull(iamProject)) {
            throw new IamAppCommException("project.name.duplicated");
        }
        iamProject = iamProjectMapper.selectOne(Wrappers.<IamProject>lambdaQuery().eq(IamProject::getCode, iamProjectVO.getCode()));
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
     * 删除
     *
     * @param projectId 项目ID
     * @param id
     */
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    @Override
    public void delete(Long projectId, Long id) {
        //如果表中含有projectId，请先查询数据，判断projectId是否一致 不一致抛异常，一致则进行删除
        iamProjectMapper.deleteById(id);
    }

    /**
     * 更新
     *
     * @param projectId    项目ID
     * @param iamProjectVO
     * @return
     */
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    @Override
    public IamProjectVO update(Long projectId, IamProjectVO iamProjectVO) {
        //最好使用自定义修改语句，修改条件包含项目ID
        IamProjectDTO dataDTO = ConvertHelper.convert(iamProjectVO, IamProjectDTO.class);
        iamProjectMapper.updateById(ConvertHelper.convert(dataDTO, IamProject.class));
        return queryOne(projectId, iamProjectVO.getId());
    }

    /**
     * 查询单个详情
     *
     * @param projectId 项目ID
     * @param id
     * @return
     */
    @Override
    public IamProjectVO queryOne(Long projectId, Long id) {
        //查询的数据 如果包含项目ID，校验项目ID是否一致，不一致抛异常
        IamProject data = iamProjectMapper.selectById(id);
        if (Objects.isNull(data)) {
            throw new IamAppCommException("common.data.null.error");
        }
        IamProjectDTO dataDTO = ConvertHelper.convert(data, IamProjectDTO.class);
        return ConvertHelper.convert(dataDTO, IamProjectVO.class);
    }

    /**
     * 分页查询
     *
     * @param iamProjectVO
     * @param projectId    项目ID
     * @param page         分页信息
     * @return
     */
    @Override
    public IPage<IamProjectVO> queryPage(IamProjectVO iamProjectVO, Long projectId, Page page) {

        IamProjectDTO iamProjectDTO = ConvertHelper.convert(iamProjectVO, IamProjectDTO.class);


        //查询
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


}

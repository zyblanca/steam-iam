package com.crc.crcloud.steam.iam.service.impl;


import cn.hutool.core.bean.BeanDesc;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crc.crcloud.steam.iam.common.enums.MemberRoleEnum;
import com.crc.crcloud.steam.iam.common.enums.MemberRoleSourceTypeEnum;
import com.crc.crcloud.steam.iam.common.enums.MemberType;
import com.crc.crcloud.steam.iam.common.exception.IamAppCommException;
import com.crc.crcloud.steam.iam.common.utils.CopyUtil;
import com.crc.crcloud.steam.iam.common.utils.PageUtil;
import com.crc.crcloud.steam.iam.dao.IamMemberRoleMapper;
import com.crc.crcloud.steam.iam.dao.IamProjectMapper;
import com.crc.crcloud.steam.iam.dao.IamUserMapper;
import com.crc.crcloud.steam.iam.entity.IamMemberRole;
import com.crc.crcloud.steam.iam.entity.IamProject;
import com.crc.crcloud.steam.iam.entity.IamUser;
import com.crc.crcloud.steam.iam.model.dto.IamUserDTO;
import com.crc.crcloud.steam.iam.model.dto.UserSearchDTO;
import com.crc.crcloud.steam.iam.model.dto.user.SearchDTO;
import com.crc.crcloud.steam.iam.model.vo.IamUserVO;
import com.crc.crcloud.steam.iam.model.vo.user.IamOrganizationUserPageRequestVO;
import com.crc.crcloud.steam.iam.model.vo.user.IamUserCreateRequestVO;
import com.crc.crcloud.steam.iam.service.IamMemberRoleService;
import com.crc.crcloud.steam.iam.service.IamUserOrganizationRelService;
import com.crc.crcloud.steam.iam.service.IamUserService;
import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.iam.ResourceLevel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;

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
    private IamMemberRoleService memberRoleService;
    @Autowired
    private IamProjectMapper iamProjectMapper;
    @Autowired
    private IamMemberRoleMapper iamMemberRoleMapper;
    /**
     * 线程安全
     */
    private final BCryptPasswordEncoder ENCODER = new BCryptPasswordEncoder();

    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    @Override
    public @NotNull IamUserDTO createUserByManual(@Valid IamUserCreateRequestVO vo, @NotEmpty Set<Long> organizationIds) {
        @NotNull IamUserDTO user = createUserByManual(vo);
        log.info("手动添加用户[{}],期望属于组织[{}]", vo.getRealName(), CollUtil.join(organizationIds, ","));
        iamUserOrganizationRelService.link(user.getId(), organizationIds);
        log.info("手动添加用户[{}],期望属于角色[{}]", vo.getRealName(), CollUtil.join(vo.getRoleIds(), ","));
        for (Long organizationId : organizationIds) {
            memberRoleService.grantUserRole(user.getId(), vo.getRoleIds(), organizationId, ResourceLevel.ORGANIZATION);
        }
        return user;
    }

    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    public @NotNull IamUserDTO createUserByManual(@Valid IamUserCreateRequestVO vo) {
        //只能以字母和数字开头，且长度不能少于2，内容可以包含字母数字.-
        Predicate<String> matchLoginName = loginName -> ReUtil.isMatch("^[a-zA-Z0-9][a-zA-Z0-9.-]+$", loginName);
        //不能以. .git .atom 结尾
        matchLoginName = matchLoginName.and(loginName -> !StrUtil.endWithAny(".", ".git", ".atom"));
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
        log.info("手动添加用户[{}]", vo.getRealName());
        return ConvertHelper.convert(entity, IamUserDTO.class);
    }

    /**
     * 初始化用户的基本属性,如果属性为null，则进行初始化
     * <p>是否管理员{@link IamUser#getIsAdmin()} ()}：false</p>
     * <p>国际电话区号{@link IamUser#getInternationalTelCode()} ()}：+86</p>
     * <p>语言{@link IamUser#getLanguage()}：zh_CN</p>
     * <p>时区{@link IamUser#getTimeZone()}：CTT</p>
     * <p>是否启用{@link IamUser#getIsEnabled()}：true</p>
     * <p>是否锁定账户{@link IamUser#getIsLocked()}：false</p>
     * <p>是否ldap来源{@link IamUser#getIsLdap()}：false</p>
     *
     * @param entity 属性
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

    public Optional<IamUserDTO> getByLoginName(@NotBlank String loginName) {
        return getOne(t -> t.eq(IamUser::getLoginName, loginName));
    }

    public Optional<IamUserDTO> getByEmail(@NotBlank String email) {
        return getOne(t -> t.eq(IamUser::getEmail, email));
    }

    /**
     * 根据条件获取第一个
     *
     * @param consumer 条件
     * @return 复合条件的第一个用户
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
    public IPage<IamUserVO> pageQueryOrganizationUser(@NotNull Long
                                                              organizationId, @Valid IamOrganizationUserPageRequestVO vo) {
        List<String> keywords = StrUtil.splitTrim(vo.getKeywords(), ",");
        if (StrUtil.isNotBlank(vo.getKeywords())) {
            keywords.add(vo.getKeywords());
        }
        SearchDTO searchDTO = SearchDTO.builder().roleIds(vo.getRoleIds()).keywords(CollUtil.newHashSet(keywords)).build();
        IPage<IamUser> pageResult = iamUserMapper.pageQueryOrganizationUser(new Page<>(vo.getPage(), vo.getPageSize()), CollUtil.newHashSet(organizationId), searchDTO);
        return pageResult.convert(t -> CopyUtil.copy(t, IamUserVO.class));
    }

    @Override
    public IPage<IamUserVO> pageByProject(Long projectId, UserSearchDTO userSearchDTO, PageUtil page) {

        userSearchDTO.setProjectId(projectId);
        userSearchDTO.setMemberSourceType(MemberRoleSourceTypeEnum.PROJECT.getSourceType());
        userSearchDTO.setMemberType(MemberType.USER.getValue());
        //查询项目下的人
        IPage<IamUser> userPage = iamUserMapper.pageByProject(page, userSearchDTO);

        IPage<IamUserVO> result = new Page<>();
        result.setTotal(userPage.getTotal());
        result.setSize(userPage.getSize());
        result.setRecords(CopyUtil.copyList(userPage.getRecords(), IamUserVO.class));
        return result;
    }

    @Override
    public List<IamUserVO> projectDropDownUser(Long projectId, UserSearchDTO userSearchDTO) {
        userSearchDTO.setProjectId(projectId);
        userSearchDTO.setMemberSourceType(MemberRoleSourceTypeEnum.PROJECT.getSourceType());
        userSearchDTO.setMemberType(MemberType.USER.getValue());
        //查询项目下的人
        List<IamUser> users = iamUserMapper.projectDropDownUser(userSearchDTO);

        return CopyUtil.copyList(users, IamUserVO.class);
    }

    @Override
    public List<IamUserVO> projectUnselectUser(Long projectId, UserSearchDTO userSearchDTO) {
        IamProject project = getAndThrowProject(projectId);
        userSearchDTO.setProjectId(projectId);
        userSearchDTO.setMemberSourceType(MemberRoleSourceTypeEnum.PROJECT.getSourceType());
        userSearchDTO.setMemberType(MemberType.USER.getValue());
        userSearchDTO.setOrganizationId(project.getOrganizationId());
        //查询组织下未被当前项目选择的人
        List<IamUser> users = iamUserMapper.projectUnselectUser(userSearchDTO);

        return CopyUtil.copyList(users, IamUserVO.class);
    }

    @Override
    public void projectBindUsers(Long projectId, List<Long> userIds) {
        if (CollectionUtils.isEmpty(userIds)) return;
        //当前写死项目拥有者权限
        IamMemberRole iamMemberRole = new IamMemberRole();
        iamMemberRole.setMemberType(MemberType.USER.getValue());
        iamMemberRole.setRoleId(MemberRoleEnum.PROJECT_OWNER.getRoleId());
        iamMemberRole.setSourceId(projectId);
        iamMemberRole.setSourceType(MemberRoleSourceTypeEnum.PROJECT.getSourceType());
        for (Long userId : userIds) {
            iamMemberRole.setMemberId(userId);
            iamMemberRoleMapper.insert(iamMemberRole);
        }
    }

    private IamProject getAndThrowProject(Long projectId) {
        IamProject iamProject = iamProjectMapper.selectById(projectId);
        if (Objects.isNull(iamProject))
            throw new IamAppCommException("project.data.null");
        return iamProject;
    }


}

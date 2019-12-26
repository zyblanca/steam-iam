package com.crc.crcloud.steam.iam.web;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.comparator.PinyinComparator;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crc.crcloud.steam.iam.common.exception.IamAppCommException;
import com.crc.crcloud.steam.iam.common.utils.ResponseEntity;
import com.crc.crcloud.steam.iam.model.dto.IamMemberRoleDTO;
import com.crc.crcloud.steam.iam.model.dto.IamRoleDTO;
import com.crc.crcloud.steam.iam.model.dto.IamUserDTO;
import com.crc.crcloud.steam.iam.model.vo.site.SiteLdapUserResponseVO;
import com.crc.crcloud.steam.iam.model.vo.site.SiteManagerUserRequestVO;
import com.crc.crcloud.steam.iam.model.vo.site.SiteManagerUserResponseVO;
import com.crc.crcloud.steam.iam.service.IamMemberRoleService;
import com.crc.crcloud.steam.iam.service.IamRoleService;
import com.crc.crcloud.steam.iam.service.IamUserService;
import io.choerodon.core.iam.InitRoleCode;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Nullable;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 平台控制器
 * <p>平台管理员列表</p>
 * <p>从LADP添加平台管理员永辉</p>
 * @author LiuYang
 * @date 2019/12/16
 */
@Slf4j
@Validated
@Api("")
@RestController
@RequestMapping(value = "/v1/site")
public class SiteController {
    @Autowired
    private IamUserService iamUserService;
    @Autowired
    private IamMemberRoleService iamMemberRoleService;
    @Autowired
    private IamRoleService iamRoleService;

    public static void main(String[] args) {
        List<String> strings = CollUtil.sortByPinyin(CollUtil.newArrayList("abc", " ", "", "刘洋", "管理员", "ldf", "杨严翠"));
        System.out.println(strings);
    }
    @Permission(level = ResourceLevel.SITE)
    @ApiOperation("平台管理-成员管理")
    @PostMapping("page")
    public ResponseEntity<IPage<SiteManagerUserResponseVO>> getSiteManagerUserPage(@RequestBody @Valid SiteManagerUserRequestVO vo) {
        //内存分页-最新授权的用户再最前面
        List<IamUserDTO> siteUsers = new ArrayList<>();
        List<IamMemberRoleDTO> siteMemberUser = iamMemberRoleService.getSiteUserMemberRoleBySource();
        Comparator<IamMemberRoleDTO> ascComparator = Comparator.comparingLong(IamMemberRoleDTO::getId);
        siteMemberUser.sort(ascComparator.reversed());
        final List<Long> userIds = siteMemberUser.stream().map(IamMemberRoleDTO::getMemberId).collect(Collectors.toList());
        @NotNull List<IamUserDTO> users = iamUserService.getUsers(CollUtil.newHashSet(userIds));
        //排序按照dateAscComparator排序
        users.sort(Comparator.comparing(user -> userIds.indexOf(user.getId())));
        siteUsers.addAll(users);
        siteUsers.addAll(iamUserService.getAdminUsers());
        //去重
        Collection<IamUserDTO> distinct = siteUsers.stream().collect(Collectors.toMap(IamUserDTO::getId, Function.identity(), (a, b) -> a, LinkedHashMap::new)).values();
        siteUsers = CollUtil.newArrayList(distinct);
        Page<SiteManagerUserResponseVO> pageResult = new Page<>(vo.getCurrent(), vo.getSize(), siteUsers.size());
        List<IamUserDTO> pageSiteUsers = CollUtil.sortPageAll(vo.getCurrent().intValue(), vo.getSize().intValue(), (o1, o2) -> {
            if (!StrUtil.isAllBlank(vo.getAsc(), vo.getDesc())) {
                AtomicBoolean isDesc = new AtomicBoolean(false);
                String fieldName = Optional.ofNullable(vo.getAsc()).filter(StrUtil::isNotBlank).orElseGet(() -> {
                    isDesc.set(true);
                    return vo.getDesc();
                });
                Object o1Value = BeanUtil.getProperty(o1, fieldName);
                Object o2Value = BeanUtil.getProperty(o2, fieldName);
                Comparator<String> pinyinComparator = Comparator.nullsFirst(new PinyinComparator());
                if (isDesc.get()) {
                    pinyinComparator = pinyinComparator.reversed();
                }
                return pinyinComparator.compare(Convert.toStr(o1Value), Convert.toStr(o2Value));
            }
            return 0;
        }, siteUsers);
        pageResult.setRecords(pageSiteUsers.stream().map(SiteManagerUserResponseVO::instance).collect(Collectors.toList()));
        return new ResponseEntity<>(pageResult);
    }

    @Permission(level = ResourceLevel.SITE)
    @ApiOperation(value = "平台管理-添加平台管理员-校验", notes = "报错或data为null表示校验失败,当不是ldap用户时,不返回数据")
    @GetMapping("ldap/check")
    public ResponseEntity<SiteLdapUserResponseVO> grantLdapCheck(@NotBlank @RequestParam("login_name") String loginName) {
        Optional<IamUserDTO> iamUser = iamUserService.getByLoginName(loginName);
        @Nullable SiteLdapUserResponseVO response = iamUser.filter(IamUserDTO::getIsLdap).map(t -> {
            SiteLdapUserResponseVO responseVO = new SiteLdapUserResponseVO();
            BeanUtil.copyProperties(t, responseVO, CopyOptions.create().ignoreNullValue());
            responseVO.setCompany("润联科技");
            responseVO.setDepartment("华润云-开发云");
            responseVO.setPosition("高级咨询经理");
            responseVO.setPhoneNumber(StrUtil.addPrefixIfNot(t.getPhone(), t.getInternationalTelCode()));
            boolean alreadySiteRole = iamRoleService.getUserRoles(t.getId(), ResourceLevel.SITE).stream().anyMatch(role -> Objects.equals(role.getCode(), InitRoleCode.SITE_ADMINISTRATOR));
            responseVO.setAlreadySiteRole(alreadySiteRole);
            return responseVO;
        }).orElse(null);
        return new ResponseEntity<>(response);
    }

    @Permission(level = ResourceLevel.SITE)
    @ApiOperation("平台管理-添加平台管理员")
    @PutMapping("ldap/role")
    public ResponseEntity grantLdapRole(@NotBlank @RequestParam("login_name") String loginName) {
        ResponseEntity<SiteLdapUserResponseVO> responseEntity = grantLdapCheck(loginName);
        SiteLdapUserResponseVO user = Optional.ofNullable(responseEntity.getData()).orElseThrow(() -> new IamAppCommException("role.grant.site.ldap.not.exist"));
        IamRoleDTO role = iamRoleService.getRoleByCode(InitRoleCode.SITE_ADMINISTRATOR).orElseThrow(() -> new IamAppCommException("role.not.exist"));
        iamMemberRoleService.grantUserSiteRole(user.getId(), CollUtil.newHashSet(role.getId()));
        return ResponseEntity.ok();
    }
}

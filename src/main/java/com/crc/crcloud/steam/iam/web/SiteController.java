package com.crc.crcloud.steam.iam.web;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crc.crcloud.steam.iam.common.exception.IamAppCommException;
import com.crc.crcloud.steam.iam.common.utils.ResponseEntity;
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
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 平台控制器
 * <p>平台管理员列表</p>
 * <p>从LADP添加平台管理员永辉</p>
 *
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

    @Permission(level = ResourceLevel.SITE)
    @ApiOperation("平台管理-成员管理")
    @PostMapping("page")
    public ResponseEntity<IPage<SiteManagerUserResponseVO>> getSiteManagerUserPage(@RequestBody @Valid SiteManagerUserRequestVO vo) {
        Page page = new Page(vo.getCurrent(), vo.getSize());
        page.setAsc(vo.getAsc());
        page.setDesc(vo.getDesc());
        @NotNull IPage<Long> pageUserIdResult = iamMemberRoleService.getSiteAdminUserId(page);
        Map<Long, IamUserDTO> userMap = iamUserService.getUsers(CollUtil.newHashSet(pageUserIdResult.getRecords()))
                .stream().collect(Collectors.toMap(IamUserDTO::getId, Function.identity()));
        IPage<SiteManagerUserResponseVO> pageResult = pageUserIdResult.convert(userId -> SiteManagerUserResponseVO.instance(userMap.get(userId)));
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
            responseVO.setPhoneNumber(t.getPhone());
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

package com.crc.crcloud.steam.iam.web;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crc.crcloud.steam.iam.common.enums.UserOriginEnum;
import com.crc.crcloud.steam.iam.common.utils.ResponseEntity;
import com.crc.crcloud.steam.iam.model.dto.IamUserDTO;
import com.crc.crcloud.steam.iam.model.vo.user.IamOrganizationUserPageRequestVO;
import com.crc.crcloud.steam.iam.model.vo.user.IamOrganizationUserPageResponseVO;
import com.crc.crcloud.steam.iam.model.vo.user.IamUserCreateRequestVO;
import com.crc.crcloud.steam.iam.model.vo.user.IamUserSafeVO;
import com.crc.crcloud.steam.iam.service.IamUserService;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * 组织成员
 */
@RestController
@RequestMapping(value = "/v1/organizations/{organization_id}/user")
public class OrganizationUserController {
    @Autowired
    private IamUserService iamUserService;

    @Permission(permissionPublic = true)
    @ApiOperation(value = "组织成员列表", notes = "分页", response = IamOrganizationUserPageResponseVO.class)
    @PostMapping("page")
    public ResponseEntity<IPage<IamOrganizationUserPageResponseVO>> page(@PathVariable("organization_id") Long organizationId
            , @RequestBody @Valid IamOrganizationUserPageRequestVO vo) {
        Supplier<IamOrganizationUserPageResponseVO> random = () -> {
            return IamOrganizationUserPageResponseVO.builder()
                    .realName("刘" + RandomUtil.randomString(10))
                    .loginName("liuyang-" + RandomUtil.randomNumbers(5))
                    .roleName(RandomUtil.randomEle(CollUtil.newArrayList("组织管理员", "组织成员")))
                    .origin(RandomUtil.randomEle(UserOriginEnum.values()).getDesc())
                    .build();
        };
        //todo 测试数据
        boolean randomBoolean = RandomUtil.randomBoolean();
        List<IamOrganizationUserPageResponseVO> records = new ArrayList<>(vo.getPageSize());
        IPage<IamOrganizationUserPageResponseVO> pageResult = new Page<>(vo.getPage(), vo.getPageSize());
        if (randomBoolean) {
            pageResult.setTotal(vo.getPageSize() + 1);
            for (Integer i = 0; i < vo.getPageSize(); i++) {
                records.add(random.get());
            }
        }
        pageResult.setRecords(records);
        return new ResponseEntity<>(pageResult);
    }

    @Permission(permissionPublic = true)
    @ApiOperation(value = "手动创建组织成员")
    @PostMapping
    public ResponseEntity<IamUserSafeVO> createUser(@PathVariable("organization_id") Long organizationId,
                                                    @RequestBody @Valid IamUserCreateRequestVO vo) {
        IamUserDTO userDTO = iamUserService.createUserByManual(vo, CollUtil.newHashSet(organizationId));
        return new ResponseEntity<>(new IamUserSafeVO(userDTO));
    }

}

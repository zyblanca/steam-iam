package com.crc.crcloud.steam.iam.web;


import com.crc.crcloud.steam.iam.common.utils.ResponseEntity;
import com.crc.crcloud.steam.iam.model.dto.IamOrganizationDTO;
import com.crc.crcloud.steam.iam.model.vo.IamOrganizationVO;
import com.crc.crcloud.steam.iam.model.vo.organization.IamOrganizationUpdateRequestVO;
import com.crc.crcloud.steam.iam.service.IamOrganizationService;
import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;


/**
 * 组织相关
 * <p>组织修改 </p>
 * <p>组织禁用启用 </p>
 * <p>组织列表查询 </p>
 * @author LiuYang
 */
@Validated
@Api("")
@RestController
@RequestMapping(value = "/v1/organizations")
public class IamOrganizationController {

    @Autowired
    private IamOrganizationService iamOrganizationService;

    /**
     * 修改组织信息
     *
     * @return 修改成功后的组织信息
     */
    @Permission(level = ResourceLevel.SITE)
    @ApiOperation(value = "全局层修改组织")
    @PutMapping(value = "/{organization_id}")
    public ResponseEntity<IamOrganizationVO> updateBySite(@PathVariable(name = "organization_id") Long id,
                                                          @RequestBody @Valid IamOrganizationUpdateRequestVO vo) {
        return updateByLevel(id, vo, ResourceLevel.SITE);
    }

    /**
     * 修改组织信息
     *
     * @return 修改成功后的组织信息
     */
    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "组织层修改组织")
    @PutMapping(value = "/{organization_id}/organization_level")
    public ResponseEntity<IamOrganizationVO> updateByOrganization(@PathVariable(name = "organization_id") Long id,
                                                                  @RequestBody @Valid IamOrganizationUpdateRequestVO vo) {
        return updateByLevel(id, vo, ResourceLevel.ORGANIZATION);
    }

    private ResponseEntity<IamOrganizationVO> updateByLevel(Long id, IamOrganizationUpdateRequestVO vo, ResourceLevel level) {
        //初始化兼容值
        vo.setIsEnabled(vo.getIsEnabled());
        Map<ResourceLevel, BiFunction<Long, IamOrganizationUpdateRequestVO, IamOrganizationDTO>> handler = new HashMap<>(3);
        handler.put(ResourceLevel.SITE, iamOrganizationService::updateBySite);
        handler.put(ResourceLevel.ORGANIZATION, iamOrganizationService::updateByOrganization);
        if (handler.containsKey(level)) {
            IamOrganizationDTO iamOrganization = handler.get(level).apply(id, vo);
            return new ResponseEntity<>(ConvertHelper.convert(iamOrganization, IamOrganizationVO.class));
        }
        return new ResponseEntity<>();
    }
}

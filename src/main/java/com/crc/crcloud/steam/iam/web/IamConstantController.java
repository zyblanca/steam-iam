package com.crc.crcloud.steam.iam.web;

import cn.hutool.core.map.MapUtil;
import com.crc.crcloud.steam.iam.common.enums.UserOriginEnum;
import com.crc.crcloud.steam.iam.common.utils.ResponseEntity;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 负责静态数据的读取，不会因为path上面的project_id和organization_id影响
 * @author hand-196
 */
@Validated
@Api("")
@RestController
@RequestMapping(value = "/v1/constant")
public class IamConstantController {

//    @Permission(level = ResourceLevel.SITE)
    @Permission(permissionLogin = true)
    @ApiOperation("获取用户来源列表-静态列表")
    @GetMapping("/user/origin")
    public ResponseEntity<List<Map<String, String>>> getUserOrigins() {
        List<Map<String, String>> collect = Arrays.stream(UserOriginEnum.values()).map(t -> {
            return MapUtil.<String, String>builder().put("value", t.getValue()).put("desc", t.getDesc()).build();
        }).collect(Collectors.toList());
        return new ResponseEntity<>(collect);
    }


}

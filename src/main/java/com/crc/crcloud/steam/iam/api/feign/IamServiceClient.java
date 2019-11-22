package com.crc.crcloud.steam.iam.api.feign;

import com.crc.crcloud.steam.iam.api.feign.callback.IamServiceClientFallback;
import com.crc.crcloud.steam.iam.model.dto.user.UserDTO;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


/**
 * @Author: tankang3
 * @Date: 2019-06-25
 * @Description: 查询用户信息
 */
@FeignClient(value = "iam-service", fallback = IamServiceClientFallback.class)
public interface IamServiceClient {

    @ApiOperation(value = "创建用户")
    @PostMapping("/v1/organizations/{organization_id}/users")
    ResponseEntity<UserDTO> create(@PathVariable(name = "organization_id") Long organizationId,
                                   @RequestBody @Validated UserDTO userDTO);
}

package com.crc.crcloud.steam.iam.api.feign;

import com.crc.crcloud.steam.iam.api.feign.callback.IamServiceClientFallback;
import com.crc.crcloud.steam.iam.api.feign.callback.SteamAgileServiceClientFallback;
import com.crc.crcloud.steam.iam.entity.IamUser;
import com.crc.crcloud.steam.iam.entity.OauthLdapErrorUser;
import com.crc.crcloud.steam.iam.model.dto.IamProjectDTO;
import com.crc.crcloud.steam.iam.model.feign.role.MemberRoleDTO;
import com.crc.crcloud.steam.iam.model.feign.role.RoleDTO;
import com.crc.crcloud.steam.iam.model.feign.user.UserDTO;
import io.choerodon.core.iam.InitRoleCode;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.core.validator.ValidList;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * @Author: tankang3
 * @Date: 2019-06-25
 * @Description: 查询用户信息
 */
@FeignClient(value = "steam-agile", fallback = SteamAgileServiceClientFallback.class)
public interface SteamAgileServiceClient {

    @PostMapping("/v1/projects/{project_id}/agile_issue/init/template/{user_id}")
    public ResponseEntity initKanbanTemplate(@PathVariable(value = "project_id") Long projectId,
                                             @PathVariable(value = "user_id") Long userId,
                                             @RequestBody IamProjectDTO iamProjectDTO);

}

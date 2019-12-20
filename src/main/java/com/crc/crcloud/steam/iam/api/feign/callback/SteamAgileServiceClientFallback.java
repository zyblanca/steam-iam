package com.crc.crcloud.steam.iam.api.feign.callback;

import com.crc.crcloud.steam.iam.api.feign.IamServiceClient;
import com.crc.crcloud.steam.iam.api.feign.SteamAgileServiceClient;
import com.crc.crcloud.steam.iam.common.exception.IamAppCommException;
import com.crc.crcloud.steam.iam.entity.IamUser;
import com.crc.crcloud.steam.iam.entity.OauthLdapErrorUser;
import com.crc.crcloud.steam.iam.model.dto.IamProjectDTO;
import com.crc.crcloud.steam.iam.model.feign.role.MemberRoleDTO;
import com.crc.crcloud.steam.iam.model.feign.role.RoleDTO;
import com.crc.crcloud.steam.iam.model.feign.user.UserDTO;
import io.choerodon.core.validator.ValidList;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author LiuYang
 * @date 2019/11/22
 */
@Component
public class SteamAgileServiceClientFallback implements SteamAgileServiceClient {


    @Override
    public ResponseEntity initKanbanTemplate(Long projectId, Long userId, IamProjectDTO iamProjectDTO) {
        throw new IamAppCommException("comm.feign.error");
    }
}

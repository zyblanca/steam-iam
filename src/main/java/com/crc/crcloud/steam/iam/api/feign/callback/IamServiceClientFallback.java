package com.crc.crcloud.steam.iam.api.feign.callback;

import com.crc.crcloud.steam.iam.api.feign.IamServiceClient;
import com.crc.crcloud.steam.iam.entity.IamUser;
import com.crc.crcloud.steam.iam.entity.OauthLdapErrorUser;
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
public class IamServiceClientFallback implements IamServiceClient {
    @Override
    public ResponseEntity<UserDTO> create(Long organizationId, UserDTO userDTO) {
        return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<RoleDTO> queryByCode(String code) {
        return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<List<MemberRoleDTO>> createOrUpdateOnSiteLevel(Boolean isEdit, String memberType, List<Long> memberIds, ValidList<MemberRoleDTO> memberRoleDTOList) {
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<List<MemberRoleDTO>> createOrUpdateOnOrganizationLevel(Boolean isEdit, Long sourceId, String memberType, List<Long> memberIds, ValidList<MemberRoleDTO> memberRoleDTOList) {
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<List<MemberRoleDTO>> createOrUpdateOnProjectLevel(Boolean isEdit, Long sourceId, String memberType, List<Long> memberIds, ValidList<MemberRoleDTO> memberRoleDTOList) {
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<List<UserDTO>> listUsersByEmails(String[] emails) {
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<UserDTO> queryByLoginName(String loginName) {
        return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<List<OauthLdapErrorUser>> syncSteamUser(Long organizationId, List<IamUser> users) {
        return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<UserDTO> syncSteamUser(UserDTO userDTO) {
        return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

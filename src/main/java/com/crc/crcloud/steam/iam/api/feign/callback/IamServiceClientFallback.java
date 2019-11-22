package com.crc.crcloud.steam.iam.api.feign.callback;

import com.crc.crcloud.steam.iam.api.feign.IamServiceClient;
import com.crc.crcloud.steam.iam.model.dto.user.UserDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

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
}

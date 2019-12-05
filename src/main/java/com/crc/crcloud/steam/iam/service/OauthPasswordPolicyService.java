package com.crc.crcloud.steam.iam.service;

import com.crc.crcloud.steam.iam.model.dto.OauthPasswordPolicyDTO;


public interface OauthPasswordPolicyService {

    OauthPasswordPolicyDTO create(Long orgId, OauthPasswordPolicyDTO passwordPolicyDTO);

    OauthPasswordPolicyDTO queryByOrgId(Long orgId);

    OauthPasswordPolicyDTO query(Long id);

    OauthPasswordPolicyDTO update(Long orgId, Long id, OauthPasswordPolicyDTO passwordPolicyDTO);

    void sagaCreatePasswordPolicy(Long orgId, OauthPasswordPolicyDTO passwordPolicyDTO);
}

package com.crc.crcloud.steam.iam.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.crc.crcloud.steam.iam.common.exception.IamAppCommException;
import com.crc.crcloud.steam.iam.dao.OauthPasswordPolicyMapper;
import com.crc.crcloud.steam.iam.entity.OauthPasswordPolicy;
import com.crc.crcloud.steam.iam.model.dto.OauthPasswordPolicyDTO;
import com.crc.crcloud.steam.iam.service.IamOrganizationService;
import com.crc.crcloud.steam.iam.service.OauthPasswordPolicyService;
import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.exception.CommonException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class OauthPasswordPolicyServiceImpl implements OauthPasswordPolicyService {


    @Autowired
    private IamOrganizationService iamOrganizationService;
    @Autowired
    private OauthPasswordPolicyMapper oauthPasswordPolicyMapper;


    @Override
    public OauthPasswordPolicyDTO create(Long orgId, OauthPasswordPolicyDTO passwordPolicyDTO) {
        // 验证组织是否存在
        iamOrganizationService.getAndThrow(orgId);
        passwordPolicyDTO.setOrganizationId(orgId);
        OauthPasswordPolicy oauthPasswordPolicy = ConvertHelper.convert(passwordPolicyDTO, OauthPasswordPolicy.class);
        ;
        if (1 != oauthPasswordPolicyMapper.insert(oauthPasswordPolicy)){
            throw new IamAppCommException("error.passwordPolicy.create");
        }
        return ConvertHelper.convert(oauthPasswordPolicy, OauthPasswordPolicyDTO.class);
    }

    @Override
    public OauthPasswordPolicyDTO queryByOrgId(Long orgId) {
        OauthPasswordPolicy query = OauthPasswordPolicy.builder().organizationId(orgId).build();
        OauthPasswordPolicy oauthPasswordPolicy = oauthPasswordPolicyMapper.selectOne(new QueryWrapper<>(query));
        return ConvertHelper.convert(oauthPasswordPolicy, OauthPasswordPolicyDTO.class);
    }

    @Override
    public OauthPasswordPolicyDTO query(Long id) {
        OauthPasswordPolicy oauthPasswordPolicy = oauthPasswordPolicyMapper.selectById(id);
        return ConvertHelper.convert(oauthPasswordPolicy, OauthPasswordPolicyDTO.class);
    }

    @Override
    public OauthPasswordPolicyDTO update(Long orgId, Long id, OauthPasswordPolicyDTO passwordPolicyDTO) {
        // 验证组织是否存在
        iamOrganizationService.getAndThrow(orgId);

        OauthPasswordPolicyDTO old = query(id);
        if (!orgId.equals(old.getOrganizationId())) {
            throw new CommonException("error.passwordPolicy.organizationId.not.same");
        }
        OauthPasswordPolicy passwordPolicy = ConvertHelper.convert(passwordPolicyDTO, OauthPasswordPolicy.class);
        oauthPasswordPolicyMapper.updateById(passwordPolicy);
        return ConvertHelper.convert(passwordPolicy, OauthPasswordPolicyDTO.class);
    }

    @Override
    public void sagaCreatePasswordPolicy(Long orgId, OauthPasswordPolicyDTO passwordPolicyDTO){
        this.create(orgId, passwordPolicyDTO);
    }
}

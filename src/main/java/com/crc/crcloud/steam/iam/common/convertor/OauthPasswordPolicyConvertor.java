package com.crc.crcloud.steam.iam.common.convertor;

import com.crc.crcloud.steam.iam.entity.OauthPasswordPolicy;
import com.crc.crcloud.steam.iam.model.dto.OauthPasswordPolicyDTO;
import com.crc.crcloud.steam.iam.model.vo.OauthPasswordPolicyVO;
import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;

public class OauthPasswordPolicyConvertor implements ConvertorI<OauthPasswordPolicyDTO, OauthPasswordPolicy, OauthPasswordPolicyVO> {

    @Override
    public OauthPasswordPolicyDTO dtoToEntity(OauthPasswordPolicyVO vo) {
        OauthPasswordPolicyDTO oauthPasswordPolicyDTO = new OauthPasswordPolicyDTO();
        BeanUtils.copyProperties(vo, oauthPasswordPolicyDTO);
        return oauthPasswordPolicyDTO;
    }

    @Override
    public OauthPasswordPolicyVO entityToDto(OauthPasswordPolicyDTO dto) {
        OauthPasswordPolicyVO oauthPasswordPolicyVO = new OauthPasswordPolicyVO();
        BeanUtils.copyProperties(dto, oauthPasswordPolicyVO);
        return oauthPasswordPolicyVO;
    }

    @Override
    public OauthPasswordPolicyDTO doToEntity(OauthPasswordPolicy dataObject) {
        OauthPasswordPolicyDTO oauthPasswordPolicyDTO = new OauthPasswordPolicyDTO();
        BeanUtils.copyProperties(dataObject, oauthPasswordPolicyDTO);
        return oauthPasswordPolicyDTO;
    }

    @Override
    public OauthPasswordPolicy entityToDo(OauthPasswordPolicyDTO entity) {
        OauthPasswordPolicy oauthPasswordPolicy = new OauthPasswordPolicy();
        BeanUtils.copyProperties(entity, oauthPasswordPolicy);
        return oauthPasswordPolicy;
    }
}

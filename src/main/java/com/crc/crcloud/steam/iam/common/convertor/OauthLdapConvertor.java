package com.crc.crcloud.steam.iam.common.convertor;

import com.crc.crcloud.steam.iam.entity.OauthLdap;
import com.crc.crcloud.steam.iam.model.dto.OauthLdapDTO;
import com.crc.crcloud.steam.iam.model.vo.OauthLdapVO;
import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * @Author
 * @Description
 * @Date 2019-11-12
 */
@Component
public class OauthLdapConvertor implements ConvertorI<OauthLdapDTO, OauthLdap, OauthLdapVO> {

    @Override
    public OauthLdapDTO dtoToEntity(OauthLdapVO vo) {
        OauthLdapDTO oauthLdapDTO = new OauthLdapDTO();
        BeanUtils.copyProperties(vo, oauthLdapDTO);
        return oauthLdapDTO;
    }

    @Override
    public OauthLdapVO entityToDto(OauthLdapDTO entity) {
        OauthLdapVO oauthLdapVO = new OauthLdapVO();
        BeanUtils.copyProperties(entity, oauthLdapVO);
        return oauthLdapVO;
    }

    @Override
    public OauthLdapDTO doToEntity(OauthLdap dataObject) {
        OauthLdapDTO oauthLdapDTO = new OauthLdapDTO();
        BeanUtils.copyProperties(dataObject, oauthLdapDTO);
        return oauthLdapDTO;
    }

    @Override
    public OauthLdap entityToDo(OauthLdapDTO dto) {
        OauthLdap oauthLdap = new OauthLdap();
        BeanUtils.copyProperties(dto, oauthLdap);
        return oauthLdap;
    }
}

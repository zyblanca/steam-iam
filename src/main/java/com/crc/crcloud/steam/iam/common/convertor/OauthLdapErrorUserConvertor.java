package com.crc.crcloud.steam.iam.common.convertor;

import com.crc.crcloud.steam.iam.entity.OauthLdapErrorUser;
import com.crc.crcloud.steam.iam.model.dto.OauthLdapErrorUserDTO;
import com.crc.crcloud.steam.iam.model.vo.OauthLdapErrorUserVO;
import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
/**
 * @Author
 * @Description 
 * @Date 2019-11-12
 */
@Component
public class OauthLdapErrorUserConvertor implements ConvertorI<OauthLdapErrorUserDTO, OauthLdapErrorUser, OauthLdapErrorUserVO> {

    @Override
    public OauthLdapErrorUserDTO dtoToEntity(OauthLdapErrorUserVO vo) {
        OauthLdapErrorUserDTO oauthLdapErrorUserDTO = new OauthLdapErrorUserDTO();
        BeanUtils.copyProperties(vo, oauthLdapErrorUserDTO);
        return oauthLdapErrorUserDTO;
    }

    @Override
    public OauthLdapErrorUserVO entityToDto(OauthLdapErrorUserDTO entity) {
        OauthLdapErrorUserVO oauthLdapErrorUserVO = new OauthLdapErrorUserVO();
        BeanUtils.copyProperties(entity, oauthLdapErrorUserVO);
        return oauthLdapErrorUserVO;
    }

    @Override
    public OauthLdapErrorUserDTO doToEntity(OauthLdapErrorUser dataObject) {
        OauthLdapErrorUserDTO oauthLdapErrorUserDTO = new OauthLdapErrorUserDTO();
        BeanUtils.copyProperties(dataObject, oauthLdapErrorUserDTO);
        return oauthLdapErrorUserDTO;
    }

    @Override
    public OauthLdapErrorUser entityToDo(OauthLdapErrorUserDTO dto) {
        OauthLdapErrorUser oauthLdapErrorUser = new OauthLdapErrorUser();
        BeanUtils.copyProperties(dto, oauthLdapErrorUser);
        return oauthLdapErrorUser;
    }
}

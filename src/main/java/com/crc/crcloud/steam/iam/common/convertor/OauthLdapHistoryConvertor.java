package com.crc.crcloud.steam.iam.common.convertor;

import com.crc.crcloud.steam.iam.entity.OauthLdapHistory;
import com.crc.crcloud.steam.iam.model.dto.OauthLdapHistoryDTO;
import com.crc.crcloud.steam.iam.model.vo.OauthLdapHistoryVO;
import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * @Author
 * @Description
 * @Date 2019-11-12
 */
@Component
public class OauthLdapHistoryConvertor implements ConvertorI<OauthLdapHistoryDTO, OauthLdapHistory, OauthLdapHistoryVO> {

    @Override
    public OauthLdapHistoryDTO dtoToEntity(OauthLdapHistoryVO vo) {
        OauthLdapHistoryDTO oauthLdapHistoryDTO = new OauthLdapHistoryDTO();
        BeanUtils.copyProperties(vo, oauthLdapHistoryDTO);
        return oauthLdapHistoryDTO;
    }

    @Override
    public OauthLdapHistoryVO entityToDto(OauthLdapHistoryDTO entity) {
        OauthLdapHistoryVO oauthLdapHistoryVO = new OauthLdapHistoryVO();
        BeanUtils.copyProperties(entity, oauthLdapHistoryVO);
        return oauthLdapHistoryVO;
    }

    @Override
    public OauthLdapHistoryDTO doToEntity(OauthLdapHistory dataObject) {
        OauthLdapHistoryDTO oauthLdapHistoryDTO = new OauthLdapHistoryDTO();
        BeanUtils.copyProperties(dataObject, oauthLdapHistoryDTO);
        return oauthLdapHistoryDTO;
    }

    @Override
    public OauthLdapHistory entityToDo(OauthLdapHistoryDTO dto) {
        OauthLdapHistory oauthLdapHistory = new OauthLdapHistory();
        BeanUtils.copyProperties(dto, oauthLdapHistory);
        return oauthLdapHistory;
    }
}

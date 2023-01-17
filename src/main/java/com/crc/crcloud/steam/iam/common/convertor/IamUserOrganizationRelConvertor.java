package com.crc.crcloud.steam.iam.common.convertor;

import com.crc.crcloud.steam.iam.entity.IamUserOrganizationRel;
import com.crc.crcloud.steam.iam.model.dto.IamUserOrganizationRelDTO;
import com.crc.crcloud.steam.iam.model.vo.IamUserOrganizationRelVO;
import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * @Author
 * @Description
 * @Date 2019-11-12
 */
@Component
public class IamUserOrganizationRelConvertor implements ConvertorI<IamUserOrganizationRelDTO, IamUserOrganizationRel, IamUserOrganizationRelVO> {

    @Override
    public IamUserOrganizationRelDTO dtoToEntity(IamUserOrganizationRelVO vo) {
        IamUserOrganizationRelDTO iamUserOrganizationRelDTO = new IamUserOrganizationRelDTO();
        BeanUtils.copyProperties(vo, iamUserOrganizationRelDTO);
        return iamUserOrganizationRelDTO;
    }

    @Override
    public IamUserOrganizationRelVO entityToDto(IamUserOrganizationRelDTO entity) {
        IamUserOrganizationRelVO iamUserOrganizationRelVO = new IamUserOrganizationRelVO();
        BeanUtils.copyProperties(entity, iamUserOrganizationRelVO);
        return iamUserOrganizationRelVO;
    }

    @Override
    public IamUserOrganizationRelDTO doToEntity(IamUserOrganizationRel dataObject) {
        IamUserOrganizationRelDTO iamUserOrganizationRelDTO = new IamUserOrganizationRelDTO();
        BeanUtils.copyProperties(dataObject, iamUserOrganizationRelDTO);
        return iamUserOrganizationRelDTO;
    }

    @Override
    public IamUserOrganizationRel entityToDo(IamUserOrganizationRelDTO dto) {
        IamUserOrganizationRel iamUserOrganizationRel = new IamUserOrganizationRel();
        BeanUtils.copyProperties(dto, iamUserOrganizationRel);
        return iamUserOrganizationRel;
    }
}

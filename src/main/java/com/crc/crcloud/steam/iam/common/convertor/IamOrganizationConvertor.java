package com.crc.crcloud.steam.iam.common.convertor;

import com.crc.crcloud.steam.iam.entity.IamOrganization;
import com.crc.crcloud.steam.iam.model.dto.IamOrganizationDTO;
import com.crc.crcloud.steam.iam.model.vo.IamOrganizationVO;
import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
/**
 * @Author
 * @Description 
 * @Date 2019-11-12
 */
@Component
public class IamOrganizationConvertor implements ConvertorI<IamOrganizationDTO, IamOrganization, IamOrganizationVO> {

    @Override
    public IamOrganizationDTO dtoToEntity(IamOrganizationVO vo) {
        IamOrganizationDTO iamOrganizationDTO = new IamOrganizationDTO();
        BeanUtils.copyProperties(vo, iamOrganizationDTO);
        return iamOrganizationDTO;
    }

    @Override
    public IamOrganizationVO entityToDto(IamOrganizationDTO entity) {
        IamOrganizationVO iamOrganizationVO = new IamOrganizationVO();
        BeanUtils.copyProperties(entity, iamOrganizationVO);
        return iamOrganizationVO;
    }

    @Override
    public IamOrganizationDTO doToEntity(IamOrganization dataObject) {
        IamOrganizationDTO iamOrganizationDTO = new IamOrganizationDTO();
        BeanUtils.copyProperties(dataObject, iamOrganizationDTO);
        return iamOrganizationDTO;
    }

    @Override
    public IamOrganization entityToDo(IamOrganizationDTO dto) {
        IamOrganization iamOrganization = new IamOrganization();
        BeanUtils.copyProperties(dto, iamOrganization);
        return iamOrganization;
    }
}

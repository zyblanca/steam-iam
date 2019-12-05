package com.crc.crcloud.steam.iam.common.convertor;

import com.crc.crcloud.steam.iam.entity.IamApplication;
import com.crc.crcloud.steam.iam.model.dto.IamApplicationDTO;
import com.crc.crcloud.steam.iam.model.vo.IamApplicationVO;
import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class IamApplicationConvertor implements ConvertorI<IamApplicationDTO, IamApplication, IamApplicationVO> {

    @Override
    public IamApplicationDTO dtoToEntity(IamApplicationVO vo) {
        IamApplicationDTO iamApplicationDTO = new IamApplicationDTO();
        BeanUtils.copyProperties(vo, iamApplicationDTO);
        return iamApplicationDTO;
    }

    @Override
    public IamApplicationVO entityToDto(IamApplicationDTO entity) {
        IamApplicationVO iamApplicationVO = new IamApplicationVO();
        BeanUtils.copyProperties(entity, iamApplicationVO);
        return iamApplicationVO;
    }

    @Override
    public IamApplicationDTO doToEntity(IamApplication dataObject) {
        IamApplicationDTO iamApplicationDTO = new IamApplicationDTO();
        BeanUtils.copyProperties(dataObject, iamApplicationDTO);
        return iamApplicationDTO;
    }

    @Override
    public IamApplication entityToDo(IamApplicationDTO dto) {
        IamApplication iamApplication = new IamApplication();
        BeanUtils.copyProperties(dto, iamApplication);
        return iamApplication;
    }
}

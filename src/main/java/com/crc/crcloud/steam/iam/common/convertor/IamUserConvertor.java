package com.crc.crcloud.steam.iam.common.convertor;

import com.crc.crcloud.steam.iam.entity.IamUser;
import com.crc.crcloud.steam.iam.model.dto.IamUserDTO;
import com.crc.crcloud.steam.iam.model.vo.IamUserVO;
import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
/**
 * @Author
 * @Description 
 * @Date 2019-11-12
 */
@Component
public class IamUserConvertor implements ConvertorI<IamUserDTO, IamUser, IamUserVO> {

    @Override
    public IamUserDTO dtoToEntity(IamUserVO vo) {
        IamUserDTO iamUserDTO = new IamUserDTO();
        BeanUtils.copyProperties(vo, iamUserDTO);
        return iamUserDTO;
    }

    @Override
    public IamUserVO entityToDto(IamUserDTO entity) {
        IamUserVO iamUserVO = new IamUserVO();
        BeanUtils.copyProperties(entity, iamUserVO);
        return iamUserVO;
    }

    @Override
    public IamUserDTO doToEntity(IamUser dataObject) {
        IamUserDTO iamUserDTO = new IamUserDTO();
        BeanUtils.copyProperties(dataObject, iamUserDTO);
        return iamUserDTO;
    }

    @Override
    public IamUser entityToDo(IamUserDTO dto) {
        IamUser iamUser = new IamUser();
        BeanUtils.copyProperties(dto, iamUser);
        return iamUser;
    }
}

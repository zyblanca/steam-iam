package com.crc.crcloud.steam.iam.common.convertor;

import com.crc.crcloud.steam.iam.entity.IamPermission;
import com.crc.crcloud.steam.iam.model.dto.IamPermissionDTO;
import com.crc.crcloud.steam.iam.model.vo.IamPermissionVO;
import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
/**
 * @Author
 * @Description 
 * @Date 2019-11-29
 */
@Component
public class IamPermissionConvertor implements ConvertorI<IamPermissionDTO, IamPermission, IamPermissionVO> {

    @Override
    public IamPermissionDTO dtoToEntity(IamPermissionVO vo) {
        IamPermissionDTO iamPermissionDTO = new IamPermissionDTO();
        BeanUtils.copyProperties(vo, iamPermissionDTO);
        return iamPermissionDTO;
    }

    @Override
    public IamPermissionVO entityToDto(IamPermissionDTO entity) {
        IamPermissionVO iamPermissionVO = new IamPermissionVO();
        BeanUtils.copyProperties(entity, iamPermissionVO);
        return iamPermissionVO;
    }

    @Override
    public IamPermissionDTO doToEntity(IamPermission dataObject) {
        IamPermissionDTO iamPermissionDTO = new IamPermissionDTO();
        BeanUtils.copyProperties(dataObject, iamPermissionDTO);
        return iamPermissionDTO;
    }

    @Override
    public IamPermission entityToDo(IamPermissionDTO dto) {
        IamPermission iamPermission = new IamPermission();
        BeanUtils.copyProperties(dto, iamPermission);
        return iamPermission;
    }
}

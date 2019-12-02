package com.crc.crcloud.steam.iam.common.convertor;

import com.crc.crcloud.steam.iam.entity.IamRolePermission;
import com.crc.crcloud.steam.iam.model.dto.IamRolePermissionDTO;
import com.crc.crcloud.steam.iam.model.vo.IamRolePermissionVO;
import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
/**
 * @Author
 * @Description 
 * @Date 2019-11-29
 */
@Component
public class IamRolePermissionConvertor implements ConvertorI<IamRolePermissionDTO, IamRolePermission, IamRolePermissionVO> {

    @Override
    public IamRolePermissionDTO dtoToEntity(IamRolePermissionVO vo) {
        IamRolePermissionDTO iamRolePermissionDTO = new IamRolePermissionDTO();
        BeanUtils.copyProperties(vo, iamRolePermissionDTO);
        return iamRolePermissionDTO;
    }

    @Override
    public IamRolePermissionVO entityToDto(IamRolePermissionDTO entity) {
        IamRolePermissionVO iamRolePermissionVO = new IamRolePermissionVO();
        BeanUtils.copyProperties(entity, iamRolePermissionVO);
        return iamRolePermissionVO;
    }

    @Override
    public IamRolePermissionDTO doToEntity(IamRolePermission dataObject) {
        IamRolePermissionDTO iamRolePermissionDTO = new IamRolePermissionDTO();
        BeanUtils.copyProperties(dataObject, iamRolePermissionDTO);
        return iamRolePermissionDTO;
    }

    @Override
    public IamRolePermission entityToDo(IamRolePermissionDTO dto) {
        IamRolePermission iamRolePermission = new IamRolePermission();
        BeanUtils.copyProperties(dto, iamRolePermission);
        return iamRolePermission;
    }
}

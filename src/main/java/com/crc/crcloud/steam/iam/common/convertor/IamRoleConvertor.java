package com.crc.crcloud.steam.iam.common.convertor;

import com.crc.crcloud.steam.iam.entity.IamRole;
import com.crc.crcloud.steam.iam.model.dto.IamRoleDTO;
import com.crc.crcloud.steam.iam.model.vo.IamRoleVO;
import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * @Author
 * @Description
 * @Date 2019-11-12
 */
@Component
public class IamRoleConvertor implements ConvertorI<IamRoleDTO, IamRole, IamRoleVO> {

    @Override
    public IamRoleDTO dtoToEntity(IamRoleVO vo) {
        IamRoleDTO iamRoleDTO = new IamRoleDTO();
        BeanUtils.copyProperties(vo, iamRoleDTO);
        return iamRoleDTO;
    }

    @Override
    public IamRoleVO entityToDto(IamRoleDTO entity) {
        IamRoleVO iamRoleVO = new IamRoleVO();
        BeanUtils.copyProperties(entity, iamRoleVO);
        return iamRoleVO;
    }

    @Override
    public IamRoleDTO doToEntity(IamRole dataObject) {
        IamRoleDTO iamRoleDTO = new IamRoleDTO();
        BeanUtils.copyProperties(dataObject, iamRoleDTO);
        return iamRoleDTO;
    }

    @Override
    public IamRole entityToDo(IamRoleDTO dto) {
        IamRole iamRole = new IamRole();
        BeanUtils.copyProperties(dto, iamRole);
        return iamRole;
    }
}

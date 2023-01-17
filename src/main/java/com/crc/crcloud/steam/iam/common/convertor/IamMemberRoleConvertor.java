package com.crc.crcloud.steam.iam.common.convertor;

import com.crc.crcloud.steam.iam.entity.IamMemberRole;
import com.crc.crcloud.steam.iam.model.dto.IamMemberRoleDTO;
import com.crc.crcloud.steam.iam.model.vo.IamMemberRoleVO;
import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * @Author
 * @Description
 * @Date 2019-11-12
 */
@Component
public class IamMemberRoleConvertor implements ConvertorI<IamMemberRoleDTO, IamMemberRole, IamMemberRoleVO> {

    @Override
    public IamMemberRoleDTO dtoToEntity(IamMemberRoleVO vo) {
        IamMemberRoleDTO iamMemberRoleDTO = new IamMemberRoleDTO();
        BeanUtils.copyProperties(vo, iamMemberRoleDTO);
        return iamMemberRoleDTO;
    }

    @Override
    public IamMemberRoleVO entityToDto(IamMemberRoleDTO entity) {
        IamMemberRoleVO iamMemberRoleVO = new IamMemberRoleVO();
        BeanUtils.copyProperties(entity, iamMemberRoleVO);
        return iamMemberRoleVO;
    }

    @Override
    public IamMemberRoleDTO doToEntity(IamMemberRole dataObject) {
        IamMemberRoleDTO iamMemberRoleDTO = new IamMemberRoleDTO();
        BeanUtils.copyProperties(dataObject, iamMemberRoleDTO);
        return iamMemberRoleDTO;
    }

    @Override
    public IamMemberRole entityToDo(IamMemberRoleDTO dto) {
        IamMemberRole iamMemberRole = new IamMemberRole();
        BeanUtils.copyProperties(dto, iamMemberRole);
        return iamMemberRole;
    }
}

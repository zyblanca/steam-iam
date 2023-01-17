package com.crc.crcloud.steam.iam.common.convertor;

import com.crc.crcloud.steam.iam.entity.IamRoleLabel;
import com.crc.crcloud.steam.iam.model.dto.IamRoleLabelDTO;
import com.crc.crcloud.steam.iam.model.vo.IamRoleLabelVO;
import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * @Author
 * @Description
 * @Date 2019-12-03
 */
@Component
public class IamRoleLabelConvertor implements ConvertorI<IamRoleLabelDTO, IamRoleLabel, IamRoleLabelVO> {

    @Override
    public IamRoleLabelDTO dtoToEntity(IamRoleLabelVO vo) {
        IamRoleLabelDTO iamRoleLabelDTO = new IamRoleLabelDTO();
        BeanUtils.copyProperties(vo, iamRoleLabelDTO);
        return iamRoleLabelDTO;
    }

    @Override
    public IamRoleLabelVO entityToDto(IamRoleLabelDTO entity) {
        IamRoleLabelVO iamRoleLabelVO = new IamRoleLabelVO();
        BeanUtils.copyProperties(entity, iamRoleLabelVO);
        return iamRoleLabelVO;
    }

    @Override
    public IamRoleLabelDTO doToEntity(IamRoleLabel dataObject) {
        IamRoleLabelDTO iamRoleLabelDTO = new IamRoleLabelDTO();
        BeanUtils.copyProperties(dataObject, iamRoleLabelDTO);
        return iamRoleLabelDTO;
    }

    @Override
    public IamRoleLabel entityToDo(IamRoleLabelDTO dto) {
        IamRoleLabel iamRoleLabel = new IamRoleLabel();
        BeanUtils.copyProperties(dto, iamRoleLabel);
        return iamRoleLabel;
    }
}

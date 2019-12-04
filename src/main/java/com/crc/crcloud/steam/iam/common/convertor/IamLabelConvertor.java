package com.crc.crcloud.steam.iam.common.convertor;

import com.crc.crcloud.steam.iam.entity.IamLabel;
import com.crc.crcloud.steam.iam.model.dto.IamLabelDTO;
import com.crc.crcloud.steam.iam.model.vo.IamLabelVO;
import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
/**
 * @Author
 * @Description 
 * @Date 2019-12-03
 */
@Component
public class IamLabelConvertor implements ConvertorI<IamLabelDTO, IamLabel, IamLabelVO> {

    @Override
    public IamLabelDTO dtoToEntity(IamLabelVO vo) {
        IamLabelDTO iamLabelDTO = new IamLabelDTO();
        BeanUtils.copyProperties(vo, iamLabelDTO);
        return iamLabelDTO;
    }

    @Override
    public IamLabelVO entityToDto(IamLabelDTO entity) {
        IamLabelVO iamLabelVO = new IamLabelVO();
        BeanUtils.copyProperties(entity, iamLabelVO);
        return iamLabelVO;
    }

    @Override
    public IamLabelDTO doToEntity(IamLabel dataObject) {
        IamLabelDTO iamLabelDTO = new IamLabelDTO();
        BeanUtils.copyProperties(dataObject, iamLabelDTO);
        return iamLabelDTO;
    }

    @Override
    public IamLabel entityToDo(IamLabelDTO dto) {
        IamLabel iamLabel = new IamLabel();
        BeanUtils.copyProperties(dto, iamLabel);
        return iamLabel;
    }
}

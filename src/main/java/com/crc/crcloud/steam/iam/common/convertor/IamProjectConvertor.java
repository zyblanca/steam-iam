package com.crc.crcloud.steam.iam.common.convertor;

import com.crc.crcloud.steam.iam.entity.IamProject;
import com.crc.crcloud.steam.iam.model.dto.IamProjectDTO;
import com.crc.crcloud.steam.iam.model.vo.IamProjectVO;
import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
/**
 * @Author
 * @Description 
 * @Date 2019-11-12
 */
@Component
public class IamProjectConvertor implements ConvertorI<IamProjectDTO, IamProject, IamProjectVO> {

    @Override
    public IamProjectDTO dtoToEntity(IamProjectVO vo) {
        IamProjectDTO iamProjectDTO = new IamProjectDTO();
        BeanUtils.copyProperties(vo, iamProjectDTO);
        return iamProjectDTO;
    }

    @Override
    public IamProjectVO entityToDto(IamProjectDTO entity) {
        IamProjectVO iamProjectVO = new IamProjectVO();
        BeanUtils.copyProperties(entity, iamProjectVO);
        return iamProjectVO;
    }

    @Override
    public IamProjectDTO doToEntity(IamProject dataObject) {
        IamProjectDTO iamProjectDTO = new IamProjectDTO();
        BeanUtils.copyProperties(dataObject, iamProjectDTO);
        return iamProjectDTO;
    }

    @Override
    public IamProject entityToDo(IamProjectDTO dto) {
        IamProject iamProject = new IamProject();
        BeanUtils.copyProperties(dto, iamProject);
        return iamProject;
    }
}

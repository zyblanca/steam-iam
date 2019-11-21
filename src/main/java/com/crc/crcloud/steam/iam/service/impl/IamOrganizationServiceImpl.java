package com.crc.crcloud.steam.iam.service.impl;



import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crc.crcloud.steam.iam.common.exception.IamAppCommException;
import com.crc.crcloud.steam.iam.dao.IamOrganizationMapper;
import com.crc.crcloud.steam.iam.entity.IamOrganization;
import com.crc.crcloud.steam.iam.model.dto.IamOrganizationDTO;
import com.crc.crcloud.steam.iam.model.vo.IamOrganizationVO;
import com.crc.crcloud.steam.iam.service.IamOrganizationService;
import io.choerodon.core.convertor.ConvertHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
/**
 * @Author
 * @Description 
 * @Date 2019-11-12
 */
@Service
public class IamOrganizationServiceImpl implements IamOrganizationService {

	@Autowired
	private IamOrganizationMapper iamOrganizationMapper;

	/**
	 * 新增
	 * @param projectId  项目ID
	 * @param iamOrganizationVO
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class,isolation = Isolation.READ_COMMITTED)
	@Override
	public IamOrganizationVO insert(Long projectId,IamOrganizationVO iamOrganizationVO){
		IamOrganizationDTO iamOrganizationDTO = ConvertHelper.convert(iamOrganizationVO,IamOrganizationDTO.class);
		IamOrganization iamOrganization = ConvertHelper.convert(iamOrganizationDTO,IamOrganization.class);
		iamOrganizationMapper.insert(iamOrganization);
		IamOrganizationDTO insertDTO = ConvertHelper.convert(iamOrganization,IamOrganizationDTO.class);
		return ConvertHelper.convert(insertDTO,IamOrganizationVO.class);
	}

	/**
	 * 删除
	 * @param projectId  项目ID
	 * @param id
	 */
	@Transactional(rollbackFor = Exception.class,isolation = Isolation.READ_COMMITTED)
	@Override
	public void delete(Long projectId,Long id){
		//如果表中含有projectId，请先查询数据，判断projectId是否一致 不一致抛异常，一致则进行删除
		iamOrganizationMapper.deleteById(id);
	}

	/**
	 * 更新
	 * @param projectId  项目ID
	 * @param iamOrganizationVO
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class,isolation = Isolation.READ_COMMITTED)
	@Override
	public IamOrganizationVO  update(Long projectId,IamOrganizationVO iamOrganizationVO){
		//最好使用自定义修改语句，修改条件包含项目ID
		IamOrganizationDTO dataDTO =ConvertHelper.convert(iamOrganizationVO,IamOrganizationDTO.class);
		iamOrganizationMapper.updateById(ConvertHelper.convert(dataDTO,IamOrganization.class));
			return queryOne(projectId ,iamOrganizationVO.getId());
	}
	/**
	 *
	 * 查询单个详情
	 * @param projectId  项目ID
	 * @param id
	 * @return
	 */
	@Override
	public IamOrganizationVO queryOne(Long projectId ,Long id){
		//查询的数据 如果包含项目ID，校验项目ID是否一致，不一致抛异常
		IamOrganization data = iamOrganizationMapper.selectById(id);
		if(Objects.isNull(data)){
			throw new IamAppCommException("common.data.null.error");
		}
		IamOrganizationDTO dataDTO = ConvertHelper.convert(data,IamOrganizationDTO.class);
		return ConvertHelper.convert(dataDTO, IamOrganizationVO.class);
	}
	/**
	 * 分页查询
	 * @param iamOrganizationVO
	 * @param projectId  项目ID
	 * @param page  分页信息
	 * @return
	 */
	@Override
	public IPage<IamOrganizationVO> queryPage(IamOrganizationVO iamOrganizationVO, Long projectId,Page page) {

		IamOrganizationDTO iamOrganizationDTO =ConvertHelper.convert(iamOrganizationVO,IamOrganizationDTO.class);


		//查询
		IPage<IamOrganization> pageResult = iamOrganizationMapper.page(page , iamOrganizationDTO);
		IPage<IamOrganizationVO> result = new Page<>();
		if (Objects.isNull(pageResult) || pageResult.getTotal() == 0) {
			return result;
		}

		result.setSize(pageResult.getSize());
		result.setTotal(pageResult.getTotal());
		List<IamOrganizationDTO> recordsDTO = ConvertHelper.convertList(pageResult.getRecords(),IamOrganizationDTO.class);
		List<IamOrganizationVO> recordsVO  = ConvertHelper.convertList(recordsDTO,IamOrganizationVO.class);
		result.setRecords(recordsVO);
		return result;
	}



}

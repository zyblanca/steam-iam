package com.crc.crcloud.steam.iam.service.impl;



import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crc.crcloud.steam.iam.common.exception.IamAppCommException;
import com.crc.crcloud.steam.iam.dao.IamUserOrganizationRelMapper;
import com.crc.crcloud.steam.iam.entity.IamUserOrganizationRel;
import com.crc.crcloud.steam.iam.model.dto.IamUserOrganizationRelDTO;
import com.crc.crcloud.steam.iam.model.vo.IamUserOrganizationRelVO;
import com.crc.crcloud.steam.iam.service.IamUserOrganizationRelService;
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
public class IamUserOrganizationRelServiceImpl implements IamUserOrganizationRelService {

	@Autowired
	private IamUserOrganizationRelMapper iamUserOrganizationRelMapper;

	/**
	 * 新增
	 * @param projectId  项目ID
	 * @param iamUserOrganizationRelVO
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class,isolation = Isolation.READ_COMMITTED)
	@Override
	public IamUserOrganizationRelVO insert(Long projectId,IamUserOrganizationRelVO iamUserOrganizationRelVO){
		IamUserOrganizationRelDTO iamUserOrganizationRelDTO = ConvertHelper.convert(iamUserOrganizationRelVO,IamUserOrganizationRelDTO.class);
		IamUserOrganizationRel iamUserOrganizationRel = ConvertHelper.convert(iamUserOrganizationRelDTO,IamUserOrganizationRel.class);
		iamUserOrganizationRelMapper.insert(iamUserOrganizationRel);
		IamUserOrganizationRelDTO insertDTO = ConvertHelper.convert(iamUserOrganizationRel,IamUserOrganizationRelDTO.class);
		return ConvertHelper.convert(insertDTO,IamUserOrganizationRelVO.class);
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
		iamUserOrganizationRelMapper.deleteById(id);
	}

	/**
	 * 更新
	 * @param projectId  项目ID
	 * @param iamUserOrganizationRelVO
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class,isolation = Isolation.READ_COMMITTED)
	@Override
	public IamUserOrganizationRelVO  update(Long projectId,IamUserOrganizationRelVO iamUserOrganizationRelVO){
		//最好使用自定义修改语句，修改条件包含项目ID
		IamUserOrganizationRelDTO dataDTO =ConvertHelper.convert(iamUserOrganizationRelVO,IamUserOrganizationRelDTO.class);
		iamUserOrganizationRelMapper.updateById(ConvertHelper.convert(dataDTO,IamUserOrganizationRel.class));
			return queryOne(projectId ,iamUserOrganizationRelVO.getId());
	}
	/**
	 *
	 * 查询单个详情
	 * @param projectId  项目ID
	 * @param id
	 * @return
	 */
	@Override
	public IamUserOrganizationRelVO queryOne(Long projectId ,Long id){
		//查询的数据 如果包含项目ID，校验项目ID是否一致，不一致抛异常
		IamUserOrganizationRel data = iamUserOrganizationRelMapper.selectById(id);
		if(Objects.isNull(data)){
			throw new IamAppCommException("common.data.null.error");
		}
		IamUserOrganizationRelDTO dataDTO = ConvertHelper.convert(data,IamUserOrganizationRelDTO.class);
		return ConvertHelper.convert(dataDTO, IamUserOrganizationRelVO.class);
	}
	/**
	 * 分页查询
	 * @param iamUserOrganizationRelVO
	 * @param projectId  项目ID
	 * @param page  分页信息
	 * @return
	 */
	@Override
	public IPage<IamUserOrganizationRelVO> queryPage(IamUserOrganizationRelVO iamUserOrganizationRelVO, Long projectId,Page page) {

		IamUserOrganizationRelDTO iamUserOrganizationRelDTO =ConvertHelper.convert(iamUserOrganizationRelVO,IamUserOrganizationRelDTO.class);


		//查询
		IPage<IamUserOrganizationRel> pageResult = iamUserOrganizationRelMapper.page(page , iamUserOrganizationRelDTO);
		IPage<IamUserOrganizationRelVO> result = new Page<>();
		if (Objects.isNull(pageResult) || pageResult.getTotal() == 0) {
			return result;
		}

		result.setSize(pageResult.getSize());
		result.setTotal(pageResult.getTotal());
		List<IamUserOrganizationRelDTO> recordsDTO = ConvertHelper.convertList(pageResult.getRecords(),IamUserOrganizationRelDTO.class);
		List<IamUserOrganizationRelVO> recordsVO  = ConvertHelper.convertList(recordsDTO,IamUserOrganizationRelVO.class);
		result.setRecords(recordsVO);
		return result;
	}



}

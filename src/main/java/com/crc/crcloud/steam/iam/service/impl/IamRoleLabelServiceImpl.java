package com.crc.crcloud.steam.iam.service.impl;



import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crc.crcloud.steam.iam.common.exception.IamAppCommException;
import com.crc.crcloud.steam.iam.dao.IamRoleLabelMapper;
import com.crc.crcloud.steam.iam.entity.IamRoleLabel;
import com.crc.crcloud.steam.iam.model.dto.IamRoleLabelDTO;
import com.crc.crcloud.steam.iam.model.vo.IamRoleLabelVO;
import com.crc.crcloud.steam.iam.service.IamRoleLabelService;
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
 * @Date 2019-12-03
 */
@Service
public class IamRoleLabelServiceImpl implements IamRoleLabelService {

	@Autowired
	private IamRoleLabelMapper iamRoleLabelMapper;

	/**
	 * 新增
	 * @param projectId  项目ID
	 * @param iamRoleLabelVO
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class,isolation = Isolation.READ_COMMITTED)
	@Override
	public IamRoleLabelVO insert(Long projectId,IamRoleLabelVO iamRoleLabelVO){
		IamRoleLabelDTO iamRoleLabelDTO = ConvertHelper.convert(iamRoleLabelVO,IamRoleLabelDTO.class);
		IamRoleLabel iamRoleLabel = ConvertHelper.convert(iamRoleLabelDTO,IamRoleLabel.class);
		iamRoleLabelMapper.insert(iamRoleLabel);
		IamRoleLabelDTO insertDTO = ConvertHelper.convert(iamRoleLabel,IamRoleLabelDTO.class);
		return ConvertHelper.convert(insertDTO,IamRoleLabelVO.class);
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
		iamRoleLabelMapper.deleteById(id);
	}

	/**
	 * 更新
	 * @param projectId  项目ID
	 * @param iamRoleLabelVO
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class,isolation = Isolation.READ_COMMITTED)
	@Override
	public IamRoleLabelVO  update(Long projectId,IamRoleLabelVO iamRoleLabelVO){
		//最好使用自定义修改语句，修改条件包含项目ID
		IamRoleLabelDTO dataDTO =ConvertHelper.convert(iamRoleLabelVO,IamRoleLabelDTO.class);
		iamRoleLabelMapper.updateById(ConvertHelper.convert(dataDTO,IamRoleLabel.class));
			return queryOne(projectId ,iamRoleLabelVO.getId());
	}
	/**
	 *
	 * 查询单个详情
	 * @param projectId  项目ID
	 * @param id
	 * @return
	 */
	@Override
	public IamRoleLabelVO queryOne(Long projectId ,Long id){
		//查询的数据 如果包含项目ID，校验项目ID是否一致，不一致抛异常
		IamRoleLabel data = iamRoleLabelMapper.selectById(id);
		if(Objects.isNull(data)){
			throw new IamAppCommException("common.data.null.error");
		}
		IamRoleLabelDTO dataDTO = ConvertHelper.convert(data,IamRoleLabelDTO.class);
		return ConvertHelper.convert(dataDTO, IamRoleLabelVO.class);
	}
	/**
	 * 分页查询
	 * @param iamRoleLabelVO
	 * @param projectId  项目ID
	 * @param page  分页信息
	 * @return
	 */
	@Override
	public IPage<IamRoleLabelVO> queryPage(IamRoleLabelVO iamRoleLabelVO, Long projectId,Page page) {

		IamRoleLabelDTO iamRoleLabelDTO =ConvertHelper.convert(iamRoleLabelVO,IamRoleLabelDTO.class);


		//查询
		IPage<IamRoleLabel> pageResult = iamRoleLabelMapper.page(page , iamRoleLabelDTO);
		IPage<IamRoleLabelVO> result = new Page<>();
		if (Objects.isNull(pageResult) || pageResult.getTotal() == 0) {
			return result;
		}

		result.setSize(pageResult.getSize());
		result.setTotal(pageResult.getTotal());
		List<IamRoleLabelDTO> recordsDTO = ConvertHelper.convertList(pageResult.getRecords(),IamRoleLabelDTO.class);
		List<IamRoleLabelVO> recordsVO  = ConvertHelper.convertList(recordsDTO,IamRoleLabelVO.class);
		result.setRecords(recordsVO);
		return result;
	}



}

package com.crc.crcloud.steam.iam.service.impl;



import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crc.crcloud.steam.iam.common.exception.IamAppCommException;
import com.crc.crcloud.steam.iam.dao.IamRoleMapper;
import com.crc.crcloud.steam.iam.entity.IamRole;
import com.crc.crcloud.steam.iam.model.dto.IamRoleDTO;
import com.crc.crcloud.steam.iam.model.vo.IamRoleVO;
import com.crc.crcloud.steam.iam.service.IamRoleService;
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
public class IamRoleServiceImpl implements IamRoleService {

	@Autowired
	private IamRoleMapper iamRoleMapper;

	/**
	 * 新增
	 * @param projectId  项目ID
	 * @param iamRoleVO
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class,isolation = Isolation.READ_COMMITTED)
	@Override
	public IamRoleVO insert(Long projectId,IamRoleVO iamRoleVO){
		IamRoleDTO iamRoleDTO = ConvertHelper.convert(iamRoleVO,IamRoleDTO.class);
		IamRole iamRole = ConvertHelper.convert(iamRoleDTO,IamRole.class);
		iamRoleMapper.insert(iamRole);
		IamRoleDTO insertDTO = ConvertHelper.convert(iamRole,IamRoleDTO.class);
		return ConvertHelper.convert(insertDTO,IamRoleVO.class);
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
		iamRoleMapper.deleteById(id);
	}

	/**
	 * 更新
	 * @param projectId  项目ID
	 * @param iamRoleVO
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class,isolation = Isolation.READ_COMMITTED)
	@Override
	public IamRoleVO  update(Long projectId,IamRoleVO iamRoleVO){
		//最好使用自定义修改语句，修改条件包含项目ID
		IamRoleDTO dataDTO =ConvertHelper.convert(iamRoleVO,IamRoleDTO.class);
		iamRoleMapper.updateById(ConvertHelper.convert(dataDTO,IamRole.class));
			return queryOne(projectId ,iamRoleVO.getId());
	}
	/**
	 *
	 * 查询单个详情
	 * @param projectId  项目ID
	 * @param id
	 * @return
	 */
	@Override
	public IamRoleVO queryOne(Long projectId ,Long id){
		//查询的数据 如果包含项目ID，校验项目ID是否一致，不一致抛异常
		IamRole data = iamRoleMapper.selectById(id);
		if(Objects.isNull(data)){
			throw new IamAppCommException("common.data.null.error");
		}
		IamRoleDTO dataDTO = ConvertHelper.convert(data,IamRoleDTO.class);
		return ConvertHelper.convert(dataDTO, IamRoleVO.class);
	}
	/**
	 * 分页查询
	 * @param iamRoleVO
	 * @param projectId  项目ID
	 * @param page  分页信息
	 * @return
	 */
	@Override
	public IPage<IamRoleVO> queryPage(IamRoleVO iamRoleVO, Long projectId,Page page) {

		IamRoleDTO iamRoleDTO =ConvertHelper.convert(iamRoleVO,IamRoleDTO.class);


		//查询
		IPage<IamRole> pageResult = iamRoleMapper.page(page , iamRoleDTO);
		IPage<IamRoleVO> result = new Page<>();
		if (Objects.isNull(pageResult) || pageResult.getTotal() == 0) {
			return result;
		}

		result.setSize(pageResult.getSize());
		result.setTotal(pageResult.getTotal());
		List<IamRoleDTO> recordsDTO = ConvertHelper.convertList(pageResult.getRecords(),IamRoleDTO.class);
		List<IamRoleVO> recordsVO  = ConvertHelper.convertList(recordsDTO,IamRoleVO.class);
		result.setRecords(recordsVO);
		return result;
	}



}

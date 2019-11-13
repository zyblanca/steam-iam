package com.crc.crcloud.steam.iam.service.impl;



import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crc.crcloud.steam.iam.common.exception.IamAppCommException;
import com.crc.crcloud.steam.iam.dao.IamMemberRoleMapper;
import com.crc.crcloud.steam.iam.entity.IamMemberRole;
import com.crc.crcloud.steam.iam.model.dto.IamMemberRoleDTO;
import com.crc.crcloud.steam.iam.model.vo.IamMemberRoleVO;
import com.crc.crcloud.steam.iam.service.IamMemberRoleService;
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
public class IamMemberRoleServiceImpl implements IamMemberRoleService {

	@Autowired
	private IamMemberRoleMapper iamMemberRoleMapper;

	/**
	 * 新增
	 * @param projectId  项目ID
	 * @param iamMemberRoleVO
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class,isolation = Isolation.READ_COMMITTED)
	@Override
	public IamMemberRoleVO insert(Long projectId,IamMemberRoleVO iamMemberRoleVO){
		IamMemberRoleDTO iamMemberRoleDTO = ConvertHelper.convert(iamMemberRoleVO,IamMemberRoleDTO.class);
		IamMemberRole iamMemberRole = ConvertHelper.convert(iamMemberRoleDTO,IamMemberRole.class);
		iamMemberRoleMapper.insert(iamMemberRole);
		IamMemberRoleDTO insertDTO = ConvertHelper.convert(iamMemberRole,IamMemberRoleDTO.class);
		return ConvertHelper.convert(insertDTO,IamMemberRoleVO.class);
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
		iamMemberRoleMapper.deleteById(id);
	}

	/**
	 * 更新
	 * @param projectId  项目ID
	 * @param iamMemberRoleVO
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class,isolation = Isolation.READ_COMMITTED)
	@Override
	public IamMemberRoleVO  update(Long projectId,IamMemberRoleVO iamMemberRoleVO){
		//最好使用自定义修改语句，修改条件包含项目ID
		IamMemberRoleDTO dataDTO =ConvertHelper.convert(iamMemberRoleVO,IamMemberRoleDTO.class);
		iamMemberRoleMapper.updateById(ConvertHelper.convert(dataDTO,IamMemberRole.class));
			return queryOne(projectId ,iamMemberRoleVO.getId());
	}
	/**
	 *
	 * 查询单个详情
	 * @param projectId  项目ID
	 * @param id
	 * @return
	 */
	@Override
	public IamMemberRoleVO queryOne(Long projectId ,Long id){
		//查询的数据 如果包含项目ID，校验项目ID是否一致，不一致抛异常
		IamMemberRole data = iamMemberRoleMapper.selectById(id);
		if(Objects.isNull(data)){
			throw new IamAppCommException("common.data.null.error");
		}
		IamMemberRoleDTO dataDTO = ConvertHelper.convert(data,IamMemberRoleDTO.class);
		return ConvertHelper.convert(dataDTO, IamMemberRoleVO.class);
	}
	/**
	 * 分页查询
	 * @param iamMemberRoleVO
	 * @param projectId  项目ID
	 * @param page  分页信息
	 * @return
	 */
	@Override
	public IPage<IamMemberRoleVO> queryPage(IamMemberRoleVO iamMemberRoleVO, Long projectId,Page page) {

		IamMemberRoleDTO iamMemberRoleDTO =ConvertHelper.convert(iamMemberRoleVO,IamMemberRoleDTO.class);


		//查询
		IPage<IamMemberRole> pageResult = iamMemberRoleMapper.page(page , iamMemberRoleDTO);
		IPage<IamMemberRoleVO> result = new Page<>();
		if (Objects.isNull(pageResult) || pageResult.getTotal() == 0) {
			return result;
		}

		result.setSize(pageResult.getSize());
		result.setTotal(pageResult.getTotal());
		List<IamMemberRoleDTO> recordsDTO = ConvertHelper.convertList(pageResult.getRecords(),IamMemberRoleDTO.class);
		List<IamMemberRoleVO> recordsVO  = ConvertHelper.convertList(recordsDTO,IamMemberRoleVO.class);
		result.setRecords(recordsVO);
		return result;
	}



}

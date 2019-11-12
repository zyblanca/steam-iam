package com.crc.crcloud.steam.iam.service.impl;



import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crc.crcloud.steam.iam.common.exception.IamAppCommException;
import com.crc.crcloud.steam.iam.dao.IamUserMapper;
import com.crc.crcloud.steam.iam.entity.IamUser;
import com.crc.crcloud.steam.iam.model.dto.IamUserDTO;
import com.crc.crcloud.steam.iam.model.vo.IamUserVO;
import com.crc.crcloud.steam.iam.service.IamUserService;
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
public class IamUserServiceImpl implements IamUserService {

	@Autowired
	private IamUserMapper iamUserMapper;

	/**
	 * 新增
	 * @param projectId  项目ID
	 * @param iamUserVO
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class,isolation = Isolation.READ_COMMITTED)
	@Override
	public IamUserVO insert(Long projectId,IamUserVO iamUserVO){
		IamUserDTO iamUserDTO = ConvertHelper.convert(iamUserVO,IamUserDTO.class);
		IamUser iamUser = ConvertHelper.convert(iamUserDTO,IamUser.class);
		iamUserMapper.insert(iamUser);
		IamUserDTO insertDTO = ConvertHelper.convert(iamUser,IamUserDTO.class);
		return ConvertHelper.convert(insertDTO,IamUserVO.class);
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
		iamUserMapper.deleteById(id);
	}

	/**
	 * 更新
	 * @param projectId  项目ID
	 * @param iamUserVO
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class,isolation = Isolation.READ_COMMITTED)
	@Override
	public IamUserVO  update(Long projectId,IamUserVO iamUserVO){
		//最好使用自定义修改语句，修改条件包含项目ID
		IamUserDTO dataDTO =ConvertHelper.convert(iamUserVO,IamUserDTO.class);
		iamUserMapper.updateById(ConvertHelper.convert(dataDTO,IamUser.class));
			return queryOne(projectId ,iamUserVO.getId());
	}
	/**
	 *
	 * 查询单个详情
	 * @param projectId  项目ID
	 * @param id
	 * @return
	 */
	@Override
	public IamUserVO queryOne(Long projectId ,Long id){
		//查询的数据 如果包含项目ID，校验项目ID是否一致，不一致抛异常
		IamUser data = iamUserMapper.selectById(id);
		if(Objects.isNull(data)){
			throw new IamAppCommException("common.data.null.error");
		}
		IamUserDTO dataDTO = ConvertHelper.convert(data,IamUserDTO.class);
		return ConvertHelper.convert(dataDTO, IamUserVO.class);
	}
	/**
	 * 分页查询
	 * @param iamUserVO
	 * @param projectId  项目ID
	 * @param page  分页信息
	 * @return
	 */
	@Override
	public IPage<IamUserVO> queryPage(IamUserVO iamUserVO, Long projectId,Page page) {

		IamUserDTO iamUserDTO =ConvertHelper.convert(iamUserVO,IamUserDTO.class);


		//查询
		IPage<IamUser> pageResult = iamUserMapper.page(page , iamUserDTO);
		IPage<IamUserVO> result = new Page<>();
		if (Objects.isNull(pageResult) || pageResult.getTotal() == 0) {
			return result;
		}

		result.setSize(pageResult.getSize());
		result.setTotal(pageResult.getTotal());
		List<IamUserDTO> recordsDTO = ConvertHelper.convertList(pageResult.getRecords(),IamUserDTO.class);
		List<IamUserVO> recordsVO  = ConvertHelper.convertList(recordsDTO,IamUserVO.class);
		result.setRecords(recordsVO);
		return result;
	}



}

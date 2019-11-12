package com.crc.crcloud.steam.iam.service.impl;



import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crc.crcloud.steam.iam.common.exception.IamAppCommException;
import com.crc.crcloud.steam.iam.dao.IamProjectMapper;
import com.crc.crcloud.steam.iam.entity.IamProject;
import com.crc.crcloud.steam.iam.model.dto.IamProjectDTO;
import com.crc.crcloud.steam.iam.model.vo.IamProjectVO;
import com.crc.crcloud.steam.iam.service.IamProjectService;
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
public class IamProjectServiceImpl implements IamProjectService {

	@Autowired
	private IamProjectMapper iamProjectMapper;

	/**
	 * 新增
	 * @param projectId  项目ID
	 * @param iamProjectVO
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class,isolation = Isolation.READ_COMMITTED)
	@Override
	public IamProjectVO insert(Long projectId,IamProjectVO iamProjectVO){
		IamProjectDTO iamProjectDTO = ConvertHelper.convert(iamProjectVO,IamProjectDTO.class);
		IamProject iamProject = ConvertHelper.convert(iamProjectDTO,IamProject.class);
		iamProjectMapper.insert(iamProject);
		IamProjectDTO insertDTO = ConvertHelper.convert(iamProject,IamProjectDTO.class);
		return ConvertHelper.convert(insertDTO,IamProjectVO.class);
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
		iamProjectMapper.deleteById(id);
	}

	/**
	 * 更新
	 * @param projectId  项目ID
	 * @param iamProjectVO
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class,isolation = Isolation.READ_COMMITTED)
	@Override
	public IamProjectVO  update(Long projectId,IamProjectVO iamProjectVO){
		//最好使用自定义修改语句，修改条件包含项目ID
		IamProjectDTO dataDTO =ConvertHelper.convert(iamProjectVO,IamProjectDTO.class);
		iamProjectMapper.updateById(ConvertHelper.convert(dataDTO,IamProject.class));
			return queryOne(projectId ,iamProjectVO.getId());
	}
	/**
	 *
	 * 查询单个详情
	 * @param projectId  项目ID
	 * @param id
	 * @return
	 */
	@Override
	public IamProjectVO queryOne(Long projectId ,Long id){
		//查询的数据 如果包含项目ID，校验项目ID是否一致，不一致抛异常
		IamProject data = iamProjectMapper.selectById(id);
		if(Objects.isNull(data)){
			throw new IamAppCommException("common.data.null.error");
		}
		IamProjectDTO dataDTO = ConvertHelper.convert(data,IamProjectDTO.class);
		return ConvertHelper.convert(dataDTO, IamProjectVO.class);
	}
	/**
	 * 分页查询
	 * @param iamProjectVO
	 * @param projectId  项目ID
	 * @param page  分页信息
	 * @return
	 */
	@Override
	public IPage<IamProjectVO> queryPage(IamProjectVO iamProjectVO, Long projectId,Page page) {

		IamProjectDTO iamProjectDTO =ConvertHelper.convert(iamProjectVO,IamProjectDTO.class);


		//查询
		IPage<IamProject> pageResult = iamProjectMapper.page(page , iamProjectDTO);
		IPage<IamProjectVO> result = new Page<>();
		if (Objects.isNull(pageResult) || pageResult.getTotal() == 0) {
			return result;
		}

		result.setSize(pageResult.getSize());
		result.setTotal(pageResult.getTotal());
		List<IamProjectDTO> recordsDTO = ConvertHelper.convertList(pageResult.getRecords(),IamProjectDTO.class);
		List<IamProjectVO> recordsVO  = ConvertHelper.convertList(recordsDTO,IamProjectVO.class);
		result.setRecords(recordsVO);
		return result;
	}



}

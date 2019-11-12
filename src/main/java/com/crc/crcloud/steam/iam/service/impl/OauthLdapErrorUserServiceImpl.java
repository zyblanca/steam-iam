package com.crc.crcloud.steam.iam.service.impl;



import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crc.crcloud.steam.iam.common.exception.IamAppCommException;
import com.crc.crcloud.steam.iam.dao.OauthLdapErrorUserMapper;
import com.crc.crcloud.steam.iam.entity.OauthLdapErrorUser;
import com.crc.crcloud.steam.iam.model.dto.OauthLdapErrorUserDTO;
import com.crc.crcloud.steam.iam.model.vo.OauthLdapErrorUserVO;
import com.crc.crcloud.steam.iam.service.OauthLdapErrorUserService;
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
public class OauthLdapErrorUserServiceImpl implements OauthLdapErrorUserService {

	@Autowired
	private OauthLdapErrorUserMapper oauthLdapErrorUserMapper;

	/**
	 * 新增
	 * @param projectId  项目ID
	 * @param oauthLdapErrorUserVO
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class,isolation = Isolation.READ_COMMITTED)
	@Override
	public OauthLdapErrorUserVO insert(Long projectId,OauthLdapErrorUserVO oauthLdapErrorUserVO){
		OauthLdapErrorUserDTO oauthLdapErrorUserDTO = ConvertHelper.convert(oauthLdapErrorUserVO,OauthLdapErrorUserDTO.class);
		OauthLdapErrorUser oauthLdapErrorUser = ConvertHelper.convert(oauthLdapErrorUserDTO,OauthLdapErrorUser.class);
		oauthLdapErrorUserMapper.insert(oauthLdapErrorUser);
		OauthLdapErrorUserDTO insertDTO = ConvertHelper.convert(oauthLdapErrorUser,OauthLdapErrorUserDTO.class);
		return ConvertHelper.convert(insertDTO,OauthLdapErrorUserVO.class);
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
		oauthLdapErrorUserMapper.deleteById(id);
	}

	/**
	 * 更新
	 * @param projectId  项目ID
	 * @param oauthLdapErrorUserVO
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class,isolation = Isolation.READ_COMMITTED)
	@Override
	public OauthLdapErrorUserVO  update(Long projectId,OauthLdapErrorUserVO oauthLdapErrorUserVO){
		//最好使用自定义修改语句，修改条件包含项目ID
		OauthLdapErrorUserDTO dataDTO =ConvertHelper.convert(oauthLdapErrorUserVO,OauthLdapErrorUserDTO.class);
		oauthLdapErrorUserMapper.updateById(ConvertHelper.convert(dataDTO,OauthLdapErrorUser.class));
			return queryOne(projectId ,oauthLdapErrorUserVO.getId());
	}
	/**
	 *
	 * 查询单个详情
	 * @param projectId  项目ID
	 * @param id
	 * @return
	 */
	@Override
	public OauthLdapErrorUserVO queryOne(Long projectId ,Long id){
		//查询的数据 如果包含项目ID，校验项目ID是否一致，不一致抛异常
		OauthLdapErrorUser data = oauthLdapErrorUserMapper.selectById(id);
		if(Objects.isNull(data)){
			throw new IamAppCommException("common.data.null.error");
		}
		OauthLdapErrorUserDTO dataDTO = ConvertHelper.convert(data,OauthLdapErrorUserDTO.class);
		return ConvertHelper.convert(dataDTO, OauthLdapErrorUserVO.class);
	}
	/**
	 * 分页查询
	 * @param oauthLdapErrorUserVO
	 * @param projectId  项目ID
	 * @param page  分页信息
	 * @return
	 */
	@Override
	public IPage<OauthLdapErrorUserVO> queryPage(OauthLdapErrorUserVO oauthLdapErrorUserVO, Long projectId,Page page) {

		OauthLdapErrorUserDTO oauthLdapErrorUserDTO =ConvertHelper.convert(oauthLdapErrorUserVO,OauthLdapErrorUserDTO.class);


		//查询
		IPage<OauthLdapErrorUser> pageResult = oauthLdapErrorUserMapper.page(page , oauthLdapErrorUserDTO);
		IPage<OauthLdapErrorUserVO> result = new Page<>();
		if (Objects.isNull(pageResult) || pageResult.getTotal() == 0) {
			return result;
		}

		result.setSize(pageResult.getSize());
		result.setTotal(pageResult.getTotal());
		List<OauthLdapErrorUserDTO> recordsDTO = ConvertHelper.convertList(pageResult.getRecords(),OauthLdapErrorUserDTO.class);
		List<OauthLdapErrorUserVO> recordsVO  = ConvertHelper.convertList(recordsDTO,OauthLdapErrorUserVO.class);
		result.setRecords(recordsVO);
		return result;
	}



}

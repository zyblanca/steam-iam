package com.crc.crcloud.steam.iam.service.impl;



import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crc.crcloud.steam.iam.common.exception.IamAppCommException;
import com.crc.crcloud.steam.iam.dao.OauthLdapMapper;
import com.crc.crcloud.steam.iam.entity.OauthLdap;
import com.crc.crcloud.steam.iam.model.dto.OauthLdapDTO;
import com.crc.crcloud.steam.iam.model.vo.OauthLdapVO;
import com.crc.crcloud.steam.iam.service.OauthLdapService;
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
public class OauthLdapServiceImpl implements OauthLdapService {

	@Autowired
	private OauthLdapMapper oauthLdapMapper;

	/**
	 * 新增
	 * @param projectId  项目ID
	 * @param oauthLdapVO
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class,isolation = Isolation.READ_COMMITTED)
	@Override
	public OauthLdapVO insert(Long projectId,OauthLdapVO oauthLdapVO){
		OauthLdapDTO oauthLdapDTO = ConvertHelper.convert(oauthLdapVO,OauthLdapDTO.class);
		OauthLdap oauthLdap = ConvertHelper.convert(oauthLdapDTO,OauthLdap.class);
		oauthLdapMapper.insert(oauthLdap);
		OauthLdapDTO insertDTO = ConvertHelper.convert(oauthLdap,OauthLdapDTO.class);
		return ConvertHelper.convert(insertDTO,OauthLdapVO.class);
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
		oauthLdapMapper.deleteById(id);
	}

	/**
	 * 更新
	 * @param projectId  项目ID
	 * @param oauthLdapVO
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class,isolation = Isolation.READ_COMMITTED)
	@Override
	public OauthLdapVO  update(Long projectId,OauthLdapVO oauthLdapVO){
		//最好使用自定义修改语句，修改条件包含项目ID
		OauthLdapDTO dataDTO =ConvertHelper.convert(oauthLdapVO,OauthLdapDTO.class);
		oauthLdapMapper.updateById(ConvertHelper.convert(dataDTO,OauthLdap.class));
			return queryOne(projectId ,oauthLdapVO.getId());
	}
	/**
	 *
	 * 查询单个详情
	 * @param projectId  项目ID
	 * @param id
	 * @return
	 */
	@Override
	public OauthLdapVO queryOne(Long projectId ,Long id){
		//查询的数据 如果包含项目ID，校验项目ID是否一致，不一致抛异常
		OauthLdap data = oauthLdapMapper.selectById(id);
		if(Objects.isNull(data)){
			throw new IamAppCommException("common.data.null.error");
		}
		OauthLdapDTO dataDTO = ConvertHelper.convert(data,OauthLdapDTO.class);
		return ConvertHelper.convert(dataDTO, OauthLdapVO.class);
	}
	/**
	 * 分页查询
	 * @param oauthLdapVO
	 * @param projectId  项目ID
	 * @param page  分页信息
	 * @return
	 */
	@Override
	public IPage<OauthLdapVO> queryPage(OauthLdapVO oauthLdapVO, Long projectId,Page page) {

		OauthLdapDTO oauthLdapDTO =ConvertHelper.convert(oauthLdapVO,OauthLdapDTO.class);


		//查询
		IPage<OauthLdap> pageResult = oauthLdapMapper.page(page , oauthLdapDTO);
		IPage<OauthLdapVO> result = new Page<>();
		if (Objects.isNull(pageResult) || pageResult.getTotal() == 0) {
			return result;
		}

		result.setSize(pageResult.getSize());
		result.setTotal(pageResult.getTotal());
		List<OauthLdapDTO> recordsDTO = ConvertHelper.convertList(pageResult.getRecords(),OauthLdapDTO.class);
		List<OauthLdapVO> recordsVO  = ConvertHelper.convertList(recordsDTO,OauthLdapVO.class);
		result.setRecords(recordsVO);
		return result;
	}



}

package com.crc.crcloud.steam.iam.service.impl;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crc.crcloud.steam.iam.common.exception.IamAppCommException;
import com.crc.crcloud.steam.iam.dao.OauthLdapHistoryMapper;
import com.crc.crcloud.steam.iam.entity.OauthLdapHistory;
import com.crc.crcloud.steam.iam.model.dto.OauthLdapHistoryDTO;
import com.crc.crcloud.steam.iam.model.vo.OauthLdapHistoryVO;
import com.crc.crcloud.steam.iam.service.OauthLdapHistoryService;
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
public class OauthLdapHistoryServiceImpl implements OauthLdapHistoryService {

    @Autowired
    private OauthLdapHistoryMapper oauthLdapHistoryMapper;

    /**
     * 新增
     *
     * @param projectId          项目ID
     * @param oauthLdapHistoryVO
     * @return
     */
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    @Override
    public OauthLdapHistoryVO insert(Long projectId, OauthLdapHistoryVO oauthLdapHistoryVO) {
        OauthLdapHistoryDTO oauthLdapHistoryDTO = ConvertHelper.convert(oauthLdapHistoryVO, OauthLdapHistoryDTO.class);
        OauthLdapHistory oauthLdapHistory = ConvertHelper.convert(oauthLdapHistoryDTO, OauthLdapHistory.class);
        oauthLdapHistoryMapper.insert(oauthLdapHistory);
        OauthLdapHistoryDTO insertDTO = ConvertHelper.convert(oauthLdapHistory, OauthLdapHistoryDTO.class);
        return ConvertHelper.convert(insertDTO, OauthLdapHistoryVO.class);
    }

    /**
     * 删除
     *
     * @param projectId 项目ID
     * @param id
     */
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    @Override
    public void delete(Long projectId, Long id) {
        //如果表中含有projectId，请先查询数据，判断projectId是否一致 不一致抛异常，一致则进行删除
        oauthLdapHistoryMapper.deleteById(id);
    }

    /**
     * 更新
     *
     * @param projectId          项目ID
     * @param oauthLdapHistoryVO
     * @return
     */
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    @Override
    public OauthLdapHistoryVO update(Long projectId, OauthLdapHistoryVO oauthLdapHistoryVO) {
        //最好使用自定义修改语句，修改条件包含项目ID
        OauthLdapHistoryDTO dataDTO = ConvertHelper.convert(oauthLdapHistoryVO, OauthLdapHistoryDTO.class);
        oauthLdapHistoryMapper.updateById(ConvertHelper.convert(dataDTO, OauthLdapHistory.class));
        return queryOne(projectId, oauthLdapHistoryVO.getId());
    }

    /**
     * 查询单个详情
     *
     * @param organizationId 项目ID
     * @param id
     * @return
     */
    @Override
    public OauthLdapHistoryVO queryOne(Long organizationId, Long id) {
        //查询的数据 如果包含项目ID，校验项目ID是否一致，不一致抛异常
        OauthLdapHistory data = oauthLdapHistoryMapper.selectById(id);
        if (Objects.isNull(data)) {
            throw new IamAppCommException("common.data.null.error");
        }
        OauthLdapHistoryDTO dataDTO = ConvertHelper.convert(data, OauthLdapHistoryDTO.class);
        return ConvertHelper.convert(dataDTO, OauthLdapHistoryVO.class);
    }

    /**
     * 分页查询
     *
     * @param oauthLdapHistoryVO
     * @param projectId          项目ID
     * @param page               分页信息
     * @return
     */
    @Override
    public IPage<OauthLdapHistoryVO> queryPage(OauthLdapHistoryVO oauthLdapHistoryVO, Long projectId, Page page) {

        OauthLdapHistoryDTO oauthLdapHistoryDTO = ConvertHelper.convert(oauthLdapHistoryVO, OauthLdapHistoryDTO.class);


        //查询
        IPage<OauthLdapHistory> pageResult = oauthLdapHistoryMapper.page(page, oauthLdapHistoryDTO);
        IPage<OauthLdapHistoryVO> result = new Page<>();
        if (Objects.isNull(pageResult) || pageResult.getTotal() == 0) {
            return result;
        }

        result.setSize(pageResult.getSize());
        result.setTotal(pageResult.getTotal());
        List<OauthLdapHistoryDTO> recordsDTO = ConvertHelper.convertList(pageResult.getRecords(), OauthLdapHistoryDTO.class);
        List<OauthLdapHistoryVO> recordsVO = ConvertHelper.convertList(recordsDTO, OauthLdapHistoryVO.class);
        result.setRecords(recordsVO);
        return result;
    }


}

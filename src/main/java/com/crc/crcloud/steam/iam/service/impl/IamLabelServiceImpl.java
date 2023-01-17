package com.crc.crcloud.steam.iam.service.impl;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crc.crcloud.steam.iam.common.exception.IamAppCommException;
import com.crc.crcloud.steam.iam.dao.IamLabelMapper;
import com.crc.crcloud.steam.iam.entity.IamLabel;
import com.crc.crcloud.steam.iam.model.dto.IamLabelDTO;
import com.crc.crcloud.steam.iam.model.vo.IamLabelVO;
import com.crc.crcloud.steam.iam.service.IamLabelService;
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
public class IamLabelServiceImpl implements IamLabelService {

    @Autowired
    private IamLabelMapper iamLabelMapper;

    /**
     * 新增
     *
     * @param projectId  项目ID
     * @param iamLabelVO
     * @return
     */
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    @Override
    public IamLabelVO insert(Long projectId, IamLabelVO iamLabelVO) {
        IamLabelDTO iamLabelDTO = ConvertHelper.convert(iamLabelVO, IamLabelDTO.class);
        IamLabel iamLabel = ConvertHelper.convert(iamLabelDTO, IamLabel.class);
        iamLabelMapper.insert(iamLabel);
        IamLabelDTO insertDTO = ConvertHelper.convert(iamLabel, IamLabelDTO.class);
        return ConvertHelper.convert(insertDTO, IamLabelVO.class);
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
        iamLabelMapper.deleteById(id);
    }

    /**
     * 更新
     *
     * @param projectId  项目ID
     * @param iamLabelVO
     * @return
     */
    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
    @Override
    public IamLabelVO update(Long projectId, IamLabelVO iamLabelVO) {
        //最好使用自定义修改语句，修改条件包含项目ID
        IamLabelDTO dataDTO = ConvertHelper.convert(iamLabelVO, IamLabelDTO.class);
        iamLabelMapper.updateById(ConvertHelper.convert(dataDTO, IamLabel.class));
        return queryOne(projectId, iamLabelVO.getId());
    }

    /**
     * 查询单个详情
     *
     * @param projectId 项目ID
     * @param id
     * @return
     */
    @Override
    public IamLabelVO queryOne(Long projectId, Long id) {
        //查询的数据 如果包含项目ID，校验项目ID是否一致，不一致抛异常
        IamLabel data = iamLabelMapper.selectById(id);
        if (Objects.isNull(data)) {
            throw new IamAppCommException("common.data.null.error");
        }
        IamLabelDTO dataDTO = ConvertHelper.convert(data, IamLabelDTO.class);
        return ConvertHelper.convert(dataDTO, IamLabelVO.class);
    }

    /**
     * 分页查询
     *
     * @param iamLabelVO
     * @param projectId  项目ID
     * @param page       分页信息
     * @return
     */
    @Override
    public IPage<IamLabelVO> queryPage(IamLabelVO iamLabelVO, Long projectId, Page page) {

        IamLabelDTO iamLabelDTO = ConvertHelper.convert(iamLabelVO, IamLabelDTO.class);


        //查询
        IPage<IamLabel> pageResult = iamLabelMapper.page(page, iamLabelDTO);
        IPage<IamLabelVO> result = new Page<>();
        if (Objects.isNull(pageResult) || pageResult.getTotal() == 0) {
            return result;
        }

        result.setSize(pageResult.getSize());
        result.setTotal(pageResult.getTotal());
        List<IamLabelDTO> recordsDTO = ConvertHelper.convertList(pageResult.getRecords(), IamLabelDTO.class);
        List<IamLabelVO> recordsVO = ConvertHelper.convertList(recordsDTO, IamLabelVO.class);
        result.setRecords(recordsVO);
        return result;
    }


}

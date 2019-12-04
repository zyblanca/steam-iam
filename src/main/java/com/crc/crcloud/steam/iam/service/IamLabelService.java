package com.crc.crcloud.steam.iam.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crc.crcloud.steam.iam .model.vo.IamLabelVO;



/**
 * @Author
 * @Description 
 * @Date 2019-12-03
 */
public interface IamLabelService {


    /**
     * 新增
     * @param projectId  项目ID
     * @param iamLabel
     * @return
     */
    IamLabelVO insert(Long projectId, IamLabelVO iamLabel);

    /**
    * 删除
    * @param projectId  项目ID
    * @param id
    */
    void delete(Long projectId, Long id);

    /**
    * 更新
    * @param projectId  项目ID
    * @param iamLabel
    * @return
    */
    IamLabelVO  update(Long projectId, IamLabelVO iamLabel);

    /**
     *
     * 查询单个详情
     * @param projectId  项目ID
     * @param id
     * @return
     */
    IamLabelVO queryOne(Long projectId, Long id);

    /**
     * 分页查询
     * @param iamLabel
     * @param projectId  项目ID
     * @param page  分页信息
     * @return
     */
    IPage<IamLabelVO> queryPage(IamLabelVO iamLabel, Long projectId, Page page);
}

package com.crc.crcloud.steam.iam.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crc.crcloud.steam.iam.model.vo.IamProjectVO;


/**
 * @Author
 * @Description
 * @Date 2019-11-12
 */
public interface IamProjectService {


    /**
     * 新增
     *
     * @param projectId  项目ID
     * @param iamProject
     * @return
     */
    IamProjectVO insert(Long projectId, IamProjectVO iamProject);

    /**
     * 删除
     *
     * @param projectId 项目ID
     * @param id
     */
    void delete(Long projectId, Long id);

    /**
     * 更新
     *
     * @param projectId  项目ID
     * @param iamProject
     * @return
     */
    IamProjectVO update(Long projectId, IamProjectVO iamProject);

    /**
     * 查询单个详情
     *
     * @param projectId 项目ID
     * @param id
     * @return
     */
    IamProjectVO queryOne(Long projectId, Long id);

    /**
     * 分页查询
     *
     * @param iamProject
     * @param projectId  项目ID
     * @param page       分页信息
     * @return
     */
    IPage<IamProjectVO> queryPage(IamProjectVO iamProject, Long projectId, Page page);
}

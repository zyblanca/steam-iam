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
     * 新增项目
     *
     * @param organizationId  组织id
     * @param iamProject 项目信息
     * @return
     */
    IamProjectVO insert(Long organizationId, IamProjectVO iamProject);

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
     * @param iamProject
     * @return
     */
    IamProjectVO update(IamProjectVO iamProject);

    /**
     * 查询单个详情
     *
     * @param projectId 项目ID
     * @return
     */
    IamProjectVO queryOne(Long projectId);

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

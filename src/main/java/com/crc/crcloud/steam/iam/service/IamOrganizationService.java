package com.crc.crcloud.steam.iam.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crc.crcloud.steam.iam.model.vo.IamOrganizationVO;


/**
 * @Author
 * @Description
 * @Date 2019-11-12
 */
public interface IamOrganizationService {


    /**
     * 新增
     *
     * @param projectId       项目ID
     * @param iamOrganization
     * @return
     */
    IamOrganizationVO insert(Long projectId, IamOrganizationVO iamOrganization);

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
     * @param projectId       项目ID
     * @param iamOrganization
     * @return
     */
    IamOrganizationVO update(Long projectId, IamOrganizationVO iamOrganization);

    /**
     * 查询单个详情
     *
     * @param projectId 项目ID
     * @param id
     * @return
     */
    IamOrganizationVO queryOne(Long projectId, Long id);

    /**
     * 分页查询
     *
     * @param iamOrganization
     * @param projectId       项目ID
     * @param page            分页信息
     * @return
     */
    IPage<IamOrganizationVO> queryPage(IamOrganizationVO iamOrganization, Long projectId, Page page);
}

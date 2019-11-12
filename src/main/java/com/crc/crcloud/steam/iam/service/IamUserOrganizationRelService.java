package com.crc.crcloud.steam.iam.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crc.crcloud.steam.iam .model.vo.IamUserOrganizationRelVO;



/**
 * @Author
 * @Description 
 * @Date 2019-11-12
 */
public interface IamUserOrganizationRelService {


    /**
     * 新增
     * @param projectId  项目ID
     * @param iamUserOrganizationRel
     * @return
     */
    IamUserOrganizationRelVO insert(Long projectId, IamUserOrganizationRelVO iamUserOrganizationRel);

    /**
    * 删除
    * @param projectId  项目ID
    * @param id
    */
    void delete(Long projectId, Long id);

    /**
    * 更新
    * @param projectId  项目ID
    * @param iamUserOrganizationRel
    * @return
    */
    IamUserOrganizationRelVO  update(Long projectId, IamUserOrganizationRelVO iamUserOrganizationRel);

    /**
     *
     * 查询单个详情
     * @param projectId  项目ID
     * @param id
     * @return
     */
    IamUserOrganizationRelVO queryOne(Long projectId, Long id);

    /**
     * 分页查询
     * @param iamUserOrganizationRel
     * @param projectId  项目ID
     * @param page  分页信息
     * @return
     */
    IPage<IamUserOrganizationRelVO> queryPage(IamUserOrganizationRelVO iamUserOrganizationRel, Long projectId, Page page);
}

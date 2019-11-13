package com.crc.crcloud.steam.iam.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crc.crcloud.steam.iam.model.vo.IamRoleVO;



/**
 * @Author
 * @Description
 * @Date 2019-11-12
 */
public interface IamRoleService {


    /**
     * 新增
     * @param projectId  项目ID
     * @param iamRole
     * @return
     */
    IamRoleVO insert(Long projectId, IamRoleVO iamRole);

    /**
    * 删除
    * @param projectId  项目ID
    * @param id
    */
    void delete(Long projectId, Long id);

    /**
    * 更新
    * @param projectId  项目ID
    * @param iamRole
    * @return
    */
    IamRoleVO  update(Long projectId, IamRoleVO iamRole);

    /**
     *
     * 查询单个详情
     * @param projectId  项目ID
     * @param id
     * @return
     */
    IamRoleVO queryOne(Long projectId, Long id);

    /**
     * 分页查询
     * @param iamRole
     * @param projectId  项目ID
     * @param page  分页信息
     * @return
     */
    IPage<IamRoleVO> queryPage(IamRoleVO iamRole, Long projectId, Page page);
}

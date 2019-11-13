package com.crc.crcloud.steam.iam.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crc.crcloud.steam.iam.model.vo.IamMemberRoleVO;



/**
 * @Author
 * @Description
 * @Date 2019-11-12
 */
public interface IamMemberRoleService {


    /**
     * 新增
     * @param projectId  项目ID
     * @param iamMemberRole
     * @return
     */
    IamMemberRoleVO insert(Long projectId, IamMemberRoleVO iamMemberRole);

    /**
    * 删除
    * @param projectId  项目ID
    * @param id
    */
    void delete(Long projectId, Long id);

    /**
    * 更新
    * @param projectId  项目ID
    * @param iamMemberRole
    * @return
    */
    IamMemberRoleVO  update(Long projectId, IamMemberRoleVO iamMemberRole);

    /**
     *
     * 查询单个详情
     * @param projectId  项目ID
     * @param id
     * @return
     */
    IamMemberRoleVO queryOne(Long projectId, Long id);

    /**
     * 分页查询
     * @param iamMemberRole
     * @param projectId  项目ID
     * @param page  分页信息
     * @return
     */
    IPage<IamMemberRoleVO> queryPage(IamMemberRoleVO iamMemberRole, Long projectId, Page page);
}

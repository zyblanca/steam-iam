package com.crc.crcloud.steam.iam.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crc.crcloud.steam.iam .model.vo.IamUserVO;



/**
 * @Author
 * @Description 
 * @Date 2019-11-12
 */
public interface IamUserService {


    /**
     * 新增
     * @param projectId  项目ID
     * @param iamUser
     * @return
     */
    IamUserVO insert(Long projectId, IamUserVO iamUser);

    /**
    * 删除
    * @param projectId  项目ID
    * @param id
    */
    void delete(Long projectId, Long id);

    /**
    * 更新
    * @param projectId  项目ID
    * @param iamUser
    * @return
    */
    IamUserVO  update(Long projectId, IamUserVO iamUser);

    /**
     *
     * 查询单个详情
     * @param projectId  项目ID
     * @param id
     * @return
     */
    IamUserVO queryOne(Long projectId, Long id);

    /**
     * 分页查询
     * @param iamUser
     * @param projectId  项目ID
     * @param page  分页信息
     * @return
     */
    IPage<IamUserVO> queryPage(IamUserVO iamUser, Long projectId, Page page);
}

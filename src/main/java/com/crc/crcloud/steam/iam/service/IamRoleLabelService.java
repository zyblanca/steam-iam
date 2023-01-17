package com.crc.crcloud.steam.iam.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crc.crcloud.steam.iam.model.vo.IamRoleLabelVO;


/**
 * @Author
 * @Description
 * @Date 2019-12-03
 */
public interface IamRoleLabelService {


    /**
     * 新增
     *
     * @param projectId    项目ID
     * @param iamRoleLabel
     * @return
     */
    IamRoleLabelVO insert(Long projectId, IamRoleLabelVO iamRoleLabel);

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
     * @param projectId    项目ID
     * @param iamRoleLabel
     * @return
     */
    IamRoleLabelVO update(Long projectId, IamRoleLabelVO iamRoleLabel);

    /**
     * 查询单个详情
     *
     * @param projectId 项目ID
     * @param id
     * @return
     */
    IamRoleLabelVO queryOne(Long projectId, Long id);

    /**
     * 分页查询
     *
     * @param iamRoleLabel
     * @param projectId    项目ID
     * @param page         分页信息
     * @return
     */
    IPage<IamRoleLabelVO> queryPage(IamRoleLabelVO iamRoleLabel, Long projectId, Page page);
}

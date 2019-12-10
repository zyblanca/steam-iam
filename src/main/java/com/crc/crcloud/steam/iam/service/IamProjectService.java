package com.crc.crcloud.steam.iam.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crc.crcloud.steam.iam.common.utils.PageUtil;
import com.crc.crcloud.steam.iam.entity.IamProject;
import com.crc.crcloud.steam.iam.model.dto.IamProjectDTO;
import com.crc.crcloud.steam.iam.model.vo.IamProjectVO;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;


/**
 * @Author
 * @Description
 * @Date 2019-11-12
 */
public interface IamProjectService {


    /**
     * 新增项目
     *
     * @param organizationId 组织id
     * @param iamProject     项目信息
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

    /**
     * 获取项目
     *
     * @param ids 项目编号
     * @return 项目集合
     */
    @NotNull
    List<IamProjectDTO> getByIds(@Nullable Set<Long> ids);

    /**
     * 分页查询项目信息
     *
     * @param project
     * @param pageUtil
     * @return
     */
    IPage<IamProjectVO> pagingQuery(IamProjectVO project, PageUtil pageUtil);

    /**
     * 单纯查询项目信息，不存在额外信息
     * 不是详情数据
     *
     * @param id
     * @return
     */
    IamProjectVO queryProjectById(Long id);

    IPage<IamProjectVO> queryAllProject(PageUtil pageUtil, IamProjectVO iamProjectVO);

    List<IamProjectVO> queryByCategory(String category);

    void check(IamProjectVO projectVO);
}

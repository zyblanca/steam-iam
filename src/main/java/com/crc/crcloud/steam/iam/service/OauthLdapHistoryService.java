package com.crc.crcloud.steam.iam.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crc.crcloud.steam.iam.model.vo.OauthLdapHistoryVO;


/**
 * @Author
 * @Description
 * @Date 2019-11-12
 */
public interface OauthLdapHistoryService {


    /**
     * 新增
     *
     * @param projectId        项目ID
     * @param oauthLdapHistory
     * @return
     */
    OauthLdapHistoryVO insert(Long projectId, OauthLdapHistoryVO oauthLdapHistory);

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
     * @param projectId        项目ID
     * @param oauthLdapHistory
     * @return
     */
    OauthLdapHistoryVO update(Long projectId, OauthLdapHistoryVO oauthLdapHistory);

    /**
     * 查询单个详情
     *
     * @param projectId 项目ID
     * @param id
     * @return
     */
    OauthLdapHistoryVO queryOne(Long projectId, Long id);

    /**
     * 分页查询
     *
     * @param oauthLdapHistory
     * @param projectId        项目ID
     * @param page             分页信息
     * @return
     */
    IPage<OauthLdapHistoryVO> queryPage(OauthLdapHistoryVO oauthLdapHistory, Long projectId, Page page);
}

package com.crc.crcloud.steam.iam.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crc.crcloud.steam.iam.model.vo.OauthLdapErrorUserVO;


/**
 * @Author
 * @Description
 * @Date 2019-11-12
 */
public interface OauthLdapErrorUserService {


    /**
     * 新增
     *
     * @param projectId          项目ID
     * @param oauthLdapErrorUser
     * @return
     */
    OauthLdapErrorUserVO insert(Long projectId, OauthLdapErrorUserVO oauthLdapErrorUser);

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
     * @param projectId          项目ID
     * @param oauthLdapErrorUser
     * @return
     */
    OauthLdapErrorUserVO update(Long projectId, OauthLdapErrorUserVO oauthLdapErrorUser);

    /**
     * 查询单个详情
     *
     * @param projectId 项目ID
     * @param id
     * @return
     */
    OauthLdapErrorUserVO queryOne(Long projectId, Long id);

    /**
     * 分页查询
     *
     * @param oauthLdapErrorUser
     * @param projectId          项目ID
     * @param page               分页信息
     * @return
     */
    IPage<OauthLdapErrorUserVO> queryPage(OauthLdapErrorUserVO oauthLdapErrorUser, Long projectId, Page page);
}

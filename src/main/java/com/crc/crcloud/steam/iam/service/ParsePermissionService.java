package com.crc.crcloud.steam.iam.service;

import io.choerodon.eureka.event.EurekaEventPayload;

/**
 * 用作服务权限初始化和角色关系
 * <p>不拥有其他业务逻辑</p>
 *
 * @author LiuYang
 * @date 2019/11/29
 */
public interface ParsePermissionService {
    /**
     * 解析swagger的文档树
     *
     * @param payload 接受的消息
     */
    void parser(EurekaEventPayload payload);
}

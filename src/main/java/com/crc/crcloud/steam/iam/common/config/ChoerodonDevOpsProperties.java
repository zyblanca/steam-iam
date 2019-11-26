package com.crc.crcloud.steam.iam.common.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 猪齿鱼相关的DevOps参数属性
 * @author LiuYang
 * @date 2019/11/26
 */
@Component
@Data
public class ChoerodonDevOpsProperties {
    /**
     * 用来处理是否发送Saga事件
     */
    @SuppressWarnings("SpellCheckingInspection")
    @Value("${choerodon.devops.message:false}")
    private boolean message;


}

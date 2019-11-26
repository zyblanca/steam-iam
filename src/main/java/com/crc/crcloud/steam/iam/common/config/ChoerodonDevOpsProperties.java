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
    public static final String PREFIX = "choerodon.devops";
    public static final String MESSAGE_KEY = "message";
    /**
     * 用来处理是否发送Saga事件
     */
    @Value("${" + PREFIX + "." + MESSAGE_KEY + ":false}")
    private boolean message;


}

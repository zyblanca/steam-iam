package com.crc.crcloud.steam.iam.common.config;

import cn.hutool.core.date.DateUtil;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * 猪齿鱼相关的DevOps参数属性
 *
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
    /**
     * 判定是否为新项目的时间戳
     * <p>填写了就不应该再修改</p>
     */
    @Deprecated
    @NotBlank
    @Getter(AccessLevel.NONE)
    @Value("${" + PREFIX + ".conditionDate}")
    private String conditionDate;

    /**
     * 获取判定时间
     * <p>大于当前时间的为新数据</p>
     * <p>数据包含：组织、项目等</p>
     *
     * @return 时间
     */
    @NotNull
    @Deprecated
    public Date getConditionDate() {
        return DateUtil.parse(conditionDate);
    }
}

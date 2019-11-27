package com.crc.crcloud.steam.iam.model.event;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import com.crc.crcloud.steam.iam.model.dto.IamUserDTO;
import org.springframework.context.ApplicationEvent;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 用户创建事件
 * @author LiuYang
 */
public class IamUserCreateEvent extends ApplicationEvent {

    public IamUserCreateEvent(@NotNull IamUserDTO source) {
        this(CollUtil.newArrayList(source));
    }

    public IamUserCreateEvent(@NotEmpty List<IamUserDTO> source) {
        super(source);
        Assert.notEmpty(source);
    }

    /**
     * @return 获取本次创建用户
     */
    @SuppressWarnings("unchecked")
    @NotEmpty
    @Override
    public List<IamUserDTO> getSource() {
        return (List<IamUserDTO>) super.getSource();
    }
}

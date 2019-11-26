package com.crc.crcloud.steam.iam.model.event;

import com.crc.crcloud.steam.iam.model.dto.IamOrganizationDTO;
import org.springframework.context.ApplicationEvent;

/**
 * 组织切换了状态之后发送事件
 * @author LiuYang
 * @date 2019/11/26
 */
public class IamOrganizationToggleEnableEvent extends ApplicationEvent {
    private Long userId;

    public IamOrganizationToggleEnableEvent(IamOrganizationDTO source, Long userId) {
        super(source);
    }

    @Override
    public IamOrganizationDTO getSource() {
        return (IamOrganizationDTO) super.getSource();
    }

    /**
     * 获取当前启用状态
     * @return true:启用
     */
    public boolean getCurrentEnableStatus() {
        return getSource().getIsEnabled();
    }

    public Long getUserId() {
        return userId;
    }
}

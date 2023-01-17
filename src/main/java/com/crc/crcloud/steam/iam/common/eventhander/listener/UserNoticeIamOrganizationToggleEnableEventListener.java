package com.crc.crcloud.steam.iam.common.eventhander.listener;

import com.crc.crcloud.steam.iam.common.config.ChoerodonDevOpsProperties;
import com.crc.crcloud.steam.iam.model.event.IamOrganizationToggleEnableEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * 禁用启用组织之后用户站内信通知
 *
 * @author LiuYang
 * @date 2019/11/26
 */
@Slf4j
@Component
@ConditionalOnProperty(prefix = ChoerodonDevOpsProperties.PREFIX, value = ChoerodonDevOpsProperties.MESSAGE_KEY, havingValue = "true")
public class UserNoticeIamOrganizationToggleEnableEventListener implements ApplicationListener<IamOrganizationToggleEnableEvent> {
    public UserNoticeIamOrganizationToggleEnableEventListener() {
        log.info("已注册启用禁用组织事件-发送用户通知(站内信)");
    }

    @Override
    public void onApplicationEvent(IamOrganizationToggleEnableEvent event) {

    }
}

package com.crc.crcloud.steam.iam.common.eventhander;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.thread.ThreadUtil;
import com.crc.crcloud.steam.iam.service.ParsePermissionService;
import io.choerodon.eureka.event.AbstractEurekaEventObserver;
import io.choerodon.eureka.event.EurekaEventPayload;
import org.springframework.stereotype.Component;

/**
 * 根据接口解析权限
 *
 * @author superlee
 */
@Component
public class ParsePermissionListener extends AbstractEurekaEventObserver {

    private ParsePermissionService parsePermissionService;

    public ParsePermissionListener(ParsePermissionService parsePermissionService) {
        this.parsePermissionService = parsePermissionService;
    }

    @Override
    public void receiveUpEvent(EurekaEventPayload payload) {
        //延迟10秒，让iam-service先执行，确保后执行，不然有可能会被iam-service删除掉差异化内容
        ThreadUtil.safeSleep(DateUnit.SECOND.getMillis() * 10);
        parsePermissionService.parser(payload);
    }

    @Override
    public void receiveDownEvent(EurekaEventPayload payload) {
        // do nothing
    }
}

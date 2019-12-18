package com.crc.crcloud.steam.iam.api.feign.callback;

import com.crc.crcloud.steam.iam.api.feign.AsgardFeignClient;
import io.choerodon.core.exception.CommonException;
import org.springframework.stereotype.Component;

/**
 * @author dengyouquan
 **/
@Component
public class AsgardFeignClientFallback implements AsgardFeignClient {
    @Override
    public void disableOrg(long orgId) {
        throw new CommonException("error.asgard.quartzTask.disableOrg");
    }

    @Override
    public void disableProj(long projectId) {
        throw new CommonException("error.asgard.quartzTask.disableProject");
    }
}

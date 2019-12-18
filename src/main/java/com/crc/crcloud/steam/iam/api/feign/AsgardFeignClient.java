package com.crc.crcloud.steam.iam.api.feign;

import com.crc.crcloud.steam.iam.api.feign.callback.AsgardFeignClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

/**
 * @author dengyouquan
 **/
@FeignClient(value = "asgard-service",
        fallback = AsgardFeignClientFallback.class)
public interface AsgardFeignClient {
    @PutMapping("/v1/schedules/organizations/{organization_id}/tasks/disable")
    void disableOrg(@PathVariable("organization_id") long orgId);

    @PutMapping("/v1/schedules/projects/{project_id}/tasks/disable")
    void disableProj(@PathVariable("project_id") long projectId);
}

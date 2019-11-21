package com.crc.crcloud.steam.iam.common.config;

import io.choerodon.core.swagger.ChoerodonRouteData;
import io.choerodon.swagger.annotation.ChoerodonExtraData;
import io.choerodon.swagger.swagger.extra.ExtraData;
import io.choerodon.swagger.swagger.extra.ExtraDataManager;


@ChoerodonExtraData
public class CustomExtraDataManager implements ExtraDataManager {
    @Override
    public ExtraData getData() {
        ChoerodonRouteData choerodonRouteData = new ChoerodonRouteData();
        choerodonRouteData.setName("steamiam");
        choerodonRouteData.setPath("/steamiam/**");
        choerodonRouteData.setServiceId("steam-iam");
        extraData.put(ExtraData.ZUUL_ROUTE_DATA, choerodonRouteData);
        return extraData;
    }
}
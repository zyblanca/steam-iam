package com.crc.crcloud.steam.iam.common.utils;

import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;

import javax.annotation.Nullable;
import java.util.Optional;

public final class UserDetail {

    @Nullable
    public static Long getUserId() {
        CustomUserDetails userDetails = DetailsHelper.getUserDetails();

        return Optional.ofNullable(userDetails).map(CustomUserDetails::getUserId).orElse(null);
    }
}

package com.crc.crcloud.steam.iam.common.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.crc.crcloud.steam.iam.common.utils.UserDetail;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 自动填充
 */
@Component
public class TestMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        Long userId = UserDetail.getUserId();

        this.setInsertFieldValByName("createdBy", userId, metaObject);
        this.setInsertFieldValByName("creationDate", new Date(), metaObject);
        this.setUpdateFieldValByName("lastUpdatedBy", userId, metaObject);
        this.setUpdateFieldValByName("lastUpdateDate", new Date(), metaObject);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        Long userId = UserDetail.getUserId();
        this.setUpdateFieldValByName("lastUpdatedBy", userId, metaObject);
        this.setUpdateFieldValByName("lastUpdateDate", new Date(), metaObject);
    }
}

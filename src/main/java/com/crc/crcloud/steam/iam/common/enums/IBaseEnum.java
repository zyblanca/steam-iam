package com.crc.crcloud.steam.iam.common.enums;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.enums.IEnum;

import java.io.Serializable;

/**
 * 枚举规范值
 * @author LiuYang
 * @date 2019/7/18
 */
public interface IBaseEnum<T extends Serializable> extends IEnum<T> {
    /**
     * 说明
     */
    String getDesc();

    /**
     * 是否相等
     * @param value 比较值
     * @return true：相等
     * @see ObjectUtil#equal(java.lang.Object, java.lang.Object)
     */
    default boolean equalsValue(T value) {
        return ObjectUtil.equal(getValue(), value);
    }
}

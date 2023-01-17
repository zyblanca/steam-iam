package com.crc.crcloud.steam.iam.common.utils;

import cn.hutool.cache.Cache;
import cn.hutool.cache.impl.LRUCache;
import cn.hutool.core.bean.BeanDesc;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.core.toolkit.LambdaUtils;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.crc.crcloud.steam.iam.common.exception.IamAppCommException;
import org.apache.ibatis.reflection.property.PropertyNamer;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 实体类工具类
 *
 * @author LiuYang
 */
public class EntityUtil {
    /**
     * 实体类ID缓存
     */
    private final static Cache<Class, String> ID_FIELD_CACHE = new LRUCache<>(200, DateUnit.DAY.getMillis());


    /**
     * 获取bean字段值
     * <code>getSimpleField(AgileIssue:getIssueId);return issue_id</code>
     *
     * @param func 字段的lambda形式
     * @return 字段名下划线表示
     */
    @NotNull
    public static <T> String getSimpleField(@NotNull SFunction<T, ?> func) {
        String property = PropertyNamer.methodToProperty(LambdaUtils.resolve(func).getImplMethodName());
        return StrUtil.toUnderlineCase(property);
    }

    /**
     * 获取bean字段值
     * <code>getSimpleField(AgileIssue:getIssueId);return issueId</code>
     *
     * @param func 字段的lambda形式
     * @return 字段名驼峰表示
     */
    @NotNull
    public static <T> String getSimpleFieldToCamelCase(@NotNull SFunction<T, ?> func) {
        return StrUtil.toCamelCase(getSimpleField(func));
    }
}

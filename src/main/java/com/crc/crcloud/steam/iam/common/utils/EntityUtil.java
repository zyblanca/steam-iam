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
 * @author LiuYang
 */
public class EntityUtil {
    /**
     * 实体类ID缓存
     */
    private final static Cache<Class, String> ID_FIELD_CACHE = new LRUCache<>(200, DateUnit.DAY.getMillis());

    /**
     * 获取字段名 下划线模式
     * <p>将entity类中字段不为空的字段读取并转为下划线</p>
     * @param entity 实体类
     * @return 转下划线字段
     * @throws IamAppCommException 当匹配到多个字段、不存在字段值、不是bean类型
     */
    @NotNull
    public static String getSimpleField(@NotNull Object entity) throws IamAppCommException {
        if (!BeanUtil.isBean(entity.getClass())) {
            throw new IamAppCommException(StrUtil.format("class {} 不是一个Bean对象", entity.getClass().getName()));
        }
        Map<String, Object> beanToMap = BeanUtil.beanToMap(entity, true, true);
        if (beanToMap.isEmpty()) {
            throw new IamAppCommException("不存在单个字段名");
        }
        if (beanToMap.size() > 1) {
            throw new IamAppCommException("匹配到多个字段值");
        }
        return CollUtil.getFirst(beanToMap.keySet());
    }

    /**
     * 获取字段名 下划线模式
     * <p>将entity类中标记{@link TableId}的字段读取出来</p>
     * @param entity 实体类
     * @return 转下划线字段
     * @throws IamAppCommException 当匹配到多个字段、不存在字段值、不是bean类型
     */
    @NotNull
    public static String getIdField(@NotNull Class entity) {
        String fieldName = ID_FIELD_CACHE.get(entity);
        if (Objects.nonNull(fieldName)) {
            return fieldName;
        }
        if (!BeanUtil.isBean(entity)) {
            throw new IamAppCommException(StrUtil.format("class {} 不是一个Bean对象", entity.getClass().getName()));
        }
        BeanDesc beanDesc = BeanUtil.getBeanDesc(entity);
        List<BeanDesc.PropDesc> idFields = beanDesc.getProps().parallelStream()
                .filter(propDesc -> propDesc.getField().isAnnotationPresent(TableId.class))
                .collect(Collectors.toList());
        if (idFields.isEmpty()) {
            throw new IamAppCommException("不存在单个字段名");
        }
        if (idFields.size() > 1) {
            throw new IamAppCommException("匹配到多个字段值");
        }
        fieldName = CollUtil.getFirst(idFields).getFieldName();
        fieldName = StrUtil.toUnderlineCase(fieldName);
        ID_FIELD_CACHE.put(entity, fieldName);
        return fieldName;
    }

    /**
     * 获取bean字段值
     * <code>getSimpleField(AgileIssue:getIssueId);return issue_id</code>
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
     * @param func 字段的lambda形式
     * @return 字段名驼峰表示
     */
    @NotNull
    public static <T> String getSimpleFieldToCamelCase(@NotNull SFunction<T, ?> func) {
        return StrUtil.toCamelCase(getSimpleField(func));
    }
}

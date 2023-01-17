package com.crc.crcloud.steam.iam.common.utils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nullable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 分页信息包装
 * <ol>
 * <li>对排序字段的包装</li>
 * <li>对排序字段的转下划线</li>
 * </ol>
 *
 * @author LiuYang
 * @date 2019/12/26
 */
@SuppressWarnings("WeakerAccess")
@Slf4j
public class PageWrapper<T> implements IPage<T> {
    /**
     * 原生分页对象
     */
    private IPage<T> nativePage;
    /**
     * 排序字段转换
     * key:下划线字段,value:实际处理转换
     */
    private final Map<String, Function<String, String>> fieldConvert = new ConcurrentHashMap<>();
    /**
     * 默认排序字段
     * key: 下划线字段,value:是否倒序,true:倒序（desc）
     */
    private final Map<String, Boolean> defaultOrderBy = Collections.synchronizedMap(new LinkedHashMap<>(3));

    public PageWrapper(@NotNull IPage<T> nativePage) {
        this.nativePage = nativePage;
    }

    public static <T> PageWrapper<T> instance(@NotNull IPage<T> nativePage) {
        return new PageWrapper<>(nativePage);
    }

    @Override
    public String[] descs() {
        return sortFieldConvert(defaultOrderByHandler(true));
    }

    @Override
    public String[] ascs() {
        return sortFieldConvert(defaultOrderByHandler(false));
    }

    /**
     * 处理默认排序
     *
     * @param isDesc 是否倒序
     * @return 添加了默认排序的字段
     */
    private String[] defaultOrderByHandler(boolean isDesc) {
        Function<String, String> convertToUnderlineCase = sort -> alreadyGbkConvert(sort) ? sort : StrUtil.toUnderlineCase(sort);
        List<String> source = CollUtil.newArrayList(clearSorts(isDesc ? nativePage.descs() : nativePage.ascs()));
        source = source.stream().map(convertToUnderlineCase).collect(Collectors.toList());
        List<String> others = CollUtil.newArrayList(clearSorts(isDesc ? nativePage.ascs() : nativePage.descs()));
        others = others.stream().map(convertToUnderlineCase).collect(Collectors.toList());
        List<String> defaultOrders = this.defaultOrderBy.entrySet().stream().filter(entry -> Objects.equals(entry.getValue(), isDesc))
                .map(Map.Entry::getKey)
                .map(convertToUnderlineCase)
                .collect(Collectors.toList());
        for (String defaultOrder : defaultOrders) {
            if (!source.contains(defaultOrder) && !others.contains(defaultOrder)) {
                source.add(defaultOrder);
            }
        }
        return source.toArray(new String[0]);
    }

    /**
     * 对排序字段进行转换
     *
     * @param sorts 原始排序值
     * @return 转换后的值并转换为下划线
     */
    private String[] sortFieldConvert(@Nullable String[] sorts) {
        @NotNull LinkedHashSet<String> clearSorts = clearSorts(sorts);
        List<String> convertValues = new ArrayList<>(clearSorts.size());
        for (String sort : clearSorts) {
            String convertValue = alreadyGbkConvert(sort) ? sort : StrUtil.toUnderlineCase(sort);
            Function<String, String> function = fieldConvert.getOrDefault(convertValue, Function.identity());
            convertValue = function.apply(convertValue);
            log.debug("native[{}] to convert[{}]", sort, convertValue);
            convertValues.add(convertValue);
        }
        return convertValues.toArray(new String[0]);
    }

    /**
     * 清洗排序数据
     * <ul>
     * <li>清除null值</li>
     * <li>清除空串</li>
     * </ul>
     *
     * @param sorts nativePage当中的原始值
     * @return 清洗过后的值
     */
    @NotNull
    private LinkedHashSet<String> clearSorts(@Nullable String[] sorts) {
        LinkedHashSet<String> set = new LinkedHashSet<>();
        if (ArrayUtil.isNotEmpty(sorts)) {
            sorts = ArrayUtil.removeBlank(sorts);
            set.addAll(Arrays.asList(sorts));
        }
        return set;
    }

    /**
     * 新增排序字段转换器
     *
     * @param function   转换器 param:排序值（上一任转换后的值),return: 转换后的值
     * @param fieldNames 排序字段名 会自动转换成下划线
     */
    public void addSortFieldConvert(@NotNull Function<String, String> function, @NotBlank String... fieldNames) {
        Arrays.stream(fieldNames).filter(StrUtil::isNotBlank)
                .forEach(fieldName -> fieldConvert.merge(StrUtil.toUnderlineCase(fieldName), function, Function::andThen));
    }

    /**
     * 新增排序字段转换器
     *
     * @param function 转换器 param:排序值（上一任转换后的值),return: 转换后的值
     * @param func     {@link EntityUtil#getSimpleField}
     */
    @SafeVarargs
    public final void addSortFieldConvert(@NotNull Function<String, String> function, SFunction<T, ?>... func) {
        addSortFieldConvert(function, Arrays.stream(func).map(EntityUtil::getSimpleField).toArray(String[]::new));
    }

    /**
     * 新增中文排序转换器
     *
     * @param fieldNames 排序字段名 会自动转换成下划线
     */
    public void addGbkFieldConvert(String... fieldNames) {
        addSortFieldConvert(t -> StrUtil.format("CONVERT({} USING gbk)", t), fieldNames);
    }

    /**
     * 新增中文排序转换器
     *
     * @param func {@link EntityUtil#getSimpleField}
     */
    @SafeVarargs
    public final void addGbkFieldConvert(SFunction<T, ?>... func) {
        addGbkFieldConvert(Arrays.stream(func).map(EntityUtil::getSimpleField).toArray(String[]::new));
    }

    /**
     * 新增表前缀
     *
     * @param tableAlias 表别名
     * @param fieldNames 排序字段名 会自动转换成下划线
     */
    public void addTableAliasSortFieldConvert(@NotBlank String tableAlias, String... fieldNames) {
        addSortFieldConvert(t -> StrUtil.format("{}.{}", tableAlias, t), fieldNames);
    }

    /**
     * 新增中文排序转换器
     *
     * @param tableAlias 表别名
     * @param func       {@link EntityUtil#getSimpleField}
     */
    @SafeVarargs
    public final void addTableAliasSortFieldConvert(@NotBlank String tableAlias, SFunction<T, ?>... func) {
        addTableAliasSortFieldConvert(tableAlias, Arrays.stream(func).map(EntityUtil::getSimpleField).toArray(String[]::new));
    }

    /**
     * 添加默认字段排序
     *
     * @param func {@link EntityUtil#getSimpleField}
     */
    @SafeVarargs
    public final void addDefaultOrderByAsc(SFunction<T, ?>... func) {
        addDefaultOrderByAsc(Arrays.stream(func).map(EntityUtil::getSimpleField).toArray(String[]::new));
    }

    /**
     * 添加默认字段排序
     *
     * @param fieldNames 排序字段名 会自动转换成下划线
     */
    public void addDefaultOrderByAsc(String... fieldNames) {
        addDefaultOrderBy(Boolean.FALSE, fieldNames);
    }

    private void addDefaultOrderBy(@NotNull Boolean isDesc, String... fieldNames) {
        Arrays.stream(fieldNames).filter(StrUtil::isNotBlank).forEach(fieldName -> {
            this.defaultOrderBy.remove(fieldName);
            this.defaultOrderBy.put(fieldName, isDesc);
        });
    }

    /**
     * 添加默认字段排序
     *
     * @param func {@link EntityUtil#getSimpleField}
     */
    @SafeVarargs
    public final void addDefaultOrderByDesc(SFunction<T, ?>... func) {
        addDefaultOrderByDesc(Arrays.stream(func).map(EntityUtil::getSimpleField).toArray(String[]::new));
    }

    /**
     * 添加默认字段排序
     *
     * @param fieldNames 排序字段名 会自动转换成下划线
     */
    public void addDefaultOrderByDesc(String... fieldNames) {
        addDefaultOrderBy(Boolean.TRUE, fieldNames);
    }

    @Override
    public List<T> getRecords() {
        return nativePage.getRecords();
    }

    @Override
    public IPage<T> setRecords(List<T> records) {
        return nativePage.setRecords(records);
    }

    @Override
    public long getTotal() {
        return nativePage.getTotal();
    }

    @Override
    public IPage<T> setTotal(long total) {
        return nativePage.setTotal(total);
    }

    @Override
    public long getSize() {
        return nativePage.getSize();
    }

    @Override
    public IPage<T> setSize(long size) {
        return nativePage.setSize(size);
    }

    @Override
    public long getCurrent() {
        return nativePage.getCurrent();
    }

    @Override
    public IPage<T> setCurrent(long current) {
        return nativePage.setCurrent(current);
    }

    @Override
    public Map<Object, Object> condition() {
        return nativePage.condition();
    }

    @Override
    public boolean optimizeCountSql() {
        return nativePage.optimizeCountSql();
    }

    @Override
    public boolean isSearchCount() {
        return nativePage.isSearchCount();
    }

    @Override
    public long offset() {
        return nativePage.offset();
    }

    @Override
    public long getPages() {
        return nativePage.getPages();
    }

    @Override
    public IPage<T> setPages(long pages) {
        return nativePage.setPages(pages);
    }

    @Override
    public String toString() {
        return StrUtil.format("PageWrapper[current:{},size:{},records:{},total:{},asc:{},desc:{}]"
                , nativePage.getCurrent(), nativePage.getSize()
                , Optional.ofNullable(nativePage.getRecords()).map(List::size).orElse(0)
                , nativePage.getTotal(), this.ascs(), this.descs());
    }

    /**
     * 是否已经被CONVERT({} USING gbk)
     *
     * @param sortField 排序字段
     * @return true:已经被使用过CONVERT({} USING gbk
     */
    private boolean alreadyGbkConvert(String sortField) {
        return Optional.ofNullable(sortField).map(t -> {
            return CollUtil.newHashSet("CONVERT", "USING", "gbk")
                    .stream().allMatch(key -> StrUtil.containsAnyIgnoreCase(t, key));
        }).orElse(Boolean.FALSE);
    }

    public static void main(String[] args) {
        Page<Object> page = new Page<>(1, 10);
        page.setAsc("name", "CONVERT(displayName USING gbk)");
        PageWrapper<Object> pageWrapper = new PageWrapper<>(page);
        pageWrapper.addGbkFieldConvert("realName");
        pageWrapper.addDefaultOrderByAsc("realName");
        System.out.println(page);
        System.out.println(pageWrapper);
    }
}

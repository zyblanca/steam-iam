package com.crc.crcloud.steam.iam.common.utils;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crc.crcloud.steam.iam.common.exception.IamAppCommException;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PageUtil<T> extends Page<T> {

    private static final String ORDER_HEARD = "CONVERT(";
    private static final String ORDER_END = "  USING gbk)";

    private static final List<String> KEYS = new ArrayList<>();
    //匹配数量，当前只有特殊查询使用
    //需求查询 需要匹配塑数据
    private Integer matchCount = 0;


    static {
        KEYS.add("summary");
        KEYS.add("name");
        KEYS.add("test_plan_name");
        KEYS.add("real_name");
    }


    @Override
    public Page<T> setAsc(String... ascs) {

        transformation(ascs);

        return super.setAsc(ascs);
    }

    @Override
    public Page<T> setAscs(List<String> ascs) {

        transformation(ascs);

        return super.setAscs(ascs);
    }

    @Override
    public Page<T> setDesc(String... descs) {

        transformation(descs);

        return super.setDesc(descs);
    }

    @Override
    public Page<T> setDescs(List<String> descs) {

        transformation(descs);

        return super.setDescs(descs);
    }


    private void transformation(String[] arg) {
        if (Objects.isNull(arg)) {
            return;
        }
        String s;
        for (int i = 0; i < arg.length; i++) {
            s = arg[i];
            valid(s);
            //拼音排序
            if (KEYS.contains(s)) {
                arg[i] = ORDER_HEARD + s + ORDER_END;
            }
        }
    }


    private void transformation(List<String> arg) {
        if (CollectionUtils.isEmpty(arg)) {
            return;
        }
        String s;
        for (int i = 0; i < arg.size(); i++) {
            s = arg.get(i);
            valid(s);
            //拼音排序
            if (KEYS.contains(s)) {
                arg.set(i, ORDER_HEARD + s + ORDER_END);
            }
        }
    }

    private void valid(String order) {
        if (StringUtils.isEmpty(order)) {
            return;
        }
        //当前只有简单的字段排序功能
        if (order.trim().contains(" ") || order.contains("(")) {
            throw new IamAppCommException("错误的排序字段");
        }

    }

    public Integer getMatchCount() {
        return matchCount;
    }

    public void setMatchCount(Integer matchCount) {
        this.matchCount = matchCount;
    }
}

package com.crc.crcloud.steam.iam.model.vo;

import cn.hutool.core.util.NumberUtil;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * 请求分页接收参数
 * @author LiuYang
 * @date 2019/12/3
 */
public class PageRequestVO {
    @ApiModelProperty("当前页;从1开始")
    private Long current;
    @ApiModelProperty("每页数量;从1开始")
    private Long size;

    public void format() {
        this.setCurrent(this.getCurrent());
        this.setSize(this.getSize());
    }

    public PageRequestVO() {
        this.format();
    }

    /**
     * 当前页码
     * <p>小于0的页码自动格式化为1</p>
     * @return 页码 从1开始
     */
    public Long getCurrent() {
        return Optional.ofNullable(current)
                .filter(t -> NumberUtil.isGreater(BigDecimal.valueOf(t), BigDecimal.ZERO))
                .orElse(1L);
    }

    public void setCurrent(Long current) {
        this.current = current;
    }

    /**
     * 每页数量
     * <p>小于0的页码自动格式化为10</p>
     * @return 每页数量 从1开始
     */
    public Long getSize() {
        return Optional.ofNullable(size)
                .filter(t -> NumberUtil.isGreater(BigDecimal.valueOf(t), BigDecimal.ZERO))
                .orElse(10L);
    }

    public void setSize(Long size) {
        this.size = size;
    }

    @Override
    public String toString() {
        return "PageRequestVO{" +
                "current=" + getCurrent() +
                ", size=" + getSize() +
                '}';
    }
}

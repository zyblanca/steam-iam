package com.crc.crcloud.steam.iam.common.utils;

import com.crc.crcloud.steam.iam.common.exception.IamAppCommException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 对象拷贝
 */
@Slf4j
public class CopyUtil {
    /**
     * 集合对象拷贝
     * targetClass 必须有无参的构造方法，或者newInstance重新后也是无参的
     *
     * @param sourceList  源集合
     * @param targetClass 拷贝对象
     * @param <T>         类型
     * @return 对应类型的集合
     */
    public static <T> List<T> copyList(List sourceList, Class<T> targetClass) {

        List<T> result = new ArrayList<>();
        if (CollectionUtils.isEmpty(sourceList) || Objects.isNull(targetClass)) {
            return result;
        }
        try {
            for (Object source : sourceList) {

                T t = targetClass.newInstance();
                BeanUtils.copyProperties(source, t);
                result.add(t);
            }
        } catch (Exception e) {

            throw new IamAppCommException("copy.init.error", targetClass.getName());
        }
        return result;
    }


    public static <T> T copy(@NotNull Object source, @NotNull Class<T> targetClass) {

        try {

            T t = targetClass.newInstance();
            if (Objects.isNull(source)) {
                return t;
            }
            BeanUtils.copyProperties(source, t);
            return t;
        } catch (Exception e) {
            throw new IamAppCommException("copy.init.error", targetClass.getName());
        }
    }

}

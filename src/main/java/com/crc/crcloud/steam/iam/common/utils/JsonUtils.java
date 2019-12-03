package com.crc.crcloud.steam.iam.common.utils;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;

/**
 * @author wuguokai
 */
public class JsonUtils {

    private static ObjectMapper om = new ObjectMapper();

    static {
        // 对象的所有字段全部列入，还是其他的选项，可以忽略 mull 等
        om.setSerializationInclusion(Include.ALWAYS);
        // 忽略空 bean 转 json 的错误
        om.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        // 忽略未知属性，防止 json 字符串中存在，java 对象中不存在对应属性的情况出现错误
        om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // 注册一个时间序列化及反序列化的处理模块，用于解决 jdk8 中 localDateTime 等的序列化问题
        om.registerModule(new JavaTimeModule());
    }

    private JsonUtils() {
    }

    public static boolean isJSONValid(String jsonInString) {
        try {
            final ObjectMapper mapper = new ObjectMapper();
            mapper.readTree(jsonInString);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static <T> T parse(String json, Class<T> clazz) {
        return parse(json, clazz, null);
    }

    private static <T> T parse(String json, Class<T> clazz, TypeReference type) {
        T obj = null;
        if (!StringUtils.isEmpty(json)) {
            try {
                if (clazz != null) {
                    obj = om.readValue(json, clazz);
                } else {
                    obj = om.readValue(json, type);
                }
            } catch (IOException e) {
                throw new IllegalArgumentException(e.getMessage());
            }
        }
        return obj;
    }
}

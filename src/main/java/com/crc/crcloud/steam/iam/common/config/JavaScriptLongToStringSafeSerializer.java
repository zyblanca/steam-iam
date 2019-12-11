package com.crc.crcloud.steam.iam.common.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

/**
 * 防止前端long型精度丢失序列化
 *<p>js 精度最大值为：9007199254740992</p>
 *<p>当数值大于9007199254740992时，序列化为string类型</p>
 * @author LiuYang
 * @date 2019/12/11
 */
public class JavaScriptLongToStringSafeSerializer extends StdSerializer<Long> {
    public JavaScriptLongToStringSafeSerializer(Class<Long> t) {
        super(t);
    }

    @Override
    public void serialize(Long value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        if (isSafeSerialize(value, gen, provider)) {
            gen.writeString(value.toString());
        } else {
            gen.writeNumber(value);
        }
    }

    /**
     * 是否需要安全序列化
     * @return true:表示需要转换成string
     */
    protected boolean isSafeSerialize(Long value, JsonGenerator gen, SerializerProvider provider) {
        return value > 9007199254740992L;
    }
}

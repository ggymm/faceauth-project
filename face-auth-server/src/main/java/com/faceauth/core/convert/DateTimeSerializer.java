package com.faceauth.core.convert;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.io.IOException;

/**
 * @author gongym
 * @version 创建时间: 2023-12-08 18:38
 */
public class DateTimeSerializer extends StdSerializer<Long> {
    public DateTimeSerializer() {
        super(Long.class);
    }

    @Override
    public void serialize(Long value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeString(DateFormatUtils.format(value, "yyyy-MM-dd HH:mm:ss"));
    }
}

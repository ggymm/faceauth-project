package com.faceauth.core.convert;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.sql.Timestamp;

/**
 * @author gongym
 * @version 创建时间: 2023-12-08 18:38
 */
public class TimestampSerializer extends StdSerializer<Long> {
    public TimestampSerializer() {
        super(Long.class);
    }

    @Override
    public void serialize(Long value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeString(new Timestamp(value).toString());
    }
}

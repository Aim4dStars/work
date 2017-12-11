package com.bt.nextgen.service.json;

import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.exception.ApiException;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

public class JsonStreamProcessor {

    private JsonStreamStrategy streamStrategy;

    public JsonStreamProcessor(JsonStreamStrategy streamStrategy) {
        this.streamStrategy = streamStrategy;
    }

    public String processJson(String json) {
        try {
            StringWriter out = new StringWriter();
            JsonWriter writer = new JsonWriter(out);
            JsonReader reader = new JsonReader(new StringReader(json));
            try {
                streamReplace(reader, writer);
                return out.toString();
            } finally {
                writer.close();
                reader.close();
            }
        } catch (Exception e) {
            throw new ApiException(ApiVersion.CURRENT_VERSION, "Can not parse json response", e);
        }
    }

    private void streamReplace(JsonReader reader, JsonWriter writer) throws IOException {
        while (true) {
            JsonToken token = reader.peek();

            switch (token) {
                case BEGIN_ARRAY:
                    reader.beginArray();
                    writer.beginArray();
                    break;
                case END_ARRAY:
                    reader.endArray();
                    writer.endArray();
                    break;
                case BEGIN_OBJECT:
                    reader.beginObject();
                    writer.beginObject();
                    break;
                case END_OBJECT:
                    reader.endObject();
                    writer.endObject();
                    break;
                case NAME:
                    String name = streamStrategy.processName(reader.nextName());
                    writer.name(name);
                    break;
                case STRING:
                    String value = streamStrategy.processValue(reader.nextString());
                    writer.value(value);
                    break;
                case NUMBER:
                    String n = streamStrategy.processNumber(reader.nextString());
                    writer.value(n);
                    break;
                case BOOLEAN:
                    boolean b = reader.nextBoolean();
                    writer.value(b);
                    break;
                case NULL:
                    reader.nextNull();
                    writer.nullValue();
                    break;
                case END_DOCUMENT:
                default:
                    return;
            }
        }
    }
}

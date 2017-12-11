package com.bt.nextgen.api.option.v1.model;

import com.bt.nextgen.core.api.model.BaseDto;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializable;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OptionsDto extends BaseDto implements JsonSerializable {
    private Map<String, String> options = new HashMap<>();

    public void addOption(String optionName, String value) {
        options.put(optionName, value);
    }

    // =======================================================================
    // Implement JsonSerializableWithType methods
    // =======================================================================

    @Override
    public void serialize(JsonGenerator jgen, SerializerProvider provider) throws IOException {
        jgen.writeStartObject();
        processMap(jgen);
        jgen.writeStringField("type", getType());
        jgen.writeEndObject();
    }

    // turn the flat key structure into a nested json object
    protected void processMap(JsonGenerator jgen) throws IOException {
        jgen.writeFieldName("options");
        jgen.writeStartObject();
        List<String> keys = new ArrayList<String>(options.keySet());
        Collections.sort(keys);
        StringBuilder prevNode = new StringBuilder("");
        Deque<String> nodes = new ArrayDeque<>();
        for (String key : keys) {
            while (!key.startsWith(prevNode.toString())) {
                jgen.writeEndObject();
                prevNode = new StringBuilder(nodes.pop());
            }
            String thisNode = key.replaceFirst(prevNode.toString(), "");
            String splitKey[] = thisNode.split("\\.", 2);
            while (splitKey.length == 2) {
                nodes.push(prevNode.toString());
                prevNode.append(splitKey[0]).append(".");
                thisNode = splitKey[1];

                jgen.writeFieldName(splitKey[0]);
                jgen.writeStartObject();
                splitKey = thisNode.split("\\.", 2);
            }
            jgen.writeFieldName(splitKey[0]);
            jgen.writeString(options.get(key));
        }
        while (!nodes.isEmpty()) {
            jgen.writeEndObject();
            nodes.pop();
        }
        jgen.writeEndObject();
    }

    @Override
    public void serializeWithType(JsonGenerator jgen, SerializerProvider provider, TypeSerializer typeSer) throws IOException {
        serialize(jgen, provider);
    }
}

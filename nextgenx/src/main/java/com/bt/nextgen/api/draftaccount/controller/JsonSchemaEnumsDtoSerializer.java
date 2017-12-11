package com.bt.nextgen.api.draftaccount.controller;

import com.bt.nextgen.api.draftaccount.model.ClientApplicationDto;
import com.bt.nextgen.api.draftaccount.model.JsonSchemaEnumsDto;
import com.bt.nextgen.api.draftaccount.schemas.v1.DirectClientApplicationFormData;
import com.bt.nextgen.api.draftaccount.schemas.v1.OnboardingApplicationFormData;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.apache.commons.lang3.Validate;

import java.io.IOException;
import java.util.Map;

/**
 * Custom JsonSchemaEnumsDto serializer to be used on new JSON format
 */
@SuppressWarnings("squid:S1948")
public class JsonSchemaEnumsDtoSerializer extends StdSerializer<JsonSchemaEnumsDto> {

    private JsonSerializer<ClientApplicationDto> defaultSerializer;

    @SuppressWarnings("unchecked")
    public JsonSchemaEnumsDtoSerializer(JsonSerializer serializer) {
        super(JsonSchemaEnumsDto.class);
        this.defaultSerializer = serializer;
    }

    /**
     * Serialize JsonSchemaEnumsDto
     *
     * @param value
     * @param gen
     * @param provider
     * @throws IOException
     */
    @Override
    public void serialize(JsonSchemaEnumsDto value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        for(Map.Entry<String, Object> entry: value.getRoot().entrySet()) {
            gen.writeFieldName(entry.getKey());//this is enum's name
            gen.writeStartObject(); //start writing enaum values one by one
            Enum<?>[] enumValues = (Enum<?>[]) entry.getValue();
            for(int i =0; i < enumValues.length; i++) {
                gen.writeFieldName(enumValues[i].name());
                gen.writeString(enumValues[i].toString());
            }
            gen.writeEndObject();//end writing enum values
        }
        //stop
        gen.writeEndObject();
    }
}
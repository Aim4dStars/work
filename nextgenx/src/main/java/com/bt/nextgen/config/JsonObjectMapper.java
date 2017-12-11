package com.bt.nextgen.config;

import com.bt.nextgen.api.draftaccount.controller.ClientApplicationDtoDeserializer;
import com.bt.nextgen.api.draftaccount.controller.ClientApplicationDtoSerializer;
import com.bt.nextgen.api.draftaccount.controller.JsonSchemaEnumsDtoSerializer;
import com.bt.nextgen.api.draftaccount.model.ClientApplicationDto;
import com.bt.nextgen.api.draftaccount.model.JsonSchemaEnumsDto;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.deser.DeserializationProblemHandler;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.fasterxml.jackson.datatype.joda.cfg.FormatConfig;
import com.fasterxml.jackson.datatype.joda.deser.DateTimeDeserializer;
import com.fasterxml.jackson.datatype.joda.ser.DateTimeSerializer;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;

@Component
@Qualifier("jsonObjectMapper")
@Primary
public class JsonObjectMapper extends ObjectMapper {
    private SimpleModule clientApplicationDtoModule = new SimpleModule();

    private static final Logger LOGGER = LoggerFactory.getLogger(JsonObjectMapper.class);

    @Autowired
    private ClientApplicationDtoDeserializer clientApplicationDtoDeserializer;

	public JsonObjectMapper() {
        this.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        //Joda DateTime serialization/deserialization configuration with Jackson 2.x (using custom DateTime format)
        this.registerModule(new JodaModule()
                        .addSerializer(DateTime.class, new DateTimeSerializer().withFormat(FormatConfig.DEFAULT_DATETIME_FORMAT))
                        .addDeserializer(DateTime.class, (JsonDeserializer) new DateTimeDeserializer(DateTime.class, FormatConfig.DEFAULT_DATETIME_FORMAT))
        );
        //add deserialization handler for unknown properties (just log a WARN message and move on)
        this.addHandler(new DeserializationProblemHandler() {
            @Override
            public boolean handleUnknownProperty(DeserializationContext ctxt, JsonParser jp, JsonDeserializer<?> deserializer, Object beanOrClass, String propertyName) throws IOException {
                LOGGER.warn("## JSON_OBJECT_MAPPER ## Unknown property name: {} when deserializing JSON string into class: {}", propertyName, beanOrClass.getClass().getName());
                return true; //just continue after logging message
            }
        });
    }

    @PostConstruct
    public void init() {
        //setup custom JSON Deserializers
        clientApplicationDtoModule.addDeserializer(ClientApplicationDto.class, clientApplicationDtoDeserializer);

        //setup custom JSON Serializers
        clientApplicationDtoModule.setSerializerModifier(new BeanSerializerModifier() {
            @Override
            public JsonSerializer<?> modifySerializer(SerializationConfig config, BeanDescription beanDesc, JsonSerializer<?> serializer) {
                if (ClientApplicationDto.class.isAssignableFrom(beanDesc.getBeanClass())) {
                    return new ClientApplicationDtoSerializer(serializer);
                }
                if (JsonSchemaEnumsDto.class.getName().equalsIgnoreCase(beanDesc.getBeanClass().getName())) {
                    return new JsonSchemaEnumsDtoSerializer(serializer);
                }
                return serializer;
            }
        });
        this.registerModule(clientApplicationDtoModule);
    }
}

package com.bt.nextgen.api.draftaccount.controller;

import java.io.IOException;

import com.bt.nextgen.api.draftaccount.schemas.v1.DirectClientApplicationFormData;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.apache.commons.lang3.Validate;

import com.bt.nextgen.api.draftaccount.model.ClientApplicationDto;
import com.bt.nextgen.api.draftaccount.schemas.v1.OnboardingApplicationFormData;

/**
 * Custom ClientApplicationDto serializer to be used on new JSON format
 */
@SuppressWarnings("squid:S1948")
public class ClientApplicationDtoSerializer extends StdSerializer<ClientApplicationDto> {

    private JsonSerializer<ClientApplicationDto> defaultSerializer;

    @SuppressWarnings("unchecked")
    public ClientApplicationDtoSerializer(JsonSerializer serializer) {
        super(ClientApplicationDto.class);
        this.defaultSerializer = serializer;
    }

    /**
     * Serialize the OnboardingApplicationFormData object (property) under the "formData" name in the JSON payload so that the webclient does not change.
     *
     * @param value
     * @param gen
     * @param provider
     * @throws IOException
     */
    @Override
    public void serialize(ClientApplicationDto value, JsonGenerator gen, SerializerProvider provider) throws IOException {

        if (!value.isJsonSchemaSupported()) {
            //pre schema JSON -> must be serialized as before
            defaultSerializer.serialize(value, gen, provider);
        } else {
            //post schema JSON -> must be serialized using the auto-generated OnboardingApplicationFormData
            Validate.notNull(value.getFormData());
            gen.writeStartObject();
            //start
            gen.writeStringField("adviserId", value.getAdviserId());
            gen.writeStringField("adviserName", value.getAdviserName());
            gen.writeStringField("referenceNumber", value.getReferenceNumber());
            gen.writeObjectField("key", value.getKey());

            if(!value.isDirectApplication()) {
                OnboardingApplicationFormData formData = (OnboardingApplicationFormData) value.getFormData();
                //do not serialize empty array in root formData -> set nu NULL if array is empty
                if (formData.getInvestors() != null && formData.getInvestors().isEmpty()) {
                    formData.setInvestors(null);
                }
                if (formData.getDirectors() != null && formData.getDirectors().isEmpty()) {
                    formData.setDirectors(null);
                }
                if (formData.getTrustees() != null && formData.getTrustees().isEmpty()) {
                    formData.setTrustees(null);
                }
                //serialize formData
                gen.writeObjectField("formData", formData);
            } else {
                DirectClientApplicationFormData formData = (DirectClientApplicationFormData) value.getFormData();
                if (formData.getInvestors() != null && formData.getInvestors().isEmpty()) {
                    formData.setInvestors(null);
                }
                //serialize formData
                gen.writeObjectField("formData", formData);
            }


            //add JSON schema supported flag for easy debugging
            gen.writeBooleanField("jsonSchemaSupported", value.isJsonSchemaSupported());

            gen.writeStringField("status", value.getStatus().toString());
            gen.writeObjectField("lastModified", value.getLastModified());
            gen.writeStringField("lastModifiedByName", value.getLastModifiedByName());
            gen.writeStringField("productName", value.getProductName());
            gen.writeStringField("productId", value.getProductId());
            //stop
            gen.writeEndObject();
        }
    }
}
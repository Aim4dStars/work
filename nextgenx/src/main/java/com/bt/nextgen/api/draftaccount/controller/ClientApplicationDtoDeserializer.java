package com.bt.nextgen.api.draftaccount.controller;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.Nonnull;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.datatype.joda.cfg.FormatConfig;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.LogLevel;
import com.github.fge.jsonschema.core.report.ProcessingMessage;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.bt.nextgen.api.draftaccount.FormDataConstants;
import com.bt.nextgen.api.draftaccount.FormDataValidator;
import com.bt.nextgen.api.draftaccount.model.ClientApplicationDto;
import com.bt.nextgen.api.draftaccount.model.ClientApplicationDtoAdvisedImpl;
import com.bt.nextgen.api.draftaccount.model.ClientApplicationDtoDirectImpl;
import com.bt.nextgen.api.draftaccount.model.ClientApplicationDtoMapImpl;
import com.bt.nextgen.api.draftaccount.model.ClientApplicationKey;
import com.bt.nextgen.api.draftaccount.schemas.v1.DirectClientApplicationFormData;
import com.bt.nextgen.api.draftaccount.schemas.v1.OnboardingApplicationFormData;
import com.bt.nextgen.draftaccount.repository.ClientApplicationStatus;

import static com.bt.nextgen.api.draftaccount.LoggingConstants.ONBOARDING;

/**
 * Custom deserializer implementation for ClientApplicationDto.class
 */
@Component
public class ClientApplicationDtoDeserializer extends StdDeserializer<ClientApplicationDto> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientApplicationDtoDeserializer.class);

    @Autowired
    @Qualifier("jsonObjectMapper")
    private ObjectMapper mapper;

    @Autowired
    private transient FormDataValidator formDataValidator;

    public ClientApplicationDtoDeserializer() {
        super(ClientApplicationDto.class);
    }

    private ClientApplicationDto validateJson(final JsonNode formDataJsonNode) throws IOException {
        ClientApplicationDto resultDto = null;
        final String formDataJsonString = formDataJsonNode.toString();
        if (formDataJsonNode.get(FormDataConstants.FIELD_VERSION) != null) { //if JSON supported schema
            try {
                LOGGER.info("{} - validate formData JSON: {}", ONBOARDING, formDataJsonString);
                ProcessingReport report = null;
                JsonNode applicationOriginJsonNode = formDataJsonNode.get(FormDataConstants.FIELD_APPLICATION_ORIGIN);
                boolean isDirect = applicationOriginJsonNode != null && applicationOriginJsonNode.textValue().equals(FormDataConstants.VALUE_APPLICATION_ORIGIN_DIRECT);
                String schema = null;
                if ( isDirect ) {
                    schema = formDataValidator.DIRECT_APPLICATION_JSON_SCHEMA;
                } else {
                    schema = formDataValidator.APPLICATION_JSON_SCHEMA;
                }
                report = formDataValidator.validate(formDataJsonNode, schema);
                logValidationMessages(report);
                if (!report.isSuccess()) {
                    throw new ClientApplicationDtoDeserializerException("JSON validation failed");
                } else {
                    if (isDirect) {
                        resultDto = new ClientApplicationDtoDirectImpl(mapper.readValue(formDataJsonNode.toString(), DirectClientApplicationFormData.class));
                    } else {
                        resultDto = new ClientApplicationDtoAdvisedImpl(mapper.readValue(formDataJsonNode.toString(), OnboardingApplicationFormData.class));
                    }
                }
                return resultDto;
            } catch (ProcessingException e) {
                LOGGER.error("{} - error trying to validate json string: {}", ONBOARDING, formDataJsonNode.toString());
                LOGGER.error("{} - error stacktrace: {}", ONBOARDING, e);
                throw new ClientApplicationDtoDeserializerException("error trying to validate json string", e);
            }
        }
        //by default it does not support JSON schemas (old Map implementation)
        Map<String, Object> formData = mapper.readValue(formDataJsonNode.toString(), new TypeReference<LinkedHashMap<String, Object>>() {});
        return
                new ClientApplicationDtoMapImpl(formData);
    }

    private void setAdviserId(JsonNode node, ClientApplicationDto clientApplicationDto) {
        if (node != null) {
            clientApplicationDto.setAdviserId(node.asText());
        }
    }

    private void setAdviserName(JsonNode node, ClientApplicationDto clientApplicationDto) {
        if (node != null) {
            clientApplicationDto.setAdviserName(node.asText());
        }
    }

    private void setReferenceNumber(JsonNode node, ClientApplicationDto clientApplicationDto){
        if (node != null) {
            clientApplicationDto.setReferenceNumber(node.asText());
        }
    }

    private void setKey(JsonNode node, ClientApplicationDto clientApplicationDto) throws IOException {
        if (node != null) {
            ClientApplicationKey key = mapper.readValue(node.toString(), ClientApplicationKey.class);
            clientApplicationDto.setKey(key);
        }
    }

    /**
     * client_application.STATUS should always contain "processing", "draft", "active", "deleted".
     */
    private void setStatus(@Nonnull JsonNode node, ClientApplicationDto clientApplicationDto) throws ClientApplicationDtoDeserializerException {
        try {
            clientApplicationDto.setStatus(ClientApplicationStatus.jsonConvert(node.asText()));
        } catch (Exception e) {
            throw new ClientApplicationDtoDeserializerException("error parsing json status", e);
        }
    }

    private void setLastModified(JsonNode node, ClientApplicationDto clientApplicationDto) throws ClientApplicationDtoDeserializerException {
        if (node != null) {
            try {
                //parse datetime string to Joda DateTime object using same formatter as JodaModule
                DateTime dt = FormatConfig.DEFAULT_DATETIME_FORMAT.rawFormatter().parseDateTime(node.asText());
                //set lastModified
                clientApplicationDto.setLastModified(dt);
            } catch (IllegalArgumentException e) {
                LOGGER.error("error parsing date: {}", node.asText());
                throw new ClientApplicationDtoDeserializerException("error parsing json date", e);
            }
        }
    }

    private void setLastModifiedByName(JsonNode node, ClientApplicationDto clientApplicationDto){
        if (node != null) {
            clientApplicationDto.setLastModifiedByName(node.asText());
        }
    }

    private void setProductName(JsonNode node, ClientApplicationDto clientApplicationDto){
        if (node != null) {
            clientApplicationDto.setProductName(node.asText());
        }
    }

    private void setProductId(JsonNode node, ClientApplicationDto clientApplicationDto){
        if (node != null) {
            clientApplicationDto.setProductId(node.asText());
        }
    }

    @Override
    public ClientApplicationDto deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        ClientApplicationDto clientApplicationDto = null;
        //validate JSON string before creating the ClientApplicationDto result bean
        JsonNode clientApplicationDtoNode = jp.readValueAsTree();
        JsonNode formDataJsonNode = clientApplicationDtoNode.findValue("formData");
        //validate JSON if supporting JSON schemas
        clientApplicationDto = validateJson(formDataJsonNode);
        //set adviserId
        setAdviserId(clientApplicationDtoNode.get("adviserId"), clientApplicationDto);
        //set adviserName
        setAdviserName(clientApplicationDtoNode.get("adviserName"), clientApplicationDto);
        //set referenceNumber
        setReferenceNumber(clientApplicationDtoNode.get("referenceNumber"), clientApplicationDto);
        //set key
        setKey(clientApplicationDtoNode.get("key"), clientApplicationDto);
        //set status
        setStatus(clientApplicationDtoNode.get("status"), clientApplicationDto);
        //set lastModified
        setLastModified(clientApplicationDtoNode.get("lastModified"), clientApplicationDto);
        //set lastModifiedByName
        setLastModifiedByName(clientApplicationDtoNode.get("lastModifiedByName"), clientApplicationDto);
        //set productName
        setProductName(clientApplicationDtoNode.get("productName"), clientApplicationDto);
        //set productId
        setProductId(clientApplicationDtoNode.get("productId"), clientApplicationDto);

        return clientApplicationDto;
    }

    /**
     * Log validation report messages
     * @param report
     * @throws IOException
     */
    private void logValidationMessages(ProcessingReport report) throws IOException {
        Iterator itr = report.iterator();
        ProcessingMessage message;
        while (itr.hasNext()) {
            message = (ProcessingMessage) itr.next();
            if (message.getLogLevel().equals(LogLevel.FATAL) || message.getLogLevel().equals(LogLevel.ERROR)) {
                LOGGER.error("{} - JSON schema error: {}", ONBOARDING, message.asJson().toString());
            }
        }
    }

    /**
     * For testing only
     * @param mapper
     */
    protected void setObjectMapper(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    /**
     * For testing only
     * @param formDataValidator
     */
    protected  void setFormDataValidator(FormDataValidator formDataValidator) {
        this.formDataValidator = formDataValidator;
    }
}

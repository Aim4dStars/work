package com.bt.nextgen.api.draftaccount.controller;

import com.bt.nextgen.api.draftaccount.FormDataValidator;
import com.bt.nextgen.api.draftaccount.FormDataValidatorImpl;
import com.bt.nextgen.api.draftaccount.model.ClientApplicationDto;
import com.bt.nextgen.api.draftaccount.schemas.v1.DirectClientApplicationFormData;
import com.bt.nextgen.api.draftaccount.schemas.v1.OnboardingApplicationFormData;
import com.bt.nextgen.api.draftaccount.schemas.v1.customer.Customer;
import com.bt.nextgen.config.JsonObjectMapper;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.cfg.DeserializerFactoryConfig;
import com.fasterxml.jackson.databind.deser.BeanDeserializerFactory;
import com.fasterxml.jackson.databind.deser.DefaultDeserializationContext;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingMessage;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import org.apache.commons.io.IOUtils;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import static java.lang.Thread.currentThread;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by F058391 on 27/07/2016.
 */
@RunWith(MockitoJUnitRunner.class)
public class ClientApplicationDtoDeserializerTest {

    @InjectMocks
    private ClientApplicationDtoDeserializer clientApplicationDtoDeserializer;

    @Mock
    private FormDataValidator formDataValidator;

    @Mock
    private ObjectMapper mapper;

    private String readJsonStringFromFile(String filePath) throws IOException {
        ClassLoader classLoader = currentThread().getContextClassLoader();
        InputStream in = classLoader.getResourceAsStream(filePath);
        if (in == null) {
            fail("JSON classpath resource not found: " + filePath);
        }
        return IOUtils.toString(in);
    }

    @Test
    public void shouldReturnOnboardingDtoForDirectApplication() throws IOException, ProcessingException {
        JsonFactory jsonFactory = new JsonFactory();
        String data = "{\n" +
                "  \"key\": {\n" +
                "    \"id\": 123\n" +
                "  },\n" +
                "  \"status\": \"draft\",\n" +
                "  \"formData\": {\n" +
                "    \"version\": \"1.0.0\",\n" +
                "    \"applicationOrigin\": \"WestpacLive\"\n" +
                "  }\n" +
                "}";
        JsonParser jp = jsonFactory.createJsonParser(data);
        jp.setCodec(new ObjectMapper());
        ProcessingReport processingReport = getProcessingReport(true);
        when(formDataValidator.validate(any(JsonNode.class), eq(formDataValidator.DIRECT_APPLICATION_JSON_SCHEMA))).thenReturn(processingReport);
        when(mapper.readValue(any(String.class), eq(DirectClientApplicationFormData.class))).thenReturn(new DirectClientApplicationFormData());
        final ClientApplicationDto applicationDto = clientApplicationDtoDeserializer.deserialize(jp, mock(DeserializationContext.class));
        assertThat(applicationDto.getType(), is("ClientApplicationDtoDirectImpl"));
    }

    @Test
    public void shouldReturnClientApplicationDtoWithoutXSSChars() throws IOException, ProcessingException {
        ClientApplicationDtoDeserializer deserializer = new ClientApplicationDtoDeserializer();
        ObjectMapper mapper = new JsonObjectMapper();
        deserializer.setObjectMapper(mapper);
        FormDataValidatorImpl validator = new FormDataValidatorImpl();
        validator.setObjectMapper(mapper);
        deserializer.setFormDataValidator(validator);

        JsonFactory jsonFactory = new JsonFactory();
        String jsonString = readJsonStringFromFile("com/bt/nextgen/api/draftaccount/model/individual-xss-names.json");

        JsonParser jp = jsonFactory.createJsonParser(jsonString);
        jp.setCodec(mapper);

        final ClientApplicationDto applicationDto = deserializer.deserialize(jp, new DefaultDeserializationContext.Impl(new BeanDeserializerFactory(new DeserializerFactoryConfig())));
        assertThat(applicationDto.getType(), is("ClientApplicationDtoAdvisedImpl"));

        OnboardingApplicationFormData formData = (OnboardingApplicationFormData) applicationDto.getFormData();
        Customer investor = formData.getInvestors().get(0);

        assertThat(investor.getFirstname(), Matchers.is("Florin script alert('XSS') /script"));
        assertThat(investor.getMiddlename(), Matchers.is("middle $$% #@#"));
        assertThat(investor.getLastname(), Matchers.is("test $$ %#@#"));
        assertThat(investor.getPreferredname(), Matchers.is("flo$$ % script alert('XSS') /script #@#"));
        assertThat(investor.getFormername(), Matchers.is("$$% #@#"));
    }

    @Test
    public void shouldReturnOnboardingDtoForAdvisedApplication() throws IOException, ProcessingException {
        JsonFactory jsonFactory = new JsonFactory();
        String data = "{\n" +
                "  \"key\": {\n" +
                "    \"id\": 123\n" +
                "  },\n" +
                "  \"status\": \"draft\",\n" +
                "  \"formData\": {\n" +
                "    \"version\": \"1.0.0\"\n" +
                "  }\n" +
                "}";
        JsonParser jp = jsonFactory.createJsonParser(data);
        jp.setCodec(new ObjectMapper());
        ProcessingReport processingReport = getProcessingReport(true);
        when(formDataValidator.validate(any(JsonNode.class), eq(formDataValidator.APPLICATION_JSON_SCHEMA))).thenReturn(processingReport);
        when(mapper.readValue(any(String.class), eq(DirectClientApplicationFormData.class))).thenReturn(new DirectClientApplicationFormData());
        final ClientApplicationDto applicationDto = clientApplicationDtoDeserializer.deserialize(jp, mock(DeserializationContext.class));
        assertThat(applicationDto.getType(), is("ClientApplicationDtoAdvisedImpl"));
    }

    @Test
    public void shouldReturnOnboardingDtoForOldMap() throws IOException, ProcessingException {
        JsonFactory jsonFactory = new JsonFactory();
        String data = "{\n" +
                "  \"key\": {\n" +
                "    \"id\": 123\n" +
                "  },\n" +
                "  \"status\": \"draft\",\n" +
                "  \"formData\": {\n" +
                "  }\n" +
                "}";
        JsonParser jp = jsonFactory.createJsonParser(data);
        jp.setCodec(new ObjectMapper());
        final ClientApplicationDto applicationDto = clientApplicationDtoDeserializer.deserialize(jp, mock(DeserializationContext.class));
        assertThat(applicationDto.getType(), is("ClientApplicationDtoMapImpl"));
    }

    private ProcessingReport getProcessingReport(boolean isSuccess) {
        ProcessingReport processingReport = mock(ProcessingReport.class);
        ProcessingMessage processingMessage = new ProcessingMessage();
        when(processingReport.isSuccess()).thenReturn(isSuccess);
        when(processingReport.iterator()).thenReturn(Arrays.asList(processingMessage).listIterator());
        return processingReport;
    }
}
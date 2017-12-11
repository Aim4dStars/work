package com.bt.nextgen.api.draftaccount.controller;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bt.nextgen.api.draftaccount.model.ClientApplicationDtoAdvisedImpl;
import com.bt.nextgen.api.draftaccount.model.ClientApplicationDtoMapImpl;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.junit.Before;
import org.junit.Test;

import com.bt.nextgen.api.draftaccount.model.ClientApplicationDto;
import com.bt.nextgen.api.draftaccount.model.ClientApplicationKey;
import com.bt.nextgen.api.draftaccount.schemas.v1.OnboardingApplicationFormData;
import com.bt.nextgen.api.draftaccount.schemas.v1.customer.Customer;
import com.bt.nextgen.draftaccount.repository.ClientApplicationStatus;

import static org.junit.Assert.assertTrue;

/**
 * Created by F030695 on 29/04/2016.
 */
public class ClientApplicationDtoSerializerTest {

    private ClientApplicationDtoSerializer serializer;

    private JsonSerializer<ClientApplicationDto> defaultSerializer;

    private StringWriter stringWriter;

    private JsonGenerator generator;

    private SerializerProvider provider;

    @Before
    public void setUp() throws IOException {
        serializer = new ClientApplicationDtoSerializer(defaultSerializer);
        stringWriter = new StringWriter();
        generator = new JsonFactory().createGenerator(stringWriter);
        generator.setCodec(new ObjectMapper());
        provider = new ObjectMapper().getSerializerProvider();
    }

    @Test
    public void testSerializing_postSchemaJson() throws IOException {
        ClientApplicationDto clientApplicationDto = getClientApplicationDto("1.0.0");
        serializer.serialize(clientApplicationDto, generator, provider);
        String jsonString = stringWriter.toString();
        stringWriter.close();

        assertTrue(jsonString.length() > 0);
        assertTrue(jsonString.equals("{\"adviserId\":\"02394DB8EF246D3B31F55295AFA28BCDC759E654BBDC78C5\",\"adviserName\":\"Spongebob "
            + "Squarepants\",\"referenceNumber\":\"R000002905\",\"key\":{\"clientApplicationKey\":2015},\"formData\":{\"version\":\""
            + "1.0.0\",\"investors\":[{\"emails\":[],\"personRoles\":[],\"phones\":[],\"firstname\":\"Homer\",\"lastname\":\"Simpson\"}]}"));
    }

    private ClientApplicationDto getClientApplicationDto(String version) {
        ClientApplicationDto clientApplicationDto = new ClientApplicationDtoAdvisedImpl(new ClientApplicationKey(new Long(2015)), getFormData(version));
        clientApplicationDto.setAdviserId("02394DB8EF246D3B31F55295AFA28BCDC759E654BBDC78C5");
        clientApplicationDto.setAdviserName("Spongebob Squarepants");
        clientApplicationDto.setReferenceNumber("R000002905");
        clientApplicationDto.setStatus(ClientApplicationStatus.draft);

        clientApplicationDto.setLastModified(null);

        clientApplicationDto.setLastModifiedByName("Spongebob Squarepants");
        clientApplicationDto.setProductName("BT Panorama Investments");
        clientApplicationDto.setProductId("F9095F64BABBDDBF95D322CD00D1C7B206FCA98EF4499FC6");
        return clientApplicationDto;
    }

    private OnboardingApplicationFormData getFormData(String version) {
        OnboardingApplicationFormData formData = new OnboardingApplicationFormData();
        formData.setVersion(version);
        formData.setInvestors(getInvestors());
        return formData;
    }

    private List<Customer> getInvestors() {
        Customer customer = new Customer();
        customer.setFirstname("Homer");
        customer.setLastname("Simpson");
        return Arrays.asList(customer);
    }

    private Map<String, Object> getFormDataMap(String version) {
        Map<String, Object> formData = new HashMap<>();
        formData.put("version", version);
        return formData;
    }
}

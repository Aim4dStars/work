package com.bt.nextgen.api.draftaccount.model;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.bt.nextgen.config.JsonObjectMapper;
import com.bt.nextgen.draftaccount.repository.ClientApplicationStatus;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class ClientApplicationDtoTest {

    private ClientApplicationDto clientApplicationDto;

    @Configuration
    static class EmptyConfig {
        //empty configuration
    }

    @Before
    public void setUp() throws Exception {
        clientApplicationDto = new ClientApplicationDtoMapImpl(new ClientApplicationKey(1123L));
        clientApplicationDto.setAdviserId("bart");
        clientApplicationDto.setStatus(ClientApplicationStatus.draft);
    }

    @Test
    public void shouldMapFormDataMapToJsonString() throws IOException {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("name", "gary");
        ArrayList<Object> values = new ArrayList<>();
        values.add(createMap("item", "first"));
        values.add(createMap("item", "second"));
        map.put("values", values);
        clientApplicationDto.setFormData(map);
        ObjectMapper mapper = new JsonObjectMapper();
        assertThat(mapper.writeValueAsString(clientApplicationDto.getFormData()), equalTo("{\"name\":\"gary\",\"values\":[{\"item\":\"first\"},{\"item\":\"second\"}]}"));
    }

    private Map<String, String> createMap(String x, String y) {
        Map<String, String> hashMap = new HashMap<>();
        hashMap.put(x, y);
        return hashMap;
    }

    @Test
    public void shouldMapJsonStringToFormDataMap() throws Exception {
        String jsonString = "{\"name\":\"gary\",\"values\":[{\"item\":\"first\"},{\"item\":\"second\"}]}";
        try {
            ObjectMapper mapper = new JsonObjectMapper();
            Object formData = mapper.readValue(jsonString, new TypeReference<LinkedHashMap<String, Object>>() {});
            clientApplicationDto.setFormData(formData);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        assertThat(((Map<String, String>) clientApplicationDto.getFormData()).get("name"), equalTo("gary"));
        assertThat(((List) ((Map)clientApplicationDto.getFormData()).get("values")).size(), equalTo(2));
    }

}
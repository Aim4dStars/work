package com.bt.nextgen.api.client.util;

import com.bt.nextgen.api.client.model.ClientDto;
import com.bt.nextgen.clients.web.model.State;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.service.integration.userinformation.ClientKey;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(MockitoJUnitRunner.class)
public class ClientFilterMatcherTest {
    private String filterQuery;
    private Map<ClientKey, ClientDto> clientKeyClientDtoMap;

    @Before
    public void setUp() throws Exception {
        filterQuery = "[{\"prop\":\"state\",\"op\":\"=\",\"val\":\"Australia Capital Territory\",\"type\":\"string\"}," +
                "{\"prop\":\"country\",\"op\":\"=\",\"val\":\"Australia\",\"type\":\"string\"}]";
        clientKeyClientDtoMap =  new HashMap<ClientKey, ClientDto>();
    }

    @Test
    public void testFilter() throws Exception {
        ClientKey key = ClientKey.valueOf("12345");
        ClientDto clientDto = new ClientDto();
        clientDto.setKey(new com.bt.nextgen.api.client.model.ClientKey("12345"));
        clientDto.setDisplayName("Test Client1");
        clientDto.setState(State.ACT.getStateValue());
        clientDto.setCountry("Australia");
        clientKeyClientDtoMap.put(key, clientDto);

        List<ApiSearchCriteria> criteriaList = ApiSearchCriteria.parseQueryString(ApiVersion.CURRENT_VERSION, filterQuery);
        ClientFilterMatcher filterMatcher = new ClientFilterMatcher(criteriaList, clientKeyClientDtoMap);
        Map<ClientKey, ClientDto> filteredMap = filterMatcher.filter();
        Assert.assertThat(filteredMap.size(), Is.is(1));

        clientDto = new ClientDto();
        clientDto.setKey(new com.bt.nextgen.api.client.model.ClientKey("67890"));
        clientDto.setDisplayName("Test Client2");
        clientDto.setState(State.NSW.getStateValue());
        clientDto.setCountry("Australia");
        clientKeyClientDtoMap.put(ClientKey.valueOf("67890"), clientDto);
        filterMatcher = new ClientFilterMatcher(criteriaList, clientKeyClientDtoMap);
        filteredMap = filterMatcher.filter();
        Assert.assertThat(filteredMap.size(), Is.is(1));

        filterQuery = "[{\"prop\":\"country\",\"op\":\"~=\",\"val\":\"Australia\",\"type\":\"string\"}]";
        clientDto = new ClientDto();
        clientDto.setKey(new com.bt.nextgen.api.client.model.ClientKey("11111"));
        clientDto.setDisplayName("International Client");
        clientDto.setCountry("Newzealand");
        criteriaList = ApiSearchCriteria.parseQueryString(ApiVersion.CURRENT_VERSION, filterQuery);
        clientKeyClientDtoMap.put(ClientKey.valueOf("11111"), clientDto);
        filterMatcher = new ClientFilterMatcher(criteriaList, clientKeyClientDtoMap);
        filteredMap = filterMatcher.filter();

        Assert.assertThat(filteredMap.size(), Is.is(1));
        Assert.assertThat(filteredMap.keySet().contains(ClientKey.valueOf("11111")), Is.is(true));
        clientDto = filteredMap.get(ClientKey.valueOf("11111"));
        Assert.assertThat(clientDto.getDisplayName(),Is.is("International Client"));
        Assert.assertThat(clientDto.getKey(), Is.is(new com.bt.nextgen.api.client.model.ClientKey("11111")));
        Assert.assertThat(clientDto.getCountry(), Is.is("Newzealand"));
    }
}
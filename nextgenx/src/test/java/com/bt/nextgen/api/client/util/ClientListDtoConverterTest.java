package com.bt.nextgen.api.client.util;

import com.bt.nextgen.api.client.model.ClientDto;
import com.bt.nextgen.api.client.model.IndividualDto;
import com.bt.nextgen.service.integration.userinformation.ClientType;
import com.bt.nextgen.clients.web.model.State;
import com.bt.nextgen.service.avaloq.domain.AddressImpl;
import com.bt.nextgen.service.avaloq.domain.CompanyImpl;
import com.bt.nextgen.service.avaloq.domain.IndividualImpl;
import com.bt.nextgen.service.integration.userinformation.Client;
import com.bt.nextgen.service.integration.userinformation.ClientKey;
import com.bt.nextgen.service.integration.domain.Address;
import com.bt.nextgen.service.integration.domain.InvestorType;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.hamcrest.core.IsInstanceOf.instanceOf;

@RunWith(MockitoJUnitRunner.class)
public class ClientListDtoConverterTest {

    private List<Client> clients;
    private ClientListDtoConverter clientListDtoConverter;

    @Before
    public void setUp() throws Exception {

        clients = new ArrayList<>();
        IndividualImpl client = new IndividualImpl();
        client.setClientKey(ClientKey.valueOf("11111"));
        client.setFirstName("clientFirstName");
        client.setLastName("clientLastName");
        client.setClientType(ClientType.N);
        List<Address> addresses = new ArrayList<>();
        AddressImpl address = new AddressImpl();
        address.setState(State.ACT.getStateValue());
        address.setCountry("Australia");
        addresses.add(address);
        client.setAddresses(addresses);
        clients.add(client);

        CompanyImpl company = new CompanyImpl();
        company.setClientKey(ClientKey.valueOf("22222"));
        company.setFullName("Company Client");
        company.setClientType(ClientType.L);
        addresses.add(address);
        company.setAddresses(addresses);
        company.setLegalForm(InvestorType.COMPANY);
        clients.add(company);
        clientListDtoConverter = new ClientListDtoConverter(clients);
    }

    @Test
    public void testConvert() throws Exception {
        Map<ClientKey, ClientDto> clientKeyClientDtoMap = clientListDtoConverter.convert();
        Assert.assertThat(clientKeyClientDtoMap.size(), Is.is(2));
        clientKeyClientDtoMap.keySet().contains(ClientKey.valueOf("11111"));
        Assert.assertThat(clientKeyClientDtoMap.get(ClientKey.valueOf("11111")), instanceOf(IndividualDto.class));
        Assert.assertThat(clientKeyClientDtoMap.get(ClientKey.valueOf("22222")), instanceOf(ClientDto.class));
        ClientDto clientDto = clientKeyClientDtoMap.get(ClientKey.valueOf("11111"));
        Assert.assertThat(clientDto.getDisplayName(), Is.is("clientLastName, clientFirstName"));
        Assert.assertThat(clientDto.getCountry(), Is.is("Australia"));
        Assert.assertThat(clientDto.getState(), Is.is(State.ACT.getStateValue()));

        clientDto = clientKeyClientDtoMap.get(ClientKey.valueOf("22222"));
        Assert.assertThat(clientDto.getDisplayName(), Is.is("Company Client"));
    }
}
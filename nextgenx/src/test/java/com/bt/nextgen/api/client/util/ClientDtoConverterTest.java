package com.bt.nextgen.api.client.util;

import com.bt.nextgen.api.client.model.ClientDto;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.avaloq.domain.AddressImpl;
import com.bt.nextgen.service.avaloq.domain.IndividualImpl;
import com.bt.nextgen.service.avaloq.domain.SmsfImpl;
import com.bt.nextgen.service.integration.domain.Address;
import com.bt.nextgen.service.integration.domain.InvestorType;
import com.bt.nextgen.service.integration.userinformation.Client;
import com.bt.nextgen.service.integration.userinformation.ClientKey;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ClientDtoConverterTest {

    private ClientDtoConverter dtoConverter;

    @Before
    public void setUp() {
        dtoConverter = new ClientDtoConverter();
    }

    @Test
    public void shouldSetAllPropertiesInResultDto() {
        ClientDto resultDto = (ClientDto) dtoConverter.toDTO(buildLegalClient());

        assertThat(EncodedString.toPlainText(resultDto.getKey().getClientId()), is("CLIENT_KEY"));
        assertThat(resultDto.getFirstName(), is("First name"));
        assertThat(resultDto.getLastName(), is("Last name"));
        assertThat(resultDto.getState(), is("NSW"));
        assertThat(resultDto.getCountry(), is("Australia"));
        assertThat(resultDto.isRegisteredOnline(), is(true));
    }

    @Test
    public void shouldSetFullNameAsDisplayNameForLegalPerson() {
        ClientDto resultDto = (ClientDto) dtoConverter.toDTO(buildLegalClient());

        assertThat(resultDto.getDisplayName(), is("Full name"));
    }

    @Test
    public void shouldSetFullNameAsCombinationOfFirstAndLastNamesForNaturalPerson() {
        ClientDto resultDto = (ClientDto) dtoConverter.toDTO(buildNaturalClient());

        assertThat(resultDto.getDisplayName(), is("Last name, First name"));
    }

    private Client buildLegalClient() {
        SmsfImpl client = new SmsfImpl();
        ClientKey clientKey = ClientKey.valueOf("CLIENT_KEY");
        client.setClientKey(clientKey);
        client.setFullName("Full name");
        client.setFirstName("First name");
        client.setLastName("Last name");
        client.setLegalForm(InvestorType.COMPANY);
        AddressImpl address = new AddressImpl();
        address.setState("NSW");
        address.setCountry("Australia");
        client.setAddresses(Arrays.<Address> asList(address));
        client.setRegistrationOnline(true);
        return client;
    }

    private Client buildNaturalClient() {
        IndividualImpl client = new IndividualImpl();
        ClientKey clientKey = ClientKey.valueOf("CLIENT_KEY");
        client.setClientKey(clientKey);
        client.setFirstName("First name");
        client.setLastName("Last name");

        return client;
    }
}
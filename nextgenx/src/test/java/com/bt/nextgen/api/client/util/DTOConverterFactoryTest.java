package com.bt.nextgen.api.client.util;

import com.bt.nextgen.service.avaloq.client.ClientListImpl;
import com.bt.nextgen.service.avaloq.domain.CompanyImpl;
import com.bt.nextgen.service.avaloq.domain.IndividualImpl;
import com.bt.nextgen.service.avaloq.domain.SmsfImpl;
import com.bt.nextgen.service.avaloq.domain.TrustImpl;
import com.bt.nextgen.service.avaloq.domain.existingclient.IndividualWithAccountDataImpl;
import com.bt.nextgen.service.avaloq.domain.existingclient.LegalClient;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class DTOConverterFactoryTest {

    @Test
    public void shouldContainConverterForIndividual() throws Exception {
        assertThat(DTOConverterFactory.getConverter(new IndividualImpl()), instanceOf(IndividualDtoConverter.class));
    }

    @Test
    public void shouldContainConverterForSMSF() throws Exception {
        assertThat(DTOConverterFactory.getConverter(new SmsfImpl()), instanceOf(ClientDtoConverter.class));
    }

    @Test
    public void shouldContainConverterForTrust() throws Exception {
        assertThat(DTOConverterFactory.getConverter(new TrustImpl()), instanceOf(ClientDtoConverter.class));
    }

    @Test
    public void shouldContainConverterForCompany() throws Exception {
        assertThat(DTOConverterFactory.getConverter(new CompanyImpl()), instanceOf(ClientDtoConverter.class));
    }

    @Test
    public void shouldContainConverterForClientList() throws Exception {
        assertThat(DTOConverterFactory.getConverter(new ClientListImpl()), instanceOf(ClientDtoConverter.class));
    }

    @Test
    public void shouldContainConverterForLegalClient() throws Exception {
        assertThat(DTOConverterFactory.getConverter(new LegalClient()), instanceOf(ExistingClientSearchDtoConverter.class));
    }

    @Test
    public void shouldContainConverterForIndividualWithAccountData() throws Exception {
        assertThat(DTOConverterFactory.getConverter(new IndividualWithAccountDataImpl()), instanceOf(IndividualWithAdvisersDtoConverter.class));
    }
}
package com.bt.nextgen.api.client.util;

import com.bt.nextgen.api.client.model.AddressDto;
import com.bt.nextgen.api.client.model.IndividualWithAdvisersDto;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.avaloq.domain.existingclient.IndividualWithAccountDataImpl;
import com.bt.nextgen.service.integration.userinformation.ClientKey;
import com.bt.nextgen.service.integration.domain.Address;
import com.bt.nextgen.service.integration.domain.IdentityVerificationStatus;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsCollectionContaining.hasItems;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class IndividualWithAdvisersDtoConverterTest {

    private IndividualWithAdvisersDtoConverter dtoConverter;

    @Before
    public void setUp(){
        dtoConverter = new IndividualWithAdvisersDtoConverter();
    }

    @Test
    public void shouldSetAllPropertiesOnTheResultDto(){
        IndividualWithAccountDataImpl individualModel = mock(IndividualWithAccountDataImpl.class);
        when(individualModel.getIdentityVerificationStatus()).thenReturn(IdentityVerificationStatus.getIdentityVerificationStatus("compl"));
        when(individualModel.getFirstName()).thenReturn("TestFirstName");
        when(individualModel.getLastName()).thenReturn("TestLastName");
        when(individualModel.getFullName()).thenReturn("TestFirstName TestLastName ASD");
        when(individualModel.getAdviserPositionIds()).thenReturn(Arrays.asList("123", "567"));
        when(individualModel.getDateOfBirth()).thenReturn(new DateTime(1980, 3, 3, 0, 0));
        when(individualModel.getClientKey()).thenReturn(ClientKey.valueOf("CLIENTID"));

        Address address = mock(Address.class);
        when(address.getSuburb()).thenReturn("SYDNEY");
        when(address.getState()).thenReturn("NSW");
        when(address.getPostCode()).thenReturn("2000");
        when(address.getCountry()).thenReturn("Australia");

        when(individualModel.getAddresses()).thenReturn(Arrays.asList(address));

        IndividualWithAdvisersDto resultDto = (IndividualWithAdvisersDto) dtoConverter.toDTO(individualModel);

        assertThat(true, is(resultDto.isIdVerified()));
        assertThat(individualModel.getFirstName(), is(resultDto.getFirstName()));
        assertThat(individualModel.getLastName(), is(resultDto.getLastName()));
        assertThat("TestLastName, TestFirstName", is(resultDto.getDisplayName()));
        assertThat(individualModel.getAdviserPositionIds(), hasItems("123", "567"));
        AddressDto addressDto = resultDto.getAddresses().get(0);
        assertThat("SYDNEY", is(addressDto.getSuburb()));
        assertThat("NSW", is(addressDto.getState()));
        assertThat("2000", is(addressDto.getPostcode()));
        assertThat("Australia", is(addressDto.getCountry()));
        assertThat("CLIENTID", is(EncodedString.toPlainText(resultDto.getKey().getClientId())));
        assertThat(resultDto.getDateOfBirth(), containsString("1980-03-03"));
        assertThat("Individual", is(resultDto.getInvestorType()));
    }
}
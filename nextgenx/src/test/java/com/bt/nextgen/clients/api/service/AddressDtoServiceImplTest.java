package com.bt.nextgen.clients.api.service;

import com.bt.nextgen.address.service.AddressValidationService;
import com.bt.nextgen.address.service.ParseAustralianAddressResponseService;
import com.bt.nextgen.clients.api.model.AddressDto;
import com.bt.nextgen.core.web.model.Address;
import ns.btfin_com.party.partyservice.partyreply.v2_1.ParseAustralianAddressResponseMsgType;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

@RunWith(MockitoJUnitRunner.class)
public class AddressDtoServiceImplTest {

    @InjectMocks
    private AddressDtoServiceImpl addressDtoService;

    @Mock
    private ParseAustralianAddressResponseService parseAustralianAddressResponseService;

    @Mock
    private AddressValidationService addressValidationService;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    @Test
    public void shouldValidateAustralianAddress() throws Exception {
        ArgumentCaptor<Address> addressArgCaptor = ArgumentCaptor.forClass(Address.class);
        AddressDto addressDto = new AddressDto();
        addressDto.setCountry("AU");
        ParseAustralianAddressResponseMsgType mockAddressResponseMsgType = Mockito.mock(ParseAustralianAddressResponseMsgType.class);
        Mockito.when(addressValidationService.validateAddress(addressArgCaptor.capture())).thenReturn(mockAddressResponseMsgType);
        Mockito.when(parseAustralianAddressResponseService.getAddressFromParseAustralianAddressResponse(mockAddressResponseMsgType)).thenReturn(new Address());

        AddressDto responseAddressDto = addressDtoService.validateAustralianAddress(addressDto);

        assertThat(responseAddressDto.getCountry(), is("AU"));
        verify(addressValidationService, times(1)).validateAddress(addressArgCaptor.getValue());
        verify(parseAustralianAddressResponseService).getAddressFromParseAustralianAddressResponse(mockAddressResponseMsgType);
    }

    @Test
    public void shouldNotValidateNonAustralianAddress() throws Exception {
        AddressDto addressDto = new AddressDto();
        addressDto.setCountry("IN");
        AddressDto responseAddress = addressDtoService.validateAustralianAddress(addressDto);

        assertThat(responseAddress, is(addressDto));
        verifyZeroInteractions(addressValidationService, parseAustralianAddressResponseService);
    }

}
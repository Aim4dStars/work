package com.bt.nextgen.address.service;

import com.btfin.panorama.core.security.saml.BankingAuthorityService;
import com.btfin.panorama.core.security.saml.SamlToken;
import com.bt.nextgen.core.web.model.Address;
import com.bt.nextgen.core.webservice.provider.WebServiceProvider;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import ns.btfin_com.party.partyservice.partyreply.v2_1.ParseAustralianAddressResponseMsgType;
import ns.btfin_com.party.partyservice.partyreply.v2_1.StatusTypeCode;
import ns.btfin_com.party.partyservice.partyrequest.v2_1.ParseAustralianAddressRequestMsgType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AddressValidationServiceImplTest {

    @InjectMocks
    private AddressValidationServiceImpl addressValidationService;

    @Mock
    private BankingAuthorityService userSamlService;

    @Mock
    private WebServiceProvider provider;

    @Mock
    private ParseAustralianAddressResponseMsgType response;

    @Before
    public void setUp() throws Exception {
        when(response.getStatus()).thenReturn(StatusTypeCode.SUCCESS);
        when(provider.sendWebServiceWithSecurityHeader(any(SamlToken.class), eq(Attribute.PARTY_KEY), any(ParseAustralianAddressRequestMsgType.class))).thenReturn(response);
    }

    @Test
    public void testValidateAddress() throws Exception
    {
        Address addressModel = new Address();
        ParseAustralianAddressResponseMsgType addressResponseMsgType  = addressValidationService.validateAddress(addressModel);
        assertThat(addressResponseMsgType, notNullValue());
        assertThat(addressResponseMsgType.getStatus(), is(StatusTypeCode.SUCCESS));

    }
}
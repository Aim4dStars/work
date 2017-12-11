package com.bt.nextgen.address.service;

import com.btfin.panorama.core.security.saml.BankingAuthorityService;
import com.bt.nextgen.core.util.Properties;
import com.bt.nextgen.core.web.model.Address;
import com.bt.nextgen.core.webservice.provider.WebServiceProvider;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import ns.btfin_com.party.partyservice.partyreply.v2_1.*;
import ns.btfin_com.party.partyservice.partyrequest.v2_1.ParseAustralianAddressRequestMsgType;
import ns.btfin_com.party.partyservice.partyrequest.v2_1.PartyRequestRequestContextType;
import ns.btfin_com.sharedservices.common.address.v2_4.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class AddressValidationServiceImpl implements AddressValidationService {

    private static final String VERSION = "party.context.version";
    private static final String RESPONSE_VERSION = "party.context.responseVersion";
    private static final String SUBMITTER = "party.context.submitter";
    private static final String REQUESTING_SYSTEM = "party.context.requestingSystem";
    private static final String TRACKING_ID = "party.context.trackingID";


    @Resource(name = "userDetailsService")
    private BankingAuthorityService userSamlService;

    @Autowired
    private WebServiceProvider provider;

    @Override
    public ParseAustralianAddressResponseMsgType validateAddress(Address address) {
        ParseAustralianAddressRequestMsgType request = createParseAustralianAddressRequestMsg(address);
        return (ParseAustralianAddressResponseMsgType) provider
                .sendWebServiceWithSecurityHeader(userSamlService.getSamlToken(), Attribute.PARTY_KEY, request);
    }


    private ParseAustralianAddressRequestMsgType createParseAustralianAddressRequestMsg(Address address) {
        ns.btfin_com.party.partyservice.partyrequest.v2_1.ObjectFactory of = new ns.btfin_com.party.partyservice.partyrequest.v2_1.ObjectFactory();

        ParseAustralianAddressRequestMsgType request = of.createParseAustralianAddressRequestMsgType();

        PartyRequestRequestContextType context = new PartyRequestRequestContextType();
        context.setVersion(Properties.get(VERSION));
        context.setResponseVersion(Properties.get(RESPONSE_VERSION));
        context.setSubmitter(Properties.get(SUBMITTER));
        context.setRequestingSystem(Properties.get(REQUESTING_SYSTEM));
        context.setTrackingID(Properties.get(TRACKING_ID));

        AustralianAddressType australianAddressType = new AustralianAddressType();
        StructuredAddressDetailType structuredAddressDetailType = new StructuredAddressDetailType();
        AddressTypeDetailType addressTypeDetailType = new AddressTypeDetailType();

        NonStandardAddressType nonStandardAddressType = new NonStandardAddressType();
        nonStandardAddressType.getAddressLine().add(address.getAddressLine1());
        nonStandardAddressType.getAddressLine().add(address.getAddressLine2());
        addressTypeDetailType.setNonStandardAddress(nonStandardAddressType);

        structuredAddressDetailType.setAddressTypeDetail(addressTypeDetailType);
        structuredAddressDetailType.setCity(address.getCity());
        structuredAddressDetailType.setCountryCode("AUS");
        structuredAddressDetailType.setPostcode(address.getPin());
        structuredAddressDetailType.setState(address.getState());
        australianAddressType.setStructuredAddressDetail(structuredAddressDetailType);

        request.setContext(context);
        request.setAddress(australianAddressType);
        return request;
    }
}

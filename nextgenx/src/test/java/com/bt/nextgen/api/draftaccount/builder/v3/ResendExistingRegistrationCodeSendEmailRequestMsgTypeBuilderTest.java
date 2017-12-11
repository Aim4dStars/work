package com.bt.nextgen.api.draftaccount.builder.v3;

import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;
import ns.btfin_com.party.v3_0.CustomerNoAllIssuerType;
import ns.btfin_com.product.panorama.credentialservice.credentialrequest.v1_0.CommunicationMediumType;
import ns.btfin_com.product.panorama.credentialservice.credentialrequest.v1_0.CreateOTPCommunicationDetailsType;
import ns.btfin_com.product.panorama.credentialservice.credentialrequest.v1_0.CreateOTPPartyDetailsType;
import ns.btfin_com.product.panorama.credentialservice.credentialrequest.v1_0.POARoleTypeType;
import ns.btfin_com.product.panorama.credentialservice.credentialrequest.v1_0.ResendExistingRegistrationCodeRequestMsgType;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.bt.nextgen.api.client.model.ClientKey;
import com.bt.nextgen.api.client.model.EmailDto;
import com.bt.nextgen.api.client.model.IndividualDto;
import com.bt.nextgen.api.client.model.PhoneDto;
import com.bt.nextgen.api.client.service.ClientListDtoService;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.domain.EmailImpl;
import com.bt.nextgen.service.avaloq.domain.PhoneImpl;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.broker.BrokerUser;
import com.bt.nextgen.service.integration.domain.AddressMedium;
import com.bt.nextgen.service.integration.domain.Email;
import com.bt.nextgen.service.integration.domain.Phone;
import com.bt.nextgen.service.integration.user.UserKey;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import com.btfin.panorama.test.schema.AbstractSchemaValidatorTest;

@RunWith(MockitoJUnitRunner.class)
public class ResendExistingRegistrationCodeSendEmailRequestMsgTypeBuilderTest extends AbstractSchemaValidatorTest {

    @Mock
    private ClientListDtoService clientListDtoService;

    @Mock
    private BrokerIntegrationService brokerIntegrationService;

    private ResendExistingRegistrationCodeSendEmailRequestMsgTypeBuilderV3 builder;

    @Mock
    private BrokerUser brokerUser;

    private String gcmId;
    private String cisId;
    private String customerNumber;
    private String clientId;
    private String adviserPositionId;
    private ServiceErrors serviceErrors;
    private IndividualDto individualDto;

    public ResendExistingRegistrationCodeSendEmailRequestMsgTypeBuilderTest() {
        super("schema/btesb/BTFin/Product/Panorama/CredentialService/V1/CredentialRequestV1_0.xsd");
    }

    @Before
    public void initBuilderAndMocks() throws Exception {
        builder = new ResendExistingRegistrationCodeSendEmailRequestMsgTypeBuilderV3();
        builder.setPoaPartyDetailsTypeBuilder(new POAPartyDetailsTypeBuilder());
        builder.setClientListDtoService(clientListDtoService);
        builder.setBrokerIntegrationService(brokerIntegrationService);

        clientId = "123";
        adviserPositionId = "456";
        gcmId = "1234567";
        cisId = "68321000053";
        customerNumber = "92773951";
        serviceErrors = new FailFastErrorsImpl();
        individualDto = new IndividualDto();
        individualDto.setGcmId(gcmId);
        individualDto.setFirstName("Jeremiah");
        individualDto.setMiddleName("Jiggery");
        individualDto.setLastName("Pokery");
        individualDto.setPreferredName("Jezza");
        individualDto.setPhones(singletonList(phone("61", "4", "18098778")));
        individualDto.setEmails(singletonList(email("jjp@gmail.com")));

        when(brokerIntegrationService.getAdviserBrokerUser(eq(BrokerKey.valueOf(adviserPositionId)), eq(serviceErrors))).thenReturn(brokerUser);
        when(clientListDtoService.find(any(ClientKey.class), eq(serviceErrors))).thenReturn(individualDto);
        when(brokerIntegrationService.getBrokerUser(eq(UserKey.valueOf(gcmId)), eq(serviceErrors))).thenReturn(brokerUser);
        when(brokerUser.getFirstName()).thenReturn("Bob");
        when(brokerUser.getLastName()).thenReturn("Broker");
        when(brokerUser.getEmails()).thenReturn(singletonList(email("advice@free.org", AddressMedium.EMAIL_PRIMARY)));
        when(brokerUser.getPhones()).thenReturn(singletonList(phone("61", "3", "82542340", AddressMedium.BUSINESS_TELEPHONE)));
    }

    @Test
    public void shouldContainThePartyDetailsContainingTheGcmIdOfTheClient() throws Exception {
        ResendExistingRegistrationCodeRequestMsgType request = builder.build(clientId, adviserPositionId, Attribute.INVESTOR, serviceErrors);
        CreateOTPPartyDetailsType partyDetails = request.getPartyDetails();

        assertThat(partyDetails.getCustomerIdentifier().getCustomerNumberIdentifier().getCustomerNumber(), is(gcmId));
        assertThat(partyDetails.getCustomerIdentifier().getCustomerNumberIdentifier().getCustomerNumberIssuer(), is(CustomerNoAllIssuerType.BT_PANORAMA));

        assertThat(partyDetails.getRoleType(), is(POARoleTypeType.CLIENT));
        validateJaxb(request);
    }

    @Test
    public void shouldSetTheInvestorDetailsIntermediaryDetailsAndCommunicationMediumOfCommunicationDetails() throws Exception {
        ResendExistingRegistrationCodeRequestMsgType request = builder.build(clientId, adviserPositionId, Attribute.INVESTOR, serviceErrors);
        CreateOTPCommunicationDetailsType communicationDetails = request.getCommunicationDetails();

        assertThat(communicationDetails.getCommunicationMedium(), is(CommunicationMediumType.EMAIL));
        assertThat(communicationDetails.getInvestorDetails(), is(notNullValue()));
        assertThat(communicationDetails.getIntermediaryDetails(), is(notNullValue()));
        validateJaxb(request);
    }

    @Test
    public void shouldContainThePartyDetailsContainingTheGcmIdOfAdviser() throws Exception {
        ResendExistingRegistrationCodeRequestMsgType request = builder.buildForAdviser(gcmId, Attribute.ADVISER, serviceErrors);
        CreateOTPPartyDetailsType partyDetails = request.getPartyDetails();

        assertThat(partyDetails.getCustomerIdentifier().getCustomerNumberIdentifier().getCustomerNumber(), is(gcmId));
        assertThat(partyDetails.getCustomerIdentifier().getCustomerNumberIdentifier().getCustomerNumberIssuer(), is(CustomerNoAllIssuerType.BT_PANORAMA));
        assertThat(partyDetails.getRoleType(), is(POARoleTypeType.ADVISER));
        validateJaxb(request);
    }

    @Test
    public void shouldSetIntermediaryDetailsAndCommunicationDetailsForAdviser() throws Exception {
        ResendExistingRegistrationCodeRequestMsgType request = builder.buildForAdviser(gcmId, Attribute.ADVISER, serviceErrors);
        CreateOTPCommunicationDetailsType communicationDetails = request.getCommunicationDetails();
        CreateOTPPartyDetailsType createOTPPartyDetails = request.getPartyDetails();

        assertThat(communicationDetails.getCommunicationMedium(), is(CommunicationMediumType.EMAIL));
        assertThat(createOTPPartyDetails, is(notNullValue()));
        assertThat(communicationDetails.getIntermediaryDetails(), is(notNullValue()));
        validateJaxb(request);
    }

    private static PhoneDto phone(String countryCode, String areaCode, String number, String type) {
        final PhoneDto phone = new PhoneDto();
        phone.setCountryCode(countryCode);
        phone.setAreaCode(areaCode);
        phone.setNumber(number);
        phone.setPhoneType(type);
        return phone;
    }

    private static PhoneDto phone(String countryCode, String areaCode, String number) {
        return phone(countryCode, areaCode, number, AddressMedium.MOBILE_PHONE_PRIMARY.getAddressType());
    }

    private static Phone phone(String countryCode, String areaCode, String number, AddressMedium type) {
        PhoneImpl phone = new PhoneImpl();
        phone.setCountryCode(countryCode);
        phone.setAreaCode(areaCode);
        phone.setNumber(number);
        phone.setType(type);
        return phone;
    }

    private static EmailDto email(String email, String type) {
        final EmailDto emailDto = new EmailDto();
        emailDto.setEmail(email);
        emailDto.setEmailType(type);
        return emailDto;
    }

    private static EmailDto email(String email) {
        return email(email, AddressMedium.EMAIL_PRIMARY.getAddressType());
    }

    private static Email email(String address, AddressMedium type) {
        final EmailImpl email = new EmailImpl();
        email.setEmail(address);
        email.setType(type);
        return email;
    }
}

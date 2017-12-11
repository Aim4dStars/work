package com.bt.nextgen.api.draftaccount.service;

import com.bt.nextgen.api.client.model.ClientKey;
import com.bt.nextgen.api.client.model.EmailDto;
import com.bt.nextgen.api.client.model.IndividualDto;
import com.bt.nextgen.api.client.model.PhoneDto;
import com.bt.nextgen.api.client.service.ClientListDtoService;
import com.bt.nextgen.api.draftaccount.builder.CreateOneTimePasswordSendEmailRequestMsgTypeBuilder;
import com.bt.nextgen.api.draftaccount.builder.v3.CreateOneTimePasswordSendEmailRequestMsgTypeBuilderV3;
import com.bt.nextgen.api.draftaccount.builder.v3.POAPartyDetailsTypeBuilder;
import com.bt.nextgen.api.draftaccount.builder.v3.ResendExistingRegistrationCodeSendEmailRequestMsgTypeBuilderV3;
import com.bt.nextgen.api.draftaccount.model.SendEmailDto;
import com.bt.nextgen.api.draftaccount.util.ResendRegistrationCodeTransactor;
import com.bt.nextgen.core.repository.OnBoardingApplication;
import com.bt.nextgen.core.repository.OnboardingApplicationRepository;
import com.bt.nextgen.core.repository.OnboardingCommunication;
import com.btfin.panorama.core.security.saml.BankingAuthorityService;
import com.btfin.panorama.core.security.saml.SamlToken;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.core.service.DateTimeServiceImpl;
import com.bt.nextgen.core.webservice.provider.CorrelatedResponse;
import com.bt.nextgen.core.webservice.provider.ESBHeaderInformationWrapper;
import com.bt.nextgen.core.webservice.provider.WebServiceProvider;
import com.bt.nextgen.draftaccount.repository.ClientApplication;
import com.bt.nextgen.draftaccount.repository.ClientApplicationRepository;
import com.bt.nextgen.draftaccount.repository.PermittedClientApplicationRepository;
import com.bt.nextgen.service.ServiceError;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.accountactivation.OnboardingApplicationKey;
import com.btfin.panorama.service.integration.broker.BrokerIdentifier;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.broker.BrokerUser;
import com.bt.nextgen.service.integration.domain.AddressMedium;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import ns.btfin_com.party.v3_0.CustomerNoAllIssuerType;
import ns.btfin_com.product.panorama.credentialservice.credentialrequest.v1_0.CreateOTPCommunicationDetailsType;
import ns.btfin_com.product.panorama.credentialservice.credentialrequest.v1_0.CreateOneTimePasswordSendEmailRequestMsgType;
import ns.btfin_com.product.panorama.credentialservice.credentialrequest.v1_0.POAInvolvedPartyDetailsType;
import ns.btfin_com.product.panorama.credentialservice.credentialrequest.v1_0.ResendExistingRegistrationCodeRequestMsgType;
import ns.btfin_com.product.panorama.credentialservice.credentialresponse.v1_0.*;
import ns.btfin_com.sharedservices.common.contact.v1_1.EmailAddressDetailType;
import ns.btfin_com.sharedservices.common.contact.v1_1.EmailAddressType;
import ns.btfin_com.sharedservices.common.contact.v1_1.EmailAddressesType;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import javax.persistence.EntityExistsException;
import javax.persistence.PersistenceException;
import javax.xml.bind.JAXBElement;
import java.util.*;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SendEmailServiceImplTest {

    @InjectMocks
    private SendEmailServiceImpl sendEmailService;

    @Mock
    private WebServiceProvider provider;

    @Mock
    private OnboardingApplicationRepository applicationRepository;

    @Mock
    private BankingAuthorityService userSamlService;

    @Mock
    private CreateOneTimePasswordSendEmailRequestMsgTypeBuilder createOneTimePasswordSendEmailRequestMsgTypeBuilder;

    @Mock
    private CreateOneTimePasswordSendEmailRequestMsgTypeBuilder resendExistingRegistrationCodeSendEmailRequestMsgTypeBuilder;

    @Mock
    private ResendRegistrationCodeTransactor resendRegistrationCodeTransactor;

    @Mock
    private PermittedClientApplicationRepository permittedClientApplicationRepository;

    @Mock
    private UserProfileService userProfileService;

    @Mock
    private DateTimeServiceImpl dateTimeService;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Mock
    private ClientApplicationRepository clientApplicationRepository;


    private final String EMAIL_ADDRESS = "test@email.com";
    private final String clientId = "TEST_CLIENT_ID";
    private final String adviserId = "TEST_ADV_ID";
    private final Long clientApplicationId = 1234L;
    private final Long onboardingApplicationId = 12124L;
    private final String communicationId = "MY_COMMUNICATION_ID";
    private final String customerNumber = "MY_CUSTOMER_NUMBER";
    private final String errorSubCode = "TEST_SUB_CODE";
    private Date requestDate;
    private SamlToken token;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp(){

        requestDate = new Date();
        token = new SamlToken(null);

        ClientListDtoService clientListDtoService = mock(ClientListDtoService.class);

        IndividualDto individualDto = new IndividualDto();

        PhoneDto phone1 = new PhoneDto();
        phone1.setPhoneType(AddressMedium.MOBILE_PHONE_PRIMARY.getAddressType());
        phone1.setNumber("04122222");

        EmailDto email1 = new EmailDto();
        email1.setEmail("test@test.com");
        email1.setEmailType(AddressMedium.EMAIL_PRIMARY.getAddressType());

        individualDto.setPhones(Collections.singletonList(phone1));
        individualDto.setEmails(Collections.singletonList(email1));

        when(clientListDtoService.findWithoutRelatedAccounts(any(ClientKey.class), any(ServiceErrors.class))).thenReturn(individualDto);

        BrokerIntegrationService brokerIntegrationService = mock(BrokerIntegrationService.class);

        BrokerUser brokerUser = mock(BrokerUser.class);

        when(brokerIntegrationService.getAdviserBrokerUser(any(BrokerKey.class), any(ServiceErrors.class))).thenReturn(brokerUser);

        final CreateOneTimePasswordSendEmailRequestMsgTypeBuilderV3 realBuilder = new CreateOneTimePasswordSendEmailRequestMsgTypeBuilderV3();
        realBuilder.setClientListDtoService(clientListDtoService);
        realBuilder.setBrokerIntegrationService(brokerIntegrationService);
        realBuilder.setPoaPartyDetailsTypeBuilder(new POAPartyDetailsTypeBuilder());

        when(createOneTimePasswordSendEmailRequestMsgTypeBuilder.isSuccessful(any())).then(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                return realBuilder.isSuccessful((CreateOneTimePasswordSendEmailResponseMsgType) invocation.getArguments()[0]);
            }
        });

        when(createOneTimePasswordSendEmailRequestMsgTypeBuilder.build(anyString(), anyString(), anyString(), any(ServiceErrors.class))).then(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                return realBuilder.build((String) invocation.getArguments()[0], (String) invocation.getArguments()[1], (String) invocation.getArguments()[2], (ServiceErrors) invocation.getArguments()[3]);
            }
        });

        when(createOneTimePasswordSendEmailRequestMsgTypeBuilder.extractCommunicationDetails(any())).then(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                return realBuilder.extractCommunicationDetails((CreateOneTimePasswordSendEmailResponseMsgType) invocation.getArguments()[0]);
            }
        });

        when(createOneTimePasswordSendEmailRequestMsgTypeBuilder.getErrorResponses(any())).then(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                return realBuilder.getErrorResponses((CreateOneTimePasswordSendEmailResponseMsgType) invocation.getArguments()[0]);
            }
        });

        when(createOneTimePasswordSendEmailRequestMsgTypeBuilder.wrap(Matchers.any())).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                return realBuilder.wrap((CreateOneTimePasswordSendEmailRequestMsgType) invocationOnMock.getArguments()[0]);
            }
        });

        final CreateOneTimePasswordSendEmailRequestMsgTypeBuilder resendExistingCodeRealBuilder = new ResendExistingRegistrationCodeSendEmailRequestMsgTypeBuilderV3();

        when(resendExistingRegistrationCodeSendEmailRequestMsgTypeBuilder.isSuccessful(any())).then(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                return resendExistingCodeRealBuilder.isSuccessful(invocation.getArguments()[0]);
            }
        });

        when(resendExistingRegistrationCodeSendEmailRequestMsgTypeBuilder.extractCommunicationDetails(any())).then(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                return resendExistingCodeRealBuilder.extractCommunicationDetails(invocation.getArguments()[0]);
            }
        });

        when(resendExistingRegistrationCodeSendEmailRequestMsgTypeBuilder.getErrorResponses(any())).then(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                return resendExistingCodeRealBuilder.getErrorResponses(invocation.getArguments()[0]);
            }
        });

        when(userSamlService.getSamlToken()).thenReturn(token);
        when(dateTimeService.getCurrentDateTime()).thenCallRealMethod();
    }

    @Test
    public void testResendRegistrationCodeIsCalledAndCommunicationIdIsInserted() throws Exception {
        ClientApplication clientApplication = new ClientApplication();
        clientApplication.setAdviserPositionId(adviserId);
        when(permittedClientApplicationRepository.find(clientApplicationId)).thenReturn(clientApplication);
        OnBoardingApplication onBoardingApplication = mock( OnBoardingApplication.class);
        when(onBoardingApplication.getKey()).thenReturn(OnboardingApplicationKey.valueOf(onboardingApplicationId));
        clientApplication.setOnboardingApplication(onBoardingApplication);

        ResendExistingRegistrationCodeRequestMsgType request = buildResendExistingCodeRequestMsgType();
        ServiceErrors serviceErrors = new ServiceErrorsImpl();

        when(resendExistingRegistrationCodeSendEmailRequestMsgTypeBuilder.build(clientId, adviserId, Attribute.INVESTOR, serviceErrors)).thenReturn(request);
        when(resendExistingRegistrationCodeSendEmailRequestMsgTypeBuilder.extractInvestorEmailAddress(request)).thenReturn(EMAIL_ADDRESS);

        when(userProfileService.getGcmId()).thenReturn("MY_GCM_ID");

        //emulate response for resend existing code operation
        ResendExistingRegistrationCodeResponseDetailsType responseDetails = buildResendExistingCodeResponseDetailsTypeWithSuccessResponse();
        CorrelatedResponse correlatedResponse = createResendExistingCodeCorrelatedResponse(requestDate, responseDetails, StatusTypeCode.SUCCESS);
        when(provider.sendWebServiceWithSecurityHeaderAndResponseCallbackWithHeaderValues(eq(token), eq(Attribute.RESEND_EXISTING_REGISTRATION_CODE_KEY), Matchers.<JAXBElement<ResendExistingRegistrationCodeRequestMsgType>>any(), any(ServiceErrors.class))).thenReturn(correlatedResponse);

        ArgumentCaptor<OnboardingCommunication> communicationArgumentCaptor = ArgumentCaptor.forClass(OnboardingCommunication.class);

        SendEmailDto sendEmailDto = new SendEmailDto(clientApplicationId, clientId);
        sendEmailDto.setRole(Attribute.INVESTOR);
        sendEmailDto = sendEmailService.submit(sendEmailDto, serviceErrors);

        verify(resendRegistrationCodeTransactor, times(1)).save(communicationArgumentCaptor.capture());
        OnboardingCommunication savedCommunication = communicationArgumentCaptor.getValue();
        assertThat(savedCommunication.getCommunicationInitiationTime(), is(requestDate));
        assertThat(savedCommunication.getCommunicationId(), is(communicationId));
        assertThat(savedCommunication.getGcmPan(), is(customerNumber));
        assertThat(savedCommunication.getOnboardingApplicationId(), is(onboardingApplicationId));
        assertThat(savedCommunication.getEmailAddress(), is(EMAIL_ADDRESS));
        assertThat(savedCommunication.getLastModifiedId(), is("MY_GCM_ID"));
        assertThat(sendEmailDto.getStatus(), is(StatusTypeCode.SUCCESS.value()));
    }

    @Test
    public void testIfServiceErrorsUpdatedWhenNullPointerExceptionIsThrown() {
        String expectedErrorMessage = "Exception while creating onboarding communication object.Null pointer exception";
        NullPointerException nullPointerException = new NullPointerException("Null pointer exception");
        shouldAssertServiceErrorWithGivenExceptionAndMessage(expectedErrorMessage, nullPointerException);
    }

    @Test
    public void testIfServiceErrorsUpdatedWhenPersistenceExceptionIsThrown() {
        String expectedErrorMessage = "Error while inserting communication details.Persistence exception";
        PersistenceException persistenceException = new PersistenceException("Persistence exception");
        shouldAssertServiceErrorWithGivenExceptionAndMessage(expectedErrorMessage, persistenceException);
    }

    @Test
    public void testIfServiceErrorsUpdatedWhenEntityExceptionIsThrown() {
        String expectedErrorMessage = "Communication id already exists. Primary key violation in Onboarding_Communication table.Entity exists exception";
        EntityExistsException entityExistsException = new EntityExistsException("Entity exists exception");
        shouldAssertServiceErrorWithGivenExceptionAndMessage(expectedErrorMessage,entityExistsException);
    }

    @Test
    public void testIfServiceErrorsUpdatedWhenResponseIsNotSuccessful() {

        ClientApplication clientApplication = new ClientApplication();
        clientApplication.setAdviserPositionId(adviserId);
        when(permittedClientApplicationRepository.find(clientApplicationId)).thenReturn(clientApplication);
        OnBoardingApplication onBoardingApplication = mock( OnBoardingApplication.class);
        when(onBoardingApplication.getKey()).thenReturn(OnboardingApplicationKey.valueOf(onboardingApplicationId));
        clientApplication.setOnboardingApplication(onBoardingApplication);

        ResendExistingRegistrationCodeRequestMsgType request = buildResendExistingCodeRequestMsgType();
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        when(resendExistingRegistrationCodeSendEmailRequestMsgTypeBuilder.build(clientId, adviserId, Attribute.INVESTOR, serviceErrors)).thenReturn(request);
        when(resendExistingRegistrationCodeSendEmailRequestMsgTypeBuilder.extractInvestorEmailAddress(request)).thenReturn(EMAIL_ADDRESS);



        ResendExistingRegistrationCodeResponseDetailsType responseDetails = buildResendExistingCodeResponseDetailsTypeWithErrorResponse();
        CorrelatedResponse correlatedResponse = createResendExistingCodeCorrelatedResponse(requestDate, responseDetails, StatusTypeCode.ERROR);
        when(provider.sendWebServiceWithSecurityHeaderAndResponseCallbackWithHeaderValues(eq(token), eq(Attribute.RESEND_EXISTING_REGISTRATION_CODE_KEY), Matchers.<JAXBElement<ResendExistingRegistrationCodeRequestMsgType>>any(), any(ServiceErrors.class))).thenReturn(correlatedResponse);

        SendEmailDto sendEmailDto = new SendEmailDto(clientApplicationId, clientId);
        sendEmailDto.setRole(Attribute.INVESTOR);

        sendEmailService.submit(sendEmailDto, serviceErrors);
        assertTrue(serviceErrors.hasErrors());
        assertThat(serviceErrors.getError(errorSubCode).getId(),is(errorSubCode));
    }

    @Test
    public void testSendEmailFromServiceOpsDesktopForInvestor() {
        //emulate response for sending new rego code operation
        CreateOneTimePasswordSendEmailResponseDetailsType newRegoResponseDetails = buildCreateOneTimePasswordSendEmailResponseDetailsTypeWithSuccessResponse();
        CorrelatedResponse newRegoCorrelatedResponse = createCorrelatedResponse(requestDate, newRegoResponseDetails, StatusTypeCode.SUCCESS);
        when(provider.sendWebServiceWithSecurityHeaderAndResponseCallbackWithHeaderValues(eq(token), eq(Attribute.RESEND_REGISTRATION_ONBOARDING_KEY), any(JAXBElement.class), any(ServiceErrors.class))).thenReturn(newRegoCorrelatedResponse);

        String clientId = "1234";
        String adviserPositionId = "45678";
        String role = Attribute.INVESTOR;
        String status = sendEmailService.sendEmailFromServiceOpsDesktopForInvestor(clientId, adviserPositionId, role, new FailFastErrorsImpl());
        assertEquals("Success", status);
    }

    @Test
    public void testSendEmailFromServiceOpsDesktopForAdviser() {
        //emulate response for sending new rego code operation
        CreateOneTimePasswordSendEmailResponseDetailsType newRegoResponseDetails = buildCreateOneTimePasswordSendEmailResponseDetailsTypeWithSuccessResponse();
        CorrelatedResponse newRegoCorrelatedResponse = createCorrelatedResponse(requestDate, newRegoResponseDetails, StatusTypeCode.SUCCESS);
        when(provider.sendWebServiceWithSecurityHeaderAndResponseCallbackWithHeaderValues(eq(token), eq(Attribute.RESEND_REGISTRATION_ONBOARDING_KEY), any(JAXBElement.class), any(ServiceErrors.class))).thenReturn(newRegoCorrelatedResponse);

        String gcmId = "1234";
        String role = Attribute.ADVISER;
        String status = sendEmailService.sendEmailFromServiceOpsDesktopForAdviser(gcmId, role, new FailFastErrorsImpl());
        assertEquals("Success", status);
    }

    @Test
    public void testSendEmailWithExistingRegoCodeForInvestor() {
        ResendExistingRegistrationCodeResponseDetailsType responseDetails = buildResendExistingCodeResponseDetailsTypeWithSuccessResponse();
        CorrelatedResponse correlatedResponse = createResendExistingCodeCorrelatedResponse(requestDate, responseDetails, StatusTypeCode.SUCCESS);
        when(provider.sendWebServiceWithSecurityHeaderAndResponseCallbackWithHeaderValues(eq(token), eq(Attribute.RESEND_EXISTING_REGISTRATION_CODE_KEY), any(JAXBElement.class), any(ServiceErrors.class))).thenReturn(correlatedResponse);

        String clientId = "1234";
        String adviserPositionId = "45678";
        String role = Attribute.INVESTOR;

        String status = sendEmailService.sendEmailWithExistingRegoCodeForInvestor(clientId, adviserPositionId, role, new FailFastErrorsImpl());
        assertEquals("Success", status);
    }

    @Test
    public void testSendEmailWithExistingRegoCodeForAdviser() {
        ResendExistingRegistrationCodeResponseDetailsType responseDetails = buildResendExistingCodeResponseDetailsTypeWithSuccessResponse();
        CorrelatedResponse correlatedResponse = createResendExistingCodeCorrelatedResponse(requestDate, responseDetails, StatusTypeCode.SUCCESS);
        when(provider.sendWebServiceWithSecurityHeaderAndResponseCallbackWithHeaderValues(eq(token), eq(Attribute.RESEND_EXISTING_REGISTRATION_CODE_KEY), any(JAXBElement.class), any(ServiceErrors.class))).thenReturn(correlatedResponse);

        String gcmId = "1234";
        String role = Attribute.ADVISER;
        String status = sendEmailService.sendEmailWithExistingRegoCodeForAdviser(gcmId, role, new FailFastErrorsImpl());
        assertEquals("Success", status);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testClientRepositoryMethodIsCalledWhenAdviserIDsPresentInDto() throws Exception
    {
        Collection <BrokerIdentifier> brokerList = new ArrayList <>();
        brokerList.add(getBrokerId("id1"));
        ClientApplication clientApplication = new ClientApplication();
        clientApplication.setAdviserPositionId(adviserId);
        when(permittedClientApplicationRepository.find(clientApplicationId)).thenReturn(clientApplication);
        when(clientApplicationRepository.find(clientApplicationId, brokerList)).thenReturn(clientApplication);
        OnBoardingApplication onBoardingApplication = mock(OnBoardingApplication.class);
        when(onBoardingApplication.getKey()).thenReturn(OnboardingApplicationKey.valueOf(onboardingApplicationId));
        clientApplication.setOnboardingApplication(onBoardingApplication);

        ResendExistingRegistrationCodeRequestMsgType request = buildResendExistingCodeRequestMsgType();
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        when(resendExistingRegistrationCodeSendEmailRequestMsgTypeBuilder.build(clientId, adviserId, Attribute.INVESTOR, serviceErrors)).thenReturn(request);
        when(resendExistingRegistrationCodeSendEmailRequestMsgTypeBuilder.extractInvestorEmailAddress(request)).thenReturn(EMAIL_ADDRESS);

        when(userProfileService.getGcmId()).thenReturn("MY_GCM_ID");

        ResendExistingRegistrationCodeResponseDetailsType responseDetails = buildResendExistingCodeResponseDetailsTypeWithSuccessResponse();
        CorrelatedResponse correlatedResponse = createResendExistingCodeCorrelatedResponse(requestDate, responseDetails, StatusTypeCode.SUCCESS);
        when(provider.sendWebServiceWithSecurityHeaderAndResponseCallbackWithHeaderValues(eq(token),
                eq(Attribute.RESEND_EXISTING_REGISTRATION_CODE_KEY),
                Matchers.<JAXBElement <CreateOneTimePasswordSendEmailRequestMsgType>> any(),
                any(ServiceErrors.class))).thenReturn(correlatedResponse);

        ArgumentCaptor <OnboardingCommunication> communicationArgumentCaptor = ArgumentCaptor.forClass(OnboardingCommunication.class);

        SendEmailDto sendEmailDto = new SendEmailDto(clientApplicationId, clientId);
        sendEmailDto.setAdviserIds(brokerList);
        sendEmailDto.setRole(Attribute.INVESTOR);
        sendEmailDto = sendEmailService.submit(sendEmailDto, serviceErrors);

        verify(clientApplicationRepository, times(1)).find(sendEmailDto.getClientApplicationId(), sendEmailDto.getAdviserIds());
        verify(resendRegistrationCodeTransactor, times(1)).save(communicationArgumentCaptor.capture());
        OnboardingCommunication savedCommunication = communicationArgumentCaptor.getValue();
        assertThat(savedCommunication.getCommunicationInitiationTime(), is(requestDate));
        assertThat(savedCommunication.getCommunicationId(), is(communicationId));
        assertThat(savedCommunication.getGcmPan(), is(customerNumber));
        assertThat(savedCommunication.getOnboardingApplicationId(), is(onboardingApplicationId));
        assertThat(savedCommunication.getEmailAddress(), is(EMAIL_ADDRESS));
        assertThat(savedCommunication.getLastModifiedId(), is("MY_GCM_ID"));
        assertThat(sendEmailDto.getStatus(), is(StatusTypeCode.SUCCESS.value()));
    }

    private void shouldAssertServiceErrorWithGivenExceptionAndMessage(String expectedErrorMessage, RuntimeException exception) {
        ClientApplication clientApplication = new ClientApplication();
        clientApplication.setAdviserPositionId(adviserId);
        when(permittedClientApplicationRepository.find(clientApplicationId)).thenReturn(clientApplication);
        OnBoardingApplication onBoardingApplication = mock( OnBoardingApplication.class);
        when(onBoardingApplication.getKey()).thenReturn(OnboardingApplicationKey.valueOf(onboardingApplicationId));
        clientApplication.setOnboardingApplication(onBoardingApplication);

        ResendExistingRegistrationCodeRequestMsgType request = buildResendExistingCodeRequestMsgType();

        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        when(resendExistingRegistrationCodeSendEmailRequestMsgTypeBuilder.build(clientId, adviserId, Attribute.INVESTOR, serviceErrors)).thenReturn(request);

        ResendExistingRegistrationCodeResponseDetailsType responseDetails = buildResendExistingCodeResponseDetailsTypeWithSuccessResponse();
        CorrelatedResponse correlatedResponse = createResendExistingCodeCorrelatedResponse(requestDate, responseDetails, StatusTypeCode.SUCCESS);
        when(provider.sendWebServiceWithSecurityHeaderAndResponseCallbackWithHeaderValues(eq(token), eq(Attribute.RESEND_EXISTING_REGISTRATION_CODE_KEY), Matchers.<JAXBElement<CreateOneTimePasswordSendEmailRequestMsgType>>any(), any(ServiceErrors.class))).thenReturn(correlatedResponse);

        when(resendRegistrationCodeTransactor.save(any(OnboardingCommunication.class))).thenThrow(exception);

        SendEmailDto sendEmailDto = new SendEmailDto(clientApplicationId, clientId);
        sendEmailService.submit(sendEmailDto, serviceErrors);
        assertTrue(serviceErrors.hasErrors());
        for (ServiceError serviceError : serviceErrors.getErrorList()) {
            assertThat(serviceError.getMessage(), is(expectedErrorMessage));
        }
    }

    private ResendExistingRegistrationCodeRequestMsgType buildResendExistingCodeRequestMsgType() {
        ResendExistingRegistrationCodeRequestMsgType request = new ResendExistingRegistrationCodeRequestMsgType();
        CreateOTPCommunicationDetailsType communicationDetails = getMockedCommunicationDetail();
        request.setCommunicationDetails(communicationDetails);
        return request;
    }

    private ResendExistingRegistrationCodeResponseDetailsType buildResendExistingCodeResponseDetailsTypeWithSuccessResponse() {

        ResendExistingRegistrationCodeResponseDetailsType responseDetails = new ResendExistingRegistrationCodeResponseDetailsType();

        ResendRegistrationCodeResponseDetailsSuccessResponseType successResponse = new ResendRegistrationCodeResponseDetailsSuccessResponseType();
        successResponse.setCommunicationId(communicationId);
        CreateOTPCustomerIdentifier createOTPCustomerIdentifier = new CreateOTPCustomerIdentifier();
        CustomerNumberIdentifier customerNumberIdentifier = new CustomerNumberIdentifier();
        customerNumberIdentifier.setCustomerNumber(customerNumber);
        customerNumberIdentifier.setCustomerNumberIssuer(CustomerNoAllIssuerType.BT_PANORAMA);
        createOTPCustomerIdentifier.setCustomerNumberIdentifier(customerNumberIdentifier);
        successResponse.setCustomerIdentifier(createOTPCustomerIdentifier);
        responseDetails.setSuccessResponse(successResponse);

        return responseDetails;
    }


    private CreateOneTimePasswordSendEmailResponseDetailsType buildCreateOneTimePasswordSendEmailResponseDetailsTypeWithSuccessResponse() {
        CreateOneTimePasswordSendEmailResponseDetailsType responseDetails = new CreateOneTimePasswordSendEmailResponseDetailsType();
        CreateOneTimePasswordSendEmailResponseDetailsSuccessResponseType successResponse = new CreateOneTimePasswordSendEmailResponseDetailsSuccessResponseType();
        successResponse.setCommunicationId(communicationId);
        CreateOTPCustomerIdentifier createOTPCustomerIdentifier = new CreateOTPCustomerIdentifier();
        CustomerNumberIdentifier customerNumberIdentifier = new CustomerNumberIdentifier();
        customerNumberIdentifier.setCustomerNumber(customerNumber);
        customerNumberIdentifier.setCustomerNumberIssuer(CustomerNoAllIssuerType.BT_PANORAMA);
        createOTPCustomerIdentifier.setCustomerNumberIdentifier(customerNumberIdentifier);
        successResponse.setCustomerIdentifier(createOTPCustomerIdentifier);
        responseDetails.setSuccessResponse(successResponse);
        return responseDetails;
    }

    private ResendExistingRegistrationCodeResponseDetailsType buildResendExistingCodeResponseDetailsTypeWithErrorResponse() {
        ResendExistingRegistrationCodeResponseDetailsType responseDetailsType = Mockito.mock(ResendExistingRegistrationCodeResponseDetailsType.class);
        ResendRegistrationCodeResponseDetailsErrorResponsesType sendEmailErrorResponsesType = Mockito.mock(ResendRegistrationCodeResponseDetailsErrorResponsesType.class);
        List<ResendRegistrationCodeResponseDetailsErrorResponseType> errorResponsesTypes = new ArrayList<>();

        ResendRegistrationCodeResponseDetailsErrorResponseType errorResponsesType = Mockito.mock(ResendRegistrationCodeResponseDetailsErrorResponseType.class);
        when(errorResponsesType.getSubCode()).thenReturn(errorSubCode);
        errorResponsesTypes.add(errorResponsesType);

        when(sendEmailErrorResponsesType.getErrorResponse()).thenReturn(errorResponsesTypes);
        when(responseDetailsType.getErrorResponses()).thenReturn(sendEmailErrorResponsesType);

        return responseDetailsType;
    }

    private CreateOneTimePasswordSendEmailResponseDetailsType buildCreateOneTimePasswordSendEmailResponseDetailsTypeWithErrorResponse() {
        CreateOneTimePasswordSendEmailResponseDetailsType responseDetailsType = Mockito.mock(CreateOneTimePasswordSendEmailResponseDetailsType.class);
        CreateOneTimePasswordSendEmailResponseDetailsErrorResponsesType sendEmailErrorResponsesType = Mockito.mock(CreateOneTimePasswordSendEmailResponseDetailsErrorResponsesType.class);
        List<CreateOneTimePasswordSendEmailResponseDetailsErrorResponseType> errorResponsesTypes = new ArrayList<>();
        CreateOneTimePasswordSendEmailResponseDetailsErrorResponseType errorResponsesType = Mockito.mock(CreateOneTimePasswordSendEmailResponseDetailsErrorResponseType.class);
        when(errorResponsesType.getSubCode()).thenReturn(errorSubCode);
        errorResponsesTypes.add(errorResponsesType);
        when(sendEmailErrorResponsesType.getErrorResponse()).thenReturn(errorResponsesTypes);
        when(responseDetailsType.getErrorResponses()).thenReturn(sendEmailErrorResponsesType);
        return responseDetailsType;
    }

    private CreateOTPCommunicationDetailsType getMockedCommunicationDetail() {
        CreateOTPCommunicationDetailsType mockedCommunication = Mockito.mock(CreateOTPCommunicationDetailsType.class);
        POAInvolvedPartyDetailsType mockedOBPOAInvolvedPartyDetailsType = Mockito.mock(POAInvolvedPartyDetailsType.class);
        EmailAddressesType mockedEmailAddressesType = Mockito.mock(EmailAddressesType.class);
        List<EmailAddressType> mockedEmailAddresses = Mockito.mock(List.class);
        EmailAddressType mockedEmailAddressType = Mockito.mock(EmailAddressType.class);
        JAXBElement<EmailAddressDetailType> mockedJAXBEmailAddressDetailType = Mockito.mock(JAXBElement.class);
        EmailAddressDetailType mockedEmailAddressDetailType = Mockito.mock(EmailAddressDetailType.class);

        when(mockedEmailAddressDetailType.getEmailAddress()).thenReturn(EMAIL_ADDRESS);
        when(mockedJAXBEmailAddressDetailType.getValue()).thenReturn(mockedEmailAddressDetailType);
        when(mockedEmailAddressType.getEmailAddressDetail()).thenReturn(mockedJAXBEmailAddressDetailType);
        when(mockedEmailAddresses.get(0)).thenReturn(mockedEmailAddressType);
        when(mockedEmailAddressesType.getEmailAddress()).thenReturn(mockedEmailAddresses);
        when(mockedOBPOAInvolvedPartyDetailsType.getEmailAddresses()).thenReturn(mockedEmailAddressesType);
        when(mockedCommunication.getInvestorDetails()).thenReturn(mockedOBPOAInvolvedPartyDetailsType);
        return mockedCommunication;
    }

    private CorrelatedResponse createResendExistingCodeCorrelatedResponse(Date requestDate, ResendExistingRegistrationCodeResponseDetailsType responseDetails, StatusTypeCode statusTypeCode) {
        ResendExistingRegistrationCodeResponseMsgType response = new ResendExistingRegistrationCodeResponseMsgType();
        response.setStatus(statusTypeCode);
        response.setResponseDetails(responseDetails);

        ESBHeaderInformationWrapper esbHeaderWrapper = new ESBHeaderInformationWrapper();
        esbHeaderWrapper.setCorrelationId("ID123");
        esbHeaderWrapper.setRequestCreationDate(requestDate);

        return new CorrelatedResponse(esbHeaderWrapper, response);
    }

    private CorrelatedResponse createCorrelatedResponse(Date requestDate, CreateOneTimePasswordSendEmailResponseDetailsType responseDetails, StatusTypeCode statusTypeCode) {
        CreateOneTimePasswordSendEmailResponseMsgType response = new CreateOneTimePasswordSendEmailResponseMsgType();
        response.setStatus(statusTypeCode);
        response.setResponseDetails(responseDetails);
        ESBHeaderInformationWrapper esbHeaderWrapper = new ESBHeaderInformationWrapper();
        esbHeaderWrapper.setCorrelationId("ID123");
        esbHeaderWrapper.setRequestCreationDate(requestDate);
        return new CorrelatedResponse(esbHeaderWrapper, response);
    }

    private BrokerIdentifier getBrokerId(final String id)
    {
        BrokerIdentifier brokerIdentifier = new BrokerIdentifier()
        {
            @Override
            public BrokerKey getKey()
            {
                return BrokerKey.valueOf(id);
            }
        };
        return brokerIdentifier;
    }

}

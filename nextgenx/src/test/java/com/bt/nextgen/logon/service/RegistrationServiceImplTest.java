package com.bt.nextgen.logon.service;

import au.com.westpac.gn.channelmanagement.services.credentialmanagement.xsd.modifychannelaccesscredential.v5.svc0310.ModifyChannelAccessCredentialResponse;
import au.com.westpac.gn.channelmanagement.services.passwordmanagement.xsd.maintainchannelaccessservicepassword.v2.svc0247.MaintainChannelAccessServicePasswordResponse;
import au.com.westpac.gn.utility.xsd.statushandling.v1.Level;
import au.com.westpac.gn.utility.xsd.statushandling.v1.ServiceStatus;
import au.com.westpac.gn.utility.xsd.statushandling.v1.StatusInfo;
import com.bt.nextgen.api.safi.facade.TwoFactorAuthenticationServiceImpl;
import com.bt.nextgen.cms.service.CmsService;
import com.bt.nextgen.core.exception.ServiceException;
import com.btfin.panorama.core.security.saml.BankingAuthorityService;
import com.btfin.panorama.core.security.saml.SamlToken;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.core.toggle.FeatureToggles;
import com.bt.nextgen.core.toggle.FeatureTogglesService;
import com.bt.nextgen.core.webservice.provider.WebServiceProvider;
import com.bt.nextgen.login.service.RegistrationServiceImpl;
import com.bt.nextgen.login.service.ValidateCredentialsResponse;
import com.bt.nextgen.login.util.SamlUtil;
import com.bt.nextgen.login.web.controller.RegistrationResponse;
import com.bt.nextgen.login.web.model.CredentialsModel;
import com.bt.nextgen.login.web.model.RegistrationModel;
import com.bt.nextgen.login.web.model.SmsCodeModel;
import com.bt.nextgen.service.ServiceError;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.rules.*;
import com.bt.nextgen.service.group.customer.CustomerTokenIntegrationService;
import com.bt.nextgen.service.group.customer.CustomerTokenRequest;
import com.bt.nextgen.service.group.customer.CustomerTokenRequestModel;
import com.bt.nextgen.service.onboarding.FirstTimeRegistrationRequest;
import com.bt.nextgen.service.onboarding.FirstTimeRegistrationResponse;
import com.bt.nextgen.service.onboarding.OnboardingIntegrationService;
import com.bt.nextgen.service.onboarding.ValidatePartyRequest;
import com.bt.nextgen.service.onboarding.btesb.ValidatePartyAndSmsAdapter;
import com.bt.nextgen.service.safi.model.SafiAnalyzeAndChallengeResponse;
import com.bt.nextgen.service.safi.model.SafiChallengeRequest;
import com.bt.nextgen.service.security.model.HttpRequestParams;
import com.bt.nextgen.service.web.UrlProxyService;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import com.bt.nextgen.web.validator.ValidationErrorCode;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.btfin.panorama.service.exception.ServiceErrorImpl;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.google.api.client.testing.http.javanet.MockHttpURLConnection;
import com.rsa.csd.ws.IdentificationData;
import org.apache.commons.collections.map.SingletonMap;
import org.hamcrest.core.Is;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.net.MalformedURLException;
import java.net.URL;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.anyMap;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class RegistrationServiceImplTest {

    @InjectMocks
    private RegistrationServiceImpl service;

    @Mock
    private UserProfileService userProfileService;

    @Mock
    private WebServiceProvider provider;

    @Mock
    private BankingAuthorityService userSamlService;
    @Mock
    private BankingAuthorityService serverSamlService;
    @Mock
    private OnboardingIntegrationService btEsbService;
    @Mock
    CmsService cmsService;
    @Mock
    Jaxb2Marshaller jaxb2Marshaller;
    @Mock
    private UrlProxyService urlProxyService;

    MockHttpServletRequest request;
    MockHttpServletResponse response;
    MockHttpSession mockHttpSession;
    MockHttpURLConnection urlConnection;

    @Mock
    private FeatureTogglesService featureTogglesService;

    @Mock
    private AvaloqRulesIntegrationService avaloqRulesIntegrationService;
    
    @Mock
    private TwoFactorAuthenticationServiceImpl twoFactorAuthenticationServiceImpl;
    
    @Mock
    private CustomerTokenIntegrationService customerTokenIntegrationService;


    private static String registrationCode = new String("110011001100");
    private static String lastName = new String("test");
    private static String postCode = new String("1010");
    private static String userName = new String("userName");
    private static String password = new String("password");

    private static String gcmId = "123456789";
    private static String ruleId = "123456";
    private static String nextStepUrl = "http://1234/";
    private static String investorEtpPath = "abcd";
    private static  String advisorEtpPath = "1234";
    private static String appContext =  "aaaaa";

    private RegistrationModel registrationModel;

    @Before
    public void setup() throws MalformedURLException {

        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        mockHttpSession = new MockHttpSession();
        urlConnection = new MockHttpURLConnection(new URL("http://something"));

        registrationModel = new RegistrationModel();
        registrationModel.setUserCode(userName);
        registrationModel.setPassword(password);
        registrationModel.setHalgm("halgm");

        when(userSamlService.getSamlToken()).thenReturn(new SamlToken("<saml:Assertion xmlns:saml=\"urn:oasis:names:tc:SAML:2.0:assertion\">    <saml:Conditions NotBefore=\"1945-06-30T03:29:32Z\" NotOnOrAfter=\"2081-08-05T09:57:45Z\">\n" +
                "        <saml:AudienceRestriction>\n" +
                "            <saml:Audience>http://ng.westpac.com.au</saml:Audience>\n" +
                "        </saml:AudienceRestriction>\n" +
                "    </saml:Conditions>\nI am a fake user level saml token</saml:Assertion>"));
        when(serverSamlService.getSamlToken()).thenReturn(new SamlToken("<saml:Assertion xmlns:saml=\"urn:oasis:names:tc:SAML:2.0:assertion\">    <saml:Conditions NotBefore=\"1945-06-30T03:29:32Z\" NotOnOrAfter=\"2081-08-05T09:57:45Z\">\n" +
                "        <saml:AudienceRestriction>\n" +
                "            <saml:Audience>http://ng.westpac.com.au</saml:Audience>\n" +
                "        </saml:AudienceRestriction>\n" +
                "    </saml:Conditions>\nI am a fake server level saml token</saml:Assertion>"));

        FeatureToggles featureToggles = new FeatureToggles();
        featureToggles.setFeatureToggle(FeatureToggles.SIMPLE_REGISTRATION, false);
        when(featureTogglesService.findOne(any(ServiceErrors.class))).thenReturn(featureToggles);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

/*
        TestingAuthenticationToken authentication = new TestingAuthenticationToken("adviser",
                "adviser",
                Roles.ROLE_ADVISER.name());
        Profile dummyProfile = new Profile(new SamlToken(SamlUtil.loadSaml()));
        authentication.setDetails(dummyProfile);
        SecurityContextHolder.getContext().setAuthentication(authentication);*/
    }

    @Test
    public void testValidateParty() throws Exception {
        CredentialsModel model = mock(CredentialsModel.class);
        ValidatePartyAndSmsAdapter response = new ValidatePartyAndSmsAdapter();
        when(btEsbService.validateParty(any(ValidatePartyRequest.class))).thenReturn(response);
        ValidateCredentialsResponse result = service.validateParty(model);
        assertThat(result.getStatusCode(), Is.is(Attribute.SUCCESS_MESSAGE));
    }

    @Test
    public void testValidatePartyWithErrorResponse() throws Exception {
        String errorMessage = "This is test error message";
        when(cmsService.getDynamicContent(anyString(), any(String[].class))).thenReturn(errorMessage);
        ValidatePartyAndSmsAdapter response = new ValidatePartyAndSmsAdapter();
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        serviceErrors.addError(new ServiceErrorImpl());
        response.setServiceErrors(serviceErrors);
        when(btEsbService.validateParty(any(ValidatePartyRequest.class))).thenReturn(response);
        CredentialsModel model = mock(CredentialsModel.class);
        ValidateCredentialsResponse result = service.validateParty(model);
        assertThat(result.getStatusCode(), Is.is(errorMessage));
    }

    @Test
    public void testValidRegistrationWithErrorResponse() throws Exception {
        String message = "Sorry, a technical error has occurred. Please call 1300 881 716 and quote error code : Bt-esb001.";
        when(cmsService.getDynamicContent(anyString(), any(String[].class))).thenReturn(message);
        FirstTimeRegistrationResponse response = new ValidatePartyAndSmsAdapter();
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        serviceErrors.addError(new ServiceErrorImpl());
        response.setServiceErrors(serviceErrors);

        when(btEsbService.validateRegistration(any(FirstTimeRegistrationRequest.class))).thenReturn(response);
        when(btEsbService.validateRegistrationDetails(any(FirstTimeRegistrationRequest.class))).thenReturn(response);

        SmsCodeModel smsCodeModel = new SmsCodeModel();
        HttpRequestParams httpParams = new HttpRequestParams();
        SafiAnalyzeAndChallengeResponse result = service.validRegistration(smsCodeModel, httpParams);
        assertThat(result.getStatusCode(), Is.is(message));
    }

    @Test
    public void testValidRegistrationWithWarningResponse() throws Exception {
        String message = "Sorry, a technical error has occurred. Please call 1300 881 716 and quote error code : Bt-esb001.";
        when(cmsService.getContent(anyString())).thenReturn(message);
        FirstTimeRegistrationResponse response = new ValidatePartyAndSmsAdapter();
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        ServiceError serviceError = new ServiceErrorImpl();
        serviceError.setId(Attribute.ERROR_CODE_INVALID_PARAMETER);
        ;
        serviceErrors.addError(serviceError);
        response.setServiceErrors(serviceErrors);
        when(btEsbService.validateRegistration(any(FirstTimeRegistrationRequest.class))).thenReturn(response);
        SmsCodeModel smsCodeModel = new SmsCodeModel();
        HttpRequestParams httpParams = new HttpRequestParams();
        SafiAnalyzeAndChallengeResponse result = service.validRegistration(smsCodeModel, httpParams);
        assertThat(result.getStatusCode(), Is.is(message));
    }

    @Test
    public void testValidRegistrationWithWarningResponseWhenFeatureToggleIsOn() throws Exception {
        FeatureToggles featureToggles = new FeatureToggles();
        featureToggles.setFeatureToggle(FeatureToggles.SIMPLE_REGISTRATION, true);
        when(featureTogglesService.findOne(any(ServiceErrors.class))).thenReturn(featureToggles);

        String message = "Sorry, a technical error has occurred. Please call 1300 881 716 and quote error code : Bt-esb001.";
        when(cmsService.getContent(anyString())).thenReturn(message);
        FirstTimeRegistrationResponse response = new ValidatePartyAndSmsAdapter();
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        ServiceError serviceError = new ServiceErrorImpl();
        serviceError.setId(Attribute.ERROR_CODE_INVALID_PARAMETER);
        ;
        serviceErrors.addError(serviceError);
        response.setServiceErrors(serviceErrors);
        when(btEsbService.validateRegistrationDetails(any(FirstTimeRegistrationRequest.class))).thenReturn(response);
        SmsCodeModel smsCodeModel = new SmsCodeModel();
        HttpRequestParams httpParams = new HttpRequestParams();
        SafiAnalyzeAndChallengeResponse result = service.validRegistration(smsCodeModel, httpParams);
        assertThat(result.getStatusCode(), Is.is(message));

    }

    @Test
    public void testValidRegistrationReturnsSuccess() throws Exception {
        FirstTimeRegistrationResponse response = new ValidatePartyAndSmsAdapter();

        when(btEsbService.validateRegistration(any(FirstTimeRegistrationRequest.class))).thenReturn(response);
        when(btEsbService.validateRegistrationDetails(any(FirstTimeRegistrationRequest.class))).thenReturn(response);

        SmsCodeModel smsCodeModel = new SmsCodeModel();
        HttpRequestParams httpParams = new HttpRequestParams();
        SafiAnalyzeAndChallengeResponse result = service.validRegistration(smsCodeModel, httpParams);
        assertThat(result.getStatusCode(), Is.is(Attribute.SUCCESS_MESSAGE));
    }

    @Test
    public void testValidRegistrationReturnsSuccesWhenFeatureToggleIsOn() throws Exception {
        FeatureToggles featureToggles = new FeatureToggles();
        featureToggles.setFeatureToggle(FeatureToggles.SIMPLE_REGISTRATION, true);
        when(featureTogglesService.findOne(any(ServiceErrors.class))).thenReturn(featureToggles);
        FirstTimeRegistrationResponse response = new ValidatePartyAndSmsAdapter();

        when(btEsbService.validateRegistrationDetails(any(FirstTimeRegistrationRequest.class))).thenReturn(response);
        when(btEsbService.validateRegistration(any(FirstTimeRegistrationRequest.class))).thenReturn(response);

        SmsCodeModel smsCodeModel = new SmsCodeModel();
        HttpRequestParams httpParams = new HttpRequestParams();
        SafiAnalyzeAndChallengeResponse result = service.validRegistration(smsCodeModel, httpParams);
        assertThat(result.getStatusCode(), Is.is(Attribute.SUCCESS_MESSAGE));
    }


    public void testCreateUser() throws Exception {
        ModifyChannelAccessCredentialResponse modifyChannelAccessResponse =
                mock(ModifyChannelAccessCredentialResponse.class);
        doAnswer(new Answer<ServiceStatus>() {
            public ServiceStatus answer(InvocationOnMock invocation) {
                ServiceStatus serviceStatus = new ServiceStatus();
                StatusInfo statusInfo = new StatusInfo();
                statusInfo.setLevel(Level.SUCCESS);
                serviceStatus.getStatusInfo().add(0, statusInfo);
                return serviceStatus;
            }
        }).when(modifyChannelAccessResponse).getServiceStatus();

        MaintainChannelAccessServicePasswordResponse maintainChannelAccessResponse =
                mock(MaintainChannelAccessServicePasswordResponse.class);
        doAnswer(new Answer<ServiceStatus>() {
            public ServiceStatus answer(InvocationOnMock invocation) {
                ServiceStatus serviceStatus = new ServiceStatus();
                StatusInfo statusInfo = new StatusInfo();
                statusInfo.setLevel(Level.SUCCESS);
                serviceStatus.getStatusInfo().add(0, statusInfo);
                return serviceStatus;
            }
        }).when(maintainChannelAccessResponse).getServiceStatus();

        when(provider.sendWebServiceWithSecurityHeader(any(SamlToken.class), eq(Attribute.GROUP_ESB_MODIFY_CHANNEL_ACCESS_CREDENTIAL),
                any(Object.class)))
                .thenReturn(modifyChannelAccessResponse);


        when(provider.sendWebServiceWithSecurityHeader(any(SamlToken.class), eq(Attribute.GROUP_ESB_MAINTAIN_CHANNEL_ACCESS_CREDENTIAL),
                any(Object.class)))
                .thenReturn(maintainChannelAccessResponse);

        when(userProfileService.isLoggedIn()).thenReturn(true);
        when(userProfileService.isSafiDeviceActive()).thenReturn(false);

        FeatureToggles featureToggles = new FeatureToggles();
        featureToggles.setFeatureToggle(FeatureToggles.SIMPLE_REGISTRATION, false);
        when(featureTogglesService.findOne(any(ServiceErrors.class))).thenReturn(featureToggles);

        String status = service.createUser(registrationModel, any(ServiceErrors.class));
        assertThat(status, Is.is(Attribute.SUCCESS_MESSAGE));
        verify(userProfileService, times(1)).updateSafiDeviceStatus(eq(true));
    }

    public void testCreateUser_UserAliasCreationFailed() throws Exception {
        ModifyChannelAccessCredentialResponse modifyChannelAccessResponse =
                mock(ModifyChannelAccessCredentialResponse.class);
        doAnswer(new Answer<ServiceStatus>() {
            public ServiceStatus answer(InvocationOnMock invocation) {
                ServiceStatus failedserviceStatus = new ServiceStatus();
                StatusInfo failedStatusInfo = new StatusInfo();
                failedStatusInfo.setLevel(Level.ERROR);
                failedserviceStatus.getStatusInfo().add(0, failedStatusInfo);
                return failedserviceStatus;
            }
        }).when(modifyChannelAccessResponse).getServiceStatus();

        MaintainChannelAccessServicePasswordResponse maintainChannelAccessResponse =
                mock(MaintainChannelAccessServicePasswordResponse.class);
        doAnswer(new Answer<ServiceStatus>() {
            public ServiceStatus answer(InvocationOnMock invocation) {
                ServiceStatus serviceStatus = new ServiceStatus();
                StatusInfo statusInfo = new StatusInfo();
                statusInfo.setLevel(Level.SUCCESS);
                serviceStatus.getStatusInfo().add(0, statusInfo);
                return serviceStatus;
            }
        }).when(maintainChannelAccessResponse).getServiceStatus();

        when(provider.sendWebServiceWithSecurityHeader(any(SamlToken.class), eq(Attribute.GROUP_ESB_MODIFY_CHANNEL_ACCESS_CREDENTIAL),
                any(Object.class)))
                .thenReturn(modifyChannelAccessResponse);


        when(provider.sendWebServiceWithSecurityHeader(any(SamlToken.class), eq(Attribute.GROUP_ESB_MAINTAIN_CHANNEL_ACCESS_CREDENTIAL),
                any(Object.class)))
                .thenReturn(maintainChannelAccessResponse);
        String status = service.createUser(registrationModel, any(ServiceErrors.class));
        assertThat(status, Is.is(ValidationErrorCode.USER_NAME_NOT_UNIQUE));
    }

    public void testCreateUser_GeneratePasswordFailed() throws Exception {

        ModifyChannelAccessCredentialResponse modifyChannelAccessResponse =
                mock(ModifyChannelAccessCredentialResponse.class);
        doAnswer(new Answer<ServiceStatus>() {
            public ServiceStatus answer(InvocationOnMock invocation) {
                ServiceStatus serviceStatus = new ServiceStatus();
                StatusInfo statusInfo = new StatusInfo();
                statusInfo.setLevel(Level.SUCCESS);
                serviceStatus.getStatusInfo().add(0, statusInfo);
                return serviceStatus;

            }
        }).when(modifyChannelAccessResponse).getServiceStatus();

        MaintainChannelAccessServicePasswordResponse maintainChannelAccessResponse =
                mock(MaintainChannelAccessServicePasswordResponse.class);
        doAnswer(new Answer<ServiceStatus>() {
            public ServiceStatus answer(InvocationOnMock invocation) {
                ServiceStatus failedserviceStatus = new ServiceStatus();
                StatusInfo failedStatusInfo = new StatusInfo();
                failedStatusInfo.setLevel(Level.ERROR);
                failedserviceStatus.getStatusInfo().add(0, failedStatusInfo);
                return failedserviceStatus;
            }
        }).when(maintainChannelAccessResponse).getServiceStatus();


        when(provider.sendWebServiceWithSecurityHeader(any(SamlToken.class), eq(Attribute.GROUP_ESB_MODIFY_CHANNEL_ACCESS_CREDENTIAL),
                any(Object.class)))
                .thenReturn(modifyChannelAccessResponse);


        when(provider.sendWebServiceWithSecurityHeader(any(SamlToken.class), eq(Attribute.GROUP_ESB_MAINTAIN_CHANNEL_ACCESS_CREDENTIAL),
                any(Object.class)))
                .thenReturn(maintainChannelAccessResponse);

        String status = service.createUser(registrationModel, any(ServiceErrors.class));
        assertThat(status, Is.is(ValidationErrorCode.ERROR_IN_REGISTRATION));
    }


    @Test
    public void testUpdateUserTwoFAStatusAsyncSuccess() throws Exception {

        RuleUpdateStatus ruleStatus = mock(RuleUpdateStatus.class);
        when(ruleStatus.getRuleId()).thenReturn(ruleId);
        when(ruleStatus.getStatus()).thenReturn(true);
        when(avaloqRulesIntegrationService.updateAvaloqRule(any(String.class), any(SingletonMap.class), any(ServiceErrors.class)))
                .thenReturn(ruleStatus);

        RuleImpl rule = mock(RuleImpl.class);
        when(rule.getRuleId()).thenReturn(ruleId);
        when(avaloqRulesIntegrationService.retrieveTwoFaRule(any(RuleType.class), any(SingletonMap.class), any(FailFastErrorsImpl.class)))
                .thenReturn(rule);

        service.updateUserTwoFAStatusAsync(gcmId);

        verify(avaloqRulesIntegrationService, times(1)).updateAvaloqRule(eq(ruleId), anyMap(), any(ServiceErrors.class));
    }

    @Test
    public void testUpdateUserTwoFAStatusAsyncFailFast() throws Exception {

        RuleUpdateStatus ruleStatus = mock(RuleUpdateStatus.class);
        when(ruleStatus.getRuleId()).thenReturn(ruleId);
        when(ruleStatus.getStatus()).thenReturn(true);
        when(avaloqRulesIntegrationService.updateAvaloqRule(any(String.class), any(SingletonMap.class), any(ServiceErrors.class))).thenThrow(new ServiceException());

        RuleImpl rule = mock(RuleImpl.class);

        when(avaloqRulesIntegrationService.retrieveTwoFaRule(any(RuleType.class), any(SingletonMap.class), any(FailFastErrorsImpl.class)))
                .thenReturn(rule);

        service.updateUserTwoFAStatusAsync(gcmId);
    }
    
    @Test
    public void testRegistrationResponseForTwoFA() throws Exception {
    	
    	CustomerTokenRequestModel customerTokenRequestModel = mock(CustomerTokenRequestModel.class);
    	when(customerTokenRequestModel.isForgotPassword()).thenReturn(true);
    
    	ServletRequestAttributes servletRequestAttributes = new ServletRequestAttributes(request);
    	RequestContextHolder requestContextHolder = Mockito.mock(RequestContextHolder.class);
    	RequestContextHolder.setRequestAttributes(servletRequestAttributes);
    	
    	IdentificationData idData = new IdentificationData();
    	
    	SafiAnalyzeAndChallengeResponse safiAnalyzeAndChallengeResponse = mock(SafiAnalyzeAndChallengeResponse.class);
        when(safiAnalyzeAndChallengeResponse.getIdentificationData()).thenReturn(idData);
    	 
    	when(twoFactorAuthenticationServiceImpl.challengeFromNotAuthCtx(any(SafiChallengeRequest.class)))
        .thenReturn(safiAnalyzeAndChallengeResponse);
    	 
   	 	RegistrationResponse response = service.registrationResponseForOptionalTwoFA(customerTokenRequestModel, request, nextStepUrl, investorEtpPath, advisorEtpPath, appContext);
    	assertThat(response.isShowSMS(), Is.is(Boolean.TRUE));
        
    }
      
    @Test
    public void testRegistrationResponseWithNoRuleIdAndActionAndNoTwoFA() throws Exception {
    	
    	CustomerTokenRequestModel customerTokenRequestModel = mock(CustomerTokenRequestModel.class);
        SamlToken samlToken = new SamlToken(SamlUtil.loadSaml());
    	when(customerTokenRequestModel.isForgotPassword()).thenReturn(false);
    	
    	RuleImpl rule = mock(RuleImpl.class); 	 	
    	when(avaloqRulesIntegrationService.retrieveTwoFaRule(any(RuleType.class), any(SingletonMap.class), any(FailFastErrorsImpl.class)))
        	.thenReturn(rule);
   	 	
    	String xmlString = SamlUtil.loadSaml("/saml-sample.xml");
    	when(customerTokenIntegrationService.getCustomerSAMLToken(any(CustomerTokenRequest.class), any(ServiceErrors.class))).thenReturn(xmlString);
        when(customerTokenRequestModel.getToken()).thenReturn(samlToken);
        when(urlProxyService.connect(new URL("http://null/pkmslogout"))).thenReturn(urlConnection);

   	 	RegistrationResponse response = service.registrationResponseForOptionalTwoFA(customerTokenRequestModel, request, nextStepUrl, investorEtpPath, advisorEtpPath, appContext);

   	 	assertThat(response.isShowSMS(), Is.is(Boolean.FALSE));
   	 	assertNotNull(response.getEamPostUrl());
   	 	assertNotNull(response.getRelayState());
   	 	assertNotNull(response.getSAMLResponse());
   	
    }
    
    @Test
    public void testRegistrationResponseWithRuleIdAndRukeActionWithNoTwoFa() throws Exception {
    	
    	CustomerTokenRequestModel customerTokenRequestModel = mock(CustomerTokenRequestModel.class);
    	when(customerTokenRequestModel.isForgotPassword()).thenReturn(false);
    	
    	RuleImpl rule = mock(RuleImpl.class); 	 	
    	when(avaloqRulesIntegrationService.retrieveTwoFaRule(any(RuleType.class), any(SingletonMap.class), any(FailFastErrorsImpl.class)))
        	.thenReturn(rule);
    	
    	when(rule.getAction()).thenReturn(RuleAction.CHK);
    	when(rule.getRuleId()).thenReturn(ruleId);
    	
    	ServletRequestAttributes servletRequestAttributes = new ServletRequestAttributes(request);
    	RequestContextHolder requestContextHolder = Mockito.mock(RequestContextHolder.class);
    	RequestContextHolder.setRequestAttributes(servletRequestAttributes);
    
    	IdentificationData idData = new IdentificationData();
    	SafiAnalyzeAndChallengeResponse safiAnalyzeAndChallengeResponse = mock(SafiAnalyzeAndChallengeResponse.class);
        when(safiAnalyzeAndChallengeResponse.getIdentificationData()).thenReturn(idData);
    	 
    	when(twoFactorAuthenticationServiceImpl.challengeFromNotAuthCtx(any(SafiChallengeRequest.class)))
        .thenReturn(safiAnalyzeAndChallengeResponse);
    	
   	 	RegistrationResponse response = service.registrationResponseForOptionalTwoFA(customerTokenRequestModel, request, nextStepUrl, investorEtpPath, advisorEtpPath, appContext);
   	 	assertThat(response.isShowSMS(), Is.is(Boolean.TRUE));
   	 	
    }
}
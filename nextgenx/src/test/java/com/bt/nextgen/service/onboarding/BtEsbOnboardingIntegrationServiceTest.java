/**
 *
 */
package com.bt.nextgen.service.onboarding;

import com.bt.nextgen.clients.util.JaxbUtil;
import com.btfin.panorama.core.security.saml.BankingAuthorityService;
import com.btfin.panorama.core.security.saml.SamlToken;
import com.bt.nextgen.core.webservice.provider.WebServiceProvider;
import com.bt.nextgen.service.onboarding.btesb.BtEsbOnboardingIntegrationServiceImpl;
import com.bt.nextgen.util.SamlUtil;
import ns.btfin_com.party.v3_0.CustomerNoAllIssuerType;
import ns.btfin_com.product.panorama.onboardingservice.onboardingresponse.v3_0.CreateOneTimePasswordSendEmailResponseMsgType;
import ns.btfin_com.product.panorama.onboardingservice.onboardingresponse.v3_0.ProvisionOnlineAccessResponseMsgType;
import ns.btfin_com.product.panorama.onboardingservice.onboardingresponse.v3_0.ValidatePartySMSOneTimePasswordChallengeResponseMsgType;
import org.hamcrest.core.Is;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;

/**
 * @author L055011
 */
//@Ignore
@RunWith(MockitoJUnitRunner.class)
public class BtEsbOnboardingIntegrationServiceTest {
    @Mock
    private BankingAuthorityService userSamlService;

    @Mock
    private BankingAuthorityService serverSamlService;

    @Mock
    private WebServiceProvider provider;

    private ValidatePartySMSOneTimePasswordChallengeResponseMsgType jaxbValidateResponse;

    private ns.btfin_com.product.panorama.credentialservice.credentialresponse.v1_0.ValidatePartySMSOneTimePasswordChallengeResponseMsgType jaxbValidateResponseNew;

    private ProvisionOnlineAccessResponseMsgType jaxbProcessAdvisersResponse;

    private ProvisionOnlineAccessResponseMsgType jaxbProvisionOnlineAccessResponse;

    private CreateOneTimePasswordSendEmailResponseMsgType jaxbCreateOneTimePasswordAndSendEmailResponse;

    private BtEsbOnboardingIntegrationServiceImpl btesbOnboarding;


    @Before
    public void setUp() throws Exception {
        btesbOnboarding = new BtEsbOnboardingIntegrationServiceImpl();

        provider = mock(WebServiceProvider.class);
        ReflectionTestUtils.setField(btesbOnboarding, "provider", provider);
        userSamlService = mock(BankingAuthorityService.class);
        ReflectionTestUtils.setField(btesbOnboarding, "userSamlService", userSamlService);

        serverSamlService = mock(BankingAuthorityService.class);
        ReflectionTestUtils.setField(btesbOnboarding, "serverSamlService", serverSamlService);

        Mockito.doAnswer(new Answer<SamlToken>() {
            @Override
            public SamlToken answer(InvocationOnMock invocation) throws Throwable {
                return new SamlToken(SamlUtil.loadSaml());
            }

        }).when(serverSamlService).getSamlToken();

        Mockito.doAnswer(new Answer<SamlToken>() {
            @Override
            public SamlToken answer(InvocationOnMock invocation) throws Throwable {
                return new SamlToken(SamlUtil.loadSaml());
            }

        }).when(userSamlService).getSamlToken();

    }

    @Test
    public void testValidateRegistrationForSuccess() {
        jaxbValidateResponse = JaxbUtil.unmarshall("/webservices/response/ValidateRegistrationSuccessResponse_UT.xml", ValidatePartySMSOneTimePasswordChallengeResponseMsgType.class);

        Mockito.doAnswer(new Answer<ValidatePartySMSOneTimePasswordChallengeResponseMsgType>() {
            @Override
            public ValidatePartySMSOneTimePasswordChallengeResponseMsgType answer(InvocationOnMock invocation) throws Throwable {
                return jaxbValidateResponse;
            }

        }).when(provider).sendWebServiceWithSecurityHeader(isA(SamlToken.class), anyString(), anyObject());

        FirstTimeRegistrationRequest firstTimeRegistrationRequest = new FirstTimeRegistrationRequestModel();
        firstTimeRegistrationRequest.setAction(ValidatePartyAndSmsAction.REGISTRATION);
        FirstTimeRegistrationResponse response = btesbOnboarding.validateRegistration(firstTimeRegistrationRequest);
        assertThat(response, notNullValue());
        assertThat(response.getServiceErrors(), nullValue());
        assertThat(response.getTransactionId(), Is.is("NEW TRANSACTION ID"));
        assertThat(response.getDeviceId(), Is.is("1234-12345-23456-23456"));
        assertThat(response.getDeviceToken(), Is.is("NEW DEVICE TOKEN"));
        assertThat(response.getSessionId(), Is.is("NEW SESSION ID"));
        assertThat(response.getUserName(), Is.is("987654321"));
    }

    @Test
    public void testValidateRegistrationDetailsForSuccess() {
        jaxbValidateResponseNew = JaxbUtil.unmarshall("/webservices/response/ValidateRegistrationDetailSuccessResponse_UT.xml",
                ns.btfin_com.product.panorama.credentialservice.credentialresponse.v1_0.ValidatePartySMSOneTimePasswordChallengeResponseMsgType.class);

        Mockito.doAnswer(new Answer<ns.btfin_com.product.panorama.credentialservice.credentialresponse.v1_0.ValidatePartySMSOneTimePasswordChallengeResponseMsgType>() {
            @Override
            public ns.btfin_com.product.panorama.credentialservice.credentialresponse.v1_0.ValidatePartySMSOneTimePasswordChallengeResponseMsgType answer(InvocationOnMock invocation) throws Throwable {
                return jaxbValidateResponseNew;
            }

        }).when(provider).sendWebServiceWithSecurityHeader(isA(SamlToken.class), anyString(), anyObject());

        FirstTimeRegistrationRequest firstTimeRegistrationRequest = new FirstTimeRegistrationRequestModel();
        firstTimeRegistrationRequest.setAction(ValidatePartyAndSmsAction.REGISTRATION);
        FirstTimeRegistrationResponse response = btesbOnboarding.validateRegistrationDetails(firstTimeRegistrationRequest);
        assertThat(response, notNullValue());
        assertThat(response.getServiceErrors(), nullValue());
        assertThat(response.getTransactionId(), Is.is("NEW TRANSACTION ID"));
        assertThat(response.getDeviceId(), Is.is("1234-12345-23456-23456"));
        assertThat(response.getDeviceToken(), Is.is("NEW DEVICE TOKEN"));
        assertThat(response.getSessionId(), Is.is("NEW SESSION ID"));
        assertThat(response.getUserName(), Is.is("987654321"));
    }

    @Test
    public void testValidateRegistrationDetailsForError() {
        jaxbValidateResponseNew = JaxbUtil.unmarshall("/webservices/response/ValidateRegistrationDetailErrorResponse_UT.xml",
                ns.btfin_com.product.panorama.credentialservice.credentialresponse.v1_0.ValidatePartySMSOneTimePasswordChallengeResponseMsgType.class);

        Mockito.doAnswer(new Answer<ns.btfin_com.product.panorama.credentialservice.credentialresponse.v1_0.ValidatePartySMSOneTimePasswordChallengeResponseMsgType>() {
            @Override
            public ns.btfin_com.product.panorama.credentialservice.credentialresponse.v1_0.ValidatePartySMSOneTimePasswordChallengeResponseMsgType answer(InvocationOnMock invocation) throws Throwable {
                return jaxbValidateResponseNew;
            }

        }).when(provider).sendWebServiceWithSecurityHeader(isA(SamlToken.class), anyString(), anyObject());

        FirstTimeRegistrationRequest firstTimeRegistrationRequest = new FirstTimeRegistrationRequestModel();
        firstTimeRegistrationRequest.setAction(ValidatePartyAndSmsAction.REGISTRATION);
        FirstTimeRegistrationResponse response = btesbOnboarding.validateRegistrationDetails(firstTimeRegistrationRequest);
        assertThat(response, notNullValue());
        assertThat(response.getServiceErrors(), notNullValue());
        //assertThat(response.getServiceErrors().getError("InvalidParameter"),notNullValue());
        //assertThat(response.getServiceErrors().getError("InvalidParameter").getMessage(),Is.is("Invalid date."));
        //assertThat(response.getServiceErrors().getError("InvalidParameter").getType(),Is.is("Sender"));

    }

    @Test
    public void testValidateRegistrationForError() {
        jaxbValidateResponse = JaxbUtil.unmarshall("/webservices/response/ValidateRegistrationErrorResponse_UT.xml", ValidatePartySMSOneTimePasswordChallengeResponseMsgType.class);

        Mockito.doAnswer(new Answer<ValidatePartySMSOneTimePasswordChallengeResponseMsgType>() {
            @Override
            public ValidatePartySMSOneTimePasswordChallengeResponseMsgType answer(InvocationOnMock invocation) throws Throwable {
                return jaxbValidateResponse;
            }

        }).when(provider).sendWebServiceWithSecurityHeader(isA(SamlToken.class), anyString(), anyObject());

        FirstTimeRegistrationRequest firstTimeRegistrationRequest = new FirstTimeRegistrationRequestModel();
        firstTimeRegistrationRequest.setAction(ValidatePartyAndSmsAction.REGISTRATION);
        FirstTimeRegistrationResponse response = btesbOnboarding.validateRegistration(firstTimeRegistrationRequest);
        assertThat(response, notNullValue());
        assertThat(response.getServiceErrors(), notNullValue());
        //assertThat(response.getServiceErrors().getError("InvalidParameter"),notNullValue());
        //assertThat(response.getServiceErrors().getError("InvalidParameter").getMessage(),Is.is("Invalid date."));
        //assertThat(response.getServiceErrors().getError("InvalidParameter").getType(),Is.is("Sender"));

    }

    @Test
    public void testProcessAdvisersForSuccess() {
        jaxbProcessAdvisersResponse = JaxbUtil.unmarshall("/webservices/response/ProcessAdvisersSuccessResponse_UT.xml", ProvisionOnlineAccessResponseMsgType.class);

        Mockito.doAnswer(new Answer<ProvisionOnlineAccessResponseMsgType>() {
            @Override
            public ProvisionOnlineAccessResponseMsgType answer(
                    InvocationOnMock invocation) throws Throwable {
                return jaxbProcessAdvisersResponse;
            }

        })
                .when(provider).sendWebServiceWithSecurityHeader(isA(SamlToken.class), anyString(), anyObject());

        CreateAccountRequest createAccountRequest = new CreateAccountRequestModel();
        Map<CustomerNoAllIssuerType, String> customerIdentifiers = new HashMap<>();
        customerIdentifiers.put(CustomerNoAllIssuerType.BT_PANORAMA, "217187598");
        customerIdentifiers.put(CustomerNoAllIssuerType.WESTPAC, "217187598");
        createAccountRequest.setCustomerIdentifiers(customerIdentifiers);
        CreateAccountResponse response = btesbOnboarding.processAdvisers(createAccountRequest);
        assertThat(response, notNullValue());
        assertThat(response.getServiceErrors(), nullValue());

    }

    @Test
    public void testProvisionOnlineAccessAndNotifySuccessResponseForAdviser() {
        jaxbProvisionOnlineAccessResponse = JaxbUtil.unmarshall("/webservices/response/ProvisionOnlineAccessAndNotifySuccessResponse_UT.xml", ProvisionOnlineAccessResponseMsgType.class);

        Mockito.doAnswer(new Answer<ProvisionOnlineAccessResponseMsgType>() {
            @Override
            public ProvisionOnlineAccessResponseMsgType answer(
                    InvocationOnMock invocation) throws Throwable {
                return jaxbProvisionOnlineAccessResponse;
            }

        })
                .when(provider).sendWebServiceWithSecurityHeader(isA(SamlToken.class), anyString(), anyObject());

        CreateAccountRequest createAccountRequest = new CreateAccountRequestModel();

        Map<CustomerNoAllIssuerType, String> customerIdentifiers = new HashMap<>();
        customerIdentifiers.put(CustomerNoAllIssuerType.BT_PANORAMA, "217187598");
        customerIdentifiers.put(CustomerNoAllIssuerType.WESTPAC, "217187598");
        createAccountRequest.setCustomerIdentifiers(customerIdentifiers);

        CreateAccountResponse response = btesbOnboarding.processAdvisers(createAccountRequest);
        assertThat(response, notNullValue());
        assertThat(response.getServiceErrors(), nullValue());
    }

    @Test
    public void testProcessAdvisersForError() {
        jaxbProcessAdvisersResponse = JaxbUtil.unmarshall("/webservices/response/ProcessAdvisersErrorResponse_UT.xml", ProvisionOnlineAccessResponseMsgType.class);

        Mockito.doAnswer(new Answer<ProvisionOnlineAccessResponseMsgType>() {
            @Override
            public ProvisionOnlineAccessResponseMsgType answer(InvocationOnMock invocation) {
                return jaxbProcessAdvisersResponse;
            }

        })
                .when(provider).sendWebServiceWithSecurityHeader(isA(SamlToken.class), anyString(), anyObject());

        CreateAccountRequest createAccountRequest = new CreateAccountRequestModel();
        Map<CustomerNoAllIssuerType, String> customerIdentifiers = new HashMap<>();
        customerIdentifiers.put(CustomerNoAllIssuerType.BT_PANORAMA, "217187598");
        customerIdentifiers.put(CustomerNoAllIssuerType.WESTPAC, "217187598");
        createAccountRequest.setCustomerIdentifiers(customerIdentifiers);
        CreateAccountResponse response = btesbOnboarding.processAdvisers(createAccountRequest);
        assertThat(response, notNullValue());
        assertThat(response.getServiceErrors(), notNullValue());
        assertThat(response.getServiceErrors().getErrorList().iterator().next(), notNullValue());
        assertThat(response.getServiceErrors().getErrorList().iterator().next().getType(), Is.is("Receiver"));
    }
}

package com.bt.nextgen.registration.web;

import com.bt.nextgen.api.safi.facade.TwoFactorAuthenticationServiceImpl;
import com.bt.nextgen.cms.service.CmsService;
import com.bt.nextgen.service.prm.service.PrmService;
import com.btfin.panorama.core.security.saml.SamlToken;
import com.bt.nextgen.core.security.api.service.PermissionBaseDtoService;
import com.btfin.panorama.core.security.profile.Profile;
import com.btfin.panorama.core.security.profile.UserProfile;
import com.bt.nextgen.core.security.profile.UserProfileAdapterImpl;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.core.web.RequestQuery;
import com.bt.nextgen.core.web.model.AjaxResponse;
import com.bt.nextgen.login.service.RegistrationService;
import com.bt.nextgen.login.service.ValidateCredentialsResponse;
import com.bt.nextgen.login.web.controller.RegistrationController;
import com.bt.nextgen.login.web.controller.RegistrationResponse;
import com.bt.nextgen.login.web.model.CredentialsModel;
import com.bt.nextgen.login.web.model.RegistrationModel;
import com.bt.nextgen.login.web.model.SmsCodeModel;
import com.bt.nextgen.portfolio.web.model.PortfolioModel;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.domain.IndividualDetailImpl;
import com.bt.nextgen.service.avaloq.rules.AvaloqRulesIntegrationService;
import com.bt.nextgen.service.avaloq.userinformation.JobRole;
import com.bt.nextgen.service.avaloq.userinformation.UserInformationImpl;
import com.bt.nextgen.service.group.customer.CustomerTokenRequest;
import com.bt.nextgen.service.integration.client.ClientIntegrationService;
import com.bt.nextgen.service.integration.user.CISKey;
import com.bt.nextgen.service.integration.userinformation.ClientKey;
import com.bt.nextgen.service.integration.userinformation.JobKey;
import com.btfin.panorama.core.security.integration.userinformation.UserInformation;
import com.btfin.panorama.core.security.integration.userprofile.JobProfile;
import com.bt.nextgen.service.onboarding.ValidatePartyResponse;
import com.bt.nextgen.service.safi.model.SafiAnalyzeAndChallengeResponse;
import com.bt.nextgen.service.safi.model.SafiAuthenticateRequest;
import com.bt.nextgen.service.safi.model.SafiAuthenticateResponse;
import com.bt.nextgen.service.security.SmsService;
import com.bt.nextgen.service.security.model.HttpRequestParams;
import com.bt.nextgen.test.MockAuthentication;
import com.bt.nextgen.util.SamlUtil;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import com.bt.nextgen.web.validator.ValidationErrorCode;
import com.rsa.csd.ws.IdentificationData;
import org.apache.struts.mock.MockHttpSession;
import org.hamcrest.core.Is;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;
import org.springframework.web.servlet.mvc.multiaction.NoSuchRequestHandlingMethodException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.bt.nextgen.core.util.SETTINGS.SAML_HEADER_WBC;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class RegistrationControllerTest extends MockAuthentication {

    public static final String SAML_AUTH_STRING = "<saml:Assertion xmlns:saml=\"urn:oasis:names:tc:SAML:2.0:assertion\"\n" +
            "                xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
            "                xmlns:xs=\"http://www.w3.org/2001/XMLSchema\"\n" +
            "                ID=\"Assertion-uuid6dd5689-0142-1c45-afcb-85b9e7469128\" IssueInstant=\"2013-10-30T00:57:18Z\"\n" +
            "                Version=\"2.0\">\n" +
            "    <saml:Issuer Format=\"urn:oasis:names:tc:SAML:2.0:nameid-format:entity\">http://staff.sts.westpac.com.au</saml:Issuer>\n" +
            "    <ds:Signature xmlns:ds=\"http://www.w3.org/2000/09/xmldsig#\" Id=\"uuid6dd571c-0142-14c0-811f-85b9e7469128\">\n" +
            "        <ds:SignedInfo>\n" +
            "            <ds:CanonicalizationMethod Algorithm=\"http://www.w3.org/2001/10/xml-exc-c14n#\"></ds:CanonicalizationMethod>\n" +
            "            <ds:SignatureMethod Algorithm=\"http://www.w3.org/2000/09/xmldsig#rsa-sha1\"></ds:SignatureMethod>\n" +
            "            <ds:Reference URI=\"#Assertion-uuid6dd5689-0142-1c45-afcb-85b9e7469128\">\n" +
            "                <ds:Transforms>\n" +
            "                    <ds:Transform Algorithm=\"http://www.w3.org/2000/09/xmldsig#enveloped-signature\"></ds:Transform>\n" +
            "                    <ds:Transform Algorithm=\"http://www.w3.org/2001/10/xml-exc-c14n#\">\n" +
            "                        <xc14n:InclusiveNamespaces xmlns:xc14n=\"http://www.w3.org/2001/10/xml-exc-c14n#\"\n" +
            "                                                   PrefixList=\"saml xsi xs\"></xc14n:InclusiveNamespaces>\n" +
            "                    </ds:Transform>\n" +
            "                </ds:Transforms>\n" +
            "                <ds:DigestMethod Algorithm=\"http://www.w3.org/2000/09/xmldsig#sha1\"></ds:DigestMethod>\n" +
            "                <ds:DigestValue>Gg/ybCNcTLsdbte+XgqDgq7MwrA=</ds:DigestValue>\n" +
            "            </ds:Reference>\n" +
            "        </ds:SignedInfo>\n" +
            "        <ds:SignatureValue>\n" +
            "            T99+6bCUug71a8w/+0kLqqAsuwMvh4MIBHTjTdmnadThchTAUp750oFd3cPOfHRWPzTjZw4ff4sPtc4qhPL1mqUy/sqZ3qQEQytdl5OJLLIJ0EAcKnT3AKz+qxn6kicoDOqrEK+8iB4yCYGW/PT3RLrCXffDIP69RxpCfziH0lH7uAk00sko63BEZQQjplNVy1//nuXND1piquDFgClKASQjstIMKL0cLBxmAt4UTfAcIIu3Gqhuo5o/4wADUEuvr1g11lphkHsod3SXIJyItmV8EHhQXhwAxT1w2Dw9gfDwGhWv5CejjIq/64K4PILCQ++y7NqJ8yddsa7WRadhBg==\n" +
            "        </ds:SignatureValue>\n" +
            "        <ds:KeyInfo>\n" +
            "            <ds:X509Data>\n" +
            "                <ds:X509Certificate>\n" +
            "                    MIIGiTCCBfKgAwIBAgIKZeqb7AACAACLCjANBgkqhkiG9w0BAQUFADCBhzESMBAGCgmSJomT8ixkARkWAmF1MRMwEQYKCZImiZPyLGQBGRYDY29tMRcwFQYKCZImiZPyLGQBGRYHd2VzdHBhYzEVMBMGCgmSJomT8ixkARkWBXdiY2F1MRUwEwYKCZImiZPyLGQBGRYFaW5mYXUxFTATBgNVBAMTDGF1MjAwNHNwMDA0MjAeFw0xMzAzMTIwNDE3MTlaFw0xNDA1MDYwNDE3MTlaMIGhMQswCQYDVQQGEwJBVTEYMBYGA1UECBMPTmV3IFNvdXRoIFdhbGVzMQ8wDQYDVQQHEwZTeWRuZXkxJDAiBgNVBAoTG1dlc3RwYWMgQmFua2luZyBDb3Jwb3JhdGlvbjEXMBUGA1UECxMOR3JvdXAgU2VydmljZXMxKDAmBgNVBAMTH2ZpbS1zaXQuaW50cmFuZXQud2VzdHBhYy5jb20uYXUwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQCf+7FzVkAfruRHeerb70+RyzilP3kHqg7KsaRMxN5EG4oUCgeC21F4wI3PWiJZO3Rl+cPfcgEzuXIuhJ+aQ3eh767TRyKIcnwe4DZCSHQl41/qdTZuGCmqWipb9Ixo2bd4jXJYm8QZP/S2SEwSDw/ylmFqRTBFCJDxGmrcdUU89fHCLGIALmrKSKOjBBeBhbAfsFkMqrkn11Qbs7oOofX0duNcssozNLwxIdKRItEfrWMnxg5KBKXuKtHGSNfl7pZzECPpBF1tXpEmt6mgK3V3JBTngir+m41zGwYHxTHscAWMQ7NF6NCQmfccFFivUJca4iMNgXjSA/SscrqEehI7AgMBAAGjggNaMIIDVjAdBgNVHQ4EFgQUrQApS6ajgHG3E4JB3TUEaXkSL5owHwYDVR0jBBgwFoAUz7t2hWQeSojWQtk1BjkCz+oL4P0wggEoBgNVHR8EggEfMIIBGzCCARegggEToIIBD4aBymxkYXA6Ly8vQ049YXUyMDA0c3AwMDQyKDIpLENOPWF1MjAwNHNwMDA0MixDTj1DRFAsQ049UHVibGljJTIwS2V5JTIwU2VydmljZXMsQ049U2VydmljZXMsQ049Q29uZmlndXJhdGlvbixEQz13YmNhdSxEQz13ZXN0cGFjLERDPWNvbSxEQz1hdT9jZXJ0aWZpY2F0ZVJldm9jYXRpb25MaXN0P2Jhc2U/b2JqZWN0Q2xhc3M9Y1JMRGlzdHJpYnV0aW9uUG9pbnSGQGh0dHA6Ly92cG5jZHAuaW5mYXUud2JjYXUud2VzdHBhYy5jb20uYXUvY3JsL2F1MjAwNHNwMDA0MigyKS5jcmwwggFFBggrBgEFBQcBAQSCATcwggEzMIG6BggrBgEFBQcwAoaBrWxkYXA6Ly8vQ049YXUyMDA0c3AwMDQyLENOPUFJQSxDTj1QdWJsaWMlMjBLZXklMjBTZXJ2aWNlcyxDTj1TZXJ2aWNlcyxDTj1Db25maWd1cmF0aW9uLERDPXdiY2F1LERDPXdlc3RwYWMsREM9Y29tLERDPWF1P2NBQ2VydGlmaWNhdGU/YmFzZT9vYmplY3RDbGFzcz1jZXJ0aWZpY2F0aW9uQXV0aG9yaXR5MHQGCCsGAQUFBzAChmhodHRwOi8vdnBuY2RwLmluZmF1LndiY2F1Lndlc3RwYWMuY29tLmF1L2NybC9hdTIwMDRzcDAwNDIuaW5mYXUud2JjYXUud2VzdHBhYy5jb20uYXVfYXUyMDA0c3AwMDQyKDIpLmNydDAMBgNVHRMBAf8EAjAAMAsGA1UdDwQEAwIFoDA8BgkrBgEEAYI3FQcELzAtBiUrBgEEAYI3FQiF3rMl4IULgtGNKYfK4XrH/BOBWIbqtn2G1tQlAgFkAgECMB0GA1UdJQQWMBQGCCsGAQUFBwMCBggrBgEFBQcDATAnBgkrBgEEAYI3FQoEGjAYMAoGCCsGAQUFBwMCMAoGCCsGAQUFBwMBMA0GCSqGSIb3DQEBBQUAA4GBADaziDNfn4T9cOCvZBGKxYx81c9E+eKOWB4Rr8QYBjAvsvBSCj3JadQ/tdk3IF9VLCdDfIIIZ2vepb6Kanx6Ht5k80tNTz9tljxZtOIWX/rcovtUEECCsLUQo7Pc4ajDNNfx57xSSULRc6afQF7Q2JOBYsL0+UmhvjytS2pP6KT1\n" +
            "                </ds:X509Certificate>\n" +
            "            </ds:X509Data>\n" +
            "        </ds:KeyInfo>\n" +
            "    </ds:Signature>\n" +
            "    <saml:Subject>\n" +
            "        <saml:NameID Format=\"urn:ibm:names:ITFIM:5.1:accessmanager\">CS057462</saml:NameID>\n" +
            "        <saml:SubjectConfirmation Method=\"urn:oasis:names:tc:SAML:2.0:cm:bearer\"></saml:SubjectConfirmation>\n" +
            "    </saml:Subject>\n" +
            "    <saml:Conditions NotBefore=\"2013-10-29T23:57:18Z\" NotOnOrAfter=\"2013-10-30T10:57:18Z\">\n" +
            "        <saml:AudienceRestriction>\n" +
            "            <saml:Audience>http://esi2.westpac.com.au</saml:Audience>\n" +
            "        </saml:AudienceRestriction>\n" +
            "    </saml:Conditions>\n" +
            "    <saml:AuthnStatement AuthnInstant=\"2013-10-30T00:57:18Z\">\n" +
            "        <saml:AuthnContext>\n" +
            "            <saml:AuthnContextClassRef>urn:oasis:names:tc:SAML:2.0:ac:classes:Password</saml:AuthnContextClassRef>\n" +
            "        </saml:AuthnContext>\n" +
            "    </saml:AuthnStatement>\n" +
            "    <saml:AttributeStatement>\n" +
            "        <saml:Attribute Name=\"AZN_CRED_NETWORK_ADDRESS_BIN\" NameFormat=\"urn:ibm:names:ITFIM:5.1:accessmanager\">\n" +
            "            <saml:AttributeValue xsi:type=\"xs:string\">0x0a94c512</saml:AttributeValue>\n" +
            "        </saml:Attribute>\n" +
            "        <saml:Attribute Name=\"AZN_CRED_BROWSER_INFO\" NameFormat=\"urn:ibm:names:ITFIM:5.1:accessmanager\">\n" +
            "            <saml:AttributeValue xsi:type=\"xs:string\">Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML,\n" +
            "                like Gecko) Chrome/30.0.1599.101 Safari/537.36\n" +
            "            </saml:AttributeValue>\n" +
            "        </saml:Attribute>\n" +
            "        <saml:Attribute Name=\"AZN_CRED_AUTH_METHOD\" NameFormat=\"urn:ibm:names:ITFIM:5.1:accessmanager\">\n" +
            "            <saml:AttributeValue xsi:type=\"xs:string\">password</saml:AttributeValue>\n" +
            "        </saml:Attribute>\n" +
            "        <saml:Attribute Name=\"AZN_CRED_PRINCIPAL_NAME\" NameFormat=\"urn:ibm:names:ITFIM:5.1:accessmanager\">\n" +
            "            <saml:AttributeValue xsi:type=\"xs:string\">%3$s</saml:AttributeValue>\n" +
            "        </saml:Attribute>\n" +
            "        <saml:Attribute Name=\"tagvalue_login_user_name\" NameFormat=\"urn:ibm:names:ITFIM:5.1:accessmanager\">\n" +
            "            <saml:AttributeValue xsi:type=\"xs:string\">%1$s</saml:AttributeValue>\n" +
            "        </saml:Attribute>\n" +
            "        <saml:Attribute Name=\"AUTHENTICATION_LEVEL\" NameFormat=\"urn:ibm:names:ITFIM:5.1:accessmanager\">\n" +
            "            <saml:AttributeValue xsi:type=\"xs:string\">1</saml:AttributeValue>\n" +
            "        </saml:Attribute>\n" +
            "        <saml:Attribute Name=\"AZN_CRED_PRINCIPAL_UUID\" NameFormat=\"urn:ibm:names:ITFIM:5.1:accessmanager\">\n" +
            "            <saml:AttributeValue xsi:type=\"xs:string\">6bf5511a-bf71-11e2-95bb-0a05eb2faa77</saml:AttributeValue>\n" +
            "        </saml:Attribute>\n" +
            "        <saml:Attribute Name=\"AZN_CRED_PRINCIPAL_DOMAIN\" NameFormat=\"urn:ibm:names:ITFIM:5.1:accessmanager\">\n" +
            "            <saml:AttributeValue xsi:type=\"xs:string\">Default</saml:AttributeValue>\n" +
            "        </saml:Attribute>\n" +
            "        <saml:Attribute Name=\"AZN_CRED_GROUPS\" NameFormat=\"urn:ibm:names:ITFIM:5.1:accessmanager\">\n" +
            "            <saml:AttributeValue xsi:type=\"xs:string\">knomeUsers</saml:AttributeValue>\n" +
            "            <saml:AttributeValue xsi:type=\"xs:string\">hswUsers</saml:AttributeValue>\n" +
            "            <saml:AttributeValue xsi:type=\"xs:string\">ngUsers</saml:AttributeValue>\n" +
            "        </saml:Attribute>\n" +
            "        <saml:Attribute Name=\"tagvalue_session_index\" NameFormat=\"urn:ibm:names:ITFIM:5.1:accessmanager\">\n" +
            "            <saml:AttributeValue xsi:type=\"xs:string\">394d006e-40fe-11e3-ba96-0a05eb2faa77</saml:AttributeValue>\n" +
            "        </saml:Attribute>\n" +
            "        <saml:Attribute Name=\"AZN_CRED_NETWORK_ADDRESS_STR\" NameFormat=\"urn:ibm:names:ITFIM:5.1:accessmanager\">\n" +
            "            <saml:AttributeValue xsi:type=\"xs:string\">10.148.197.18</saml:AttributeValue>\n" +
            "        </saml:Attribute>\n" +
            "        <saml:Attribute Name=\"AZN_CRED_IP_FAMILY\" NameFormat=\"urn:ibm:names:ITFIM:5.1:accessmanager\">\n" +
            "            <saml:AttributeValue xsi:type=\"xs:string\">AF_INET</saml:AttributeValue>\n" +
            "        </saml:Attribute>\n" +
            "        <saml:Attribute Name=\"AZN_CRED_AUTHZN_ID\" NameFormat=\"urn:ibm:names:ITFIM:5.1:accessmanager\">\n" +
            "            <saml:AttributeValue xsi:type=\"xs:string\">uid=M034791,ou=Users,o=WESTPAC</saml:AttributeValue>\n" +
            "        </saml:Attribute>\n" +
            "        <saml:Attribute Name=\"AZN_CRED_QOP_INFO\" NameFormat=\"urn:ibm:names:ITFIM:5.1:accessmanager\">\n" +
            "            <saml:AttributeValue xsi:type=\"xs:string\">SSK: TLSV1: 35</saml:AttributeValue>\n" +
            "        </saml:Attribute>\n" +
            "        <saml:Attribute Name=\"AZN_CRED_VERSION\" NameFormat=\"urn:ibm:names:ITFIM:5.1:accessmanager\">\n" +
            "            <saml:AttributeValue xsi:type=\"xs:string\">0x00000611</saml:AttributeValue>\n" +
            "        </saml:Attribute>\n" +
            "        <saml:Attribute Name=\"AZN_CRED_MECH_ID\" NameFormat=\"urn:ibm:names:ITFIM:5.1:accessmanager\">\n" +
            "            <saml:AttributeValue xsi:type=\"xs:string\">IV_LDAP_V3.0</saml:AttributeValue>\n" +
            "        </saml:Attribute>\n" +
            "        <saml:Attribute Name=\"AZN_CRED_REGISTRY_ID\" NameFormat=\"urn:ibm:names:ITFIM:5.1:accessmanager\">\n" +
            "            <saml:AttributeValue xsi:type=\"xs:string\">uid=M034791,ou=Users,o=WESTPAC</saml:AttributeValue>\n" +
            "        </saml:Attribute>\n" +
            "        <saml:Attribute Name=\"AZN_CRED_AUTHNMECH_INFO\" NameFormat=\"urn:ibm:names:ITFIM:5.1:accessmanager\">\n" +
            "            <saml:AttributeValue xsi:type=\"xs:string\">LDAP Registry</saml:AttributeValue>\n" +
            "        </saml:Attribute>\n" +
            "        <saml:Attribute Name=\"tagvalue_failover_amweb_session_id\" NameFormat=\"urn:ibm:names:ITFIM:5.1:accessmanager\">\n" +
            "            <saml:AttributeValue xsi:type=\"xs:string\">2_1_USNnh-io+IPHjizOmEoJPfK0yXXCBVGur-EU-IRcBUJfoFHz\n" +
            "            </saml:AttributeValue>\n" +
            "        </saml:Attribute>\n" +
            "    </saml:AttributeStatement>\n" +
            "</saml:Assertion>";
    @InjectMocks
    private RegistrationController registrationController;

    MockHttpServletRequest request;
    MockHttpServletResponse response;
    MockHttpSession mockHttpSession;

    AnnotationMethodHandlerAdapter annotationMethodHandlerAdapter;

    @Mock
    private RegistrationService registrationService;

    @Mock
    private CmsService cmsService;

    @Mock
    private SmsService smsService;

    @Mock
    private SmsCodeModel conversation;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private RegistrationModel registrationModel;

    @Mock
    private CredentialsModel credentialsModel;

    @Mock
    private RequestQuery requestQuery;

    @Mock
    UserProfileService userProfileService;

    @Mock
    private PermissionBaseDtoService permissionBaseService;

    @Mock
    private ClientIntegrationService clientIntegrationService;

    @Mock
    private AvaloqRulesIntegrationService rulesIntegrationService;

    @Mock
    private TwoFactorAuthenticationServiceImpl twoFactorAuthenticationFacade;

    @Mock
    private PrmService prmService;

    private SamlToken samlToken;

    @Before
    public void setup() throws Exception {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        mockHttpSession = new MockHttpSession();
        when(bindingResult.hasErrors()).thenReturn(false);
        annotationMethodHandlerAdapter = new AnnotationMethodHandlerAdapter();
        HttpMessageConverter[] messageConverters =
                {
                        new MappingJackson2HttpMessageConverter()
                };
        annotationMethodHandlerAdapter.setMessageConverters(messageConverters);
    }

    @Test
    public void testValidateCredentialsValidationFailed() throws Exception {
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(true);
        AjaxResponse ajaxResponse = registrationController.validateCredentials(credentialsModel, bindingResult, request);
        assertFalse("Check validateCredentials status is failed",
                ajaxResponse.isSuccess());
    }

    @Test
    public void testValidateCredentialsSamlAuthenticated() throws Exception {
        request = new MockHttpServletRequest();
        request.addHeader(SAML_HEADER_WBC.value(), SAML_AUTH_STRING);

        AjaxResponse ajaxResponse = registrationController.validateCredentials(credentialsModel, bindingResult, request);
        assertFalse("Check validateCredentials status is failed",
                ajaxResponse.isSuccess());
    }

    @Test
    public void testValidateRegistrationSamlAuthenticated() throws Exception {
        request = new MockHttpServletRequest();
        request.addHeader(SAML_HEADER_WBC.value(), SAML_AUTH_STRING);

        AjaxResponse ajaxResponse = registrationController.validateRegistration(conversation,
                bindingResult,
                request,
                mockHttpSession);

        assertThat(ajaxResponse.isSuccess(), Is.is(Boolean.FALSE));
    }

    @Test
    public void testValidateCredentialsSuccess() throws Exception {
        ValidateCredentialsResponse serviceResponse = mock(ValidateCredentialsResponse.class);

        ValidatePartyResponse partyResponse = mock(ValidatePartyResponse.class);
        when(partyResponse.getDeviceId()).thenReturn("cfe2-sdd4-ertt-3wer");

        when(serviceResponse.getStatusCode()).thenReturn(
                Attribute.SUCCESS_MESSAGE);
        when(serviceResponse.getUserName()).thenReturn("test-username");
        when(serviceResponse.getZNumber()).thenReturn("123-aa-tt");
        when(serviceResponse.getValidatePartyResponse()).thenReturn(
                partyResponse);

        when(registrationService.validateParty(any(CredentialsModel.class)))
                .thenReturn(serviceResponse);
        RegistrationResponse registrationResp = mock(RegistrationResponse.class);
        when(registrationResp.getEamPostUrl()).thenReturn("postUrl");
        when(registrationResp.getRelayState()).thenReturn("relayState");
        when(registrationResp.getSAMLResponse()).thenReturn("saml_resp");
        when(
                registrationService.registrationResponseForOptionalTwoFA(
                        any(CustomerTokenRequest.class),
                        any(HttpServletRequest.class), anyString(),
                        anyString(), anyString(), anyString())).thenReturn(
                registrationResp);

        AjaxResponse ajaxResponse = registrationController.validateCredentials(
                credentialsModel, bindingResult, request);
        assertTrue("Check validateCredentials status is success",
                ajaxResponse.isSuccess());
        assertThat(ajaxResponse.getData(), notNullValue());
        assertThat(response.getStatus(), Is.is(HttpServletResponse.SC_OK));

        RegistrationResponse registrationResponse = (RegistrationResponse) ajaxResponse
                .getData();
        assertNotNull(registrationResponse.getEamPostUrl());
        assertNotNull(registrationResp.getRelayState());
        assertNotNull(registrationResp.getSAMLResponse());
    }

    @Test
    public void testValidateCredentialsFail() throws Exception {
        String errorMessage = "this is test error message";
        ValidateCredentialsResponse serviceResponse = mock(ValidateCredentialsResponse.class);
        when(serviceResponse.getStatusCode()).thenReturn(errorMessage);
        when(registrationService.validateParty(any(CredentialsModel.class)))
                .thenReturn(serviceResponse);
        AjaxResponse ajaxResponse = registrationController.validateCredentials(
                credentialsModel, bindingResult, request);
        assertThat(ajaxResponse.isSuccess(), Is.is(Boolean.FALSE));
    }

    @Test
    public void testValidateCredentialException() throws Exception {

        ValidateCredentialsResponse serviceResponse = mock(ValidateCredentialsResponse.class);
        when(serviceResponse.getStatusCode()).thenReturn(
                Attribute.SUCCESS_MESSAGE);
        when(serviceResponse.getUserName()).thenReturn(null);
        when(registrationService.validateParty(credentialsModel)).thenReturn(
                serviceResponse);
        Throwable throwable = new Throwable("test");
        when(
                registrationService.registrationResponseForOptionalTwoFA(
                        any(CustomerTokenRequest.class),
                        any(HttpServletRequest.class), anyString(),
                        anyString(), anyString(), anyString())).thenThrow(
                new RuntimeException("test", throwable));
        AjaxResponse ajaxResponse = registrationController.validateCredentials(
                credentialsModel, bindingResult, request);
        assertThat(ajaxResponse.isSuccess(), Is.is(Boolean.FALSE));
    }

    @Test
    public void testForgotPasswordSuccess() throws Exception {
        ValidateCredentialsResponse serviceResponse = mock(ValidateCredentialsResponse.class);

        ValidatePartyResponse partyResponse = mock(ValidatePartyResponse.class);
        when(partyResponse.getDeviceId()).thenReturn("cfe2-sdd4-ertt-3wer");

        when(serviceResponse.getStatusCode()).thenReturn(
                Attribute.SUCCESS_MESSAGE);
        when(serviceResponse.getUserName()).thenReturn("test-username");
        when(serviceResponse.getZNumber()).thenReturn("123-aa-tt");
        when(serviceResponse.getValidatePartyResponse()).thenReturn(
                partyResponse);

        when(registrationService.validateParty(any(CredentialsModel.class)))
                .thenReturn(serviceResponse);
        RegistrationResponse registrationResp = mock(RegistrationResponse.class);
        when(registrationResp.getEamPostUrl()).thenReturn("postUrl");
        when(registrationResp.getRelayState()).thenReturn("relayState");
        when(registrationResp.getSAMLResponse()).thenReturn("saml_resp");
        when(
                registrationService.registrationResponseForOptionalTwoFA(
                        any(CustomerTokenRequest.class),
                        any(HttpServletRequest.class), anyString(),
                        anyString(), anyString(), anyString())).thenReturn(
                registrationResp);

        AjaxResponse ajaxResponse = registrationController.forgotPassword(
                credentialsModel, bindingResult, request);
        assertTrue("Check validateCredentials status is success",
                ajaxResponse.isSuccess());
        assertThat(ajaxResponse.getData(), notNullValue());
        assertThat(response.getStatus(), Is.is(HttpServletResponse.SC_OK));

        RegistrationResponse registrationResponse = (RegistrationResponse) ajaxResponse
                .getData();
        assertNotNull(registrationResponse.getEamPostUrl());
        assertNotNull(registrationResp.getRelayState());
        assertNotNull(registrationResp.getSAMLResponse());
    }

    @Test
    public void testForgotPasswordFail() throws Exception {
        String errorMessage = "this is test error message";
        ValidateCredentialsResponse serviceResponse = mock(ValidateCredentialsResponse.class);
        when(serviceResponse.getStatusCode()).thenReturn(errorMessage);
        when(registrationService.validateParty(any(CredentialsModel.class)))
                .thenReturn(serviceResponse);
        AjaxResponse ajaxResponse = registrationController.forgotPassword(
                credentialsModel, bindingResult, request);
        assertThat(ajaxResponse.isSuccess(), Is.is(Boolean.FALSE));
    }

    @Test
    public void testForgotPasswordException() throws Exception {

        ValidateCredentialsResponse serviceResponse = mock(ValidateCredentialsResponse.class);
        when(serviceResponse.getStatusCode()).thenReturn(
                Attribute.SUCCESS_MESSAGE);
        when(serviceResponse.getUserName()).thenReturn(null);
        when(registrationService.validateParty(credentialsModel)).thenReturn(
                serviceResponse);
        Throwable throwable = new Throwable("test");
        when(
                registrationService.registrationResponseForOptionalTwoFA(
                        any(CustomerTokenRequest.class),
                        any(HttpServletRequest.class), anyString(),
                        anyString(), anyString(), anyString())).thenThrow(
                new RuntimeException("test", throwable));
        AjaxResponse ajaxResponse = registrationController.forgotPassword(
                credentialsModel, bindingResult, request);
        assertThat(ajaxResponse.isSuccess(), Is.is(Boolean.FALSE));
    }

    @Test
    public void testForgotPasswordBindingResultError() throws Exception {

        ValidateCredentialsResponse serviceResponse = mock(ValidateCredentialsResponse.class);
        when(serviceResponse.getStatusCode()).thenReturn(
                Attribute.SUCCESS_MESSAGE);
        when(serviceResponse.getUserName()).thenReturn("test");
        when(registrationService.validateParty(credentialsModel)).thenReturn(
                serviceResponse);
        when(bindingResult.hasErrors()).thenReturn(true);
        AjaxResponse ajaxResponse = registrationController.forgotPassword(
                credentialsModel, bindingResult, request);
        assertThat(ajaxResponse.isSuccess(), Is.is(Boolean.FALSE));
        assertThat(ajaxResponse.getData(), notNullValue());
    }

    @Test
    public void testVerifyDealerGroupSmsAndRegistration() throws Exception {

        IdentificationData idData = mock(IdentificationData.class);
        SafiAnalyzeAndChallengeResponse analyzeResp = mock(SafiAnalyzeAndChallengeResponse.class);
        when(analyzeResp.getIdentificationData()).thenReturn(idData);
        when(analyzeResp.getRuleId()).thenReturn("1234567");

        SafiAuthenticateResponse safiResponse = mock(SafiAuthenticateResponse.class);
        when(safiResponse.isSuccessFlag()).thenReturn(true);

        RegistrationResponse regResp = mock(RegistrationResponse.class);

        when(twoFactorAuthenticationFacade.authenticate(any(SafiAuthenticateRequest.class))).thenReturn(safiResponse);
        when(registrationService.registrationResponseForOptionalTwoFA(any(CustomerTokenRequest.class), any(HttpServletRequest.class), anyString(), anyString(), anyString(), anyString())).thenReturn(regResp);
        when(conversation.getUserCode()).thenReturn("12");

        mockHttpSession = new MockHttpSession();
        mockHttpSession.setAttribute(RegistrationController.SAFI_ANALYZE_RESULT_ATTRIBUTE, analyzeResp);

        request.setSession(mockHttpSession);
        AjaxResponse ajaxResponse = registrationController.verifyDealerGroupSmsAndRegistration(conversation, bindingResult, request);
        assertThat(ajaxResponse.isSuccess(), Is.is(Boolean.TRUE));
    }

    @Test
    public void testRegisterUser() throws Exception {
        UserProfile activeProfile = getProfile(JobRole.ADVISER, "job id 1", "client1");
        when(userProfileService.getActiveProfile()).thenReturn(activeProfile);
        samlToken = new SamlToken(SamlUtil.loadWplSamlNewPanoramaCustomer());
        Mockito.when(userProfileService.getSamlToken()).thenReturn(samlToken);
        when(userProfileService.getUserId()).thenReturn("201603880");
        IndividualDetailImpl individualDetailImpl = Mockito.mock(IndividualDetailImpl.class);
        when(individualDetailImpl.getCISKey()).thenReturn(CISKey.valueOf("83971220010"));
        when(userProfileService.getActiveProfile()).thenReturn(mock(UserProfile.class));
        when(userProfileService.getUserId()).thenReturn("201603880");
        when(userProfileService.isEmulating()).thenReturn(true);
        when(userProfileService.getEffectiveProfile()).thenReturn(mock(Profile.class));
        when(userProfileService.getEffectiveProfile().getUserId()).thenReturn("201603880");
        when(userProfileService.getActiveProfile().getUsername()).thenReturn("201603880");
        when(userProfileService.getUsername()).thenReturn("Darryl");
        when(permissionBaseService.hasBasicPermission("feature.global.prmNonValueEvents")).thenReturn(true);

        mockHttpSession = new MockHttpSession();
        mockHttpSession.setAttribute("userCode", "110011001100");
        request.setSession(mockHttpSession);

        mockAuthentication("investor");
        PortfolioModel model = new PortfolioModel();
        model.setPortfolioId(EncodedString.fromPlainText("121212"));
        Mockito.when(registrationService.createUser(any(RegistrationModel.class), any(ServiceErrors.class))).thenReturn(Attribute.SUCCESS_MESSAGE);
        AjaxResponse ajaxResponse = registrationController.registerUser(registrationModel, bindingResult, request);
        assertThat(ajaxResponse.isSuccess(), Is.is(Boolean.TRUE));
    }

    @Test
    public void testRegisterUser_ErrorRegistration() throws Exception {
        UserProfile activeProfile = getProfile(JobRole.ADVISER, "job id 1", "client1");
        when(userProfileService.getActiveProfile()).thenReturn(activeProfile);
        samlToken = new SamlToken(SamlUtil.loadWplSamlNewPanoramaCustomer());
        Mockito.when(userProfileService.getSamlToken()).thenReturn(samlToken);
        when(userProfileService.getUserId()).thenReturn("201603880");
        when(permissionBaseService.hasBasicPermission("feature.global.prmNonValueEvents")).thenReturn(true);
        when(registrationService.createUser(registrationModel, mock(ServiceErrors.class))).thenReturn(Attribute.FAILURE_MESSAGE);
        when(cmsService.getContent(ValidationErrorCode.ERROR_IN_REGISTRATION)).thenReturn("Error in registering user");
        PortfolioModel model = new PortfolioModel();
        model.setPortfolioId(EncodedString.fromPlainText("121212"));
        AjaxResponse ajaxResponse = registrationController.registerUser(registrationModel, bindingResult, request);
        assertThat(ajaxResponse.isSuccess(), Is.is(Boolean.FALSE));
    }

    @Test
    public void testRegisterUser_BindingError() throws Exception {
        UserProfile activeProfile = getProfile(JobRole.ADVISER, "job id 1", "client1");
        when(userProfileService.getActiveProfile()).thenReturn(activeProfile);
        samlToken = new SamlToken(SamlUtil.loadWplSamlNewPanoramaCustomer());
        Mockito.when(userProfileService.getSamlToken()).thenReturn(samlToken);
        when(userProfileService.getUserId()).thenReturn("201603880");
        IndividualDetailImpl individualDetailImpl = Mockito.mock(IndividualDetailImpl.class);
        when(individualDetailImpl.getCISKey()).thenReturn(CISKey.valueOf("83971220010"));
        when(userProfileService.getActiveProfile()).thenReturn(mock(UserProfile.class));
        when(userProfileService.getUserId()).thenReturn("201603880");
        when(userProfileService.isEmulating()).thenReturn(true);
        when(userProfileService.getEffectiveProfile()).thenReturn(mock(Profile.class));
        when(userProfileService.getEffectiveProfile().getUserId()).thenReturn("201603880");
        when(userProfileService.getActiveProfile().getUsername()).thenReturn("201603880");
        when(userProfileService.getUsername()).thenReturn("Darryl");
        when(permissionBaseService.hasBasicPermission("feature.global.prmNonValueEvents")).thenReturn(true);
        when(bindingResult.hasErrors()).thenReturn(true);
        when(registrationService.createUser(registrationModel, mock(ServiceErrors.class))).thenReturn(Attribute.FAILURE_MESSAGE);
        AjaxResponse ajaxResponse = registrationController.registerUser(registrationModel, bindingResult, request);
        assertThat(ajaxResponse.isSuccess(), Is.is(Boolean.FALSE));
        assertThat(ajaxResponse.getData(), notNullValue());
    }

    @Test(expected = HttpRequestMethodNotSupportedException.class)
    public void testRegisterUser_RequestMethod() throws Exception {
        request.setMethod(RequestMethod.GET.name());
        request.setRequestURI("/secure/api/registerUser");

        when(registrationService.createUser(registrationModel, mock(ServiceErrors.class))).thenReturn(Attribute.SUCCESS_MESSAGE);
        annotationMethodHandlerAdapter.handle(request, response, registrationController);
    }

    @Test
    public void testRegisterUser_URL() throws Exception {
        request.setMethod(RequestMethod.POST.name());
        request.setRequestURI("/secure/api/registerUser");
        mockAuthentication("investor");
        when(registrationService.createUser(registrationModel, mock(ServiceErrors.class))).thenReturn(Attribute.SUCCESS_MESSAGE);
        PortfolioModel model = new PortfolioModel();
        model.setPortfolioId(EncodedString.fromPlainText("121212"));
        annotationMethodHandlerAdapter.handle(request, response, registrationController);
        assertThat(response.getStatus(), Is.is(HttpServletResponse.SC_OK));
    }

    @Test
    public void testvalidateRegistration_Model() throws Exception {
        request.setSession(mockHttpSession);
        SafiAnalyzeAndChallengeResponse safiAnalyse = new SafiAnalyzeAndChallengeResponse();
        when(registrationService.validRegistration(any(SmsCodeModel.class), any(HttpRequestParams.class))).thenReturn(safiAnalyse);
        AjaxResponse ajaxResponse = registrationController.validateRegistration(conversation,
                bindingResult,
                request,
                mockHttpSession);
        //assertThat(ajaxResponse.isSuccess(), Is.is(Boolean.TRUE));

        when(cmsService.getContent(ValidationErrorCode.EXPIRED_REGISTRATION_CODE)).thenReturn("EXPIRED_REGISTRATION_CODE");
        when(registrationService.validRegistration(any(SmsCodeModel.class), any(HttpRequestParams.class))).thenReturn(safiAnalyse);
        ajaxResponse = registrationController.validateRegistration(conversation, bindingResult, request, mockHttpSession);
        assertThat(ajaxResponse.isSuccess(), Is.is(Boolean.FALSE));
        assertThat((String) ajaxResponse.getData(), notNullValue());

        when(cmsService.getContent(ValidationErrorCode.FORGETPASSWORD_INVALID_DATA)).thenReturn("FORGETPASSWORD_INVALID_DATA");
        when(registrationService.validRegistration(any(SmsCodeModel.class), any(HttpRequestParams.class))).thenReturn(safiAnalyse);
        ajaxResponse = registrationController.validateRegistration(conversation, bindingResult, request, mockHttpSession);
        assertThat(ajaxResponse.isSuccess(), Is.is(Boolean.FALSE));
        assertThat((String) ajaxResponse.getData(), notNullValue());
    }

    @Test(expected = HttpRequestMethodNotSupportedException.class)
    public void testvalidateRegistration_RequestMethod() throws Exception {
        request.setMethod(RequestMethod.POST.name());
        request.setRequestURI("/public/api/validateRegistration");

        annotationMethodHandlerAdapter.handle(request, response, registrationController);
    }

    @Test
    public void testvalidateRegistration_URL() throws Exception {
        request.setMethod(RequestMethod.GET.name());
        request.setRequestURI("/public/api/validateRegistration");
        SafiAnalyzeAndChallengeResponse safiAnalyse = new SafiAnalyzeAndChallengeResponse();
        when(registrationService.validRegistration(any(SmsCodeModel.class), any(HttpRequestParams.class))).thenReturn(safiAnalyse);
        annotationMethodHandlerAdapter.handle(request, response, registrationController);
        assertThat(response.getStatus(), Is.is(HttpServletResponse.SC_OK));
    }

    @Test
    public void testvalidateRegistration_BindingError() throws Exception {
        when(bindingResult.hasErrors()).thenReturn(true);
        AjaxResponse ajaxResponse = registrationController.validateRegistration(conversation,
                bindingResult,
                request,
                mockHttpSession);
        assertThat(ajaxResponse.isSuccess(), Is.is(Boolean.FALSE));
        assertThat(ajaxResponse.getData(), notNullValue());
    }

    @Test(expected = HttpRequestMethodNotSupportedException.class)
    public void testVerifySmsCode_RequestMethod() throws Exception {
        request.setMethod(RequestMethod.POST.name());
        request.setRequestURI("/public/api/verifySmsCode");
        annotationMethodHandlerAdapter.handle(request, response, registrationController);
    }

    @Test
    public void testVerifySmsCode_URL() throws Exception {
        request.setMethod(RequestMethod.GET.name());
        request.setRequestURI("/public/api/verifySmsCode");
        annotationMethodHandlerAdapter.handle(request, response, registrationController);
        assertThat(response.getStatus(), Is.is(HttpServletResponse.SC_OK));
    }

    @Test(expected = HttpRequestMethodNotSupportedException.class)
    public void testVerifySmsAndRegistration_RequestMethod() throws Exception {
        request.setRequestURI("/public/api/verifySmsAndRegistration");
        request.setMethod(RequestMethod.POST.name());
        annotationMethodHandlerAdapter.handle(request, response, registrationController);
    }

    @Test
    public void testVerifySmsAndRegistration_URL() throws Exception {
        request.setRequestURI("/public/api/verifySmsAndRegistration");
        request.setMethod(RequestMethod.GET.name());
        request.setParameter("smsCode", "smsCode");
        when(cmsService.getContent(anyString())).thenReturn("test");
        annotationMethodHandlerAdapter.handle(request, response, registrationController);
        assertThat(response.getStatus(), Is.is(HttpServletResponse.SC_OK));
    }

    @Test
    public void testVerifySmsAndRegistration_BindingError() throws Exception {
        when(bindingResult.hasErrors()).thenReturn(true);
        SafiAuthenticateResponse safiResponse = mock(SafiAuthenticateResponse.class);
        when(safiResponse.isSuccessFlag()).thenReturn(true);

        when(twoFactorAuthenticationFacade.authenticate(any(SafiAuthenticateRequest.class))).thenReturn(safiResponse);
        AjaxResponse ajaxResponse = registrationController.verifySmsAndRegistration(conversation, bindingResult, request);
        assertThat(ajaxResponse.isSuccess(), Is.is(Boolean.FALSE));
        assertThat(ajaxResponse.getData(), notNullValue());
    }

    @Test
    public void testVerifySmsAndRegistrationGetSmsFails() throws Exception {
        SmsCodeModel conversation = mock(SmsCodeModel.class);
        when(conversation.getSmsCode()).thenReturn("12345");

        SafiAuthenticateResponse safiResponse = mock(SafiAuthenticateResponse.class);
        when(safiResponse.isSuccessFlag()).thenReturn(false);

        when(twoFactorAuthenticationFacade.authenticate(any(SafiAuthenticateRequest.class))).thenReturn(safiResponse);

        AjaxResponse ajaxResponse = registrationController.verifySmsAndRegistration(conversation, bindingResult, request);
        assertThat(ajaxResponse.isSuccess(), Is.is(Boolean.FALSE));
    }

    @Test
    public void testRegistrationStepTwo() throws Exception {
        UserProfile activeProfile = getProfile(JobRole.ADVISER, "job id 1", "client1");
        when(userProfileService.getActiveProfile()).thenReturn(activeProfile);
        samlToken = new SamlToken(SamlUtil.loadWplSamlNewPanoramaCustomer());
        Mockito.when(userProfileService.getSamlToken()).thenReturn(samlToken);
        when(userProfileService.getUserId()).thenReturn("201603880");
        IndividualDetailImpl individualDetailImpl = Mockito.mock(IndividualDetailImpl.class);
        when(individualDetailImpl.getCISKey()).thenReturn(CISKey.valueOf("83971220010"));
        when(userProfileService.getActiveProfile()).thenReturn(mock(UserProfile.class));
        when(userProfileService.getUserId()).thenReturn("201603880");
        when(userProfileService.isEmulating()).thenReturn(true);
        when(userProfileService.getEffectiveProfile()).thenReturn(mock(Profile.class));
        when(userProfileService.getEffectiveProfile().getUserId()).thenReturn("201603880");
        when(userProfileService.getActiveProfile().getUsername()).thenReturn("201603880");
        when(userProfileService.getUsername()).thenReturn("Darryl");
        when(bindingResult.hasErrors()).thenReturn(true);
        when(permissionBaseService.hasBasicPermission("feature.global.prmNonValueEvents")).thenReturn(true);
        mockAuthentication("investor");
        String RegistrationStepTwo_VIEW = "registrationStepTwo";
        String showTermsModel = "showTerms";
        super.mockAuthentication(Attribute.ROLE_INVESTOR);
        ModelMap modelMap = mock(ModelMap.class);
        String view = registrationController.registrationStepTwo(modelMap);
        assertThat(view, notNullValue());
        assertThat(view, Is.is(RegistrationStepTwo_VIEW));
        verify(modelMap, times(1)).put(eq(showTermsModel), anyBoolean());
    }

    @Test
    public void testRegistrationStepTwo_URL() throws Exception {
        UserProfile activeProfile = getProfile(JobRole.ADVISER, "job id 1", "client1");
        when(userProfileService.getActiveProfile()).thenReturn(activeProfile);

        samlToken = new SamlToken(SamlUtil.loadWplSamlNewPanoramaCustomer());
        Mockito.when(userProfileService.getSamlToken()).thenReturn(samlToken);
        when(userProfileService.getUserId()).thenReturn("201603880");
        when(permissionBaseService.hasBasicPermission("feature.global.prmNonValueEvents")).thenReturn(true);


        super.mockAuthentication(Attribute.ROLE_INVESTOR);
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.setRequestURI("/secure/util/registrationStepTwo");
        request.setMethod(RequestMethod.GET.name());
        AnnotationMethodHandlerAdapter annotationMethodHandlerAdapter = new AnnotationMethodHandlerAdapter();
        ModelAndView modelAndView = annotationMethodHandlerAdapter.handle(request, response, registrationController);
        assertThat(response.getStatus(), Is.is(HttpServletResponse.SC_OK));
        assertThat(modelAndView.getViewName(), notNullValue());
    }

    @Test(expected = NoSuchRequestHandlingMethodException.class)
    public void testRegistrationStepTwo_MethodNotSupported() throws Exception {
        super.mockAuthentication(Attribute.ROLE_INVESTOR);
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.setRequestURI("/secure/page/registrationStepTwo");
        request.setMethod(RequestMethod.POST.name());
        AnnotationMethodHandlerAdapter annotationMethodHandlerAdapter = new AnnotationMethodHandlerAdapter();
        annotationMethodHandlerAdapter.handle(request, response, registrationController);
    }

    public UserProfile getProfile(final JobRole role, final String jobId, final String customerId) {
        UserInformation user = new UserInformationImpl();
        user.setClientKey(ClientKey.valueOf(customerId));
        JobProfile job = getJobProfile(role, jobId);
        UserProfile profile = new UserProfileAdapterImpl(user, job);
        return profile;
    }

    private JobProfile getJobProfile(final JobRole role, final String jobId) {
        JobProfile job = Mockito.mock(JobProfile.class);
        when(job.getJobRole()).thenReturn(role);
        when(job.getJob()).thenReturn(JobKey.valueOf(jobId));
        return job;
    }
}

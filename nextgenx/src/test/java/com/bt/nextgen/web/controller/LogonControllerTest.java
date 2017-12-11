package com.bt.nextgen.web.controller;

import com.bt.nextgen.cms.service.CmsService;
import com.bt.nextgen.config.ApplicationContextProvider;
import com.btfin.panorama.core.security.Roles;
import com.btfin.panorama.core.security.saml.SamlToken;
import com.bt.nextgen.core.security.TamOperationCode;
import com.bt.nextgen.core.security.api.service.PermissionBaseDtoService;
import com.btfin.panorama.core.security.profile.Profile;
import com.btfin.panorama.core.security.profile.UserProfile;
import com.bt.nextgen.core.security.profile.UserProfileAdapterImpl;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.core.service.CredentialService;
import com.bt.nextgen.core.service.MessageService;
import com.bt.nextgen.core.util.DatabaseManager;
import com.bt.nextgen.core.web.RequestQuery;
import com.bt.nextgen.core.web.model.AjaxResponse;
import com.bt.nextgen.core.web.model.UserReset;
import com.bt.nextgen.core.web.util.View;
import com.bt.nextgen.login.web.model.PasswordResetModel;
import com.bt.nextgen.login.web.model.RegistrationModel;
import com.bt.nextgen.logon.service.LogonService;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.core.security.avaloq.Constants;
import com.bt.nextgen.service.avaloq.domain.IndividualDetailImpl;
import com.bt.nextgen.service.avaloq.userinformation.JobRole;
import com.bt.nextgen.service.avaloq.userinformation.UserInformationImpl;
import com.bt.nextgen.service.group.customer.CredentialRequest;
import com.btfin.panorama.core.security.integration.customer.CustomerCredentialInformation;
import com.bt.nextgen.service.group.customer.CustomerLoginManagementIntegrationService;
import com.btfin.panorama.core.security.integration.customer.UserGroup;
import com.bt.nextgen.service.integration.user.CISKey;
import com.bt.nextgen.service.integration.userinformation.ClientKey;
import com.bt.nextgen.service.integration.userinformation.JobKey;
import com.btfin.panorama.core.security.integration.userinformation.UserInformation;
import com.btfin.panorama.core.security.integration.userprofile.JobProfile;
import com.bt.nextgen.service.security.SmsService;
import com.bt.nextgen.serviceops.model.UserAccountStatusModel;
import com.btfin.panorama.core.security.UserAccountStatus;
import com.bt.nextgen.util.SamlUtil;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import com.bt.nextgen.web.validator.ValidationErrorCode;
import org.apache.commons.codec.binary.Base64;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;
import org.springframework.web.servlet.mvc.multiaction.NoSuchRequestHandlingMethodException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

import static com.bt.nextgen.core.util.SETTINGS.SECURITY_BRAND_PARAM;
import static com.bt.nextgen.core.util.SETTINGS.SECURITY_DEFAULT_BRAND;
import static com.bt.nextgen.core.util.SETTINGS.SECURITY_HALGM_PARAM;
import static com.bt.nextgen.core.util.SETTINGS.SECURITY_JS_OBFUSCATION_URL_DEV;
import static com.bt.nextgen.core.util.SETTINGS.SECURITY_PASSWORD_PARAM;
import static com.bt.nextgen.core.util.SETTINGS.SECURITY_USERNAME_PARAM;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test class for {@link LogonController}.
 */
@RunWith(MockitoJUnitRunner.class)
public class LogonControllerTest {
    @InjectMocks
    private LogonController logonController;

    @Mock
    private MessageService msgService;

    @Mock
    private LogonService mockLogonService;

    @Mock
    private CmsService mockCmsService;

    @Mock
    private SmsService mockSmsService;

    @Mock
    private RequestQuery requestQuery;

    @Mock
    private MessageService mockMessageService;

    @Mock
    private CredentialService credentialService;

    @Mock
    private UserProfileService profileService;

    @Mock
    private PermissionBaseDtoService permissionBaseService;

    @Mock
    private CustomerLoginManagementIntegrationService customerLoginService;

    @Mock
    private Profile profile;

    @Mock
    private UserProfile userProfile;

    private SamlToken samlToken;

    private final Base64 base64 = new Base64();

    private final String fakeSaml = "<saml:Assertion xmlns:saml=\"urn:oasis:names:tc:SAML:2.0:assertion\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\"\n"
            + "                xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"
            + "                ID=\"Assertion-uuidf0850b26-013f-196f-8085-f925cd50cdb3\" IssueInstant=\"2013-07-18T06:43:38Z\"\n"
            + "                Version=\"2.0\">\n" + "    <!-- This is a simulated saml -->\n"
            + "    <saml:Issuer Format=\"urn:oasis:names:tc:SAML:2.0:nameid-format:entity\">http://sts.westpac.com.au</saml:Issuer>\n"
            + "    <ds:Signature xmlns:ds=\"http://www.w3.org/2000/09/xmldsig#\" Id=\"uuidf0850b55-013f-1dda-b2a4-f925cd50cdb3\">\n"
            + "        <ds:SignedInfo>\n"
            + "            <ds:CanonicalizationMethod Algorithm=\"http://www.w3.org/2001/10/xml-exc-c14n#\"></ds:CanonicalizationMethod>\n"
            + "            <ds:SignatureMethod Algorithm=\"http://www.w3.org/2000/09/xmldsig#rsa-sha1\"></ds:SignatureMethod>\n"
            + "            <ds:Reference URI=\"#Assertion-uuidf0850b26-013f-196f-8085-f925cd50cdb3\">\n"
            + "                <ds:Transforms>\n"
            + "                    <ds:Transform Algorithm=\"http://www.w3.org/2000/09/xmldsig#enveloped-signature\"></ds:Transform>\n"
            + "                    <ds:Transform Algorithm=\"http://www.w3.org/2001/10/xml-exc-c14n#\">\n"
            + "                        <xc14n:InclusiveNamespaces xmlns:xc14n=\"http://www.w3.org/2001/10/xml-exc-c14n#\"\n"
            + "                                                   PrefixList=\"xsi xs saml\"></xc14n:InclusiveNamespaces>\n"
            + "                    </ds:Transform>\n" + "                </ds:Transforms>\n"
            + "                <ds:DigestMethod Algorithm=\"http://www.w3.org/2000/09/xmldsig#sha1\"></ds:DigestMethod>\n"
            + "                <ds:DigestValue>v+ViRbKjCf5FLI5b/QWon9v6uq8=</ds:DigestValue>\n" + "            </ds:Reference>\n"
            + "        </ds:SignedInfo>\n" + "        <ds:SignatureValue>\n"
            + "            N6aYIW94HoH36Z54FiE2dJgrXrQm9jR9If5peAVaTcljmbuqb8qEEtQxNjcp5gGfeoBmVC5shI73EryYk2/UYvHX//U6RxJZRyY8OCE3LG/442ew34/L2mMUltUgV05+YfVH1LmBV7qqZnxX7JVZRLA/phR6Rfal0ryI+WlyXzg=\n"
            + "        </ds:SignatureValue>\n" + "        <ds:KeyInfo>\n" + "            <ds:X509Data>\n"
            + "                <ds:X509Certificate>\n"
            + "                    MIIGKzCCBZSgAwIBAgIKEhqdRgACAABvvDANBgkqhkiG9w0BAQUFADCBhzESMBAGCgmSJomT8ixkARkWAmF1MRMwEQYKCZImiZPyLGQBGRYDY29tMRcwFQYKCZImiZPyLGQBGRYHd2VzdHBhYzEVMBMGCgmSJomT8ixkARkWBXdiY2F1MRUwEwYKCZImiZPyLGQBGRYFaW5mYXUxFTATBgNVBAMTDGF1MjAwNHNwMDA0MjAeFw0xMjA4MTQwMTEwMjdaFw0xMzEwMDgwMTEwMjdaMIHHMQswCQYDVQQGEwJBVTEYMBYGA1UECBMPTmV3IFNvdXRoIFdhbGVzMQ8wDQYDVQQHEwZTeWRuZXkxJDAiBgNVBAoTG1dlc3RwYWMgQmFua2luZyBDb3Jwb3JhdGlvbjEsMCoGA1UECxMjV2VzdHBhYyBSZXRhaWwgYW5kIEJ1c2luZXNzIEJhbmtpbmcxOTA3BgNVBAMTMG90cFRhY3RpY2FsLWludEN1c3RJZFByb3Yub2xiLnNydi53ZXN0cGFjLmNvbS5hdTCBnzANBgkqhkiG9w0BAQEFAAOBjQAwgYkCgYEAulAP1ywKzsLOCUklvHE1AlmUs259phslbzE7RD6iJHQaLch/XNW5PrEFeY+ZB1lpRZStWgtMj87aUBRADHdtZkzXs2YGU9IPIqlGOeMpE0T4VviZca/ZPA2nN1cPEYZBoJGtmNV9V877pPFweWqXKh+ltIKn5dJCdZFIX8U3TtUCAwEAAaOCA1owggNWMB0GA1UdDgQWBBQNVDb6wpHX6EwV+FuqwtqExWHBYDAfBgNVHSMEGDAWgBTPu3aFZB5KiNZC2TUGOQLP6gvg/TCCASgGA1UdHwSCAR8wggEbMIIBF6CCAROgggEPhoHKbGRhcDovLy9DTj1hdTIwMDRzcDAwNDIoMiksQ049YXUyMDA0c3AwMDQyLENOPUNEUCxDTj1QdWJsaWMlMjBLZXklMjBTZXJ2aWNlcyxDTj1TZXJ2aWNlcyxDTj1Db25maWd1cmF0aW9uLERDPXdiY2F1LERDPXdlc3RwYWMsREM9Y29tLERDPWF1P2NlcnRpZmljYXRlUmV2b2NhdGlvbkxpc3Q/YmFzZT9vYmplY3RDbGFzcz1jUkxEaXN0cmlidXRpb25Qb2ludIZAaHR0cDovL3ZwbmNkcC5pbmZhdS53YmNhdS53ZXN0cGFjLmNvbS5hdS9jcmwvYXUyMDA0c3AwMDQyKDIpLmNybDCCAUUGCCsGAQUFBwEBBIIBNzCCATMwgboGCCsGAQUFBzAChoGtbGRhcDovLy9DTj1hdTIwMDRzcDAwNDIsQ049QUlBLENOPVB1YmxpYyUyMEtleSUyMFNlcnZpY2VzLENOPVNlcnZpY2VzLENOPUNvbmZpZ3VyYXRpb24sREM9d2JjYXUsREM9d2VzdHBhYyxEQz1jb20sREM9YXU/Y0FDZXJ0aWZpY2F0ZT9iYXNlP29iamVjdENsYXNzPWNlcnRpZmljYXRpb25BdXRob3JpdHkwdAYIKwYBBQUHMAKGaGh0dHA6Ly92cG5jZHAuaW5mYXUud2JjYXUud2VzdHBhYy5jb20uYXUvY3JsL2F1MjAwNHNwMDA0Mi5pbmZhdS53YmNhdS53ZXN0cGFjLmNvbS5hdV9hdTIwMDRzcDAwNDIoMikuY3J0MAwGA1UdEwEB/wQCMAAwCwYDVR0PBAQDAgWgMDwGCSsGAQQBgjcVBwQvMC0GJSsGAQQBgjcVCIXesyXghQuC0Y0ph8rhesf8E4FYhuq2fYbW1CUCAWQCAQIwHQYDVR0lBBYwFAYIKwYBBQUHAwIGCCsGAQUFBwMBMCcGCSsGAQQBgjcVCgQaMBgwCgYIKwYBBQUHAwIwCgYIKwYBBQUHAwEwDQYJKoZIhvcNAQEFBQADgYEAhdlzZk75aGgSr5SbLL7qw+G6jUwyGKzTJSvLRz2l5GHFWKXinSeSN2CQ3/xz0CO+rr+crB/zYcF1gXOTwgrw7xTi2anXhPNPaQWNDcpN+HvDVbPBl3hP2uiN2o0o+FiE6brkQ/zuKkq0r9xvXmjO+WWEbRlt+mHeC259Fjo58Yw=\n"
            + "                </ds:X509Certificate>\n" + "            </ds:X509Data>\n" + "        </ds:KeyInfo>\n"
            + "    </ds:Signature>\n" + "    <saml:Subject>\n"
            + "        <saml:NameID Format=\"urn:ibm:names:ITFIM:5.1:accessmanager\">56463638</saml:NameID>\n"
            + "        <saml:SubjectConfirmation Method=\"urn:oasis:names:tc:SAML:2.0:cm:bearer\">\n"
            + "            <SubjectConfirmationData Recipient=\"http://localhost:8080\">\n"
            + "            </SubjectConfirmationData>\n" + "        </saml:SubjectConfirmation>\n" + "    </saml:Subject>\n"
            + "    <saml:Conditions NotBefore=\"1945-06-30T03:29:32Z\" NotOnOrAfter=\"2081-08-05T09:57:45Z\">\n"
            + "        <saml:AudienceRestriction>\n" + "            <saml:Audience>http://ng.westpac.com.au</saml:Audience>\n"
            + "        </saml:AudienceRestriction>\n" + "    </saml:Conditions>\n"
            + "    <saml:AuthnStatement AuthnInstant=\"2013-07-18T06:43:38Z\" SessionNotOnOrAfter=\"2013-07-18T14:43:37Z\">\n"
            + "        <saml:AuthnContext>\n"
            + "            <saml:AuthnContextClassRef>urn:oasis:names:tc:SAML:2.0:ac:classes:Password</saml:AuthnContextClassRef>\n"
            + "        </saml:AuthnContext>\n" + "    </saml:AuthnStatement>\n" + "    <saml:AttributeStatement>\n"
            + "        <saml:Attribute Name=\"credentialId\" NameFormat=\"urn:ibm:names:ITFIM:5.1:accessmanager\">\n"
            + "            <saml:AttributeValue xsi:type=\"xs:string\">af98cafc-b8f1-456d-be78-4ee2215c16ad</saml:AttributeValue>\n"
            + "        </saml:Attribute>\n"
            + "        <saml:Attribute Name=\"AZN_CRED_NETWORK_ADDRESS_BIN\" NameFormat=\"urn:ibm:names:ITFIM:5.1:accessmanager\">\n"
            + "            <saml:AttributeValue xsi:type=\"xs:string\">0x0a94c512</saml:AttributeValue>\n"
            + "        </saml:Attribute>\n"
            + "        <saml:Attribute Name=\"badSigninCount\" NameFormat=\"urn:ibm:names:ITFIM:5.1:accessmanager\">\n"
            + "            <saml:AttributeValue xsi:type=\"xs:string\">0</saml:AttributeValue>\n" + "        </saml:Attribute>\n"
            + "        <saml:Attribute Name=\"trackerId\" NameFormat=\"urn:ibm:names:ITFIM:5.1:accessmanager\">\n"
            + "            <saml:AttributeValue xsi:type=\"xs:string\">51d345e9-a12f-44bd-85b3-44c72af19fee</saml:AttributeValue>\n"
            + "        </saml:Attribute>\n"
            + "        <saml:Attribute Name=\"AZN_CRED_NETWORK_ADDRESS_STR\" NameFormat=\"urn:ibm:names:ITFIM:5.1:accessmanager\">\n"
            + "            <saml:AttributeValue xsi:type=\"xs:string\">10.148.197.18</saml:AttributeValue>\n"
            + "        </saml:Attribute>\n"
            + "        <saml:Attribute Name=\"AZN_CRED_AUTH_METHOD\" NameFormat=\"urn:ibm:names:ITFIM:5.1:accessmanager\">\n"
            + "            <saml:AttributeValue xsi:type=\"xs:string\">ext-auth-interface</saml:AttributeValue>\n"
            + "        </saml:Attribute>\n"
            + "        <saml:Attribute Name=\"brand\" NameFormat=\"urn:ibm:names:ITFIM:5.1:accessmanager\">\n"
            + "            <saml:AttributeValue xsi:type=\"xs:string\">wbc</saml:AttributeValue>\n"
            + "        </saml:Attribute>\n"
            + "        <saml:Attribute Name=\"bankDefinedLogin\" NameFormat=\"urn:ibm:names:ITFIM:5.1:accessmanager\">\n"
            + "            <saml:AttributeValue xsi:type=\"xs:string\">56463638</saml:AttributeValue>\n"
            + "        </saml:Attribute>\n"
            + "        <saml:Attribute Name=\"AZN_CRED_GROUP_UUIDS\" NameFormat=\"urn:ibm:names:ITFIM:5.1:accessmanager\">\n"
            + "            <saml:AttributeValue xsi:type=\"xs:string\">5dc7b0bc-e2d8-11e2-8d36-0e346a598d0a</saml:AttributeValue>\n"
            + "            <saml:AttributeValue xsi:type=\"xs:string\">eb8c6cdc-edcb-11e2-9086-0e346a598d0a</saml:AttributeValue>\n"
            + "        </saml:Attribute>\n"
            + "        <saml:Attribute Name=\"AUTHENTICATION_LEVEL\" NameFormat=\"urn:ibm:names:ITFIM:5.1:accessmanager\">\n"
            + "            <saml:AttributeValue xsi:type=\"xs:string\">10</saml:AttributeValue>\n" + "        </saml:Attribute>\n"
            + "        <saml:Attribute Name=\"tagvalue_user_session_id\" NameFormat=\"urn:ibm:names:ITFIM:5.1:accessmanager\">\n"
            + "            <saml:AttributeValue xsi:type=\"xs:string\">\n"
            + "                TkcA_UeeOmgAAAAIAAAAwT2xEaQAAAAETgNjYeTd0ZlA5cVlOblAwOWFjQ0JJUTgzbkZSRkxKdnMzemtXcU91Yk5iSm13MUZsR3V2:default\n"
            + "            </saml:AttributeValue>\n" + "        </saml:Attribute>\n"
            + "        <saml:Attribute Name=\"AZN_CRED_AUTHZN_ID\" NameFormat=\"urn:ibm:names:ITFIM:5.1:accessmanager\">\n"
            + "            <saml:AttributeValue xsi:type=\"xs:string\">56463638</saml:AttributeValue>\n"
            + "        </saml:Attribute>\n"
            + "        <saml:Attribute Name=\"AZN_CRED_GROUP_REGISTRY_IDS\" NameFormat=\"urn:ibm:names:ITFIM:5.1:accessmanager\">\n"
            + "            <saml:AttributeValue xsi:type=\"xs:string\">cn=bt-adviser,ou=groups,o=westpac</saml:AttributeValue>\n"
            + "            <saml:AttributeValue xsi:type=\"xs:string\">cn=ng_personal_password_policy,ou=groups,o=westpac\n"
            + "            </saml:AttributeValue>\n" + "        </saml:Attribute>\n"
            + "        <saml:Attribute Name=\"AZN_CRED_REGISTRY_ID\" NameFormat=\"urn:ibm:names:ITFIM:5.1:accessmanager\">\n"
            + "            <saml:AttributeValue xsi:type=\"xs:string\">uid=2dc11c44-3e34-47f0-8cbf-ab4c44953426,ou=users,o=westpac\n"
            + "            </saml:AttributeValue>\n" + "        </saml:Attribute>\n"
            + "        <saml:Attribute Name=\"tagvalue_session_index\" NameFormat=\"urn:ibm:names:ITFIM:5.1:accessmanager\">\n"
            + "            <saml:AttributeValue xsi:type=\"xs:string\">4f99dea8-ef75-11e2-98de-0e346a598d0a</saml:AttributeValue>\n"
            + "        </saml:Attribute>\n"
            + "        <saml:Attribute Name=\"AZN_CRED_BROWSER_INFO\" NameFormat=\"urn:ibm:names:ITFIM:5.1:accessmanager\">\n"
            + "            <saml:AttributeValue xsi:type=\"xs:string\">Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML,\n"
            + "                like Gecko) Chrome/28.0.1500.72 Safari/537.36\n" + "            </saml:AttributeValue>\n"
            + "        </saml:Attribute>\n"
            + "        <saml:Attribute Name=\"eaiCredEvent\" NameFormat=\"urn:ibm:names:ITFIM:5.1:accessmanager\">\n"
            + "            <saml:AttributeValue xsi:type=\"xs:string\">login</saml:AttributeValue>\n"
            + "        </saml:Attribute>\n"
            + "        <saml:Attribute Name=\"registeringInvParty\" NameFormat=\"urn:ibm:names:ITFIM:5.1:accessmanager\">\n"
            + "            <saml:AttributeValue xsi:type=\"xs:string\">NOT_FOUND</saml:AttributeValue>\n"
            + "        </saml:Attribute>\n"
            + "        <saml:Attribute Name=\"custDefinedLogin\" NameFormat=\"urn:ibm:names:ITFIM:5.1:accessmanager\">\n"
            + "            <saml:AttributeValue xsi:type=\"xs:string\">NOT_FOUND</saml:AttributeValue>\n"
            + "        </saml:Attribute>\n"
            + "        <saml:Attribute Name=\"AZN_CRED_VERSION\" NameFormat=\"urn:ibm:names:ITFIM:5.1:accessmanager\">\n"
            + "            <saml:AttributeValue xsi:type=\"xs:string\">0x00000700</saml:AttributeValue>\n"
            + "        </saml:Attribute>\n"
            + "        <saml:Attribute Name=\"AZN_CRED_PRINCIPAL_UUID\" NameFormat=\"urn:ibm:names:ITFIM:5.1:accessmanager\">\n"
            + "            <saml:AttributeValue xsi:type=\"xs:string\">e6261273-013e-1564-aa44-9d6fa7363429</saml:AttributeValue>\n"
            + "        </saml:Attribute>\n"
            + "        <saml:Attribute Name=\"tagvalue_login_user_name\" NameFormat=\"urn:ibm:names:ITFIM:5.1:accessmanager\">\n"
            + "            <saml:AttributeValue xsi:type=\"xs:string\">aNewUsername</saml:AttributeValue>\n"
            + "        </saml:Attribute>\n"
            + "        <saml:Attribute Name=\"lastLogin\" NameFormat=\"urn:ibm:names:ITFIM:5.1:accessmanager\">\n"
            + "            <saml:AttributeValue xsi:type=\"xs:string\">20130718051647Z</saml:AttributeValue>\n"
            + "        </saml:Attribute>\n"
            + "        <saml:Attribute Name=\"AZN_CRED_PRINCIPAL_DOMAIN\" NameFormat=\"urn:ibm:names:ITFIM:5.1:accessmanager\">\n"
            + "            <saml:AttributeValue xsi:type=\"xs:string\">Default</saml:AttributeValue>\n"
            + "        </saml:Attribute>\n"
            + "        <saml:Attribute Name=\"AZN_CUSTOM_ATTRIBUTES\" NameFormat=\"urn:ibm:names:ITFIM:5.1:accessmanager\">\n"
            + "            <saml:AttributeValue xsi:type=\"xs:string\">custDefinedLogin</saml:AttributeValue>\n"
            + "            <saml:AttributeValue xsi:type=\"xs:string\">loginUsed</saml:AttributeValue>\n"
            + "            <saml:AttributeValue xsi:type=\"xs:string\">am_eai_xattr_session_lifetime</saml:AttributeValue>\n"
            + "            <saml:AttributeValue xsi:type=\"xs:string\">credentialId</saml:AttributeValue>\n"
            + "            <saml:AttributeValue xsi:type=\"xs:string\">lastLogin</saml:AttributeValue>\n"
            + "            <saml:AttributeValue xsi:type=\"xs:string\">bankDefinedLogin</saml:AttributeValue>\n"
            + "            <saml:AttributeValue xsi:type=\"xs:string\">eaiCredEvent</saml:AttributeValue>\n"
            + "            <saml:AttributeValue xsi:type=\"xs:string\">am_eai_xattr_session_inactive_timeout</saml:AttributeValue>\n"
            + "            <saml:AttributeValue xsi:type=\"xs:string\">failedPwdCount</saml:AttributeValue>\n"
            + "            <saml:AttributeValue xsi:type=\"xs:string\">trackerId</saml:AttributeValue>\n"
            + "            <saml:AttributeValue xsi:type=\"xs:string\">credentialType</saml:AttributeValue>\n"
            + "            <saml:AttributeValue xsi:type=\"xs:string\">origSourceIp</saml:AttributeValue>\n"
            + "            <saml:AttributeValue xsi:type=\"xs:string\">badSigninCount</saml:AttributeValue>\n"
            + "            <saml:AttributeValue xsi:type=\"xs:string\">registeringInvParty</saml:AttributeValue>\n"
            + "            <saml:AttributeValue xsi:type=\"xs:string\">totalBadSigininCount</saml:AttributeValue>\n"
            + "            <saml:AttributeValue xsi:type=\"xs:string\">brand</saml:AttributeValue>\n"
            + "        </saml:Attribute>\n"
            + "        <saml:Attribute Name=\"credentialType\" NameFormat=\"urn:ibm:names:ITFIM:5.1:accessmanager\">\n"
            + "            <saml:AttributeValue xsi:type=\"xs:string\">online</saml:AttributeValue>\n"
            + "        </saml:Attribute>\n"
            + "        <saml:Attribute Name=\"am_eai_xattr_session_inactive_timeout\" NameFormat=\"urn:ibm:names:ITFIM:5.1:accessmanager\">\n"
            + "            <saml:AttributeValue xsi:type=\"xs:string\">600</saml:AttributeValue>\n"
            + "        </saml:Attribute>\n"
            + "        <saml:Attribute Name=\"AZN_CRED_IP_FAMILY\" NameFormat=\"urn:ibm:names:ITFIM:5.1:accessmanager\">\n"
            + "            <saml:AttributeValue xsi:type=\"xs:string\">AF_INET</saml:AttributeValue>\n"
            + "        </saml:Attribute>\n"
            + "        <saml:Attribute Name=\"origSourceIp\" NameFormat=\"urn:ibm:names:ITFIM:5.1:accessmanager\">\n"
            + "            <saml:AttributeValue xsi:type=\"xs:string\">NOT_FOUND</saml:AttributeValue>\n"
            + "        </saml:Attribute>\n"
            + "        <saml:Attribute Name=\"AZN_CRED_MECH_ID\" NameFormat=\"urn:ibm:names:ITFIM:5.1:accessmanager\">\n"
            + "            <saml:AttributeValue xsi:type=\"xs:string\">IV_LDAP_V3.0</saml:AttributeValue>\n"
            + "        </saml:Attribute>\n"
            + "        <saml:Attribute Name=\"AZN_CRED_GROUPS\" NameFormat=\"urn:ibm:names:ITFIM:5.1:accessmanager\">\n"
            + "            <saml:AttributeValue xsi:type=\"xs:string\">bt-adviser</saml:AttributeValue>\n"
            + "            <saml:AttributeValue xsi:type=\"xs:string\">ng_personal_password_policy</saml:AttributeValue>\n"
            + "        </saml:Attribute>\n"
            + "        <saml:Attribute Name=\"totalBadSigininCount\" NameFormat=\"urn:ibm:names:ITFIM:5.1:accessmanager\">\n"
            + "            <saml:AttributeValue xsi:type=\"xs:string\">2</saml:AttributeValue>\n" + "        </saml:Attribute>\n"
            + "        <saml:Attribute Name=\"AZN_CRED_QOP_INFO\" NameFormat=\"urn:ibm:names:ITFIM:5.1:accessmanager\">\n"
            + "            <saml:AttributeValue xsi:type=\"xs:string\">SSK: TLSV11: 2F</saml:AttributeValue>\n"
            + "        </saml:Attribute>\n"
            + "        <saml:Attribute Name=\"loginUsed\" NameFormat=\"urn:ibm:names:ITFIM:5.1:accessmanager\">\n"
            + "            <saml:AttributeValue xsi:type=\"xs:string\">wrong_value</saml:AttributeValue>\n"
            + "        </saml:Attribute>\n"
            + "        <saml:Attribute Name=\"failedPwdCount\" NameFormat=\"urn:ibm:names:ITFIM:5.1:accessmanager\">\n"
            + "            <saml:AttributeValue xsi:type=\"xs:string\">0</saml:AttributeValue>\n" + "        </saml:Attribute>\n"
            + "        <saml:Attribute Name=\"AZN_CRED_PRINCIPAL_NAME\" NameFormat=\"urn:ibm:names:ITFIM:5.1:accessmanager\">\n"
            + "            <saml:AttributeValue xsi:type=\"xs:string\">aUserName</saml:AttributeValue>\n"
            + "        </saml:Attribute>\n" + "    </saml:AttributeStatement>\n" + "</saml:Assertion>";

    private AnnotationMethodHandlerAdapter annotationMethodHandlerAdapter;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private ApplicationContext context;
    private ApplicationContextProvider provider;
    private MockHttpSession session;

    private final String ERROR_MSG = "error is here";
    private final String ERROR_CODE = "msg000";

    private static final String GET_METHOD = RequestMethod.GET.name();
    private static final String POST_METHOD = RequestMethod.POST.name();
    private static final String CMS_CONTENT = "cmsContent";

    @Before
    public void setup() throws Exception {
        when(profileService.getActiveProfile()).thenReturn(userProfile);
        when(userProfile.getJobRole()).thenReturn(JobRole.ADVISER);
        when(profileService.getUsername()).thenReturn("adviser");
        when(profileService.getFirstName()).thenReturn("Nick");
        when(profileService.getUsername()).thenReturn("getLastName");
        when(profileService.getGcmId()).thenReturn("3256");
        annotationMethodHandlerAdapter = new AnnotationMethodHandlerAdapter();
        HttpMessageConverter[] messageConverters = {new MappingJackson2HttpMessageConverter()};
        annotationMethodHandlerAdapter.setMessageConverters(messageConverters);
        context = mock(ApplicationContext.class);
        provider = new ApplicationContextProvider(new DatabaseManager());
        provider.setApplicationContext(context);
        request = new MockHttpServletRequest();
        session = new MockHttpSession();
        request.setParameter("userName", "investor");
        request.setParameter("password", "investor");
        request.setParameter("modifiedUsername", "newuser");
        request.setParameter("confirmPassword", "hello1234");
        request.setParameter("newPassword", "hello1234");
        request.setParameter("halgm", new String(base64.encode("test1234".getBytes())));
        request.setCookies(new Cookie("process_timer_webclient", ""));
        when(requestQuery.isWebSealRequest()).thenReturn(false);
        SamlToken token = new SamlToken(SamlUtil.loadSaml());
        when(requestQuery.getSamlToken()).thenReturn(token);
        response = new MockHttpServletResponse();
        UserAccountStatusModel userAccountStatusModel = new UserAccountStatusModel();
        userAccountStatusModel.setUserAccountStatus(UserAccountStatus.ACTIVE);
        when(credentialService.lookupStatus(anyString(), any(ServiceErrors.class))).thenReturn(userAccountStatusModel);
        Mockito.when(mockLogonService.updatePassword(any(UserReset.class), any(ServiceErrors.class)))
                .thenReturn(Attribute.SUCCESS_MESSAGE);
        ApplicationContext applicationContext = mock(ApplicationContext.class);
        CmsService cmsService = mock(CmsService.class);
        Mockito.when(applicationContext.getBean(any(Class.class))).thenReturn(cmsService);
        ApplicationContextProvider applicationContextProvider = new ApplicationContextProvider(null);
        applicationContextProvider.setApplicationContext(applicationContext);
        when(msgService.lookup(ERROR_CODE)).thenReturn(ERROR_MSG);
    }

    @Before
    public void setupAuthentication() {
        TestingAuthenticationToken authentication = new TestingAuthenticationToken("", "", Roles.ROLE_ADVISER.name());
        Profile dummyProfile = new Profile(new SamlToken(SamlUtil.loadSaml()));
        authentication.setDetails(dummyProfile);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    public void testResetPassword_SetPasswordFailStatus() throws Exception {
        UserReset userReset = new UserReset();
        userReset.setUserName("investor");
        userReset.setPassword("investor");
        userReset.setNewpassword("test1234");
        userReset.setConfirmPassword("test1234");
        userReset.setHalgm(new String(Base64.encodeBase64("test1234".getBytes())));
        BindingResult bindingResult = mock(BindingResult.class);
        AjaxResponse result;
        when(mockLogonService.updatePassword(any(UserReset.class), any(ServiceErrors.class)))
                .thenReturn(Attribute.FAILURE_MESSAGE);
        when(mockCmsService.getContent(LogonController.FAILED_RESET_PASSWORD)).thenReturn(CMS_CONTENT);
        result = logonController.changePassword(userReset, bindingResult);
        assertThat(result.isSuccess(), is(false));
    }

    // TODO fix commented test cases
    @Ignore
    public void testHandleTamOperationURL() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.setRequestURI(LogonController.LOGON);
        request.setParameter("TAM_OP", Constants.LOGIN_SUCCESS);
        request.setMethod(GET_METHOD);
        AnnotationMethodHandlerAdapter annotationMethodHandlerAdapter = new AnnotationMethodHandlerAdapter();
        ModelAndView modelAndView = annotationMethodHandlerAdapter.handle(request, response, logonController);
        assertThat(response.getStatus(), is(HttpServletResponse.SC_OK));
        assertThat(modelAndView.getViewName(), is(HomePageController.REDIRECT_HOMEPAGE));
        ModelMap model = modelAndView.getModelMap();
        assertThat((Boolean) model.get(LogonController.SYSTEM_EVENT_MSG_FOUND), Is.is(false));
    }

    @Test
    public void testHandleTamOperation_authenticatedUser() {
        ModelMap mockMap = mock(ModelMap.class);
        when(requestQuery.isUserAuthenticated()).thenReturn(true);
        when(requestQuery.isAdviserOnAdviserSite()).thenReturn(true);
        when(mockCmsService.getContent(ValidationErrorCode.ISAUTHENTICATE)).thenReturn(CMS_CONTENT);
        String loginPage = logonController.handleTamOperation(mockMap, Constants.LOGIN_BLOCKED, request,
                new RedirectAttributesModelMap(), response);
        assertModelSetup(mockMap);
        verify(mockMap, times(1)).addAttribute(eq(LogonController.SYSTEM_EVENT_MSG_FOUND), anyString());
        verify(mockMap, times(1)).addAttribute(eq(LogonController.MSG_KEY), anyString());
        verify(mockMap, times(1)).addAttribute(eq(LogonController.MSG_TYPE), eq("ERROR"));
        verify(mockMap, times(1)).addAttribute(eq("tamOperation"), eq("blocked"));
        assertThat(loginPage, equalTo(View.LOGON));
    }

    @Test
    public void testHandleTamOperation_authenticatedUser_loginSuccess() throws Exception {
        ModelMap mockMap = mock(ModelMap.class);
        request.addHeader("wbctoken", fakeSaml);
        when(mockCmsService.getContent(ValidationErrorCode.ISAUTHENTICATE)).thenReturn(CMS_CONTENT);
        String loginPage = logonController.handleTamOperation(mockMap, Constants.LOGIN_SUCCESS, request,
                new RedirectAttributesModelMap(), response);
        verify(mockMap, times(1)).addAttribute(eq(LogonController.SYSTEM_EVENT_MSG_FOUND), anyString());
        assertThat(loginPage, equalTo(HomePageController.REDIRECT_HOMEPAGE));
    }

    @Test(expected = HttpRequestMethodNotSupportedException.class)
    public void testHandleTamOperation_IncorrectMethod() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.setRequestURI(LogonController.LOGON);
        request.setParameter("TAM_OP", Constants.LOGIN_SUCCESS);
        request.setMethod(POST_METHOD);
        AnnotationMethodHandlerAdapter annotationMethodHandlerAdapter = new AnnotationMethodHandlerAdapter();
        annotationMethodHandlerAdapter.handle(request, response, logonController);
    }

    @Test(expected = NoSuchRequestHandlingMethodException.class)
    public void testHandleTamOperation_MissingParameter_tamOperation() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.setRequestURI(LogonController.LOGON);
        request.setMethod(GET_METHOD);
        AnnotationMethodHandlerAdapter annotationMethodHandlerAdapter = new AnnotationMethodHandlerAdapter();
        annotationMethodHandlerAdapter.handle(request, response, logonController);
    }

    @Test
    public void testHandleTamOperation_SystemEventMsg_modelSetup() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        ModelMap mockMap = mock(ModelMap.class);
        when(mockCmsService.getContent(LogonController.SYSTEM_EVENT_MSG)).thenReturn(CMS_CONTENT);
        String loginPage = logonController.handleTamOperation(mockMap, "random", request, new RedirectAttributesModelMap(),
                response);
        assertModelSetup(mockMap);
        assertThat(loginPage, is(View.LOGON));
        assertModelSetup(mockMap);
        // Changed expected result to false as this as it is now covered by the F5 load balancer
        verify(mockMap, times(1)).addAttribute(LogonController.SYSTEM_EVENT_MSG_FOUND, Boolean.FALSE);
        verify(mockMap, times(1)).addAttribute(eq(Attribute.OBFUSCATION_URL), eq(SECURITY_JS_OBFUSCATION_URL_DEV.value()));
    }

    @Test
    public void testHandleTamOperation_SysEventMsgNF_modelSetup() throws Exception {
        ModelMap mockMap = mock(ModelMap.class);
        when(mockCmsService.getContent(LogonController.SYSTEM_EVENT_MSG)).thenReturn(null);
        String loginPage = logonController.handleTamOperation(mockMap, "random", request, new RedirectAttributesModelMap(),
                response);
        verify(mockMap, times(1)).addAttribute(LogonController.SYSTEM_EVENT_MSG_FOUND, Boolean.FALSE);
        assertThat(loginPage, is(View.LOGON));
        assertModelSetup(mockMap);
        verify(mockMap, times(1)).addAttribute(LogonController.SYSTEM_EVENT_MSG_FOUND, Boolean.FALSE);
        verify(mockMap, times(1)).addAttribute(eq(Attribute.OBFUSCATION_URL), eq(SECURITY_JS_OBFUSCATION_URL_DEV.value()));
    }

    @Test
    public void testHandleTamOperation_LoginSuccess_modelSetup() throws Exception {
        ModelMap mockMap = mock(ModelMap.class);
        String loginPage = logonController.handleTamOperation(mockMap, Constants.LOGIN_SUCCESS, request,
                new RedirectAttributesModelMap(), response);
        assertThat(loginPage, is(HomePageController.REDIRECT_HOMEPAGE));
        verify(mockMap, times(1)).addAttribute(LogonController.SYSTEM_EVENT_MSG_FOUND, Boolean.FALSE);
    }

    @Test
    public void testHandleTamOperation_Login_modelSetup() throws Exception {
        ModelMap mockMap = mock(ModelMap.class);
        String loginPage = logonController.handleTamOperation(mockMap, Constants.LOGIN, request, new RedirectAttributesModelMap(),
                response);
        assertModelSetup(mockMap);
        assertThat(loginPage, is(View.LOGON));
        assertModelSetup(mockMap);
        verify(mockMap, times(1)).addAttribute(LogonController.SYSTEM_EVENT_MSG_FOUND, Boolean.FALSE);
        verify(mockMap, times(1)).addAttribute(eq(Attribute.OBFUSCATION_URL), eq(SECURITY_JS_OBFUSCATION_URL_DEV.value()));
    }

    @Test
    public void testLogoutSuccess_ReturnLoginPage_modelSetup() throws Exception {
        samlToken = new SamlToken(SamlUtil.loadWplSamlNewPanoramaCustomer());
        Mockito.when(profileService.getSamlToken()).thenReturn(samlToken);
        when(profileService.getUserId()).thenReturn("201603880");
        // when(permissionBaseService.hasBasicPermission("feature.global.prmNonValueEvents")).thenReturn(true);
        CustomerCredentialInformation customerCredential = mock(CustomerCredentialInformation.class);
        when(customerCredential.getUsername()).thenReturn("Martin Taylor");
        when(customerLoginService.getCustomerInformation(any(CredentialRequest.class), any(ServiceErrors.class)))
                .thenReturn(customerCredential);
        // when(profileService.getBaseProfile()).thenReturn(profile);
        IndividualDetailImpl individualDetailImpl = Mockito.mock(IndividualDetailImpl.class);
        when(individualDetailImpl.getCISKey()).thenReturn(CISKey.valueOf("83971220010"));
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        mockRequest.setScheme("http");
        mockRequest.setServerName("geomajas.org");
        mockRequest.setServerPort(80);
        mockRequest.setContextPath("/test");
        ServletRequestAttributes attributes = new ServletRequestAttributes(mockRequest);
        RequestContextHolder.setRequestAttributes(attributes);
        when(profileService.getActiveProfile()).thenReturn(mock(UserProfile.class));
        when(profileService.getUserId()).thenReturn("201603880");
        when(profileService.isEmulating()).thenReturn(true);
        when(profileService.getEffectiveProfile()).thenReturn(mock(Profile.class));
        when(profileService.getEffectiveProfile().getUserId()).thenReturn("201603880");
        when(profileService.getBaseProfile()).thenReturn(profile);
        when(profileService.getActiveProfile().getUsername()).thenReturn("201603880");
        when(profileService.getUsername()).thenReturn("Darryl");
        when(permissionBaseService.hasBasicPermission("feature.global.prmNonValueEvents")).thenReturn(true);
        // when("feature.global.prmNonValueEvents".toString()).thenReturn("true");
        ModelMap mockMap = mock(ModelMap.class);
        // ParentAuthenticationController parentAuthenticationController=mock(ParentAuthenticationController.class);
        when(mockCmsService.getContent(LogonController.LOGOUT_MSG_KEY)).thenReturn(CMS_CONTENT);
        String loginPage = logonController.handleTamOperation(mockMap, Constants.LOGOUT, request,
                new RedirectAttributesModelMap(), response);
        assertThat(loginPage, is(HomePageController.REDIRECT_LOGOUT));
    }

    @Test
    public void testHandleTamOperation_LogoutKeyNull_modelSetup() throws Exception {
        CustomerCredentialInformation customerCredential = mock(CustomerCredentialInformation.class);
        when(customerCredential.getUsername()).thenReturn("Martin Taylor");
        when(customerLoginService.getCustomerInformation(any(CredentialRequest.class), any(ServiceErrors.class)))
                .thenReturn(customerCredential);
        samlToken = new SamlToken(SamlUtil.loadWplSamlNewPanoramaCustomer());
        Mockito.when(profileService.getSamlToken()).thenReturn(samlToken);
        when(profileService.getUserId()).thenReturn("201603880");
        IndividualDetailImpl individualDetailImpl = Mockito.mock(IndividualDetailImpl.class);
        when(individualDetailImpl.getCISKey()).thenReturn(CISKey.valueOf("83971220010"));
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        mockRequest.setScheme("http");
        mockRequest.setServerName("geomajas.org");
        mockRequest.setServerPort(80);
        mockRequest.setContextPath("/test");
        ServletRequestAttributes attributes = new ServletRequestAttributes(mockRequest);
        RequestContextHolder.setRequestAttributes(attributes);
        when(profileService.getActiveProfile()).thenReturn(mock(UserProfile.class));
        when(profileService.getBaseProfile()).thenReturn(profile);
        when(profileService.getUserId()).thenReturn("201603880");
        when(profileService.isEmulating()).thenReturn(true);
        when(profileService.getEffectiveProfile()).thenReturn(mock(Profile.class));
        when(profileService.getEffectiveProfile().getUserId()).thenReturn("201603880");
        when(profileService.getActiveProfile().getUsername()).thenReturn("201603880");
        when(profileService.getUsername()).thenReturn("Darryl");
        when(permissionBaseService.hasBasicPermission("feature.global.prmNonValueEvents")).thenReturn(true);
        ModelMap mockMap = mock(ModelMap.class);
        when(mockCmsService.getContent(LogonController.LOGOUT_MSG_KEY)).thenReturn(null);
        String loginPage = logonController.handleTamOperation(mockMap, Constants.LOGOUT, request,
                new RedirectAttributesModelMap(), response);
        assertNotNull(loginPage);
        assertThat(loginPage, is(HomePageController.REDIRECT_LOGOUT));
    }

    @Test
    public void testLogoutError_ReturnLoginPage_modelSetup() throws Exception {
        CustomerCredentialInformation customerCredential = mock(CustomerCredentialInformation.class);
        when(customerCredential.getUsername()).thenReturn("Martin Taylor");
        when(customerLoginService.getCustomerInformation(any(CredentialRequest.class), any(ServiceErrors.class)))
                .thenReturn(customerCredential);
        samlToken = new SamlToken(SamlUtil.loadWplSamlNewPanoramaCustomer());
        Mockito.when(profileService.getSamlToken()).thenReturn(samlToken);
        when(profileService.getUserId()).thenReturn("201603880");
        // when(permissionBaseService.hasBasicPermission("feature.global.prmNonValueEvents")).thenReturn(true);
        IndividualDetailImpl individualDetailImpl = Mockito.mock(IndividualDetailImpl.class);
        when(individualDetailImpl.getCISKey()).thenReturn(CISKey.valueOf("83971220010"));
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        mockRequest.setScheme("http");
        mockRequest.setServerName("geomajas.org");
        mockRequest.setServerPort(80);
        mockRequest.setContextPath("/test");
        ServletRequestAttributes attributes = new ServletRequestAttributes(mockRequest);
        RequestContextHolder.setRequestAttributes(attributes);
        when(profileService.getActiveProfile()).thenReturn(mock(UserProfile.class));
        when(profileService.getBaseProfile()).thenReturn(profile);
        when(profileService.getUserId()).thenReturn("201603880");
        when(profileService.isEmulating()).thenReturn(true);
        when(profileService.getEffectiveProfile()).thenReturn(mock(Profile.class));
        when(profileService.getEffectiveProfile().getUserId()).thenReturn("201603880");
        when(profileService.getActiveProfile().getUsername()).thenReturn("201603880");
        when(profileService.getUsername()).thenReturn("Darryl");
        when(permissionBaseService.hasBasicPermission("feature.global.prmNonValueEvents")).thenReturn(true);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServerName("localhost:9080");
        request.setRequestURI("/ng/public/page/logon");
        request.setQueryString("TAM_OP=logout&ERROR_CODE=0x00000000&URL=%2Fpkmslogout%26ERROR%3D500");
        ModelMap mockMap = mock(ModelMap.class);
        when(mockCmsService.getContent(LogonController.MSG_UI_API_ERROR)).thenReturn(CMS_CONTENT);
        String loginPage = logonController.handleTamOperation(mockMap, Constants.LOGOUT, request,
                new RedirectAttributesModelMap(), response);
        assertThat(loginPage, is(HomePageController.REDIRECT_LOGOUT + "?ERROR=500"));
    }

    @Test
    public void testLogonFailed_ReturnLoginPage_modelSetup() throws Exception {
        ModelMap mockMap = mock(ModelMap.class);
        when(mockCmsService.getContent(TamOperationCode.AUTH_FAILURE.getTamMessageId())).thenReturn(CMS_CONTENT);
        String loginPage = logonController.handleTamOperation(mockMap, Constants.AUTH_FAILURE, request,
                new RedirectAttributesModelMap(), response);
        assertModelSetup(mockMap);
        assertModelSetupWithErrorMsg(mockMap);
        assertThat(loginPage, equalTo(View.LOGON));
    }

    @Test
    public void testHandleTamOperation_LoginIncorNull_modelSetup() throws Exception {
        ModelMap mockMap = mock(ModelMap.class);
        when(mockCmsService.getContent(LogonController.LOGIN_INCORRECT_KEY)).thenReturn(null);
        String loginPage = logonController.handleTamOperation(mockMap, Constants.AUTH_FAILURE, request,
                new RedirectAttributesModelMap(), response);
        assertModelSetup(mockMap);
        assertNoModelSetupWithErrorMsg(mockMap);
        assertNotNull(loginPage);
        assertThat(loginPage, is(View.LOGON));
    }

    @Test
    public void testLoginBlocked_ReturnLoginPage_modelSetup() throws Exception {
        ModelMap mockMap = mock(ModelMap.class);
        when(mockCmsService.getContent(LogonController.LOGIN_BLOCKED_KEY)).thenReturn(CMS_CONTENT);
        String loginPage = logonController.handleTamOperation(mockMap, Constants.LOGIN_BLOCKED, request,
                new RedirectAttributesModelMap(), response);
        assertModelSetup(mockMap);
        verify(mockMap, times(1)).addAttribute(eq(LogonController.MSG_KEY), anyString());
        verify(mockMap, times(1)).addAttribute(eq(LogonController.MSG_TYPE), eq("ERROR"));
        assertThat(loginPage, equalTo(View.LOGON));
    }

    @Test
    public void testHandleTamOperation_LoginBlockKeyNull_modelSetup() throws Exception {
        ModelMap mockMap = mock(ModelMap.class);
        when(mockCmsService.getContent(LogonController.LOGIN_BLOCKED_KEY)).thenReturn(null);
        String loginPage = logonController.handleTamOperation(mockMap, Constants.LOGIN_BLOCKED, request,
                new RedirectAttributesModelMap(), response);
        assertModelSetup(mockMap);
        assertNoModelSetupWithErrorMsg(mockMap);
        assertNotNull(loginPage);
        assertThat(loginPage, is(View.LOGON));
    }

    @Test
    public void testAccessDenied_ReturnLoginPage_modelSetup() throws Exception {
        ModelMap mockMap = mock(ModelMap.class);
        when(mockCmsService.getContent(TamOperationCode.AUTH_FAILURE.getTamMessageId())).thenReturn(CMS_CONTENT);
        String loginPage = logonController.handleTamOperation(mockMap, TamOperationCode.AUTH_FAILURE.getTamOperationCode(),
                request, new RedirectAttributesModelMap(), response);
        assertModelSetup(mockMap);
        verify(mockMap, times(1)).addAttribute(eq(LogonController.SYSTEM_EVENT_MSG_FOUND), anyString());
        verify(mockMap, times(1)).addAttribute(eq(LogonController.MSG_KEY), anyString());
        verify(mockMap, times(1)).addAttribute(eq(LogonController.MSG_TYPE), eq("ERROR"));
        assertThat(loginPage, equalTo(View.LOGON));
    }

    @Test
    public void testAccessDenied_AccessDeniedKeyNull_modelSetup() throws Exception {
        ModelMap mockMap = mock(ModelMap.class);
        when(mockCmsService.getContent(LogonController.ACCESS_DENIED_KEY)).thenReturn(null);
        String loginPage = logonController.handleTamOperation(mockMap, Constants.AUTH_INFO, request,
                new RedirectAttributesModelMap(), response);
        assertModelSetup(mockMap);
        assertNoModelSetupWithErrorMsg(mockMap);
        assertThat(loginPage, equalTo(View.LOGON));
    }

    @Test
    public void testHandleTamOperation_OtherValuesAccessDenied_modelSetup() throws Exception {
        ModelMap mockMap = mock(ModelMap.class);
        when(mockCmsService.getContent("err00100")).thenReturn(CMS_CONTENT);
        String loginPage = logonController.handleTamOperation(mockMap, "random", request, new RedirectAttributesModelMap(),
                response);
        assertModelSetup(mockMap);
        verify(mockMap, times(1)).addAttribute(eq(LogonController.SYSTEM_EVENT_MSG_FOUND), anyString());
        verify(mockMap, times(1)).addAttribute(eq(LogonController.MSG_KEY), anyString());
        verify(mockMap, times(1)).addAttribute(eq(LogonController.MSG_TYPE), anyString());
        assertThat(loginPage, equalTo(View.LOGON));
    }

    @Test
    public void testResetPasswordURL() throws Exception {
        request.setRequestURI("/public/api/resetPassword");
        request.setMethod(POST_METHOD);
        annotationMethodHandlerAdapter.handle(request, response, logonController);
        assertThat(response.getStatus(), is(HttpServletResponse.SC_OK));
        assertThat(response.getContentType().contains("application/json"), is(true));
    }

    @Test(expected = HttpRequestMethodNotSupportedException.class)
    public void testResetPassword_IncorrectMethod() throws Exception {
        Mockito.when(mockLogonService.updatePassword(any(UserReset.class), any(ServiceErrors.class)))
                .thenReturn(Attribute.SUCCESS_MESSAGE);
        request.setRequestURI("/public/api/resetPassword");
        request.setMethod(GET_METHOD);
        annotationMethodHandlerAdapter.handle(request, response, logonController);
        assertThat(response.getStatus(), is(HttpServletResponse.SC_OK));
        assertThat(response.getContentType().contains("application/json"), is(true));
    }

    @Test
    public void testResetPasssword_NewPassConfirmPswValues() throws Exception {
        UserReset userReset = new UserReset();
        userReset.setUserName("investor");
        userReset.setPassword("investor");
        userReset.setNewpassword("test1234");
        userReset.setConfirmPassword("test12345");
        userReset.setHalgm(new String(Base64.encodeBase64("test1234".getBytes())));
        BindingResult bindingResult = mock(BindingResult.class);
        AjaxResponse result = logonController.changePassword(userReset, bindingResult);
        assertThat(result.isSuccess(), is(false));
        assertNotNull(result.getData());
        // New password and Confirm password are same
        userReset.setConfirmPassword("test1234");
        when(mockLogonService.updatePassword(any(UserReset.class), any(ServiceErrors.class)))
                .thenReturn(Attribute.SUCCESS_MESSAGE);
        result = logonController.changePassword(userReset, bindingResult);
        assertThat(result.isSuccess(), is(true));
        assertNotNull(result.getData());
        UserReset resultModel = (UserReset) result.getData();
        assertThat(resultModel.getUserName(), is("investor"));
    }

    @Test
    public void testResetPasssword_IncorrectCurrentPassword() throws Exception {
        UserReset userReset = new UserReset();
        userReset.setUserName("investor");
        userReset.setPassword("investor");
        userReset.setNewpassword("test1234");
        userReset.setConfirmPassword("test1234");
        userReset.setHalgm(new String(Base64.encodeBase64("test1234".getBytes())));
        BindingResult bindingResult = mock(BindingResult.class);
        AjaxResponse result;
        when(mockLogonService.updatePassword(any(UserReset.class), any(ServiceErrors.class))).thenReturn("authenticateUserFault");
        result = logonController.changePassword(userReset, bindingResult);
        assertThat(result.isSuccess(), is(false));
    }

    @Test
    public void testResetPassword_WithBindingResultErrors() throws Exception {
        UserReset userReset = new UserReset();
        userReset.setUserName("investor");
        userReset.setPassword("investor");
        userReset.setNewpassword("test1234");
        userReset.setConfirmPassword("test1234");
        userReset.setHalgm(new String(Base64.encodeBase64("test1234".getBytes())));
        BindingResult bindingResult = mock(BindingResult.class);
        AjaxResponse result = logonController.resetPassword(userReset, bindingResult);
        assertThat(result.isSuccess(), is(true));
        // if binding result has error then response should be false
        when(bindingResult.hasErrors()).thenReturn(true);
        List<FieldError> errors = new ArrayList<>();
        errors.add(new FieldError("error", "newPassword", "err00026"));
        Mockito.when(bindingResult.getFieldErrors()).thenReturn(errors);
        result = logonController.resetPassword(userReset, bindingResult);
        assertThat(result.isSuccess(), is(false));
    }

    @Test
    public void testResetUsernameURL() throws Exception {
        request.setRequestURI("/secure/api/resetUsername");
        request.setMethod(POST_METHOD);
        when(mockLogonService.modifyUserAlias(any(UserReset.class), any(ServiceErrors.class)))
                .thenReturn(Attribute.SUCCESS_MESSAGE);
        annotationMethodHandlerAdapter.handle(request, response, logonController);
        assertThat(response.getStatus(), is(HttpServletResponse.SC_OK));
    }

    @Test(expected = HttpRequestMethodNotSupportedException.class)
    public void testResetUsername_IncorrectMethod() throws Exception {
        request.setRequestURI("/secure/api/resetUsername");
        request.setMethod(GET_METHOD);
        annotationMethodHandlerAdapter.handle(request, response, logonController);
        assertThat(response.getStatus(), is(HttpServletResponse.SC_OK));
    }

    @Test
    public void testResetUsername_WithBindingResultErrors() throws Exception {
        UserReset userReset = new UserReset();
        userReset.setUserName("investor");
        userReset.setPassword("investor");
        userReset.setNewUserName("modified");
        userReset.setHalgm(new String(Base64.encodeBase64("test1234".getBytes())));
        BindingResult bindingResult = mock(BindingResult.class);
        List<FieldError> errors = new ArrayList<>();
        errors.add(new FieldError("error", "resetUser", "err00035"));
        when(bindingResult.hasErrors()).thenReturn(true);
        when(bindingResult.getFieldErrors()).thenReturn(errors);
        AjaxResponse result = logonController.resetUsername(userReset, bindingResult);
        assertThat(result.isSuccess(), is(false));
        assertNotNull(result.getData());
    }

    @Test
    public void testResetUsernameResponse() throws Exception {
        when(mockLogonService.modifyUserAlias(any(UserReset.class), any(ServiceErrors.class)))
                .thenReturn(Attribute.SUCCESS_MESSAGE);
        UserReset userReset = new UserReset();
        userReset.setUserName("investor");
        userReset.setPassword("investor");
        userReset.setNewUserName("modified");
        userReset.setHalgm(new String(Base64.encodeBase64("test1234".getBytes())));
        BindingResult bindingResult = mock(BindingResult.class);
        AjaxResponse result = logonController.resetUsername(userReset, bindingResult);
        assertThat(result.isSuccess(), is(true));
        assertNotNull(result.getData());
        UserReset resultModel = (UserReset) result.getData();
        assertThat(resultModel.getUserName(), is("investor"));
    }

    @Test
    public void testChangePasswordURL() throws Exception {
        request.setRequestURI("/secure/api/changePassword");
        request.setMethod(POST_METHOD);
        annotationMethodHandlerAdapter.handle(request, response, logonController);
        assertThat(response.getStatus(), is(HttpServletResponse.SC_OK));
    }

    @Test(expected = HttpRequestMethodNotSupportedException.class)
    public void testChangePassword_IncorrectMethod() throws Exception {
        request.setRequestURI("/secure/api/changePassword");
        request.setMethod(GET_METHOD);
        annotationMethodHandlerAdapter.handle(request, response, logonController);
        assertThat(response.getStatus(), is(HttpServletResponse.SC_OK));
    }

    @Test
    public void testChangePassword_FailStatus() throws Exception {
        UserReset userReset = new UserReset();
        userReset.setUserName("testuser");
        userReset.setNewpassword("test123");
        userReset.setConfirmPassword("test123");
        userReset.setHalgm(new String(Base64.encodeBase64("test1234".getBytes())));
        BindingResult bindingResult = mock(BindingResult.class);
        when(mockLogonService.updatePassword(any(UserReset.class), any(ServiceErrors.class)))
                .thenReturn(Attribute.FAILURE_MESSAGE);
        when(mockCmsService.getContent(anyString())).thenReturn(CMS_CONTENT);
        AjaxResponse result;
        result = logonController.resetPassword(userReset, bindingResult);
        assertThat(result.isSuccess(), is(false));
    }

    @Test
    public void testChangePasswordResponse() throws Exception {
        when(mockLogonService.updatePassword(any(UserReset.class), any(ServiceErrors.class)))
                .thenReturn(Attribute.SUCCESS_MESSAGE);
        when(mockCmsService.getContent(anyString())).thenReturn("Any Valid message");
        UserReset userReset = new UserReset();
        userReset.setUserName("valid");
        userReset.setNewpassword("valid");
        userReset.setConfirmPassword("valid");
        userReset.setHalgm(new String(Base64.encodeBase64("test1234".getBytes())));
        BindingResult bindingResult = mock(BindingResult.class);
        AjaxResponse result;
        result = logonController.changePassword(userReset, bindingResult);
        assertThat(result.isSuccess(), is(true));
        // assertThat(result.getData(), IsNot.not(null));
        // Binding result has error
        when(bindingResult.hasErrors()).thenReturn(true);
        List<FieldError> errors = new ArrayList<>();
        errors.add(new FieldError("error", "newPassword", "err00035"));
        Mockito.when(bindingResult.getFieldErrors()).thenReturn(errors);
        result = logonController.changePassword(userReset, bindingResult);
        assertThat(result.isSuccess(), is(false));
    }

    @Test
    public void testLogout_ErrorMessage_modelSetup() throws Exception {
        CustomerCredentialInformation customerCredential = mock(CustomerCredentialInformation.class);
        when(customerCredential.getUsername()).thenReturn("Martin Taylor");
        when(customerLoginService.getCustomerInformation(any(CredentialRequest.class), any(ServiceErrors.class)))
                .thenReturn(customerCredential);
        samlToken = new SamlToken(SamlUtil.loadWplSamlNewPanoramaCustomer());
        Mockito.when(profileService.getSamlToken()).thenReturn(samlToken);
        when(profileService.getUserId()).thenReturn("201603880");
        // when(permissionBaseService.hasBasicPermission("feature.global.prmNonValueEvents")).thenReturn(true);
        IndividualDetailImpl individualDetailImpl = Mockito.mock(IndividualDetailImpl.class);
        when(individualDetailImpl.getCISKey()).thenReturn(CISKey.valueOf("83971220010"));
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        mockRequest.setScheme("http");
        mockRequest.setServerName("geomajas.org");
        mockRequest.setServerPort(80);
        mockRequest.setContextPath("/test");
        ServletRequestAttributes attributes = new ServletRequestAttributes(mockRequest);
        RequestContextHolder.setRequestAttributes(attributes);
        when(profileService.getActiveProfile()).thenReturn(mock(UserProfile.class));
        when(profileService.getBaseProfile()).thenReturn(profile);
        when(profileService.getUserId()).thenReturn("201603880");
        when(profileService.isEmulating()).thenReturn(true);
        when(profileService.getEffectiveProfile()).thenReturn(mock(Profile.class));
        when(profileService.getEffectiveProfile().getUserId()).thenReturn("201603880");
        when(profileService.getActiveProfile().getUsername()).thenReturn("201603880");
        when(profileService.getUsername()).thenReturn("Darryl");
        when(permissionBaseService.hasBasicPermission("feature.global.prmNonValueEvents")).thenReturn(true);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter("ERROR", "500");
        request.setCookies(new Cookie("process_timer_webclient", ""));
        ModelMap mockMap = mock(ModelMap.class);
        when(mockCmsService.getContent(LogonController.MSG_UI_API_ERROR)).thenReturn(CMS_CONTENT);
        String loginPage = logonController.logoutStep(mockMap, request, response);
        assertModelSetup(mockMap);
        assertThat(loginPage, is("logout"));
    }

    @Test
    public void testLogout_Success_WithoutUnknownErrorCode() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(new Cookie("process_timer_webclient", ""));
        request.setParameter("ERROR", "@!#@!@#");
        ModelMap mockMap = mock(ModelMap.class);
        String loginPage = logonController.logoutStep(mockMap, request, response);
        assertThat(loginPage, is("logout"));
    }

    @Test
    public void testLogout_Success_modelSetup() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(new Cookie("process_timer_webclient", ""));
        ModelMap mockMap = mock(ModelMap.class);
        String loginPage = logonController.logoutStep(mockMap, request, response);
        assertThat(loginPage, is("logout"));
    }

    @Test
    public void testHandleTamOperation_tempPassword() throws Exception {
        CustomerCredentialInformation customerCredential = mock(CustomerCredentialInformation.class);
        when(customerCredential.getUsername()).thenReturn("Martin Taylor");
        when(customerLoginService.getCustomerInformation(any(CredentialRequest.class), any(ServiceErrors.class)))
                .thenReturn(customerCredential);
        samlToken = new SamlToken(SamlUtil.loadWplSamlNewPanoramaCustomer());
        Mockito.when(profileService.getSamlToken()).thenReturn(samlToken);
        when(profileService.getUserId()).thenReturn("201603880");
        // when(permissionBaseService.hasBasicPermission("feature.global.prmNonValueEvents")).thenReturn(true);
        IndividualDetailImpl individualDetailImpl = Mockito.mock(IndividualDetailImpl.class);
        when(individualDetailImpl.getCISKey()).thenReturn(CISKey.valueOf("83971220010"));
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        mockRequest.setScheme("http");
        mockRequest.setServerName("geomajas.org");
        mockRequest.setServerPort(80);
        mockRequest.setContextPath("/test");
        ServletRequestAttributes attributes = new ServletRequestAttributes(mockRequest);
        RequestContextHolder.setRequestAttributes(attributes);
        when(profileService.getActiveProfile()).thenReturn(mock(UserProfile.class));
        when(profileService.getUserId()).thenReturn("201603880");
        when(profileService.isEmulating()).thenReturn(true);
        when(profileService.getEffectiveProfile()).thenReturn(mock(Profile.class));
        when(profileService.getEffectiveProfile().getUserId()).thenReturn("201603880");
        when(profileService.getBaseProfile()).thenReturn(profile);
        when(profileService.getActiveProfile().getUsername()).thenReturn("201603880");
        when(profileService.getUsername()).thenReturn("Darryl");
        when(permissionBaseService.hasBasicPermission("feature.global.prmNonValueEvents")).thenReturn(true);
        ModelMap mockMap = mock(ModelMap.class);
        String loginPage = logonController.handleTamOperation(mockMap, TamOperationCode.TEMP_PASSWORD.getTamOperationCode(),
                request, new RedirectAttributesModelMap(), response);
        assertModelSetup(mockMap);
        assertThat(loginPage, is(HomePageController.REDIRECT_TEMP_PASSWORD));
        assertModelSetup(mockMap);
        verify(mockMap, times(1)).addAttribute(LogonController.SYSTEM_EVENT_MSG_FOUND, Boolean.FALSE);
        verify(mockMap, times(1)).addAttribute(eq(Attribute.OBFUSCATION_URL), eq(SECURITY_JS_OBFUSCATION_URL_DEV.value()));
    }

    @Test
    public void testHandleTamOperation_authSuspicious() throws Exception {
        ModelMap mockMap = mock(ModelMap.class);
        when(mockCmsService.getContent(TamOperationCode.AUTH_SUSP.getTamMessageId())).thenReturn(CMS_CONTENT);
        String loginPage = logonController.handleTamOperation(mockMap, TamOperationCode.AUTH_SUSP.getTamOperationCode(), request,
                new RedirectAttributesModelMap(), response);
        assertModelSetup(mockMap);
        verify(mockMap, times(1)).addAttribute(eq(LogonController.SYSTEM_EVENT_MSG_FOUND), anyString());
        verify(mockMap, times(1)).addAttribute(eq(LogonController.MSG_KEY), anyString());
        verify(mockMap, times(1)).addAttribute(eq(LogonController.MSG_TYPE), eq("ERROR"));
        assertThat(loginPage, equalTo(View.LOGON));
    }

    @Test
    public void testHandleTamOperation_authTimeout() throws Exception {
        ModelMap mockMap = mock(ModelMap.class);
        when(mockCmsService.getContent(TamOperationCode.AUTH_TIMEOUT.getTamMessageId())).thenReturn(CMS_CONTENT);
        String loginPage = logonController.handleTamOperation(mockMap, TamOperationCode.AUTH_TIMEOUT.getTamOperationCode(),
                request, new RedirectAttributesModelMap(), response);
        assertModelSetup(mockMap);
        verify(mockMap, times(1)).addAttribute(eq(LogonController.SYSTEM_EVENT_MSG_FOUND), anyString());
        verify(mockMap, times(1)).addAttribute(eq(LogonController.MSG_KEY), anyString());
        verify(mockMap, times(1)).addAttribute(eq(LogonController.MSG_TYPE), eq("ERROR"));
        assertThat(loginPage, equalTo(View.LOGON));
    }

    @Test
    public void testHandleTamOperation_authEaiError() throws Exception {
        ModelMap mockMap = mock(ModelMap.class);
        when(mockCmsService.getContent(TamOperationCode.EAI_AUTH_ERROR.getTamMessageId())).thenReturn(CMS_CONTENT);
        String loginPage = logonController.handleTamOperation(mockMap, TamOperationCode.EAI_AUTH_ERROR.getTamOperationCode(),
                request, new RedirectAttributesModelMap(), response);
        assertModelSetup(mockMap);
        verify(mockMap, times(1)).addAttribute(eq(LogonController.SYSTEM_EVENT_MSG_FOUND), anyString());
        verify(mockMap, times(1)).addAttribute(eq(LogonController.MSG_KEY), anyString());
        verify(mockMap, times(1)).addAttribute(eq(LogonController.MSG_TYPE), eq("ERROR"));
        assertThat(loginPage, equalTo(View.LOGON));
    }

    @Test
    public void testHandleTamOperation_authError() throws Exception {
        ModelMap mockMap = mock(ModelMap.class);
        when(mockCmsService.getContent(TamOperationCode.ERROR.getTamMessageId())).thenReturn(CMS_CONTENT);
        String loginPage = logonController.handleTamOperation(mockMap, TamOperationCode.ERROR.getTamOperationCode(), request,
                new RedirectAttributesModelMap(), response);
        assertModelSetup(mockMap);
        verify(mockMap, times(1)).addAttribute(eq(LogonController.SYSTEM_EVENT_MSG_FOUND), anyString());
        verify(mockMap, times(1)).addAttribute(eq(LogonController.MSG_KEY), anyString());
        verify(mockMap, times(1)).addAttribute(eq(LogonController.MSG_TYPE), eq("ERROR"));
        assertThat(loginPage, equalTo(View.LOGON));
    }

    @Test
    public void testHandleTamOperation_help() throws Exception {
        ModelMap mockMap = mock(ModelMap.class);
        String loginPage = logonController.handleTamOperation(mockMap, TamOperationCode.HELP.getTamOperationCode(), request,
                new RedirectAttributesModelMap(), response);
        assertModelSetup(mockMap);
        assertThat(loginPage, is(View.LOGON));
        assertModelSetup(mockMap);
        verify(mockMap, times(1)).addAttribute(LogonController.SYSTEM_EVENT_MSG_FOUND, Boolean.FALSE);
        verify(mockMap, times(1)).addAttribute(eq(Attribute.OBFUSCATION_URL), eq(SECURITY_JS_OBFUSCATION_URL_DEV.value()));
    }

    @Test
    public void testHandleTamOperation_Stepup() throws Exception {
        ModelMap mockMap = mock(ModelMap.class);
        String loginPage = logonController.handleTamOperation(mockMap, "stepup", request,
                new RedirectAttributesModelMap(), response);
        assertModelSetup(mockMap);
        assertThat(loginPage, is(View.LOGON));
        assertModelSetup(mockMap);
    }

    @Test
    public void testHandleTamOperation_unkown() throws Exception {
        ModelMap mockMap = mock(ModelMap.class);
        String loginPage = logonController.handleTamOperation(mockMap, "unknown", request,
                new RedirectAttributesModelMap(), response);
        assertModelSetup(mockMap);
        assertThat(loginPage, is(View.LOGON));
        assertModelSetup(mockMap);
    }

    @Test
    public void testCloseAccountPageWithNullStatus() throws Exception {
        ModelMap mockMap = mock(ModelMap.class);
        String statusPage = logonController.handleClosedAccount(mockMap, null, request, response);
        assertThat(statusPage, is(View.LOGON));
    }

    @Test
    public void testCloseAccountPageWithNonCloseStatus() throws Exception {
        ModelMap mockMap = mock(ModelMap.class);
        String statusPage = logonController.handleClosedAccount(mockMap, "new", request, response);
        assertThat(statusPage, is(View.LOGON));
    }

    @Test
    public void testCloseAccountPageWithCloseStatus() throws Exception {
        ModelMap mockMap = mock(ModelMap.class);
        String statusPage = logonController.handleClosedAccount(mockMap, "closed", request, response);
        assertThat(statusPage, is(View.LOGON));
    }

    @Test
    public void testHandleAvaloqFailure() throws Exception {
        ModelMap mockMap = mock(ModelMap.class);
        String statusPage = logonController.handleAvaloqConnectionFailure(mockMap, request, response);
        assertThat(statusPage, is(View.LOGON));
    }

    @Test
    public void testAccountStatusPage() throws Exception {
        ModelMap mockMap = mock(ModelMap.class);
        String statusPage = logonController.accountStatus(mockMap);
        assertModelSetup(mockMap);
        assertThat(statusPage, is(View.STATUS));
    }

    @Test
    public void testAccountActivationPage() throws Exception {
        ModelMap mockMap = mock(ModelMap.class);
        String statusPage = logonController.accountActivation(mockMap);
        assertModelSetup(mockMap);
        assertThat(statusPage, is(View.ACTIVATION));
    }

    @Test
    public void testTermsAndConditionsPage() throws Exception {
        String statusPage = logonController.termsAndConditions();
        assertThat(statusPage, is("termsandconditions"));
    }

    @Test
    public void testResetTemporaryPasswordPage() throws Exception {
        ModelMap mockMap = mock(ModelMap.class);
        List<UserGroup> userGroupList = new ArrayList<>();
        UserGroup userGroup = UserGroup.fromRawType("bt-investor");
        userGroupList.add(userGroup);
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
        SamlToken samlToken = new SamlToken(fakeSaml);
        when(profileService.getSamlToken()).thenReturn(samlToken);
//        when(samlToken.getUserGroup()).thenReturn(userGroupList);
        String statusPage = logonController.registrationStepTwo(mockMap, redirectAttributes);
        verify(mockMap, times(1)).addAttribute(eq(Attribute.OBFUSCATION_URL), eq(SECURITY_JS_OBFUSCATION_URL_DEV.value()));
        verify(mockMap, times(1)).addAttribute(eq(Attribute.BRAND_FIELD_NAME), eq(SECURITY_BRAND_PARAM.value()));
        verify(mockMap, times(1)).addAttribute(eq(Attribute.HALGM_FIELD_NAME), eq(SECURITY_HALGM_PARAM.value()));
        assertThat(statusPage, is("tempPassword"));
    }


    /*
     * @Test public void testForgetPasswordValidateResponse() throws Exception {
     * when(mockSmsService.sendSmsCodeFromSafi()).thenReturn(true); when(mockLogonService.validateUser(anyString(), anyString(),
     * anyInt())).thenReturn(Attribute.SUCCESS_MESSAGE); SmsCodeModel conversation = new SmsCodeModel();
     * conversation.setUserCode("validUsername"); conversation.setLastName("validLastname"); conversation.setPostcode("1111");
     * AjaxResponse result = logonController.forgetPasswordValidate(conversation); assertThat(result.isSuccess(), is(true));
     * assertNotNull(result.getData()); assertThat((Boolean)result.getData(), is(true));
     * 
     * when(mockSmsService.sendSmsCodeFromSafi()).thenReturn(false); result =
     * logonController.forgetPasswordValidate(conversation); assertThat(result.isSuccess(), is(true));
     * assertThat((Boolean)result.getData(), is(false)); }
     * 
     * @Test public void testForgetPasswordVerifySmsURL() throws Exception { MockHttpServletRequest request = new
     * MockHttpServletRequest(); MockHttpServletResponse response = new MockHttpServletResponse();
     * request.setRequestURI("/public/api/forgetPasswordVerifySms"); request.setParameter("userCode", "valid");
     * request.setParameter("lastName", "valid"); request.setParameter("postcode", "1111"); request.setParameter("smsCode",
     * "valid"); request.setMethod(GET_METHOD); when(mockLogonService.verifySmsCode("valid", "valid", 1111,
     * "valid")).thenReturn(Attribute.SUCCESS_MESSAGE); annotationMethodHandlerAdapter.handle(request, response, logonController);
     * assertThat(response.getStatus(), is(HttpServletResponse.SC_OK)); }
     * 
     * @Test(expected = HttpRequestMethodNotSupportedException.class) public void testForgetPasswordVerifySms_IncorrectMethod()
     * throws Exception { request.setRequestURI("/public/api/forgetPasswordVerifySms"); request.setMethod(POST_METHOD);
     * 
     * annotationMethodHandlerAdapter.handle(request, response, logonController); assertThat(response.getStatus(),
     * is(HttpServletResponse.SC_OK)); assertThat(response.getContentType().contains("application/json"), is(true)); }
     */

    private void assertModelSetup(ModelMap mock) {
        verify(mock, times(1)).addAttribute(eq(Attribute.PASSWORD_RESET_MODEL), any(PasswordResetModel.class));
        verify(mock, times(1)).addAttribute(eq(Attribute.REGISTRATION_MODEL), any(RegistrationModel.class));
        verify(mock, times(1)).addAttribute(eq(Attribute.LOGON_BRAND), eq(SECURITY_DEFAULT_BRAND.value()));
        verify(mock, times(1)).addAttribute(eq(Attribute.PASSWORD_FIELD_NAME), eq(SECURITY_PASSWORD_PARAM.value()));
        verify(mock, times(1)).addAttribute(eq(Attribute.USERNAME_FIELD_NAME), eq(SECURITY_USERNAME_PARAM.value()));
        verify(mock, times(1)).addAttribute(eq(Attribute.BRAND_FIELD_NAME), eq(SECURITY_BRAND_PARAM.value()));
        verify(mock, times(1)).addAttribute(eq(Attribute.HALGM_FIELD_NAME), eq(SECURITY_HALGM_PARAM.value()));
    }

    private void assertNoModelSetupWithErrorMsg(ModelMap mockMap) {
        verify(mockMap, never()).addAttribute(eq(LogonController.MSG_KEY), anyString());
        verify(mockMap, never()).addAttribute(eq(LogonController.MSG_TYPE), eq("ERROR"));
    }

    private void assertModelSetupWithErrorMsg(ModelMap mockMap) {
        verify(mockMap, times(1)).addAttribute(eq(LogonController.MSG_KEY), anyString());
        verify(mockMap, times(1)).addAttribute(eq(LogonController.MSG_TYPE), eq("ERROR"));
    }

    public UserProfile getProfile(final JobRole role, final String jobId, final String customerId) {
        UserInformation user = new UserInformationImpl();
        user.setClientKey(ClientKey.valueOf(customerId));
        JobProfile job = getJobProfile(role, jobId);
        return new UserProfileAdapterImpl(user, job);
    }

    private JobProfile getJobProfile(final JobRole role, final String jobId) {
        JobProfile job = Mockito.mock(JobProfile.class);
        when(job.getJobRole()).thenReturn(role);
        when(job.getJob()).thenReturn(JobKey.valueOf(jobId));
        return job;
    }
}

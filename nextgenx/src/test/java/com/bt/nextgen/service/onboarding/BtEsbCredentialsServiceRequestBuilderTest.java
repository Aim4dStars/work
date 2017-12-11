package com.bt.nextgen.service.onboarding;


import com.btfin.panorama.core.security.Roles;
import com.bt.nextgen.service.onboarding.btesb.BtEsbRequestBuilder;
import com.bt.nextgen.service.security.model.HttpRequestParams;
import com.btfin.panorama.test.jaxb.AbstractJaxbTest;
import com.btfin.panorama.test.schema.AbstractSchemaValidatorTest;
import static junit.framework.Assert.assertNotNull;
import ns.btfin_com.party.v3_0.CustomerNoAllIssuerType;
import ns.btfin_com.product.panorama.credentialservice.credentialrequest.v1_0.ProvisionMFAMobileDeviceRequestMsgType;
import ns.btfin_com.product.panorama.credentialservice.credentialrequest.v1_0.ProvisionOnlineAccessRequestMsgType;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.btfin.panorama.test.jaxb.AbstractJaxbMarshallerTest.marshal;
import static com.btfin.panorama.test.jaxb.AbstractJaxbTest.xml;
import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual;

public class BtEsbCredentialsServiceRequestBuilderTest extends AbstractSchemaValidatorTest {

    public static final String CREDENTIALS_SERVICE_REQUEST_SCHEMA_PATH = "schema/btesb/BTFin/Product/Panorama/CredentialService/V1/CredentialRequestV1_0.xsd";

    private static final Class<BtEsbCredentialsServiceRequestBuilderTest> MY_CLASS = BtEsbCredentialsServiceRequestBuilderTest.class;

    private BtEsbRequestBuilder btEsbRequestBuilder;

    @BeforeClass
    public static void configureXmlUnit() {
        AbstractJaxbTest.configureXmlUnit();
    }

    public BtEsbCredentialsServiceRequestBuilderTest() {
        super(CREDENTIALS_SERVICE_REQUEST_SCHEMA_PATH);
    }

    @Before
    public void setUp() throws Exception {
        btEsbRequestBuilder = new BtEsbRequestBuilder();
    }

    @Test
    public void buildValidatePartyRequest() throws Exception {
        ValidatePartyRequest request = new FirstTimeRegistrationRequestModel("1111", "test-lastname", "2170");
        Object instance = btEsbRequestBuilder.buildValidatePartyRequest(request);
        validateJaxb(instance);
        assertXMLEqual(marshal(instance, logger), xml(MY_CLASS, "ValidatePartyRegistrationRequestMsg"));
    }

    @Test
    public void buildValidatePartySMSRequest() throws Exception {
        FirstTimeRegistrationRequestModel request = new FirstTimeRegistrationRequestModel("registrationCode", "Lastname_test", "2000", "Username_test");
        request.setAction(ValidatePartyAndSmsAction.FORGOT_PASSWORD);
        request.setDeviceToken("version%3D1%26pm%5Ffpua%3Dmozilla%2F5%2E0%20%28windows%20nt%206%2E3%3B%20wow64%29%20applewebkit%2F537%2E36%20%28khtml%2C%20like%20gecko%29%20chrome%2F50%2E0%2E2661%2E94%20safari%2F537%2E36%7C5%2E0%20%28Windows%20NT%206%2E3%3B%20WOW64%29%20AppleWebKit%2F537%2E36%20%28KHTML%2C%20like%20Gecko%29%20Chrome%2F50%2E0%2E2661%2E94%20Safari%2F537%2E36%7CWin32%26pm%5Ffpsc%3D24%7C1920%7C1080%7C1040%26pm%5Ffpsw%3D%26pm%5Ffptz%3D10%26pm%5Ffpln%3Dlang%3Den%2DUS%7Csyslang%3D%7Cuserlang%3D%26pm%5Ffpjv%3D0%26pm%5Ffpco%3D1");
        HttpRequestParams params = new HttpRequestParams();
        params.setHttpAccept("application/json, text/javascript, */*; q=0.01");
        params.setHttpAcceptEncoding("gzip, deflate");
        params.setHttpAcceptLanguage("en-US,en;q=0.8");
        params.setHttpReferrer("https://dev2.panoramaadviser.srv.westpac.com.au/ng/public/page/logon?TAM_OP=error&ERROR_CODE=0x00000016&URL=%2Fng%2Fpublic%2Fpage%2Flogon%3FTAM_OP%3Dlogin");
        params.setHttpOriginatingIpAddress("10.18.11.82");
        params.setHttpAcceptChars("ISO-8859-1");
        params.setHttpUserAgent("Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.94 Safari/537.36");
        request.setHttpRequestParams(params);
        Object instance = btEsbRequestBuilder.buildRegistrationDetails(request);
        validateJaxb(instance);
        assertXMLEqual(marshal(instance, logger), xml(MY_CLASS, "ValidatePartySMSOneTimePasswordChallengeRequestMsgCred"));
    }

    @Test
    public void provisionOnlineAccessForAdviser() throws Exception {
        CreateAccountRequest request = new CreateAccountRequestModel();
        Map<CustomerNoAllIssuerType, String> customerIdentifiers = new HashMap<>();
        customerIdentifiers.put(CustomerNoAllIssuerType.BT_PANORAMA, "201618191");
        request.setCustomerIdentifiers(customerIdentifiers);
        request.setLastName("Damon");
        request.setFirstName("Salvatore");
        request.setPrimaryEmailAddress("damon.salvatore@test.com");
        request.setPrimaryMobileNumber("0456789123");
        assertMarshalledXml(btEsbRequestBuilder.build(request), "ProvisionOnlineAccessRequestMsg-adviser");
    }

    @Test
    public void provisionOnlineAccessForInvestor() throws Exception {
        ResendRegistrationEmailRequest request = new CreateInvestorAccountRequestModel();
        request.setInvestorFirstName("Martin");
        request.setInvestorLastName("Taylor");
        request.setInvestorPrimaryEmailAddress("test@demo.com");
        request.setInvestorPrimaryContactNumber("0420359664");
        request.setAdviserFirstName("Liz");
        request.setAdviserLastName("Hurlay");
        request.setAdviserPrimaryContactNumber("0420359966");
        request.setAdviserPrimaryEmailAddress("adviser@test.com");
        request.setCustomerIdentifiers(buildCustomerIdentifiers());
        request.setPersonRole(Roles.ROLE_INVESTOR);
        ProvisionOnlineAccessRequestMsgType jaxbRequest = btEsbRequestBuilder.buildInvestorProcessRequest(request);
        assertMarshalledXml(jaxbRequest, "ProvisionOnlineAccessRequestMsg-investor");
    }

    //This builder is not used for CreateOneTimePasswordSendEmailRequestMsg. Hence ignoring this test.
    @Ignore
    @Test
    public void buildCreateOneTimePasswordSendEmailRequestMsgType() throws Exception {
        ResendRegistrationEmailRequest request = new ResendRegistrationEmailRequestModel();
        request.setAdviserFirstName("adviserFirstName");
        request.setAdviserLastName("adviserLastName");
        request.setAdviserOracleUserId("adviserOracleUserId");
        request.setAdviserPrimaryContactNumber("0455666777");
        request.setAdviserPrimaryEmailAddress("adviserPrimaryEmailAddress");
        request.setAdviserPrimaryContactNumberType("Mobile");
        request.setInvestorFirstName("investorFirstName");
        request.setInvestorGender("Male");
        request.setInvestorLastName("investorLastName");
        request.setInvestorPrimaryContactNumber("0394452134");
        request.setInvestorPrimaryEmailAddress("investorPrimaryEmailAddress");
        request.setInvestorSalutation("Mr");
        request.setPersonRole(Roles.ROLE_INVESTOR);
        request.setInvestorPrimaryContactNumberType("Landline");
        request.setCustomerIdentifiers(buildCustomerIdentifiers());

        assertMarshalledXml(btEsbRequestBuilder.build(request), "CreateOneTimePasswordSendEmailRequestMsg");
    }


    private Map<CustomerNoAllIssuerType, String> buildCustomerIdentifiers() {
        Map<CustomerNoAllIssuerType, String> customerIdentifiers = new HashMap<>();
        customerIdentifiers.put(CustomerNoAllIssuerType.WESTPAC, "92773951");
        customerIdentifiers.put(CustomerNoAllIssuerType.BT_PANORAMA, "217293786");
        customerIdentifiers.put(CustomerNoAllIssuerType.WESTPAC_LEGACY, "68321000053");
        return customerIdentifiers;
    }

    private void assertMarshalledXml(Object instance, String fileName) throws JAXBException, SAXException, IOException {
        validateJaxb(instance);
        assertXMLEqual(marshal(instance, logger), xml(MY_CLASS, fileName));
    }

    @Test
    public void testBuildProvisionMFADeviceRequest() throws IOException, JAXBException, SAXException {
        ProvisionMFADeviceRequest provisionMFADeviceRequest = new ProvisionMFADeviceRequestModel();
        Map<CustomerNoAllIssuerType, String> customerIdentifiers = new HashMap<>();
        customerIdentifiers.put(CustomerNoAllIssuerType.BT_PANORAMA,"201654479");
        provisionMFADeviceRequest.setCustomerIdentifiers(customerIdentifiers);
        provisionMFADeviceRequest.setCanonicalProductCode("3fb3e732d5c5429d97af392ab18e998b");
        provisionMFADeviceRequest.setPrimaryMobileNumber("61444888448");

        ProvisionMFAMobileDeviceRequestMsgType provisionMFAMobileDeviceRequestMsgType = btEsbRequestBuilder.buildProvisionMFADeviceRequest(provisionMFADeviceRequest);
        assertNotNull(provisionMFAMobileDeviceRequestMsgType);
        assertMarshalledXml(provisionMFAMobileDeviceRequestMsgType,"ProvisionMFAMobileDeviceRequestMsg");

    }


}
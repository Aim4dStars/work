/**
 *
 */
package com.bt.nextgen.service.onboarding;

import com.bt.nextgen.service.onboarding.btesb.BtEsbRequestBuilder;
import com.bt.nextgen.service.security.model.HttpRequestParams;
import com.btfin.panorama.test.jaxb.AbstractJaxbTest;
import com.btfin.panorama.test.schema.AbstractSchemaValidatorTest;
import ns.btfin_com.party.v3_0.CustomerNoAllIssuerType;
import ns.btfin_com.product.panorama.credentialservice.credentialrequest.v1_0.ProvisionOnlineAccessRequestMsgType;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import static com.btfin.panorama.test.jaxb.AbstractJaxbMarshallerTest.marshal;
import static com.btfin.panorama.test.jaxb.AbstractJaxbMarshallerTest.xml;
import static junit.framework.Assert.assertEquals;
import static ns.btfin_com.product.panorama.onboardingservice.onboardingrequest.v3_0.OnboardingRequestSchemaValidatorTest.ONBOARDING_REQUEST_V3_SCHEMA_PATH;
import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual;


/**
 * Tests for the {@code BtEsbRequestBuilder}.
 */
@RunWith(MockitoJUnitRunner.class)
public class BtEsbRequestBuilderTest extends AbstractSchemaValidatorTest {
    private static final Class<BtEsbRequestBuilderTest> MY_CLASS = BtEsbRequestBuilderTest.class;

    private final Logger logger = Logger.getLogger(getClass().getName());

    private BtEsbRequestBuilder btEsbRequestBuilder;

    public BtEsbRequestBuilderTest() {
        super(ONBOARDING_REQUEST_V3_SCHEMA_PATH);
    }

    @BeforeClass
    public static void configureXmlUnit() {
        AbstractJaxbTest.configureXmlUnit();
    }

    @Before
    public void setUp() throws Exception {
        btEsbRequestBuilder = new BtEsbRequestBuilder();
    }

    @Test
    public void buildValidatePartySMSOneTimePasswordChallengeRequestMsgType() throws Exception {
        FirstTimeRegistrationRequest request = new FirstTimeRegistrationRequestModel();
        request.setAction(ValidatePartyAndSmsAction.REGISTRATION);
        request.setLastName("lastName");
        request.setPostalCode("2000");
        request.setRegistrationCode("registrationCode");
        request.setDeviceToken("deviceToken");
        HttpRequestParams params = new HttpRequestParams();
        params.setHttpAccept("application/json, text/javascript, */*; q=0.01");
        params.setHttpAcceptChars("ISO-8859-1");
        params.setHttpAcceptEncoding("gzip, deflate");
        params.setHttpAcceptLanguage("en-GB,en;q=0.5");
        params.setHttpReferrer("http://localhost:9080/ng/public/page/logon");
        params.setHttpOriginatingIpAddress("0:0:0:0:0:0:0:1");
        params.setHttpUserAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:42.0) Gecko/20100101 Firefox/42.0");
        request.setHttpRequestParams(params);

        assertMarshalledXml(btEsbRequestBuilder.build(request));
    }


    /**
     * Marshall the provided object to XML using JAXB, and then check that it matches the XML present in the specified classpath resource
     * file. Also check that it validates against the ICC OnboardingRequest V3 schema.
     *
     * @param instance JAXB object to be marshalled.
     * @param fileName name of the XML file in the classpath to compare it to, minus the &quot;.xml&quot; suffix. Should exist in the same
     *                 package as this test class.
     */
    private void assertMarshalledXml(Object instance, String fileName) throws JAXBException, SAXException, IOException {
        validateJaxb(instance);
        assertXMLEqual(marshal(instance, logger), xml(MY_CLASS, fileName));
    }

    /**
     * Marshall the provided object to XML using JAXB, and then check that it matches the XML present in the specified classpath resource
     * file. File name is expected to match the class name (minus the &quot;Type&quot; suffix) of the incoming object.
     *
     * @param instance JAXB object to be marshalled.
     */
    private void assertMarshalledXml(Object instance) throws JAXBException, SAXException, IOException {
        String fileName = instance.getClass().getSimpleName();
        if (fileName.endsWith("Type")) {
            fileName = fileName.substring(0, fileName.length() - 4);
        }
        assertMarshalledXml(instance, fileName);
    }

    @Test
    public void testbuildInvestorProcessRequest(){

        ResendRegistrationEmailRequest request = new ResendRegistrationEmailRequestModel();
        Map<CustomerNoAllIssuerType, String> customerIdentifiers = new HashMap<>();
        customerIdentifiers.put(CustomerNoAllIssuerType.WESTPAC_LEGACY,"");
        customerIdentifiers.put(CustomerNoAllIssuerType.WESTPAC,"");
        request.setAdviserFirstName("Adviser");
        request.setCustomerIdentifiers(customerIdentifiers);

        ProvisionOnlineAccessRequestMsgType requestMsgType = btEsbRequestBuilder.buildInvestorProcessRequest(request);

        assertEquals(1 ,requestMsgType.getPartyDetails().getCustomerIdentifiers().getCustomerIdentifier().size());

        customerIdentifiers.put(CustomerNoAllIssuerType.WESTPAC_LEGACY,null);
        customerIdentifiers.put(CustomerNoAllIssuerType.WESTPAC,null);

         requestMsgType = btEsbRequestBuilder.buildInvestorProcessRequest(request);

        assertEquals(1 ,requestMsgType.getPartyDetails().getCustomerIdentifiers().getCustomerIdentifier().size());

        customerIdentifiers.put(CustomerNoAllIssuerType.WESTPAC_LEGACY,"1234");
        customerIdentifiers.put(CustomerNoAllIssuerType.WESTPAC,null);

        requestMsgType = btEsbRequestBuilder.buildInvestorProcessRequest(request);

        assertEquals(2 ,requestMsgType.getPartyDetails().getCustomerIdentifiers().getCustomerIdentifier().size());
    }

}

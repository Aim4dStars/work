package com.bt.nextgen.core.security;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;

import com.bt.nextgen.config.TestConfig;

/**
 * Created with IntelliJ IDEA.
 * User: Andrew Barker (m035652)
 * Date: 13/08/13
 * Time: 2:57 PM
 */
public class SecurityTokenRequestTest
{

    private static final Logger logger = LoggerFactory.getLogger(SecurityTokenRequestTest.class);


	StringWriter writer = new StringWriter();
	String request = "<RequestSecurityToken xmlns=\"http://docs.oasis-open.org/ws-sx/ws-trust/200512/\" xmlns:wsa=\"http://www.w3.org/2005/08/addressing\"\n" +
		"                      xmlns:pol=\"http://schemas.xmlsoap.org/ws/2004/09/policy\" xmlns:wpsts=\"http://www.westpac.com.au/sts\">\n" +
		"    <pol:AppliesTo>\n" +
		"            <wsa:EndpointReference>\n" +
		"                <wsa:Address>{0}</wsa:Address>\n" +
		"                <wpsts:PortType>{1}</wpsts:PortType>\n" +
		"                <wpsts:OperationName>{2}</wpsts:OperationName>\n" +
		"                <wpsts:OriginatingSystemId >{3}</wpsts:OriginatingSystemId>\n" +
		"                <wpsts:Actions >{4}</wpsts:Actions>\n" +
		"                <wpsts:MessageId>{9}</wpsts:MessageId>\n" +
		"                <wpsts:Version >{5}</wpsts:Version>\n" +
		"                <wpsts:Environment >{6}</wpsts:Environment>\n" +
		"            </wsa:EndpointReference>\n" +
		"    </pol:AppliesTo>\n" +
		"    <Issuer>\n" +
		"        <wsa:Address>urn:ESBMessagingDomain</wsa:Address>\n" +
		"    </Issuer>\n" +
		"    <RequestType>{7}</RequestType>\n" +
		"    <TokenType>{8}</TokenType>\n" +
		"</RequestSecurityToken>";

    @Test
    public void testGenerateSecurityTokenRequest()  throws Exception
    {
		SecurityTokenRequest tmp = new SecurityTokenRequest() ;
		tmp.setEndPointAlias("sts.avaloq.");
		/*tmp.setOperationName("operationName");
		tmp.setTokenType("tokenType");
		tmp.setVersion("version");
		tmp.setPortType("portType");
		tmp.setRequestType("requestType");
		tmp.setActions("actions");
		tmp.setEndpointAddress("endpointAddress");
		tmp.setOriginatingSystemId("originiatingSystemId");
		tmp.setEnvironment("environment");*/

		InputStream is = new ByteArrayInputStream(request.getBytes());
		IOUtils.copy(is,writer);
		tmp.setTemplate(writer);



        String result = tmp.generateSecurityTokenRequest();
        assertNotNull(result);
        assertThat(result, containsString("AvaloqGateway"));
        assertThat(result, containsString("NG-001"));
		assertThat(result, containsString("V1_0"));
		assertThat(result, containsString("ns://private.btfin.com/Avaloq/AvaloqGateway/V1_0/HTTPInterface/AvaloqGatewayRequest"));
        assertThat(result, containsString("DEV1"));


    }




}

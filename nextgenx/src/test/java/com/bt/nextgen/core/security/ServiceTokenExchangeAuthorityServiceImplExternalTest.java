package com.bt.nextgen.core.security;

import com.bt.nextgen.config.BaseSecureIntegrationTest;
import com.bt.nextgen.config.SecureTestContext;
import com.bt.nextgen.core.webservice.provider.WebServiceProvider;
import com.bt.nextgen.core.xml.XmlUtil;
import com.bt.nextgen.util.SamlUtil;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.client.core.WebServiceMessageCallback;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.w3c.dom.Document;

import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;
import  com.bt.nextgen.security.SecurityHeaderFactory;

/**
 * Created with IntelliJ IDEA.
 * User: Andrew Barker (m035652)
 * Date: 12/08/13
 * Time: 3:41 PM
 *
 * To run the tests contained in this class the ssl keystore needs to be configured
 * The sts-saml service also needs to be uncommented as this is used to check the saml token
 *
 * Filestubs also need to be turned off for the sts service
 *
 */
public class ServiceTokenExchangeAuthorityServiceImplExternalTest extends BaseSecureIntegrationTest
{

   	@Autowired
    ServiceTokenExchangeAuthorityServiceImpl sts;
	@Autowired
	private  WebServiceProvider provider;
	private static final String TRUST_REQUEST_TYPE = "http://docs.oasis-open.org/ws-sx/ws-trust/200512/Issue";
	private static final String SAML_TOKEN_TYPE = "http://docs.oasis-open.org/wss/oasis-wss-saml-token-profile-1.1#SAMLV2.0";

	private final static Logger logger = LoggerFactory.getLogger(ServiceTokenExchangeAuthorityServiceImpl.class);

	@Autowired
	private SecurityTokenRequest avaloqSecurityRequest;

	@SecureTestContext(username = "investor_investor_10000705", password="z", authorities = {"ROLE_INVESTOR"})
	@Test
    public void testSTSRequest() throws Exception
   	{

	   	logger.debug("===== Making a Request for an STS token =======");
	   	String securityTokenResponse = sts.requestAuthorityForEndpoint();

	   	assertThat(securityTokenResponse,is(notNullValue()));

	   //String token = sts.extractSamlToken(securityTokenResponse);
	   //assertThat(token,is(notNullValue()));
	   	//assertThat(token,is(not("")));
	   	logger.debug("===== Saml Assertion has been loaded, now test this by requesting another token using the original SAML token =======");
	  // logger.info("The current SAML Assertion is:\n{}" , token);
	   final com.btfin.panorama.core.security.saml.SamlToken samlToken = SamlUtil.getSamlTokenFromDocument(XmlUtil.parseDocument(securityTokenResponse));
		//final SamlToken samlToken = new SamlToken(token);
	   	StreamSource source = new StreamSource(new StringReader(avaloqSecurityRequest.generateSecurityTokenRequest()));

	   	StreamResult result = new StreamResult(System.out);
	   	//STS-SAML needs to be declared as a spring web service template to execute this test
		final WebServiceTemplate template = provider.getWebServiceTemplate("sts-saml");

	   	//template.
	   	template.sendSourceAndReceiveToResult(source, new WebServiceMessageCallback()
	   {
		   public void doWithMessage(WebServiceMessage message) throws IOException
		   {
			   SecurityHeaderFactory.addSecurityHeader(message, samlToken,template);
		   }
	   }, result);


	}

	@SecureTestContext(username = "investor_investor_10000705", password="z", authorities = {"ROLE_INVESTOR"})
	@Test
	public void testSamlParsing() throws Exception
	{

		StringWriter writer = new StringWriter();
		IOUtils.copy(new ClassPathResource("server-saml-response.xml").getInputStream(), writer);

		String securityTokenResponse = writer.toString();

		Document xml = XmlUtil.parseDocument(securityTokenResponse);
		com.btfin.panorama.core.security.saml.SamlToken samlToken = SamlUtil.getSamlTokenFromDocument(xml);
		assertThat(samlToken,is(notNullValue()));
		com.btfin.panorama.core.security.saml.SamlToken samlTokenExtractedByRegex = new com.btfin.panorama.core.security.saml.SamlToken(SamlUtil.extractSamlToken(securityTokenResponse));
		assertThat(samlToken.getToken(), equalTo(samlTokenExtractedByRegex.getToken()));

		logger.debug("The samlToken we loaded is {}",samlToken.getToken());


	}



}

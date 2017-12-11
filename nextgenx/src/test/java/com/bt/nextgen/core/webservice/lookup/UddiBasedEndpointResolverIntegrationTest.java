package com.bt.nextgen.core.webservice.lookup;

import com.bt.nextgen.config.BaseSecureIntegrationTest;
import com.bt.nextgen.core.webservice.provider.SpringWebServiceTemplateProvider;
import com.bt.nextgen.core.webservice.provider.WebServiceProvider;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.test.client.MockWebServiceServer;
import org.springframework.xml.transform.StringSource;

import static org.springframework.ws.test.client.RequestMatchers.xpath;
import static org.springframework.ws.test.client.ResponseCreators.withPayload;

public class UddiBasedEndpointResolverIntegrationTest extends BaseSecureIntegrationTest
{
	private static final String RESPONSE = "<bindingDetail truncated=\"false\" "
		+ "xmlns=\"urn:uddi-org:api_v3\"><bindingTemplate bindingKey=\"uddi:icc.btfin.com:dp:dev1:c52b941b-fb35-4cfc-9488-32cbd9ff77fc\" "
		+ "serviceKey=\"uddi:1d82f945-208c-43aa-9174-aa8a5175193a\"><description xml:lang=\"en\">FileNet P8 CMIS Discovery Service</description>"
		+ "<accessPoint useType=\"endPoint\">https://dp-ma.dev1.btesb.srv.westpac.com.au/fncmis/DiscoveryService</accessPoint></bindingTemplate>"
		+ "</bindingDetail>";
	public static final String XPATH_EXPRESSION = "//*[local-name() = 'get_bindingDetail']";

	private static final String UDDI_ADDRESS = "https://dp-ma.dev1.btesb.srv.westpac.com.au/fncmis/DiscoveryService";

	@Autowired
	private WebServiceProvider provider;

	private UddiBasedEndpointResolver resolver;

	@Before
	public void setUp() throws Exception
	{
		WebServiceTemplate template = provider.getWebServiceTemplate(SpringWebServiceTemplateProvider.SERVICE_UDDI);
		MockWebServiceServer server = MockWebServiceServer.createServer(template);

		server.expect(xpath(XPATH_EXPRESSION).exists()).andRespond(withPayload(new StringSource(RESPONSE)));
		resolver = new UddiBasedEndpointResolver(provider);
	}

	@Test
	public void testResolveEndPoint() throws Exception
	{
		Assert.assertEquals(UDDI_ADDRESS,
			resolver.resolveEndPoint("uddi:icc.btfin.com:dp:dev1:c52b941b-fb35-4cfc-9488-32cbd9ff77fc"));
	}
}

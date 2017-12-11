package com.bt.nextgen.core.webservice.provider;

import com.bt.nextgen.config.BaseSecureIntegrationTest;
import com.bt.nextgen.core.webservice.lookup.EndpointResolver;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created with IntelliJ IDEA.
 * User: Andrew Barker (m035652)
 * Date: 14/08/13
 * Time: 8:39 AM
 */
public class SpringTemplateProviderExternalTest extends BaseSecureIntegrationTest
{

	Logger logger = LoggerFactory.getLogger(SpringTemplateProviderExternalTest.class);

    @Autowired
    WebServiceProvider provider;

    @Test
    public void testUDDIDiscovery() throws Exception
    {

		assertNotNull(System.getProperty("javax.net.ssl.keyStore"));
		assertNotNull(System.getProperty("javax.net.ssl.keyStorePassword"));
		assertNotNull(System.getProperty("javax.net.ssl.trustStore"));
		assertNotNull(System.getProperty("javax.net.ssl.trustStorePassword"));

		assertTrue(provider instanceof SpringWebServiceTemplateProvider);
        SpringWebServiceTemplateProvider prov = (SpringWebServiceTemplateProvider)provider;

        EndpointResolver resolver = prov.getUddiResolver();
        String ep1 = resolver.resolveEndPoint("4c4ad71f-74a4-419a-b973-44791b81b75f");
		assertNotNull(ep1);


		String endpoint = resolver.resolveEndPoint("2f88d8af-1fb8-4aee-b93d-58bd87a4776b");
        assertNotNull(endpoint);


    }


	@Test
	public void testTokenWebservice()
	{
		//get the payload here
		provider.sendWebService(SpringWebServiceTemplateProvider.SERVICE_UDDI, "");

	}


}

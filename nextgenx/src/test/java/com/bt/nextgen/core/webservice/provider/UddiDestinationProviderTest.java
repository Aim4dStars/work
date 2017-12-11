package com.bt.nextgen.core.webservice.provider;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.URI;

import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.uddi.api_v3.AccessPoint;
import org.uddi.api_v3.BindingDetail;
import org.uddi.api_v3.BindingTemplate;
import org.uddi.api_v3.ObjectFactory;

import com.bt.nextgen.core.webservice.lookup.UddiBasedEndpointResolver;

public class UddiDestinationProviderTest
{
	private UddiDestinationProvider provider;

	@Before
	public void setUp() throws Exception
	{
		WebServiceTemplate template = mock(WebServiceTemplate.class);
		//JAXBElement jaxbElement = mock(JAXBElement.class);
		ObjectFactory of = new ObjectFactory();
		BindingDetail bindingDetail = of.createBindingDetail();
		BindingTemplate bindingTemplate = of.createBindingTemplate();
		AccessPoint accessPoint = of.createAccessPoint();
		accessPoint.setValue("https://dummyValue");
		bindingTemplate.setAccessPoint(accessPoint);
		bindingDetail.getBindingTemplate().add(bindingTemplate);
		//when(jaxbElement.getValue()).thenReturn(bindingDetail);
		when(template.marshalSendAndReceive(anyObject())).thenReturn(bindingDetail);
		SpringWebServiceTemplateProvider factory = new SpringWebServiceTemplateProvider();
		factory.addTemplate(SpringWebServiceTemplateProvider.SERVICE_UDDI, template);
		provider = new UddiDestinationProvider(new UddiBasedEndpointResolver(factory), "bindingKey");
	}

	@Test
	public void testLookupDestination() throws Exception
	{
		// Use Jaxb stubs directly in this test.
		// To test the SOAP stuffs. Go to to the UddiBasedEndpointResolver.
		// In this test, it is assumed that the whole jaxb business will work right.
		URI url = provider.getDestination();
		Assert.assertThat(url.getHost(), Is.is("dummyValue"));
		Assert.assertThat(url.getScheme(), Is.is("https"));
	}
}

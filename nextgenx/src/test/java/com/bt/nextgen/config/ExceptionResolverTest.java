package com.bt.nextgen.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.xml.transform.dom.DOMSource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.ws.soap.SoapFault;
import org.springframework.ws.soap.client.SoapFaultClientException;

import com.btfin.panorama.core.security.Roles;
import com.btfin.panorama.core.security.profile.Profile;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.core.webservice.exception.WebServiceClientException;

@RunWith(MockitoJUnitRunner.class)
public class ExceptionResolverTest
{
	@InjectMocks
	private ExceptionResolver exceptionResolver = new ExceptionResolver();

	@Mock
	private UserProfileService mockProfileService;

	@Before
	public void setup()
	{
		Profile profile = mock(Profile.class);
		when(mockProfileService.getEffectiveProfile()).thenReturn(profile);

		when(profile.getRoles()).thenReturn(new Roles[0]);
	}

	@Test
	public void testSetExceptionMappingsProperties()
	{
		Properties properties = new Properties();
		exceptionResolver.setExceptionMappings(properties);
		String value = properties.getProperty("java.lang.Exception");
		assertNotNull(value);
		assertEquals(value, "error");
		
	}

	@Test
	public void testGetModelAndView()
	{
		Exception ex = mock(Exception.class);
		HttpServletRequest request= mock(HttpServletRequest.class);
		ModelAndView modelAndView =exceptionResolver.getModelAndView("error", ex, request);
		assertNotNull(modelAndView);
		
		SoapFaultClientException soapFaultClientException =  mock(SoapFaultClientException.class);
		when(soapFaultClientException.getCause()).thenReturn( new Throwable("Soap fault"));
		SoapFault soapFault = mock(SoapFault.class);
		when(soapFault.getSource()).thenReturn(new DOMSource());
		when(soapFaultClientException.getSoapFault()).thenReturn(soapFault);
		ex = new WebServiceClientException("Web Service Exception",soapFaultClientException);
	    modelAndView =exceptionResolver.getModelAndView("error", ex, request);
	    assertNotNull(modelAndView);
		
	}

}

package com.bt.nextgen.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.xml.transform.TransformerException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;
import org.springframework.ws.soap.client.SoapFaultClientException;

import com.bt.nextgen.core.exception.XmlUnmarshallException;
import com.btfin.panorama.core.security.Roles;
import com.btfin.panorama.core.security.profile.Profile;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.core.web.util.View;
import com.bt.nextgen.core.webservice.exception.WebServiceClientException;
import com.bt.nextgen.core.xml.XmlUtil;
import com.bt.nextgen.service.avaloq.gateway.AvaloqException;
import com.bt.nextgen.service.error.BTEsbException;
import com.bt.nextgen.service.error.GroupEsbException;
import com.bt.nextgen.service.error.IntegrationException;

public class ExceptionResolver extends SimpleMappingExceptionResolver
{
	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private UserProfileService profileService;

	public ExceptionResolver()
	{
		super();
		setDefaultErrorView(View.ERROR_500);
	}

	@Override
	public void setExceptionMappings(Properties mappings)
	{
		mappings.setProperty("java.lang.Exception", "error");
		super.setExceptionMappings(mappings);
	}

	private final String TEMPLATE = "subTemplate";
	@Override
	protected ModelAndView getModelAndView(String viewName, Exception e, HttpServletRequest request)
	{
		if(isUnauthorised())
		{
			return new ModelAndView("redirect:/public/static/page/maintenance.html");
		}

		ModelAndView model = super.getModelAndView(viewName, e);
		model.addObject("subTemplate", "base");

		if (e instanceof WebServiceClientException && e.getCause() instanceof SoapFaultClientException && ((SoapFaultClientException) e.getCause()).getSoapFault() != null && ((SoapFaultClientException) e.getCause()).getSoapFault().getSource() != null)
		{
			try
			{
				String xml = XmlUtil.prettyPrint(((SoapFaultClientException) e.getCause()).getSoapFault().getSource());
				logger.info("Soap fault:\n" + xml);
				xml = xml.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
				model.addObject("soapException", xml);
			}
			catch (TransformerException ex)
			{
				logger.error("Exception caught while transforming xml", ex);
			}
		}

		if(e instanceof XmlUnmarshallException){
			model.addObject("subTemplate", "unmarshall");
			logger.error("returning the special view, oldViewName was {}", viewName);
			model.setViewName("error.xmlUnmarshall");
			model.addObject("rootCauseException",e);
		}
		
		if (e instanceof GroupEsbException || e instanceof BTEsbException || e instanceof AvaloqException)
		{
			model.addObject("errorcode",((IntegrationException) e).getAbbreviatedErrorForDisplay());
			model.setViewName(View.ERROR_FATAL);
			logger.error("Fatal exception occured", e);
			return model;
		}
		
		model.addObject("exceptions", produceStackTrace(e));

		logger.warn("Unhandled exception", e);

		return model;
	}

	Collection<Throwable> produceStackTrace(Throwable rootCause)
	{
		Collection<Throwable> exceptions = new ArrayList<>();
		Throwable exception = rootCause;
		while (exception != null)
		{
			exceptions.add(exception);
			exception = exception.getCause();
		}
		return exceptions;

	}

	private boolean isUnauthorised()
	{
		final Profile effectiveProfile = profileService.getEffectiveProfile();

		List<Roles> roles = Arrays.asList(effectiveProfile.getRoles());
		return roles.contains(Roles.ROLE_ANONYMOUS) || roles.size() == 0;
	}

}

package com.bt.nextgen.core.web.controller;

import com.bt.nextgen.core.exception.AccessDeniedException;
import com.bt.nextgen.core.web.interceptor.Spring3CorsFilter;
import com.bt.nextgen.core.webservice.lookup.EndpointResolver;
import com.bt.nextgen.core.webservice.lookup.UddiBasedEndpointResolver;
import com.bt.nextgen.core.webservice.provider.SpringWebServiceTemplateProvider;
import com.bt.nextgen.core.webservice.provider.UddiDestinationProvider;
import com.bt.nextgen.core.webservice.provider.WebServiceProvider;
import com.btfin.panorama.core.security.avaloq.Constants;
import com.bt.nextgen.service.avaloq.bankdate.BankDateValueGetterImpl;
import com.bt.nextgen.util.Environment;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.client.support.destination.DestinationProvider;
import org.springframework.ws.soap.SoapMessageCreationException;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.URI;

@Controller
public class HeartbeatController
{
	private static final Logger logger = LoggerFactory.getLogger(HeartbeatController.class);

	protected static final String UDDI_KEY_FAILURE = "UDDI-1234-NULL-5678-TEST";

	protected static final String STATUS_OK = "OK";

	protected static final String STATUS_ERROR = "ERROR";

	protected static final String STATUS_NA = "N/A";

	// Make the destinationProvider optionally settable for testing
	private DestinationProvider settableDestinationProvider;

	@Autowired
	private BankDateValueGetterImpl bankDateValueGetterImpl;

	@Autowired
	private WebServiceProvider webServiceProvider;

	/**
	 * Get the URI of the Endpoint for the GESB Generate Credential - where there is no key so we get an NA.
	 *
	 * eg /public/heartbeat/uddi/gesb-generate-credentials
	 *
	 * @param webServiceTemplateKey
	 * @return
	 */
	@RequestMapping(value="/public/heartbeat/uddi/{webServiceTemplateKey}",
			method = RequestMethod.GET, produces = "text/plain")
	@Spring3CorsFilter
	public void showGenericUDDIKeyResolution(@PathVariable("webServiceTemplateKey") String webServiceTemplateKey,
											 HttpServletResponse response) throws IOException
	{
		logger.info("/public/heartbeat/uddi/{} called", webServiceTemplateKey);
		if(Environment.isProduction()){
			throw new AccessDeniedException("You are not authorised");
		}
		WebServiceTemplate template = webServiceProvider.getWebServiceTemplate(webServiceTemplateKey);
		DestinationProvider destProvider = template.getDestinationProvider();
		String result = "";
		if (destProvider.getDestination() == null) {
			result = template.getDefaultUri();
		} else {
			// Is this a non-UDDI provider
			if (destProvider instanceof UddiDestinationProvider) {
				logger.info("/public/heartbeat/uddi/{} - UDDI key is set", webServiceTemplateKey);
				((UddiDestinationProvider) destProvider).refreshDestination();
			} else {
				logger.info("/public/heartbeat/uddi/{} - no UDDI key set", webServiceTemplateKey);
			}
			URI uri = destProvider.getDestination();
			result = (String) uri.toASCIIString();
		}
		//Handle status methods calling this with a null value
		if (response != null) {
			setCorsHeaders(response);
			response.getWriter().write(result);
		}
	}

	private void setCorsHeaders(HttpServletResponse response) {
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
		response.setHeader("Access-Control-Max-Age", "3600");
		response.setHeader("Access-Control-Allow-Headers", "x-requested-with");
	}

	/**
	 * Return the status of the UDDI resolution for a generic web service template
	 *
	 * eg /public/heartbeat/uddi/gesb-generate-credentials/status
	 *
	 * @return
	 */
	@RequestMapping(value = {
			"/public/heartbeat/uddi/{webServiceTemplateKey}/status",
	}, method = RequestMethod.GET, produces = "text/plain")
	@Spring3CorsFilter
	public void showGenericUDDIKeyResolutionStatus(@PathVariable("webServiceTemplateKey") String
			webServiceTemplateKey, HttpServletResponse response) throws IOException
	{
		if(Environment.isProduction()){
			throw new AccessDeniedException("You are not authorised");
		}
		String result = "";
		try {
			WebServiceTemplate template = webServiceProvider.getWebServiceTemplate(webServiceTemplateKey);

			DestinationProvider destProvider = template.getDestinationProvider();
			URI uri = destProvider.getDestination();
			String resultURI = (String) uri.toASCIIString();

			// Did we get the default back for an invalid key?
			WebServiceTemplate defaultTemplate = webServiceProvider.getWebServiceTemplate("stub");
			DestinationProvider defaultDestProvider = defaultTemplate.getDestinationProvider();
			URI defaultUri = defaultDestProvider.getDestination();
			String defaultURI = (String) defaultUri.toASCIIString();
			if (resultURI.equalsIgnoreCase(defaultURI)) {
				result = STATUS_ERROR;
			} else if (!(destProvider instanceof UddiDestinationProvider)) {
				result = STATUS_NA;
			} else {
				showGenericUDDIKeyResolution(webServiceTemplateKey, null);
				result = STATUS_OK;
			}
		} catch (SoapMessageCreationException e) {
			logger.error("showGenericUDDIKeyResolutionStatus error: ", e);
			result = STATUS_ERROR;
		}
		setCorsHeaders(response);
		response.getWriter().write(result);
	}

	/**
	 * Demonstrate returning the error of a bogus key.
	 * @return
	 */
	@RequestMapping(value = {
			"/public/heartbeat/uddi/null-test",
	}, method = RequestMethod.GET, produces = "text/plain")
	@Spring3CorsFilter
	public void showNullUDDIKeyResolutionFailure(HttpServletResponse response) throws IOException
	{
		if(Environment.isProduction()){
			throw new AccessDeniedException("You are not authorised");
		}
		WebServiceTemplate template = webServiceProvider.getWebServiceTemplate(SpringWebServiceTemplateProvider.SERVICE_UDDI);
		EndpointResolver resolver = new UddiBasedEndpointResolver(webServiceProvider);
		DestinationProvider destProvider = uddiDestinationProviderConstructorFactory(resolver, UDDI_KEY_FAILURE);
		String result = "";
		if (null == template.getDestinationProvider()) {
			result = template.getDefaultUri();
		} else {
			((UddiDestinationProvider) destProvider).refreshDestination();
			// Expect this to blow up with SOAPExceptionImpl in SaajSoapMessageFactory.java:206
			URI uri = destProvider.getDestination();
			result = (String) uri.toASCIIString();
		}
		setCorsHeaders(response);
		response.getWriter().write(result);
	}

	@RequestMapping(value = {
		"/public/heartbeat/uddi/null-test/status",
	}, method = RequestMethod.GET, produces = "text/plain")
	@Spring3CorsFilter
	public void showNullUDDIKeyResolutionFailureStatus(HttpServletResponse response) throws IOException
	{
		if(Environment.isProduction()){
			throw new AccessDeniedException("You are not authorised");
		}
		String result = "";
		try {
			WebServiceTemplate template = webServiceProvider.getWebServiceTemplate(SpringWebServiceTemplateProvider.SERVICE_UDDI);
			if (null == template.getDestinationProvider()) {
				result = STATUS_NA;
			}
			showNullUDDIKeyResolutionFailure(response);
			result = STATUS_OK;
		} catch (SoapMessageCreationException e) {
			logger.info("The WARNING message below is not supposed to break anything. It proves that the Heartbeat is working as expected.");
			logger.warn("showNullUDDIKeyResolutionFailureStatus error: ", e);
			result = STATUS_ERROR;
		}
		setCorsHeaders(response);
		response.getWriter().write(result);
	}

	/**
	 * Get the value of the current date on Avaloq - to show that Avaloq is available and all the intermediate
	 * infrastructure is working correctly.
	 * @return String of current Avaloq date
	 */
	@RequestMapping(value = {
		"/public/heartbeat/avaloq",
	}, method = RequestMethod.GET, produces = "text/plain")
	@Spring3CorsFilter
	public void showAvaloqDate(HttpServletResponse response) throws IOException
	{
		if(Environment.isProduction()){
			throw new AccessDeniedException("You are not authorised");
		}
		DateTime dt = (DateTime)bankDateValueGetterImpl.getValue(Constants.BANKDATE);
		DateTimeFormatter fmt = DateTimeFormat.fullDateTime();
		String result = "";
		result = fmt.print(dt);
		if (response != null) {
			setCorsHeaders(response);
			response.getWriter().write(result);
		}
	}

	@RequestMapping(value = {
		"/public/heartbeat/avaloq/status",
	}, method = RequestMethod.GET, produces = "text/plain")
	@Spring3CorsFilter
	public void showAvaloqDateStatus(HttpServletResponse response) throws IOException
	{
		if(Environment.isProduction()){
			throw new AccessDeniedException("You are not authorised");
		}
		String result = "";
		try {
			showAvaloqDate(null);
			result = STATUS_OK;
		} catch (SoapMessageCreationException e) {
			logger.error("showAvaloqDateStatus error: ",e);
			result = STATUS_ERROR;
		}
		setCorsHeaders(response);
		response.getWriter().write(result);
	}

	/**
	 * Return the status of the socket opening for a particular endpoint
	 *
	 * eg /public/heartbeat/socket/gesb-generate-credentials/status
	 *
	 * @return
	 */
	@RequestMapping(value = {
			"/public/heartbeat/socket/{webServiceTemplateKey}/status",
	}, method = RequestMethod.GET, produces = "text/plain")
	@Spring3CorsFilter
	public void showGenericSocketOpeningStatus(@PathVariable("webServiceTemplateKey") String
														   webServiceTemplateKey, HttpServletResponse response)
			throws IOException
	{
		if(Environment.isProduction()){
			throw new AccessDeniedException("You are not authorised");
		}
		String result = "";
		try {
			WebServiceTemplate template = webServiceProvider.getWebServiceTemplate(webServiceTemplateKey);
			DestinationProvider destProvider = template.getDestinationProvider();

			String host = destProvider.getDestination().getHost();

			int port = destProvider.getDestination().getPort();
			if (port == -1)
			{
				port = 443;
			}
			InetSocketAddress socketAddress = new InetSocketAddress(host, port);

			Socket socket = new Socket();
			socket.connect(socketAddress, 1000);

			result = STATUS_OK;
		} catch (SocketTimeoutException e) {
			logger.error("showGenericUDDIKeyResolutionStatus error: ", e);
			result = STATUS_ERROR;
		}
		setCorsHeaders(response);
		response.getWriter().write(result);
	}

	/**
	 * Include for testing - so UDDIDestinationProvider can be mocked
	 * @param destinationProvider
	 */
	@SuppressWarnings({"squid:UnusedProtectedMethod"})
	protected void setUDDIDestinationProvider(DestinationProvider destinationProvider) {
		settableDestinationProvider = destinationProvider;
	}

	public DestinationProvider uddiDestinationProviderConstructorFactory(EndpointResolver resolver, String key) {
		DestinationProvider returnVal = null;
		if (settableDestinationProvider != null) {
			returnVal = settableDestinationProvider;
		} else {
			returnVal = new UddiDestinationProvider(resolver, key);
		}
		return returnVal;
	}


	/**
	 * Catches Errors and displays an error
	 * @param exception - type of RuntimeException
	 *
	 * @return ModelAndView - error page
	 */
	/*
	@ExceptionHandler({
			NullPointerException.class,
			SOAPVersionMismatchException.class,
			SoapMessageCreationException.class,
			SOAPExceptionImpl.class,
			RuntimeException.class
	})
	public String handleSoapFaultClientException(Exception exception) {
		logger.info("Exception to resolve. " + exception.getClass().getSimpleName());
		return exception.toString();
	}
	*/

}
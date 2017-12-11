package com.bt.nextgen.core.web.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;

import javax.servlet.http.HttpServletResponse;

import org.hamcrest.core.Is;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.client.support.destination.DestinationProvider;
import org.springframework.ws.client.support.destination.Wsdl11DestinationProvider;
import org.springframework.ws.soap.SoapMessageCreationException;

import com.bt.nextgen.core.exception.AccessDeniedException;
import com.bt.nextgen.core.util.Properties;
import com.bt.nextgen.core.webservice.lookup.EndpointResolver;
import com.bt.nextgen.core.webservice.lookup.UddiBasedEndpointResolver;
import com.bt.nextgen.core.webservice.provider.SpringWebServiceTemplateProvider;
import com.bt.nextgen.core.webservice.provider.UddiDestinationProvider;
import com.bt.nextgen.core.webservice.provider.WebServiceProvider;
import com.btfin.panorama.core.security.avaloq.Constants;
import com.bt.nextgen.service.avaloq.bankdate.BankDateValueGetterImpl;
import com.bt.nextgen.web.controller.cash.util.Attribute;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.when;


public class HeartbeatControllerTest
{
	private HeartbeatController controller = new HeartbeatController();

	@Before
	public void beforeTest() {
		setEnvironmentToDev();
	}

	@Test
	public void testShowAvaloqDate_returnsAString() throws Exception
	{
		BankDateValueGetterImpl mockBankDateGetter = Mockito.mock(BankDateValueGetterImpl.class);
		ReflectionTestUtils.setField(controller, "bankDateValueGetterImpl", mockBankDateGetter);

		DateTime dateTime = new DateTime();
		when(mockBankDateGetter.getValue(Constants.BANKDATE)).thenReturn(dateTime);

		HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
		StringWriter sw = new StringWriter();
		PrintWriter writer = new PrintWriter(sw);
		when(response.getWriter()).thenReturn(writer);

		controller.showAvaloqDate(response);
		writer.flush();
		String result = sw.toString();
	}

	/**
	 * Testing /public/heartbeat/uddi/gesb-generate-credentials/status
	 * @throws Exception
	 */
	@Test
	public void showAvaloqDateStatus_isCorrect() throws Exception
	{
		BankDateValueGetterImpl mockBankDateGetter = Mockito.mock(BankDateValueGetterImpl.class);
		ReflectionTestUtils.setField(controller, "bankDateValueGetterImpl", mockBankDateGetter);

		DateTime dateTime = new DateTime();
		when(mockBankDateGetter.getValue(Constants.BANKDATE)).thenReturn(dateTime);

		HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
		StringWriter sw = new StringWriter();
		PrintWriter writer = new PrintWriter(sw);
		when(response.getWriter()).thenReturn(writer);

		controller.showAvaloqDateStatus(response);

		writer.flush();
		String result = sw.toString();

		assertThat(result, is(equalTo(HeartbeatController.STATUS_OK)));
	}

	@Test
	public void showApplicationSubmissionUDDIKeyResolution_returnsString() throws Exception
	{
		WebServiceProvider mockWebServiceProvider = Mockito.mock(WebServiceProvider.class);
		ReflectionTestUtils.setField(controller, "webServiceProvider", mockWebServiceProvider);

		WebServiceTemplate mockWebServiceTemplate = Mockito.mock(WebServiceTemplate.class);

		String testURI = "www.abc123.com";
		URI uri = new URI(testURI);
		UddiDestinationProvider mockDestinationProvider = Mockito.mock(UddiDestinationProvider.class);

		when(mockDestinationProvider.getDestination()).thenReturn(uri);
		when(mockWebServiceTemplate.getDestinationProvider()).thenReturn(mockDestinationProvider);
		when(mockWebServiceProvider.getWebServiceTemplate(Attribute.APPLICATION_SUBMISSION_KEY))
				.thenReturn(mockWebServiceTemplate);

		HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
		StringWriter sw = new StringWriter();
		PrintWriter writer = new PrintWriter(sw);
		when(response.getWriter()).thenReturn(writer);

		controller.showGenericUDDIKeyResolution(Attribute.APPLICATION_SUBMISSION_KEY, response);

		writer.flush();
		String result = sw.toString();

		assertThat(result, Is.is(testURI));
	}


	@Test(expected = AccessDeniedException.class)
	public void showGenericUDDIKeyResolution_FailsInProduction() throws Exception {
		setEnvironmentToProd();

		mockWebServiceProvider();

		HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
		StringWriter sw = new StringWriter();
		PrintWriter writer = new PrintWriter(sw);
		when(response.getWriter()).thenReturn(writer);

		controller.showGenericUDDIKeyResolution(Attribute.APPLICATION_SUBMISSION_KEY, response);

		setEnvironmentToDev();
	}

	@Test(expected = AccessDeniedException.class)
	public void showGenericUDDIKeyResolutionStatus_FailsInProduction() throws Exception {
		setEnvironmentToProd();

		mockWebServiceProvider();

		HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
		StringWriter sw = new StringWriter();
		PrintWriter writer = new PrintWriter(sw);
		when(response.getWriter()).thenReturn(writer);

		controller.showGenericUDDIKeyResolutionStatus(Attribute.APPLICATION_SUBMISSION_KEY, response);

		setEnvironmentToDev();
	}

	@Test(expected = AccessDeniedException.class)
	public void showNullUDDIKeyResolutionFailure_FailsInProduction() throws Exception {
		setEnvironmentToProd();

		mockWebServiceProvider();

		HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
		StringWriter sw = new StringWriter();
		PrintWriter writer = new PrintWriter(sw);
		when(response.getWriter()).thenReturn(writer);

		controller.showNullUDDIKeyResolutionFailure(response);

		setEnvironmentToDev();
	}

	@Test(expected = AccessDeniedException.class)
	public void showNullUDDIKeyResolutionFailureStatus_FailsInProduction() throws Exception {
		setEnvironmentToProd();

		mockWebServiceProvider();

		HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
		StringWriter sw = new StringWriter();
		PrintWriter writer = new PrintWriter(sw);
		when(response.getWriter()).thenReturn(writer);

		controller.showNullUDDIKeyResolutionFailureStatus(response);

		setEnvironmentToDev();
	}

	@Test(expected = AccessDeniedException.class)
	public void showAvaloqDate_FailsInProduction() throws Exception {
		setEnvironmentToProd();

		mockWebServiceProvider();

		HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
		StringWriter sw = new StringWriter();
		PrintWriter writer = new PrintWriter(sw);
		when(response.getWriter()).thenReturn(writer);

		controller.showAvaloqDate(response);

		setEnvironmentToDev();
	}

	@Test(expected = AccessDeniedException.class)
	public void showAvaloqDateStatus_FailsInProduction() throws Exception {
		setEnvironmentToProd();

		mockWebServiceProvider();

		HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
		StringWriter sw = new StringWriter();
		PrintWriter writer = new PrintWriter(sw);
		when(response.getWriter()).thenReturn(writer);

		controller.showAvaloqDateStatus(response);

		setEnvironmentToDev();
	}

	@Test(expected = AccessDeniedException.class)
	public void showGenericSocketOpeningStatus_FailsInProduction() throws Exception {
		setEnvironmentToProd();

		mockWebServiceProvider();

		HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
		StringWriter sw = new StringWriter();
		PrintWriter writer = new PrintWriter(sw);
		when(response.getWriter()).thenReturn(writer);

		controller.showGenericSocketOpeningStatus(Attribute.APPLICATION_SUBMISSION_KEY, response);

		setEnvironmentToDev();
	}

	/**
	 * Testing /public/heartbeat/uddi/gesb-generate-credentials
	 * @throws Exception
	 */
	@Test
	public void showGenerateCredentialsUDDIKeyResolution_returnsString() throws Exception
	{
		WebServiceProvider mockWebServiceProvider = Mockito.mock(WebServiceProvider.class);
		ReflectionTestUtils.setField(controller, "webServiceProvider", mockWebServiceProvider);

		WebServiceTemplate mockWebServiceTemplate = Mockito.mock(WebServiceTemplate.class);

		String testURI = "www.abc123.com";
		URI uri = new URI(testURI);
		UddiDestinationProvider mockDestinationProvider = Mockito.mock(UddiDestinationProvider.class);

		when(mockDestinationProvider.getDestination()).thenReturn(uri);
		when(mockWebServiceTemplate.getDestinationProvider()).thenReturn(mockDestinationProvider);
		when(mockWebServiceProvider.getWebServiceTemplate(Attribute.GROUP_ESB_GENERATE_SECURITY_CREDENTIAL))
				.thenReturn(mockWebServiceTemplate);

		HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
		StringWriter sw = new StringWriter();
		PrintWriter writer = new PrintWriter(sw);
		when(response.getWriter()).thenReturn(writer);

		controller.showGenericUDDIKeyResolution(Attribute.GROUP_ESB_GENERATE_SECURITY_CREDENTIAL, response);
		writer.flush();
		String result = sw.toString();

		assertThat(result, Is.is(testURI));
	}

	/**
	 * Testing /public/heartbeat/uddi/gesb-generate-credentials/status
	 * @throws Exception
	 */
	@Test
	public void showGenerateCredentialsStatusUDDIKeyResolutionStatus_isCorrect() throws Exception
	{
		WebServiceProvider mockWebServiceProvider = Mockito.mock(WebServiceProvider.class);
		ReflectionTestUtils.setField(controller, "webServiceProvider", mockWebServiceProvider);

		WebServiceTemplate mockWebServiceTemplate = Mockito.mock(WebServiceTemplate.class);
		WebServiceTemplate mockdefaultWebServiceTemplate = Mockito.mock(WebServiceTemplate.class);

		String testURI = "www.abc123.com";
		URI uri = new URI(testURI);
		DestinationProvider mockDestinationProvider = Mockito.mock(Wsdl11DestinationProvider.class);
		String testDefaultURI = "www.abc1234.com";
		URI defaultUri = new URI(testDefaultURI);
		DestinationProvider mockDefaultDestinationProvider = Mockito.mock(Wsdl11DestinationProvider.class);

		when(mockDestinationProvider.getDestination()).thenReturn(uri);
		when(mockWebServiceTemplate.getDestinationProvider()).thenReturn(mockDestinationProvider);
		when(mockWebServiceProvider.getWebServiceTemplate(Attribute.GROUP_ESB_GENERATE_SECURITY_CREDENTIAL))
				.thenReturn(mockWebServiceTemplate);

		when(mockDefaultDestinationProvider.getDestination()).thenReturn(defaultUri);
		when(mockdefaultWebServiceTemplate.getDestinationProvider()).thenReturn(mockDefaultDestinationProvider);
		when(mockWebServiceProvider.getWebServiceTemplate("stub"))
				.thenReturn(mockdefaultWebServiceTemplate);

		HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
		StringWriter sw = new StringWriter();
		PrintWriter writer = new PrintWriter(sw);
		when(response.getWriter()).thenReturn(writer);

		controller.showGenericUDDIKeyResolutionStatus(Attribute.GROUP_ESB_GENERATE_SECURITY_CREDENTIAL, response);
		writer.flush();
		String result = sw.toString();

		assertThat(result, is(equalTo(HeartbeatController.STATUS_NA)));
	}

	/**
	 * Testing /public/heartbeat/uddi/gesb-generate-credentials/status
	 * @throws Exception
	 */
	@Test
	public void showApplicationSubmissionStatusUDDIKeyResolutionStatus_isCorrect() throws Exception
	{
		WebServiceProvider mockWebServiceProvider = Mockito.mock(WebServiceProvider.class);
		ReflectionTestUtils.setField(controller, "webServiceProvider", mockWebServiceProvider);

		WebServiceTemplate mockWebServiceTemplate = Mockito.mock(WebServiceTemplate.class);

		String testURI = "www.abc123.com";
		URI uri = new URI(testURI);
		UddiDestinationProvider mockDestinationProvider = Mockito.mock(UddiDestinationProvider.class);

		when(mockDestinationProvider.getDestination()).thenReturn(uri);
		when(mockWebServiceTemplate.getDestinationProvider()).thenReturn(mockDestinationProvider);
		when(mockWebServiceProvider.getWebServiceTemplate(Attribute.APPLICATION_SUBMISSION_KEY))
				.thenReturn(mockWebServiceTemplate);



		WebServiceTemplate mockdefaultWebServiceTemplate = Mockito.mock(WebServiceTemplate.class);

		String testDefaultURI = "www.abc1234.com";
		URI defaultUri = new URI(testDefaultURI);
		DestinationProvider mockDefaultDestinationProvider = Mockito.mock(Wsdl11DestinationProvider.class);

		when(mockDefaultDestinationProvider.getDestination()).thenReturn(defaultUri);
		when(mockdefaultWebServiceTemplate.getDestinationProvider()).thenReturn(mockDefaultDestinationProvider);
		when(mockWebServiceProvider.getWebServiceTemplate("stub"))
				.thenReturn(mockdefaultWebServiceTemplate);

		HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
		StringWriter sw = new StringWriter();
		PrintWriter writer = new PrintWriter(sw);
		when(response.getWriter()).thenReturn(writer);

		controller.showGenericUDDIKeyResolutionStatus(Attribute.APPLICATION_SUBMISSION_KEY, response);
		writer.flush();
		String result = sw.toString();

		assertThat(result, is(equalTo(HeartbeatController.STATUS_OK)));
	}

	/**
	 * Testing /public/heartbeat/uddi/null-test
	 * @throws Exception
	 */
	@Test(expected = NullPointerException.class)
	public void showNullTestUDDIKeyResolution_returnsError() throws Exception
	{
		WebServiceProvider mockWebServiceProvider = Mockito.mock(WebServiceProvider.class);
		ReflectionTestUtils.setField(controller, "webServiceProvider", mockWebServiceProvider);

		WebServiceTemplate mockWebServiceTemplate = Mockito.mock(WebServiceTemplate.class);

		DestinationProvider mockDestinationProvider = Mockito.mock(DestinationProvider.class);

		when(mockDestinationProvider.getDestination()).thenThrow(IOException.class);

		when(mockWebServiceTemplate.getDestinationProvider()).thenReturn(mockDestinationProvider);
		when(mockWebServiceProvider.getWebServiceTemplate("null-test"))
				.thenReturn(mockWebServiceTemplate);

		HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
		StringWriter sw = new StringWriter();
		PrintWriter writer = new PrintWriter(sw);
		when(response.getWriter()).thenReturn(writer);

		controller.showNullUDDIKeyResolutionFailure(response);
	}

	/**
	 * Testing /public/heartbeat/uddi/null-test/status
	 * @throws Exception
	 */
	@Test
	public void showNullTestStatusUDDIKeyResolutionStatus_isCorrect() throws Exception
	{
		WebServiceProvider mockWebServiceProvider = Mockito.mock(WebServiceProvider.class);
		ReflectionTestUtils.setField(controller, "webServiceProvider", mockWebServiceProvider);

		WebServiceTemplate mockWebServiceTemplate = Mockito.mock(WebServiceTemplate.class);

		String testURI = "www.abc123.com";
		URI uri = new URI(testURI);
		UddiDestinationProvider mockDestinationProvider = Mockito.mock(UddiDestinationProvider.class);
		when(mockDestinationProvider.toString()).thenReturn("UddiDestinationProviderMock");
		EndpointResolver resolver = Mockito.mock(UddiBasedEndpointResolver.class);
		controller.setUDDIDestinationProvider(mockDestinationProvider);

		when(mockDestinationProvider.getDestination()).thenThrow(SoapMessageCreationException.class);

		when(mockWebServiceTemplate.getDestinationProvider()).thenReturn(mockDestinationProvider);
		when(mockWebServiceProvider.getWebServiceTemplate(SpringWebServiceTemplateProvider.SERVICE_UDDI))
				.thenReturn(mockWebServiceTemplate);

		HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
		StringWriter sw = new StringWriter();
		PrintWriter writer = new PrintWriter(sw);
		when(response.getWriter()).thenReturn(writer);

		controller.showNullUDDIKeyResolutionFailureStatus(response);
		writer.flush();
		String result = sw.toString();

		assertThat(result, is(equalTo(HeartbeatController.STATUS_ERROR)));
	}

	/**
	 * Testing /public/heartbeat/uddi/gesb-generate-credentials/status
	 * @throws Exception
	 */
	// trying to build in cloud, currently can't connect to this server
	@Ignore
	@Test
	public void showGenericSocketOpeningStatusUp_isCorrect() throws Exception
	{
		WebServiceProvider mockWebServiceProvider = Mockito.mock(WebServiceProvider.class);
		ReflectionTestUtils.setField(controller, "webServiceProvider", mockWebServiceProvider);

		WebServiceTemplate mockWebServiceTemplate = Mockito.mock(WebServiceTemplate.class);

		// See if the Go server is up
		String testURI = "https://dev1.panoramaadviser.srv.westpac.com.au/";
		URI uri = new URI(testURI);
		UddiDestinationProvider mockDestinationProvider = Mockito.mock(UddiDestinationProvider.class);

		when(mockDestinationProvider.getDestination()).thenReturn(uri);
		when(mockWebServiceTemplate.getDestinationProvider()).thenReturn(mockDestinationProvider);
		when(mockWebServiceProvider.getWebServiceTemplate(Attribute.APPLICATION_SUBMISSION_KEY))
				.thenReturn(mockWebServiceTemplate);

		HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
		StringWriter sw = new StringWriter();
		PrintWriter writer = new PrintWriter(sw);
		when(response.getWriter()).thenReturn(writer);

		controller.showGenericSocketOpeningStatus(Attribute.APPLICATION_SUBMISSION_KEY, response);
		writer.flush();
		String result = sw.toString();

		//assertThat(result, is(equalTo(HeartbeatController.STATUS_OK))); //Sometimes unreliable
	}

	/**
	 * Testing /public/heartbeat/uddi/gesb-generate-credentials/status
	 * @throws Exception
	 */
	// trying to build in cloud, currently can't connect to this server
	@Ignore
	@Test
	public void showGenericSocketOpeningStatusDown_isCorrect() throws Exception
	{
		WebServiceProvider mockWebServiceProvider = Mockito.mock(WebServiceProvider.class);
		ReflectionTestUtils.setField(controller, "webServiceProvider", mockWebServiceProvider);

		WebServiceTemplate mockWebServiceTemplate = Mockito.mock(WebServiceTemplate.class);

		// See if the Go server is up
		String testURI = "https://dev1.panoramaadviser.srv.westpac.com.au:444/";
		URI uri = new URI(testURI);
		UddiDestinationProvider mockDestinationProvider = Mockito.mock(UddiDestinationProvider.class);

		when(mockDestinationProvider.getDestination()).thenReturn(uri);
		when(mockWebServiceTemplate.getDestinationProvider()).thenReturn(mockDestinationProvider);
		when(mockWebServiceProvider.getWebServiceTemplate(Attribute.APPLICATION_SUBMISSION_KEY))
				.thenReturn(mockWebServiceTemplate);

		HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
		StringWriter sw = new StringWriter();
		PrintWriter writer = new PrintWriter(sw);
		when(response.getWriter()).thenReturn(writer);

		controller.showGenericSocketOpeningStatus(Attribute.APPLICATION_SUBMISSION_KEY, response);
		writer.flush();
		String result = sw.toString();

		assertThat(result, is(equalTo(HeartbeatController.STATUS_ERROR)));
	}

	public static void setEnvironmentToProd() {
		java.util.Properties properties = Properties.all();
		properties.setProperty("environment","PROD");
	}

	public static void setEnvironmentToDev() {
		java.util.Properties properties = Properties.all();
		properties.setProperty("environment","DEV");
		Assert.assertEquals(properties.getProperty("environment"), "DEV");
	}

	private void mockWebServiceProvider() {
		WebServiceProvider mockWebServiceProvider = Mockito.mock(WebServiceProvider.class);
		ReflectionTestUtils.setField(controller, "webServiceProvider", mockWebServiceProvider);
	}
}

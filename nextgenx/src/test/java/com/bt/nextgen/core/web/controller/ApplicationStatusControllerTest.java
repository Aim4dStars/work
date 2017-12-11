package com.bt.nextgen.core.web.controller;

import com.bt.nextgen.core.IServiceStatus;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.exception.AccessDeniedException;
import com.bt.nextgen.core.repository.RequestKey;
import com.bt.nextgen.core.repository.RequestRegister;
import com.bt.nextgen.core.repository.RequestRegisterRepository;
import com.bt.nextgen.core.util.Properties;
import com.bt.nextgen.core.web.model.ApplicationServiceStatusDto;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.bgp.BackGroundProcessImpl;
import com.bt.nextgen.service.integration.bgp.BackGroundProcess;
import com.bt.nextgen.service.integration.bgp.BackGroundProcessIntegrationService;
import com.btfin.panorama.service.client.status.ServiceStatus;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.bt.nextgen.core.api.UriMappingConstants.NEXTGEN_WEB;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.when;


public class ApplicationStatusControllerTest
{
	private ApplicationStatusController controller = new ApplicationStatusController();

	private static final String STATUS_OK = "OK";

	private static final String STATUS_ERROR = "NOT OK";

	@Before
	public void beforeTest() {
		setEnvironmentToDev();
	}

	@Test
	public void showLoginStatus_returnsString_OK() throws Exception
	{
        RequestRegister mockRequest = Mockito.mock(RequestRegister.class);
        when(mockRequest.getReceivedTime()).thenReturn(new Date());

        RequestRegisterRepository mockRequestRegisterRepository = Mockito.mock(RequestRegisterRepository.class);
        when(mockRequestRegisterRepository.findRequestEntry(Mockito.any(RequestKey.class))).thenReturn(mockRequest);
        ReflectionTestUtils.setField(controller, "requestRegisterRepository", mockRequestRegisterRepository);

		String result = controller.showLoginStatus();

		assertThat(result, is(STATUS_OK));
	}

	@Test
	public void showLoginStatus_returnsString_NOTOK_WhenReceivedTimePriorToJVMStartup() throws Exception
	{
		RequestRegister mockRequest = Mockito.mock(RequestRegister.class);
		when(mockRequest.getReceivedTime()).thenReturn(new Date(new DateTime().minusDays(1).getMillis()));

		RequestRegisterRepository mockRequestRegisterRepository = Mockito.mock(RequestRegisterRepository.class);
		when(mockRequestRegisterRepository.findRequestEntry(Mockito.any(RequestKey.class))).thenReturn(mockRequest);
		ReflectionTestUtils.setField(controller, "requestRegisterRepository", mockRequestRegisterRepository);

		String result = controller.showLoginStatus();

		assertThat(result, is(STATUS_ERROR));
	}

	@Test
	public void showLoginStatus_returnsString_NOT_OK() throws Exception
	{
        RequestRegister mockRequest = Mockito.mock(RequestRegister.class);
        when(mockRequest.getReceivedTime()).thenReturn(null);

        RequestRegisterRepository mockRequestRegisterRepository = Mockito.mock(RequestRegisterRepository.class);
        when(mockRequestRegisterRepository.findRequestEntry(Mockito.any(RequestKey.class))).thenReturn(mockRequest);
        ReflectionTestUtils.setField(controller, "requestRegisterRepository", mockRequestRegisterRepository);

        String result = controller.showLoginStatus();

		assertThat(result, is(STATUS_ERROR));
	}

	@Test(expected = AccessDeniedException.class)
	public void showLoginStatus_FailsInProduction() throws Exception {
		setEnvironmentToProd();

		controller.showLoginStatus();

		setEnvironmentToDev();
	}

	@Test
	public void showDashboardDashboard_returnsString() throws Exception
	{
		setEnvironmentToDev();
        mockServletContext();

		String result = controller.showDashboardDashboard();

		assertThat(result != null, is(true));
		assertThat(result.length() > 0, is(true));
	}

	@Test(expected = AccessDeniedException.class)
	public void showDashboardDashboard_FailsInProduction() throws Exception {
		setEnvironmentToProd();

		controller.showDashboardDashboard();

		setEnvironmentToDev();
	}

    @Test
    public void showPermGenStatus_returnsString() throws Exception
    {
        setEnvironmentToDev();

        String result = controller.showPermGenStatus();

        assertThat(result != null, is(true));
        assertThat(result.length() > 0, is(true));
    }

    @Test(expected = AccessDeniedException.class)
    public void showPermGenStatus_FailsInProduction() throws Exception {
        setEnvironmentToProd();

        controller.showPermGenStatus();

        setEnvironmentToDev();
    }

    @Test
    public void getPermGenReport_returnsString() throws Exception
    {
        setEnvironmentToDev();
        mockServletContext();

        String result = controller.getPermGenReport();

        assertThat(result != null, is(true));
        assertThat(result.length() > 0, is(true));
    }

    @Test(expected = AccessDeniedException.class)
    public void getPermGenReport_FailsInProduction() throws Exception {
        setEnvironmentToProd();

        controller.getPermGenReport();

        setEnvironmentToDev();
    }

	@Test
	public void testShowDashboardOldRedirectDeprecated() throws Exception
	{
		ApplicationStatusController controller = new ApplicationStatusController();
		ModelAndView modelAndView = controller.showDashBoardOldRedirect();
		Assert.assertThat(modelAndView.getViewName(), is("redirect:" + NEXTGEN_WEB + "dashboard/dashboard?reveal"));
	}

	@Test
	public void testShowDashboardNewDirectDeprecated() throws Exception
	{
		ApplicationStatusController controller = new ApplicationStatusController();
		ModelAndView modelAndView = controller.showDashboardNewerRedirect();
		Assert.assertThat(modelAndView.getViewName(), is("redirect:" + NEXTGEN_WEB + "dashboard/dashboard?reveal"));
	}

	@Test
	public void getBGPReport_returnsString() throws Exception
	{
		setEnvironmentToDev();
		mockServletContext();

        List<BackGroundProcess> bgps = new ArrayList<>();
        BackGroundProcess mockBGP = Mockito.mock(BackGroundProcessImpl.class);
        when(mockBGP.getBGPId()).thenReturn("1234");
        when(mockBGP.getBGPInstance()).thenReturn("ABC");
        when(mockBGP.getBGPName()).thenReturn("My BGP");
        when(mockBGP.getCurrentTime()).thenReturn(new DateTime());
        when(mockBGP.getSID()).thenReturn("4321");
        when(mockBGP.isBGPValid()).thenReturn(true);

        BackGroundProcessIntegrationService mockBackGroundProcessIntegrationService = Mockito.mock(BackGroundProcessIntegrationService.class);
        when(mockBackGroundProcessIntegrationService.getCurrentTime(Mockito.any(ServiceErrors.class))).thenReturn(new DateTime());
        when(mockBackGroundProcessIntegrationService.getBackGroundProcesses(Mockito.any(ServiceErrors.class))).thenReturn(bgps);

        ReflectionTestUtils.setField(controller, "backGroundProcessIntegrationService", mockBackGroundProcessIntegrationService);

        String result = controller.getBGPReport();

		assertThat(result != null, is(true));
		assertThat(result.length() > 0, is(true));
	}

    @Test
    public void getReleaseNotes_returnsString() throws Exception
    {
        setEnvironmentToDev();
        mockServletContext();

        String result = controller.getReleaseNotes();

        assertThat(result != null, is(true));
        assertThat(result.length() > 0, is(true));
    }

	@Test
	public void getDetailedApplicationStatus_returnApplicationStatusStarted() {
		IServiceStatus mockServiceStatus = Mockito.mock(IServiceStatus.class);
		ServiceStatus serviceStatus1 = new ServiceStatus("Service1", "Started");
		ServiceStatus serviceStatus2 = new ServiceStatus("Service2", "Started");

		when(mockServiceStatus.getServiceStatus()).thenReturn(Arrays.asList(serviceStatus1,serviceStatus2));

		ReflectionTestUtils.setField(controller, "serviceStatus", mockServiceStatus);

		ApiResponse applicationStatus = controller.getDetailedApplicationStatus();
		ApplicationServiceStatusDto applicationStatusData = (ApplicationServiceStatusDto) applicationStatus.getData();
		assertThat(applicationStatusData.getServiceStatus(), is("Started"));
		assertThat(applicationStatusData.getCacheStatuses().size(), is(2));
	}

	@Test
	public void getDetailedApplicationStatus_returnApplicationStatusStarting() {
		IServiceStatus mockServiceStatus = Mockito.mock(IServiceStatus.class);
		ServiceStatus serviceStatus1 = new ServiceStatus("Service1", "Starting");
		ServiceStatus serviceStatus2 = new ServiceStatus("Service2", "Started");
		ServiceStatus serviceStatus3 = new ServiceStatus("Service3", "Started");

		when(mockServiceStatus.getServiceStatus()).thenReturn(Arrays.asList(serviceStatus1,serviceStatus2, serviceStatus3));

		ReflectionTestUtils.setField(controller, "serviceStatus", mockServiceStatus);

		ApiResponse applicationStatus = controller.getDetailedApplicationStatus();
		ApplicationServiceStatusDto applicationStatusData = (ApplicationServiceStatusDto) applicationStatus.getData();
		assertThat(applicationStatusData.getServiceStatus(), is("Starting"));
		assertThat(applicationStatusData.getCacheStatuses().size(), is(3));
	}

	@Test
	public void getDetailedApplicationStatus_returnApplicationStatusServiceUnavailable() {
		IServiceStatus mockServiceStatus = Mockito.mock(IServiceStatus.class);
		ServiceStatus serviceStatus1 = new ServiceStatus("Service1", "Service Unavailable");
		ServiceStatus serviceStatus2 = new ServiceStatus("Service2", "Started");
		ServiceStatus serviceStatus3 = new ServiceStatus("Service3", "Starting");

		when(mockServiceStatus.getServiceStatus()).thenReturn(Arrays.asList(serviceStatus1,serviceStatus2, serviceStatus3));

		ReflectionTestUtils.setField(controller, "serviceStatus", mockServiceStatus);

		ApiResponse applicationStatus = controller.getDetailedApplicationStatus();
		ApplicationServiceStatusDto applicationStatusData = (ApplicationServiceStatusDto) applicationStatus.getData();
		assertThat(applicationStatusData.getServiceStatus(), is("Service Unavailable"));
		assertThat(applicationStatusData.getCacheStatuses().size(), is(3));
	}

	@Test
	public void getApplicationStatus_returnsServiceUnavailable() {
		IServiceStatus mockServiceStatus = Mockito.mock(IServiceStatus.class);
		ServiceStatus serviceStatus1 = new ServiceStatus("Service1", "Service Unavailable");
		ServiceStatus serviceStatus2 = new ServiceStatus("Service2", "Started");
		ServiceStatus serviceStatus3 = new ServiceStatus("Service3", "Starting");

		when(mockServiceStatus.getServiceStatus()).thenReturn(Arrays.asList(serviceStatus1,serviceStatus2, serviceStatus3));

		ReflectionTestUtils.setField(controller, "serviceStatus", mockServiceStatus);

		assertThat(controller.getApplicationStatus(), is("Service Unavailable"));
	}

	@Test
	public void getApplicationStatus_returnsStarted() {
		IServiceStatus mockServiceStatus = Mockito.mock(IServiceStatus.class);
		ServiceStatus serviceStatus1 = new ServiceStatus("Service1", "Started");
		ServiceStatus serviceStatus2 = new ServiceStatus("Service2", "Started");

		when(mockServiceStatus.getServiceStatus()).thenReturn(Arrays.asList(serviceStatus1,serviceStatus2));

		ReflectionTestUtils.setField(controller, "serviceStatus", mockServiceStatus);

		assertThat(controller.getApplicationStatus(), is("Started"));
	}

	@Test(expected = AccessDeniedException.class)
	public void getDetailedApplicationStatus_throwsExceptionIfEnvironmentIsProd() {
		setEnvironmentToProd();
		IServiceStatus mockServiceStatus = Mockito.mock(IServiceStatus.class);

		ReflectionTestUtils.setField(controller, "serviceStatus", mockServiceStatus);

		controller.getDetailedApplicationStatus();
	}


    private void setEnvironmentToProd() {
		java.util.Properties properties = Properties.all();
		properties.setProperty("environment","PROD");
	}

	private void setEnvironmentToDev() {
		java.util.Properties properties = Properties.all();
		properties.setProperty("environment","DEV");
		Assert.assertEquals(properties.getProperty("environment"), "DEV");
	}

    private void mockServletContext() {
        ServletContext mockServletContext = Mockito.mock(ServletContext.class);
        when(mockServletContext.getContextPath()).thenReturn("/ng");
        ReflectionTestUtils.setField(controller, "context", mockServletContext);
    }
}

package com.bt.nextgen.core.web.controller;

import com.bt.nextgen.core.cache.CacheType;
import com.bt.nextgen.core.exception.AccessDeniedException;
import com.bt.nextgen.core.jms.listener.ChunkListenerContainer;
import com.bt.nextgen.core.jms.listener.FilterDelegationMessageListener;
import com.bt.nextgen.core.repository.RequestKey;
import com.bt.nextgen.core.repository.RequestRegister;
import com.bt.nextgen.core.repository.RequestRegisterRepository;
import com.bt.nextgen.core.util.Properties;
import com.bt.nextgen.service.avaloq.gateway.EventType;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;

import static com.bt.nextgen.core.api.UriMappingConstants.NEXTGEN_MODULE_VERSION;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CacheStatusControllerTest
{
	private CacheStatusController controller = new CacheStatusController();

	@Before
	public void beforeTest() {
		setEnvironmentToDev();
	}

	@Test
	public void showCacheConfigStatus_returnsAString() throws Exception
	{
        setEnvironmentToDev();
        mockRequestRegister();
        mockServletContext();
        ChunkListenerContainer messageListenerContainer = new ChunkListenerContainer(mock(FilterDelegationMessageListener.class));
        controller.setMessageListenerContainer(messageListenerContainer);
        System.getProperties().setProperty("loadAllCachesCalled", "true");

		String result = controller.showCacheConfigStatus();
        assertThat("Not null", result != null);
        assertThat("Has content", result.length() > 0);
	}

    @Test
    public void showCacheConfigStatus_HandlesNullMessageListener() throws Exception
    {
        setEnvironmentToDev();
        mockRequestRegister();
        mockServletContext();
        System.getProperties().setProperty("loadAllCachesCalled", "true");

        String result = controller.showCacheConfigStatus();
        assertThat("Not null", result != null);
        assertThat("Has content", result.length() > 0);
    }

    @Test
    public void showCacheConfigStatus_HandleNullCachesCalled() throws Exception
    {
        setEnvironmentToDev();
        mockRequestRegister();
        mockServletContext();
        System.getProperties().remove("loadAllCachesCalled");

        ChunkListenerContainer messageListenerContainer = new ChunkListenerContainer(mock(FilterDelegationMessageListener.class));
        controller.setMessageListenerContainer(messageListenerContainer);

        String result = controller.showCacheConfigStatus();
        assertThat("Not null", result != null);
        assertThat("Has content", result.length() > 0);
    }

	@Test(expected = AccessDeniedException.class)
	public void showCacheConfigStatus_FailsInProduction() throws Exception {
		setEnvironmentToProd();

        mockRequestRegister();

        controller.showCacheConfigStatus();

		setEnvironmentToDev();
        // Note that the test here is that the controller throws an Access Denied exception
	}

    @Test
    public void showCacheRegisterStatus_returnsAString() throws Exception
    {
        setEnvironmentToDev();
        mockRequestRegister();
        mockServletContext();

        String result = controller.showCacheRegisterStatus();
        assertThat("Not null", result != null);
        assertThat("Has content", result.length() > 0);
    }

    @Test
    public void showCacheRegisterStatus_HasNullRequestRegisterStarting() throws Exception
    {
        setEnvironmentToDev();
        mockRequestRegisterWithNullRequestEntry();
        mockServletContext();

        String result = controller.showCacheRegisterStatus();
        assertThat("Not null", result != null);
        assertThat("Has content", result.length() > 0);
    }

    @Test(expected = AccessDeniedException.class)
    public void showCacheRegisterStatus_FailsInProduction() throws Exception {
        setEnvironmentToProd();

        mockRequestRegister();

        controller.showCacheRegisterStatus();

        setEnvironmentToDev();
        // Note that the test here is that the controller throws an Access Denied exception
    }

    @Test
    public void showCachePropertyStatus_returnsAString() throws Exception
    {
        setEnvironmentToDev();
        mockRequestRegister();
        mockServletContext();

        String result = controller.showCachePropertyStatus();
        assertThat("Not null", result != null);
        assertThat("Has content", result.length() > 0);
    }

    @Test(expected = AccessDeniedException.class)
    public void showCachePropertyStatus_FailsInProduction() throws Exception {
        setEnvironmentToProd();

        mockRequestRegister();

        controller.showCachePropertyStatus();

        setEnvironmentToDev();
        // Note that the test here is that the controller throws an Access Denied exception
    }

    @Test
    public void showCacheRegisterLoadTime_returnsAString() throws Exception
    {
        setEnvironmentToDev();
        mockRequestRegister();

        HttpServletResponse response;
        response = mock(HttpServletResponse.class);
        StringWriter sw = new StringWriter();
        PrintWriter writer = new PrintWriter(sw);
        when(response.getWriter()).thenReturn(writer);

        controller.showCacheRegisterLoadTime(CacheType.AVAILABLE_ASSET_LIST_CACHE.getId(),
                EventType.STARTUP.toString(),response);

        writer.flush();
        String result = sw.toString();

        assertThat("Not null", result != null);
        assertThat("Has content", result.length() > 0);
    }

    @Test
    public void showCacheRegisterLoadTime_HandlesNullResponse() throws Exception
    {
        setEnvironmentToDev();
        mockRequestRegister();

        HttpServletResponse response = null;

        controller.showCacheRegisterLoadTime(CacheType.AVAILABLE_ASSET_LIST_CACHE.getId(),
                EventType.STARTUP.toString(),response);
    }

    @Test
    public void showCacheRegisterLoadTime_HandlesNullRegister() throws Exception
    {
        setEnvironmentToDev();
        mockRequestRegisterWithNullRequestEntry();

        HttpServletResponse response;
        response = mock(HttpServletResponse.class);
        StringWriter sw = new StringWriter();
        PrintWriter writer = new PrintWriter(sw);
        when(response.getWriter()).thenReturn(writer);

        controller.showCacheRegisterLoadTime(CacheType.AVAILABLE_ASSET_LIST_CACHE.getId(),
                EventType.STARTUP.toString(),response);

        writer.flush();
        String result = sw.toString();

        assertThat("Not null", result != null);
        assertThat("Has content", result.length() > 0);
    }

    /**
     * Note that for production we're leaving this one open because of explicit requirements
     * @throws Exception
     */
    @Test
    public void showCacheRegisterLoadTime_ReturnsAStringInProduction() throws Exception {
        setEnvironmentToProd();

        mockRequestRegister();

        HttpServletResponse response;
        response = mock(HttpServletResponse.class);
        StringWriter sw = new StringWriter();
        PrintWriter writer = new PrintWriter(sw);
        when(response.getWriter()).thenReturn(writer);

        controller.showCacheRegisterLoadTime(CacheType.AVAILABLE_ASSET_LIST_CACHE.getId(),
                EventType.STARTUP.toString(),response);

        writer.flush();
        String result = sw.toString();
        assertThat("Not null", result != null);
        assertThat("Has content", result.length() > 0);

        setEnvironmentToDev();
    }

    /**
     * Note that for production we're leaving this one open because of explicit requirements
     * @throws Exception
     */
    @Test
    public void showCacheRegisterLoadTime_HandlesInvalidCacheName() throws Exception {
        setEnvironmentToProd();

        mockRequestRegister();

        HttpServletResponse response;
        response = mock(HttpServletResponse.class);
        StringWriter sw = new StringWriter();
        PrintWriter writer = new PrintWriter(sw);
        when(response.getWriter()).thenReturn(writer);

        controller.showCacheRegisterLoadTime("InvalidCacheName",
                EventType.STARTUP.toString(),response);

        writer.flush();
        String result = sw.toString();
        assertThat("Not null", result != null);
        assertThat("Has content", result.length() > 0);
        assertThat("Has invalid cache name error message", result.contains("Invalid cache name"));

        setEnvironmentToDev();
    }

    @Test
    public void showCacheRegisterLoadTime_HandlesInvalidEventName() throws Exception {
        setEnvironmentToProd();

        mockRequestRegister();

        HttpServletResponse response;
        response = mock(HttpServletResponse.class);
        StringWriter sw = new StringWriter();
        PrintWriter writer = new PrintWriter(sw);
        when(response.getWriter()).thenReturn(writer);

        controller.showCacheRegisterLoadTime(CacheType.AVAILABLE_ASSET_LIST_CACHE.getId(),
                "InvalidEventName",response);

        writer.flush();
        String result = sw.toString();
        assertThat("Not null", result != null);
        assertThat("Has content", result.length() > 0);
        assertThat("Has invalid event name error message", result.contains("Invalid event name"));

        setEnvironmentToDev();
    }

    @Test
    public void showCacheRegisterLoadTime_HandlesNoEventReturned() throws Exception {
        setEnvironmentToProd();

        mockRequestRegister();

        HttpServletResponse response;
        response = mock(HttpServletResponse.class);
        StringWriter sw = new StringWriter();
        PrintWriter writer = new PrintWriter(sw);
        when(response.getWriter()).thenReturn(writer);

        controller.showCacheRegisterLoadTime(CacheType.AVAILABLE_ASSET_LIST_BROKER_CACHE.getId(),
                EventType.STARTUP.toString(),response);

        writer.flush();
        String result = sw.toString();
        assertThat("Not null", result != null);
        assertThat("Has content", result.length() > 0);
        assertThat("Has invalid request entry error message", result.contains("Not started"));

        setEnvironmentToDev();
    }

    @Test
    public void showCacheRegisterLoadTime_ReturnsEntryButNoStartTime() throws Exception {
        setEnvironmentToProd();

        RequestRegister mockRequest = mock(RequestRegister.class);
        when(mockRequest.getSentTime()).thenReturn(null);

        RequestRegisterRepository mockRequestRegisterRepository = mock(RequestRegisterRepository.class);
        when(mockRequestRegisterRepository.findRequestEntry(any(RequestKey.class))).thenReturn(mockRequest);
        ReflectionTestUtils.setField(controller, "requestRegisterRepository", mockRequestRegisterRepository);

        HttpServletResponse response;
        response = mock(HttpServletResponse.class);
        StringWriter sw = new StringWriter();
        PrintWriter writer = new PrintWriter(sw);
        when(response.getWriter()).thenReturn(writer);


        controller.showCacheRegisterLoadTime(CacheType.AVAILABLE_ASSET_LIST_BROKER_CACHE.getId(),
                EventType.STARTUP.toString(),response);

        writer.flush();
        String result = sw.toString();
        assertThat("Not null", result != null);
        assertThat("Has content", result.length() > 0);
        assertThat("Has Not started error message", result.contains("Not started"));

        setEnvironmentToDev();
    }

    @Test
    public void showCacheRegisterLoadTime_ReturnsEntryWithStartTimeButNoFinishTime() throws Exception {
        setEnvironmentToProd();

        RequestRegister mockRequest = mock(RequestRegister.class);
        when(mockRequest.getSentTime()).thenReturn(new Date());
        when(mockRequest.getReceivedTime()).thenReturn(null);

        RequestRegisterRepository mockRequestRegisterRepository = mock(RequestRegisterRepository.class);
        when(mockRequestRegisterRepository.findRequestEntry(any(RequestKey.class))).thenReturn(mockRequest);
        ReflectionTestUtils.setField(controller, "requestRegisterRepository", mockRequestRegisterRepository);

        HttpServletResponse response;
        response = mock(HttpServletResponse.class);
        StringWriter sw = new StringWriter();
        PrintWriter writer = new PrintWriter(sw);
        when(response.getWriter()).thenReturn(writer);


        controller.showCacheRegisterLoadTime(CacheType.AVAILABLE_ASSET_LIST_BROKER_CACHE.getId(),
                EventType.STARTUP.toString(),response);

        writer.flush();
        String result = sw.toString();
        assertThat("Not null", result != null);
        assertThat("Has content", result.length() > 0);
        assertThat("Has time", result.contains("minutes"));

        setEnvironmentToDev();
    }

    @Test
    public void showCacheRegisterLoadTime_ReturnsEntryWithStartTimeFromPreviousJVMInstantiation() throws Exception {
        setEnvironmentToProd();

        RequestRegister mockRequest = mock(RequestRegister.class);
        when(mockRequest.getSentTime()).thenReturn(new Date(new DateTime().minusDays(1).getMillis()));
        when(mockRequest.getReceivedTime()).thenReturn(null);

        RequestRegisterRepository mockRequestRegisterRepository = mock(RequestRegisterRepository.class);
        when(mockRequestRegisterRepository.findRequestEntry(any(RequestKey.class))).thenReturn(mockRequest);
        ReflectionTestUtils.setField(controller, "requestRegisterRepository", mockRequestRegisterRepository);

        HttpServletResponse response;
        response = mock(HttpServletResponse.class);
        StringWriter sw = new StringWriter();
        PrintWriter writer = new PrintWriter(sw);
        when(response.getWriter()).thenReturn(writer);


        controller.showCacheRegisterLoadTime(CacheType.AVAILABLE_ASSET_LIST_CACHE.getId(),
                EventType.STARTUP.toString(),response);

        writer.flush();
        String result = sw.toString();
        assertThat("Not null", result != null);
        assertThat("Has content", result.length() > 0);
        assertThat("Shows not started", result.contains("Not started"));

        setEnvironmentToDev();
    }


    @Test
    public void showCacheRegisterLoadTime_ReturnsEntryWithStartTimeAndFinishTime() throws Exception {
        setEnvironmentToProd();

        RequestRegister mockRequest = mock(RequestRegister.class);
        when(mockRequest.getSentTime()).thenReturn(new Date());
        when(mockRequest.getReceivedTime()).thenReturn(new Date());

        RequestRegisterRepository mockRequestRegisterRepository = mock(RequestRegisterRepository.class);
        when(mockRequestRegisterRepository.findRequestEntry(any(RequestKey.class))).thenReturn(mockRequest);
        ReflectionTestUtils.setField(controller, "requestRegisterRepository", mockRequestRegisterRepository);

        HttpServletResponse response;
        response = mock(HttpServletResponse.class);
        StringWriter sw = new StringWriter();
        PrintWriter writer = new PrintWriter(sw);
        when(response.getWriter()).thenReturn(writer);


        controller.showCacheRegisterLoadTime(CacheType.AVAILABLE_ASSET_LIST_BROKER_CACHE.getId(),
                EventType.STARTUP.toString(),response);

        writer.flush();
        String result = sw.toString();
        assertThat("Not null", result != null);
        assertThat("Has content", result.length() > 0);
        assertThat("Has time", result.contains("minutes"));

        setEnvironmentToDev();
    }

    @Test
    public void showCacheStatus_returnsAString() throws Exception
    {
        setEnvironmentToDev();
        mockRequestRegister();

        HttpServletResponse response;
        response = mock(HttpServletResponse.class);
        StringWriter sw = new StringWriter();
        PrintWriter writer = new PrintWriter(sw);
        when(response.getWriter()).thenReturn(writer);

        controller.showCacheStatus(CacheType.AVAILABLE_ASSET_LIST_CACHE.getId(),
                EventType.STARTUP.toString(),response);

        writer.flush();
        String result = sw.toString();

        assertThat("Not null", result != null);
        assertThat("Has content", result.length() > 0);
    }

    @Test
    public void showCacheStatus_handlesNullRequestRegister() throws Exception
    {
        setEnvironmentToDev();
        mockRequestRegisterWithNullRequestEntry();

        HttpServletResponse response;
        response = mock(HttpServletResponse.class);
        StringWriter sw = new StringWriter();
        PrintWriter writer = new PrintWriter(sw);
        when(response.getWriter()).thenReturn(writer);

        controller.showCacheStatus(CacheType.AVAILABLE_ASSET_LIST_CACHE.getId(),
                EventType.STARTUP.toString(),response);

        writer.flush();
        String result = sw.toString();

        assertThat("Not null", result != null);
        assertThat("Has content", result.length() > 0);
    }

    @Test
    public void showCacheStatus_HandlesNullResponse() throws Exception
    {
        setEnvironmentToDev();
        mockRequestRegister();

        HttpServletResponse response = null;

        controller.showCacheStatus(CacheType.AVAILABLE_ASSET_LIST_CACHE.getId(),
                EventType.STARTUP.toString(),response);
    }

    /**
     * Note that for production we're leaving this one open because of explicit requirements
     * @throws Exception
     */
    @Test
    public void showCacheStatus_ReturnsAStringInProduction() throws Exception {
        setEnvironmentToProd();

        mockRequestRegister();

        HttpServletResponse response;
        response = mock(HttpServletResponse.class);
        StringWriter sw = new StringWriter();
        PrintWriter writer = new PrintWriter(sw);
        when(response.getWriter()).thenReturn(writer);

        controller.showCacheStatus(CacheType.AVAILABLE_ASSET_LIST_CACHE.getId(),
                EventType.STARTUP.toString(),response);

        writer.flush();
        String result = sw.toString();
        assertThat("Not null", result != null);
        assertThat("Has content", result.length() > 0);

        setEnvironmentToDev();
    }

    /**
     * Note that for production we're leaving this one open because of explicit requirements
     * @throws Exception
     */
    @Test
    public void showCacheStatus_HandlesInvalidCacheName() throws Exception {
        setEnvironmentToProd();

        mockRequestRegister();

        HttpServletResponse response;
        response = mock(HttpServletResponse.class);
        StringWriter sw = new StringWriter();
        PrintWriter writer = new PrintWriter(sw);
        when(response.getWriter()).thenReturn(writer);

        controller.showCacheStatus("InvalidCacheName",
                EventType.STARTUP.toString(),response);

        writer.flush();
        String result = sw.toString();
        assertThat("Not null", result != null);
        assertThat("Has content", result.length() > 0);
        assertThat("Has invalid cache name error message", result.contains("Invalid cache name"));

        setEnvironmentToDev();
    }

    @Test
    public void showCacheStatus_HandlesInvalidEventName() throws Exception {
        setEnvironmentToProd();

        mockRequestRegister();

        HttpServletResponse response;
        response = mock(HttpServletResponse.class);
        StringWriter sw = new StringWriter();
        PrintWriter writer = new PrintWriter(sw);
        when(response.getWriter()).thenReturn(writer);

        controller.showCacheStatus(CacheType.AVAILABLE_ASSET_LIST_CACHE.getId(),
                "InvalidEventName",response);

        writer.flush();
        String result = sw.toString();
        assertThat("Not null", result != null);
        assertThat("Has content", result.length() > 0);
        assertThat("Has invalid event name error message", result.contains("Invalid event name"));

        setEnvironmentToDev();
    }

    @Test
    public void showCacheStatus_HandlesNoEventReturned() throws Exception {
        setEnvironmentToProd();

        mockRequestRegister();

        HttpServletResponse response;
        response = mock(HttpServletResponse.class);
        StringWriter sw = new StringWriter();
        PrintWriter writer = new PrintWriter(sw);
        when(response.getWriter()).thenReturn(writer);

        controller.showCacheStatus(CacheType.AVAILABLE_ASSET_LIST_BROKER_CACHE.getId(),
                EventType.STARTUP.toString(),response);

        writer.flush();
        String result = sw.toString();
        assertThat("Not null", result != null);
        assertThat("Has content", result.length() > 0);
        System.out.println("result: " + result);
        assertThat("Has invalid request entry error message", result.contains(CacheStatusController.STATUS_NOT_STARTED));

        setEnvironmentToDev();
    }

    @Test
    public void showCacheStatus_ReturnsEntryButNoStartTime() throws Exception {
        setEnvironmentToProd();

        RequestRegister mockRequest = mock(RequestRegister.class);
        when(mockRequest.getSentTime()).thenReturn(null);

        RequestRegisterRepository mockRequestRegisterRepository = mock(RequestRegisterRepository.class);
        when(mockRequestRegisterRepository.findRequestEntry(any(RequestKey.class))).thenReturn(mockRequest);
        ReflectionTestUtils.setField(controller, "requestRegisterRepository", mockRequestRegisterRepository);

        HttpServletResponse response;
        response = mock(HttpServletResponse.class);
        StringWriter sw = new StringWriter();
        PrintWriter writer = new PrintWriter(sw);
        when(response.getWriter()).thenReturn(writer);


        controller.showCacheStatus(CacheType.AVAILABLE_ASSET_LIST_BROKER_CACHE.getId(),
                EventType.STARTUP.toString(),response);

        writer.flush();
        String result = sw.toString();
        assertThat("Not null", result != null);
        assertThat("Has content", result.length() > 0);
        assertThat("Has Not started error message", result.contains(CacheStatusController.STATUS_NOT_STARTED));

        setEnvironmentToDev();
    }

    @Test
    public void showCacheStatus_ReturnsEntryWithStartTimeButNoFinishTimeAndUnderExpectedTime() throws Exception {
        setEnvironmentToProd();

        RequestRegister mockRequest = mock(RequestRegister.class);
        when(mockRequest.getSentTime()).thenReturn(new Date());
        when(mockRequest.getReceivedTime()).thenReturn(null);

        RequestRegisterRepository mockRequestRegisterRepository = mock(RequestRegisterRepository.class);
        when(mockRequestRegisterRepository.findRequestEntry(any(RequestKey.class))).thenReturn(mockRequest);
        ReflectionTestUtils.setField(controller, "requestRegisterRepository", mockRequestRegisterRepository);

        String cacheName = CacheType.AVAILABLE_ASSET_LIST_BROKER_CACHE.getId();
        Properties.all().put("cache.loadtime." + cacheName,"5");

        HttpServletResponse response;
        response = mock(HttpServletResponse.class);
        StringWriter sw = new StringWriter();
        PrintWriter writer = new PrintWriter(sw);
        when(response.getWriter()).thenReturn(writer);


        controller.showCacheStatus(cacheName,
                EventType.STARTUP.toString(),response);

        writer.flush();
        String result = sw.toString();
        assertThat("Not null", result != null);
        assertThat("Has content", result.length() > 0);
        assertThat("Has time", result.contains(CacheStatusController.STATUS_LOADING));

        setEnvironmentToDev();
    }

    @Test
    public void showCacheStatus_ReturnsEntryWithStartTimeFromPreviousJVMInstantiation() throws Exception {
        setEnvironmentToProd();

        RequestRegister mockRequest = mock(RequestRegister.class);
        when(mockRequest.getSentTime()).thenReturn(new Date(new DateTime().minusDays(1).getMillis()));
        when(mockRequest.getReceivedTime()).thenReturn(null);

        RequestRegisterRepository mockRequestRegisterRepository = mock(RequestRegisterRepository.class);
        when(mockRequestRegisterRepository.findRequestEntry(any(RequestKey.class))).thenReturn(mockRequest);
        ReflectionTestUtils.setField(controller, "requestRegisterRepository", mockRequestRegisterRepository);

        String cacheName = CacheType.AVAILABLE_ASSET_LIST_CACHE.getId();
        Properties.all().put("cache.loadtime." + cacheName,"5");

        HttpServletResponse response;
        response = mock(HttpServletResponse.class);
        StringWriter sw = new StringWriter();
        PrintWriter writer = new PrintWriter(sw);
        when(response.getWriter()).thenReturn(writer);

        controller.showCacheStatus(cacheName,
                EventType.STARTUP.toString(),response);

        writer.flush();
        String result = sw.toString();
        assertThat("Not null", result != null);
        assertThat("Has content", result.length() > 0);
        assertThat("Not started", result.contains(CacheStatusController.STATUS_NOT_STARTED));

        setEnvironmentToDev();
    }


    @Test
    public void showCacheStatus_ReturnsEntryWithStartTimeButNoFinishTimeAndOverExpectedTime() throws Exception {
        setEnvironmentToProd();

        RequestRegister mockRequest = mock(RequestRegister.class);
        when(mockRequest.getSentTime()).thenReturn(new Date());
        when(mockRequest.getReceivedTime()).thenReturn(null);

        RequestRegisterRepository mockRequestRegisterRepository = mock(RequestRegisterRepository.class);
        when(mockRequestRegisterRepository.findRequestEntry(any(RequestKey.class))).thenReturn(mockRequest);
        ReflectionTestUtils.setField(controller, "requestRegisterRepository", mockRequestRegisterRepository);

        String cacheName = CacheType.AVAILABLE_ASSET_LIST_BROKER_CACHE.getId();
        Properties.all().put("cache.loadtime." + cacheName,"0");

        HttpServletResponse response;
        response = mock(HttpServletResponse.class);
        StringWriter sw = new StringWriter();
        PrintWriter writer = new PrintWriter(sw);
        when(response.getWriter()).thenReturn(writer);


        controller.showCacheStatus(cacheName,
                EventType.STARTUP.toString(),response);

        writer.flush();
        String result = sw.toString();
        assertThat("Not null", result != null);
        assertThat("Has content", result.length() > 0);
        assertThat("Has time", result.contains(CacheStatusController.STATUS_TIMEOUT_ERROR));

        setEnvironmentToDev();
    }

    @Test
    public void showCacheStatus_ReturnsEntryWithStartTimeAndFinishTime() throws Exception {
        setEnvironmentToProd();

        RequestRegister mockRequest = mock(RequestRegister.class);
        when(mockRequest.getSentTime()).thenReturn(new Date());
        when(mockRequest.getReceivedTime()).thenReturn(new Date());

        RequestRegisterRepository mockRequestRegisterRepository = mock(RequestRegisterRepository.class);
        when(mockRequestRegisterRepository.findRequestEntry(any(RequestKey.class))).thenReturn(mockRequest);
        ReflectionTestUtils.setField(controller, "requestRegisterRepository", mockRequestRegisterRepository);

        HttpServletResponse response;
        response = mock(HttpServletResponse.class);
        StringWriter sw = new StringWriter();
        PrintWriter writer = new PrintWriter(sw);
        when(response.getWriter()).thenReturn(writer);


        controller.showCacheStatus(CacheType.AVAILABLE_ASSET_LIST_BROKER_CACHE.getId(),
                EventType.STARTUP.toString(),response);

        writer.flush();
        String result = sw.toString();
        assertThat("Not null", result != null);
        assertThat("Has content", result.length() > 0);
        assertThat("Has time", result.contains(CacheStatusController.STATUS_OK));

        setEnvironmentToDev();
    }

    @Test
    public void showCacheStatusRedirect_returnsAString() throws Exception
    {
        setEnvironmentToDev();
        mockRequestRegister();

        ModelAndView modelAndView = controller.showCacheStatusRedirect();
        assertThat(modelAndView.getViewName(), is("redirect:"+NEXTGEN_MODULE_VERSION + "/cachestatus/request"));
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

	private void mockRequestRegister() {
        RequestRegisterRepository mockRequestRegisterRepository = mock(RequestRegisterRepository.class);
        when(mockRequestRegisterRepository.findRequestEntry(any(RequestKey.class))).thenReturn(new RequestRegister());
		ReflectionTestUtils.setField(controller, "requestRegisterRepository", mockRequestRegisterRepository);
	}

    private void mockRequestRegisterWithNullRequestEntry() {
        RequestRegisterRepository mockRequestRegisterRepository = mock(RequestRegisterRepository.class);
        when(mockRequestRegisterRepository.findRequestEntry(any(RequestKey.class))).thenReturn(null);
        ReflectionTestUtils.setField(controller, "requestRegisterRepository", mockRequestRegisterRepository);
    }

	private void mockServletContext() {
        ServletContext mockServletContext = mock(ServletContext.class);
        when(mockServletContext.getContextPath()).thenReturn("/ng");
        ReflectionTestUtils.setField(controller, "context", mockServletContext);
    }
}

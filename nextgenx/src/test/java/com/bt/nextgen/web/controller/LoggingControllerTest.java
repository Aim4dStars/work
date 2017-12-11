package com.bt.nextgen.web.controller;

import ch.qos.logback.classic.Level;
import com.bt.nextgen.core.web.model.AjaxResponse;
import com.bt.nextgen.service.avaloq.userinformation.JobRole;
import com.bt.nextgen.web.model.ClientLogInformation;
import com.bt.nextgen.web.model.ClientLogger;
import com.btfin.panorama.core.security.profile.UserProfile;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.hamcrest.core.Is;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.concurrent.ExecutorService;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class LoggingControllerTest {
    @InjectMocks
    private LoggingController loggingController;

    @Mock
    private UserProfileService profileService;

    @Mock
    private UserProfile userProfile;

    @Mock
    private ClientLogger clientLogger;

    private ExecutorService executor;

    private Logger logger;
    private AjaxResponse response;
    private ClientLogInformation clientLogInformation;
    private MockHttpServletRequest request;
    private MockHttpSession mockHttpSession;

    @Before
    public void setup() {
        request = new MockHttpServletRequest();
        mockHttpSession = (MockHttpSession) request.getSession();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        when(profileService.getActiveProfile()).thenReturn(userProfile);
        when(userProfile.getJobRole()).thenReturn(JobRole.ADVISER);
        when(profileService.getUsername()).thenReturn("adviser");
        when(profileService.getFirstName()).thenReturn("Nick");
        when(profileService.getUsername()).thenReturn("getLastName");
    }

    @Test
    public void testLogMessage() throws JsonProcessingException {
        logger = mock(Logger.class);
        clientLogInformation = new ClientLogInformation();
        response = loggingController.logMessage(clientLogInformation);
        executor = mock(ExecutorService.class);
        doAnswer(new Answer<Object>() {
            public Object answer(InvocationOnMock invocation) throws Exception {
                ((Runnable) invocation.getArguments()[0]).run();
                return null;
            }
        }).when(executor).submit(any(Runnable.class));
        verify(clientLogger, times(1)).logClientInfo(any(ClientLogInformation.class));
        assertEquals(response.isSuccess(), true);
        assertEquals(clientLogInformation.getOriginatingSystem(), "Panorama");
    }

    @Test
    public void testLogMessage_ForOtherOriginatingSystem() throws JsonProcessingException {
        mockHttpSession.setAttribute("originatingSystem", "WLIVE");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        logger = mock(Logger.class);
        clientLogInformation = new ClientLogInformation();
        response = loggingController.logMessage(clientLogInformation);
        executor = mock(ExecutorService.class);
        doAnswer(new Answer<Object>() {
            public Object answer(InvocationOnMock invocation) throws Exception {
                ((Runnable) invocation.getArguments()[0]).run();
                return null;
            }
        }).when(executor).submit(any(Runnable.class));
        //verify(clientLogger, times(1)).logClientInfo(any(ClientLogInformation.class));
        assertEquals(response.isSuccess(), true);
        assertEquals(clientLogInformation.getOriginatingSystem(), "WLIVE");
    }


    @Test
    public void testHandleException() {
        logger = mock(Logger.class);
        clientLogInformation = new ClientLogInformation();
        ReflectionTestUtils.setField(loggingController, "logger", logger);
        response = loggingController.handleException(new Exception("unparsable input"));
        verify(logger).error(anyString(), anyString());
        assertEquals(response.isSuccess(), false);
    }

    @Test
    public void testPrintLogSettings() {
        ch.qos.logback.classic.Logger logger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(getClass());
        logger.setLevel(Level.INFO);

        AjaxResponse result = loggingController.querySettings(getClass().getCanonicalName());
        LoggingController.SettingsResponse response = (LoggingController.SettingsResponse) result.getData();

        assertThat("I just set this!", result.isSuccess(), Is.is(true));
        assertThat("I just set this!", response.getName(), Is.is(getClass().getCanonicalName()));
        assertThat("I just set this!", response.getLevel(), Is.is("INFO"));

        logger.setLevel(Level.DEBUG);
        result = loggingController.querySettings(getClass().getCanonicalName());
        response = (LoggingController.SettingsResponse) result.getData();
        assertThat("I just set this!", result.isSuccess(), Is.is(true));
        assertThat("I just set this!", response.getLevel(), Is.is("DEBUG"));

    }


    @Test
    public void testChangeLogSettingsForProduction() {
        loggingController.updateSettings(getClass().getCanonicalName(), "WARN");
        AjaxResponse result = loggingController.querySettings(getClass().getCanonicalName());
        LoggingController.SettingsResponse response = (LoggingController.SettingsResponse) result.getData();
        assertThat("I just set this!", result.isSuccess(), Is.is(true));
        assertThat("I just set this!", response.getLevel(), Is.is("WARN"));
    }

    @Test
    public void testChangeLogSettings() {
        ch.qos.logback.classic.Logger logger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(getClass());
        logger.setLevel(Level.INFO);
        loggingController.updateSettings(getClass().getCanonicalName(), "WARN");
        AjaxResponse result = loggingController.querySettings(getClass().getCanonicalName());
        LoggingController.SettingsResponse response = (LoggingController.SettingsResponse) result.getData();
        assertThat("I just set this!", result.isSuccess(), Is.is(true));
        assertThat("I just set this!", response.getLevel(), Is.is("WARN"));
    }

    @Test
    public void testChangeLogSettingsDefaultDebug() {
        ch.qos.logback.classic.Logger logger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(getClass());
        logger.setLevel(Level.INFO);
        loggingController.updateSettings(getClass().getCanonicalName(), "INVALID");
        AjaxResponse result = loggingController.querySettings(getClass().getCanonicalName());
        LoggingController.SettingsResponse response = (LoggingController.SettingsResponse) result.getData();
        assertThat("I just set this!", result.isSuccess(), Is.is(true));
        assertThat("I just set this!", response.getLevel(), Is.is("DEBUG"));
    }

}

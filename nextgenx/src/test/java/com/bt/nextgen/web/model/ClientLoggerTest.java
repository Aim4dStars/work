package com.bt.nextgen.web.model;

import com.btfin.panorama.core.security.profile.UserProfile;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.broker.BrokerHelperService;
import com.bt.nextgen.service.avaloq.userinformation.JobRole;
import com.btfin.panorama.service.integration.broker.Broker;
import com.btfin.panorama.core.security.integration.userprofile.JobProfile;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.bt.nextgen.service.avaloq.userinformation.JobRole.ADVISER;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.slf4j.MarkerFactory.getMarker;

@RunWith(MockitoJUnitRunner.class)
public class ClientLoggerTest {

    @InjectMocks
    private ClientLogger clientLogger;

    @Mock
    UserProfileService profileService;

    @Mock
    BrokerHelperService brokerHelperService;

    private Logger logger;
    private ClientLogInformation clientLogInformation;
    private UserProfile profile;
    private Broker dealerGroup;

    @Before
    public void setup() {
        logger = mock(Logger.class);
        profile = mock(UserProfile.class);
        when(profile.getJobRole()).thenReturn(JobRole.INVESTOR);
        when(profileService.getActiveProfile()).thenReturn(profile);

        dealerGroup = mock(Broker.class);
        when(dealerGroup.getPositionName()).thenReturn("Dealer group name");

        when(brokerHelperService.getDealerGroupsforInvestor(any(ServiceErrors.class))).thenReturn(Collections.singleton(dealerGroup));
        when(brokerHelperService.getDealerGroupForIntermediary(any(JobProfile.class), any(ServiceErrors.class))).thenReturn(dealerGroup);
    }

    @Test
    public void testLogClientInfo_Investor() throws JsonProcessingException {
        clientLogInformation = getClientLogInformation(true, false);
        ReflectionTestUtils.setField(clientLogger, "logger", logger);
        clientLogger.logClientInfo(clientLogInformation);

        verify(logger, times(2)).info(eq(getMarker("CLIENT_PERF")), anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString());
        verify(logger, times(0)).info(eq(getMarker("CLIENT_LOG")), anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString());
    }

    @Test
    public void testLogClientInfo_Intermediary() throws JsonProcessingException {
        when(profile.getJobRole()).thenReturn(ADVISER);

        clientLogInformation = getClientLogInformation(true, false);
        ReflectionTestUtils.setField(clientLogger, "logger", logger);
        clientLogger.logClientInfo(clientLogInformation);

        verify(logger, times(2)).info(eq(getMarker("CLIENT_PERF")), anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString());
        verify(logger, times(0)).info(eq(getMarker("CLIENT_LOG")), anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString());
    }

    @Test
    public void testLogClientInfo_IntermediaryNoDG() throws JsonProcessingException {
        when(profile.getJobRole()).thenReturn(ADVISER);
        when(brokerHelperService.getDealerGroupForIntermediary(any(JobProfile.class), any(ServiceErrors.class))).thenReturn(null);

        clientLogInformation = getClientLogInformation(true, false);
        ReflectionTestUtils.setField(clientLogger, "logger", logger);
        clientLogger.logClientInfo(clientLogInformation);

        verify(logger, times(2)).info(eq(getMarker("CLIENT_PERF")), anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString());
        verify(logger, times(0)).info(eq(getMarker("CLIENT_LOG")), anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString());
    }

    @Test
    public void testLogClientInfoErrorLogsOnly() throws JsonProcessingException {
        clientLogInformation = getClientLogInformation(false, true);
        ReflectionTestUtils.setField(clientLogger, "logger", logger);
        clientLogger.logClientInfo(clientLogInformation);
        verify(logger, times(0)).info(eq(getMarker("CLIENT_PERF")), anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString());
        verify(logger, times(2)).info(eq(getMarker("CLIENT_LOG")), anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString());
    }

    @Test
    public void testLogClientInfoAll() throws JsonProcessingException {
        clientLogInformation = getClientLogInformation(true, true);
        ReflectionTestUtils.setField(clientLogger, "logger", logger);

        clientLogger.logClientInfo(clientLogInformation);
        verify(logger, times(2)).info(eq(getMarker("CLIENT_PERF")), anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString());
        verify(logger, times(2)).info(eq(getMarker("CLIENT_LOG")), anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString());
    }

    private ClientLogInformation getClientLogInformation(boolean setPerformanceLogs, boolean setErrorLogs) {

        List<PerformanceLog> perfLogs = setPerformanceLogs ? getPerfLogs() : new ArrayList<PerformanceLog>();
        List<ErrorLog> errorLogs = setErrorLogs ? getErrorLogs() : new ArrayList<ErrorLog>();

        ClientLogInformation info = mock(ClientLogInformation.class);
        when(info.getClientType()).thenReturn("WEB");
        when(info.getClientVersion()).thenReturn("version1");
        when(info.getPerformanceLogs()).thenReturn(perfLogs);
        when(info.getErrorLogs()).thenReturn(errorLogs);
        return info;
    }

    private List<PerformanceLog> getPerfLogs() {
        List<PerformanceLog> performanceLogs = new ArrayList<>();
        performanceLogs.add(new PerformanceLog());
        performanceLogs.add(new PerformanceLog());
        return performanceLogs;
    }

    private List<ErrorLog> getErrorLogs() {
        List<ErrorLog> errorLogs = new ArrayList<>();
        errorLogs.add(new ErrorLog());
        errorLogs.add(new ErrorLog());
        return errorLogs;
    }
}

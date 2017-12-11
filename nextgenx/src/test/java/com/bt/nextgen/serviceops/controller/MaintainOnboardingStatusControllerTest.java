package com.bt.nextgen.serviceops.controller;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.ModelAndView;

import com.bt.nextgen.api.client.service.MaintainOnboardingStatusService;
import com.bt.nextgen.core.exception.AccessDeniedException;
import com.bt.nextgen.core.repository.OnBoardingApplication;
import com.bt.nextgen.core.web.util.View;
import com.bt.nextgen.serviceops.model.MaintainOnboardingStatusModel;
import com.bt.nextgen.serviceops.service.ServiceOpsService;

@RunWith(MockitoJUnitRunner.class)
public class MaintainOnboardingStatusControllerTest {
    @Mock
    private ServiceOpsService serviceOpsService;

    @Mock
    private MaintainOnboardingStatusService maintainOnboardingStatusService;

    MockHttpServletRequest request;

    MockHttpServletResponse response;
    
    @InjectMocks
    private MaintainOnboardingStatusController maintainOnboardingStatusController;
    @Test(expected = AccessDeniedException.class)
    public void testGetOnBoardingStatusNoSupportRole() {
        
        Mockito.when(serviceOpsService.isServiceOpsITSupportRole()).thenReturn(
                false);
        maintainOnboardingStatusController.getOnBoardingStatus(null);
    }
    @Test
    public void testGetOnBoardingStatus() {
        Mockito.when(serviceOpsService.isServiceOpsRestricted()).thenReturn(
                false);
        Mockito.when(serviceOpsService.isServiceOpsITSupportRole()).thenReturn(
                true);
        ModelAndView modelAndView = maintainOnboardingStatusController.getOnBoardingStatus(null);
        assertThat(modelAndView.getViewName(), is(View.ONBOARDING_STATUS));
    }
    
    @Test
    public void testGetOnBoardingStatusResponse() {
        Mockito.when(serviceOpsService.isServiceOpsRestricted()).thenReturn(
                false);
        Mockito.when(serviceOpsService.isServiceOpsITSupportRole()).thenReturn(
                true);
        MaintainOnboardingStatusModel maintainOnboardingStatusModel = mock(MaintainOnboardingStatusModel.class);
        when(maintainOnboardingStatusService.find(Long.valueOf(12)))
        .thenReturn(maintainOnboardingStatusModel);
        ModelAndView modelAndView = maintainOnboardingStatusController.getOnBoardingStatus("12");
        assertThat(modelAndView.getViewName(), is(View.ONBOARDING_STATUS_RESPONSE));
    }
    @Test
    public void testChangeStatus() {
        request = new MockHttpServletRequest();
        request.setParameter("appId", "12");
        request.setParameter("status", "active");
        Mockito.when(serviceOpsService.isServiceOpsRestricted()).thenReturn(
                false);
        Mockito.when(serviceOpsService.isServiceOpsITSupportRole()).thenReturn(
                true);
        MaintainOnboardingStatusModel maintainOnboardingStatusModel = new MaintainOnboardingStatusModel();
                
        OnBoardingApplication onBoardingApplication = new OnBoardingApplication();
        maintainOnboardingStatusModel.setOnBoardingApplication(onBoardingApplication);
        when(maintainOnboardingStatusService.find(Long.valueOf(12)))
        .thenReturn(maintainOnboardingStatusModel);
        when(maintainOnboardingStatusService.update(maintainOnboardingStatusModel , Long.valueOf(12)))
        .thenReturn(maintainOnboardingStatusModel);
        ModelAndView modelAndView = maintainOnboardingStatusController.changeStatus(request);
        assertThat(modelAndView.getViewName(), is(View.ONBOARDING_STATUS_RESPONSE));
    }
    @Test(expected = AccessDeniedException.class)
    public void testChangeStatusNoSupportRole() {
        request = new MockHttpServletRequest();
        request.setParameter("appId", "12");
        request.setParameter("status", "active");
        Mockito.when(serviceOpsService.isServiceOpsRestricted()).thenReturn(
                false);
        Mockito.when(serviceOpsService.isServiceOpsITSupportRole()).thenReturn(
                false);
        MaintainOnboardingStatusModel maintainOnboardingStatusModel = new MaintainOnboardingStatusModel();
                
        OnBoardingApplication onBoardingApplication = new OnBoardingApplication();
        maintainOnboardingStatusModel.setOnBoardingApplication(onBoardingApplication);
        when(maintainOnboardingStatusService.find(Long.valueOf(12)))
        .thenReturn(maintainOnboardingStatusModel);
        when(maintainOnboardingStatusService.update(maintainOnboardingStatusModel , Long.valueOf(12)))
        .thenReturn(maintainOnboardingStatusModel);
        maintainOnboardingStatusController.changeStatus(request);
    }
}

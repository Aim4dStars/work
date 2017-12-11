package com.bt.nextgen.core.web.controller;

import com.bt.nextgen.api.profile.v1.model.ProfileDetailsDto;
import com.bt.nextgen.api.profile.v1.service.ProfileDetailsDtoService;
import com.bt.nextgen.core.util.Properties;
import com.bt.nextgen.core.web.util.View;
import com.bt.nextgen.service.ServiceErrors;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test class for {@link SpaController}
 * <p>
 * Created by M044020 on 27/02/2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class SpaControllerTest {
    @InjectMocks
    private SpaController spaController;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private ProfileDetailsDtoService profileService;

    private ProfileDetailsDto profile;

    private String webClientLocationProperty = "webclient.resource.location";
    private String originalWebClientLocation;
    private java.util.Properties properties;

    @Before
    public void setup() {
        profile = mock(ProfileDetailsDto.class);
        when(profileService.findOne(any(ServiceErrors.class))).thenReturn(profile);

        properties = Properties.all();
        originalWebClientLocation = (String) properties.get(webClientLocationProperty);
        properties.setProperty(webClientLocationProperty, "/");
    }

    @After
    public void reset(){
        properties.setProperty(webClientLocationProperty, originalWebClientLocation);
    }

    @Test
    public void showPageWithoutKernel() throws Exception {
        when(profile.isIntermediary()).thenReturn(false);
        when(profile.getHasAsimAccounts()).thenReturn(false);

        String page = spaController.showPage(request, response);
        assertThat(page, is(View.SINGLE_PAGE_APPLICATION));
        verify(request, times(1)).setAttribute("apploc", "/index-nw-standalone.html");
    }

    @Test
    public void showPageWithKernelAdviser() throws Exception {
        when(profile.isIntermediary()).thenReturn(true);

        String page = spaController.showPage(request, response);
        assertThat(page, is(View.SINGLE_PAGE_APPLICATION));
        verify(request, times(1)).setAttribute("apploc", "/index.html");
    }

    @Test
    public void showPageWithKernel_ASIMInvestor() throws Exception {
        when(profile.getHasAsimAccounts()).thenReturn(true);

        String page = spaController.showPage(request, response);
        assertThat(page, is(View.SINGLE_PAGE_APPLICATION));
        verify(request, times(1)).setAttribute("apploc", "/index.html");
    }

    @Test
    public void showPageWithKernel_ParameterInRequest() throws Exception {
        when(request.getParameter(anyString())).thenReturn("true");

        String page = spaController.showPage(request, response);
        assertThat(page, is(View.SINGLE_PAGE_APPLICATION));
        verify(request, times(1)).setAttribute("apploc", "/index.html");
    }

    @Test
    public void getItRight() throws Exception {
        String page = spaController.getItRight();
        assertThat(page, is("redirect:/secure/app/"));
    }

    @Test
    public void app() throws Exception {
        String page = spaController.app();
        assertThat(page, is("redirect:/secure/app/"));
    }

    @Test
    public void investorSite() throws Exception {
        String page = spaController.investorSite();
        assertThat(page, is("redirect:/public/site/investorpre/index.html"));
    }
}
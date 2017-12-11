package com.bt.nextgen.web.controller;

import com.bt.nextgen.api.userpreference.model.UserPreferenceEnum;
import com.bt.nextgen.core.exception.AvaloqConnectionException;
import com.bt.nextgen.core.repository.UserPreference;
import com.bt.nextgen.core.repository.UserPreferenceKey;
import com.bt.nextgen.core.repository.UserPreferenceRepository;
import com.btfin.panorama.core.security.saml.SamlToken;
import com.bt.nextgen.core.security.encryption.DecryptionServiceImpl;
import com.btfin.panorama.core.security.profile.UserProfile;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.core.toggle.FeatureToggles;
import com.bt.nextgen.core.toggle.FeatureTogglesService;
import com.bt.nextgen.core.web.RequestQuery;
import com.bt.nextgen.core.web.controller.SpaController;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.userinformation.JobRole;
import com.bt.nextgen.util.SamlUtil;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import org.hamcrest.core.Is;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;

import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class HomePageControllerTest {

    private static final String GCM_ID = "123456";

    @InjectMocks
    private HomePageController homePageController = new HomePageController();

    @Mock
    private UserProfileService profileService;

    @Mock
    private RequestQuery requestQuery;

    @Mock
    private UserPreferenceRepository userPreferenceRepository;

    @Mock
    private AnnotationMethodHandlerAdapter annotationMethodHandler;

    @Mock
    private FeatureTogglesService togglesService;

    @Mock
    private FeatureToggles featureToggles;

    @Spy
    private DecryptionServiceImpl decryptionService;

    private SamlToken samlToken;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private MockHttpSession session;

    private UserProfile userProfile;

    @Before
    public void setup() throws Exception {
        request = new MockHttpServletRequest();
        request.setCookies();
        response = new MockHttpServletResponse();
        session = new MockHttpSession();
        samlToken = new SamlToken(SamlUtil.loadWplSaml());
        userProfile = mock(UserProfile.class, RETURNS_DEEP_STUBS);
        when(profileService.getActiveProfile()).thenReturn(userProfile);
        when(userProfile.getJobRole()).thenReturn(JobRole.ADVISER);
        when(profileService.getGcmId()).thenReturn(GCM_ID);
        annotationMethodHandler = new AnnotationMethodHandlerAdapter();
        HttpMessageConverter[] messageConverters = {new MappingJackson2HttpMessageConverter()};
        annotationMethodHandler.setMessageConverters(messageConverters);
        when(requestQuery.getOriginalHost()).thenReturn("host");
    }

    @Test
    public void test_HomePage_Url() throws Exception {
        UserProfile userProfile = mock(UserProfile.class);
        when(profileService.isServiceOperator()).thenReturn(true);
        when(profileService.getActiveProfile()).thenReturn(userProfile);
        when(userProfile.getJobRole()).thenReturn(JobRole.ASSISTANT);
        request.setRequestURI("/home");
        request.setMethod("GET");
        annotationMethodHandler.handle(request, response, homePageController);
        assertThat(response.getStatus(), Is.is(HttpServletResponse.SC_OK));
    }

    @Test(expected = HttpRequestMethodNotSupportedException.class)
    public void test_HomePage__IncorrectMethod() throws Exception {
        request.setRequestURI("/secure/page/home");
        request.setMethod("POST");
        annotationMethodHandler.handle(request, response, homePageController);
    }

    @Test
    public void testHomePageWithBadRequestHeader() throws Exception {
        Cookie cookie = new Cookie("process_timer_LOGON", URLEncoder.encode("invalid\n", "UTF-8"));
        request.setCookies(cookie);
        homePageController.root(request, new MockHttpServletResponse());
    }

    @Test
    public void testHomePage_serviceOps() throws Exception {
        UserProfile userProfile = mock(UserProfile.class);
        when(profileService.isServiceOperator()).thenReturn(true);
        when(profileService.getActiveProfile()).thenReturn(userProfile);
        when(userProfile.getJobRole()).thenReturn(JobRole.ASSISTANT);
        String response = homePageController.root(request, new MockHttpServletResponse());
        assertThat(response, notNullValue());
        assertThat(response, Is.is(HomePageController.REDIRECT_SERVICEOP_HOMEPAGE));
    }

    @Test
    public void testHomePage_serviceOpsWithException() throws Exception {
        UserProfile userProfile = mock(UserProfile.class);
        when(profileService.isServiceOperator()).thenThrow(new IllegalStateException("Testing exception."));
        when(profileService.getActiveProfile()).thenReturn(userProfile);
        when(userProfile.getJobRole()).thenReturn(JobRole.ADVISER);
        String response = homePageController.root(request, new MockHttpServletResponse());
        assertThat(response, notNullValue());
        assertThat(response, Is.is(SpaController.REDIRECT_APP_HOME));
    }

    @Test
    public void testHomePage_redirectAdmin() throws Exception {
        UserProfile userProfile = mock(UserProfile.class);
        when(profileService.isAdmin()).thenReturn(true);
        when(profileService.isServiceOperator()).thenReturn(false);
        when(profileService.getActiveProfile()).thenReturn(userProfile);
        when(userProfile.getJobRole()).thenReturn(JobRole.ADVISER);
        String response = homePageController.root(request, new MockHttpServletResponse());
        assertThat(response, notNullValue());
        assertThat(response, Is.is(HomePageController.REDIRECT_ADMIN_HOME));
    }

    @Test
    public void testHomePage_nullUserProfile() throws Exception {
        when(profileService.isAdmin()).thenReturn(false);
        when(profileService.isServiceOperator()).thenReturn(false);
        when(profileService.getActiveProfile()).thenThrow(new IllegalStateException("Testing Closed Down Logon."));
        String response = homePageController.root(request, new MockHttpServletResponse());
        assertThat(response, notNullValue());
        assertThat(response, Is.is(HomePageController.REDIRECT_CLOSED_LOGON));
    }

    @Test
    public void test_directOnboarding_homePage_url() throws Exception {
        UserProfile userProfile = mock(UserProfile.class);
        when(profileService.getSamlToken()).thenReturn(samlToken);
        when(userProfile.getJobRole()).thenReturn(JobRole.INVESTOR);
        String response = homePageController.directOnboardingHome(new MockHttpServletResponse());
        assertThat(response, notNullValue());
        assertThat(response, Is.is(SpaController.REDIRECT_DIRECT_ONBOARDING_HOME));
    }

    @Test
    public void test_directOnboarding_homePage_exception() throws Exception {
        when(profileService.getSamlToken()).thenThrow(new IllegalStateException("Test SAMLToken exception."));
        String response = homePageController.directOnboardingHome(new MockHttpServletResponse());
        assertThat(response, notNullValue());
        assertThat(response, Is.is(HomePageController.REDIRECT_CLOSED_LOGON));
    }

    @Test
    public void testWPL_SSO_Enabled_Wrong_Account() throws Exception {
        when(featureToggles.getFeatureToggle(eq("wpl.sso.encryptedaccount"))).thenReturn(true);
        when(togglesService.findOne(Matchers.any(ServiceErrors.class))).thenReturn(featureToggles);
        String homePageResp = homePageController.home(session, request, response, "", "WLIVE");
        verify(userPreferenceRepository, never()).save(Matchers.<UserPreference>any());
        assertThat(homePageResp, Is.is(SpaController.REDIRECT_APP_HOME));
        homePageResp = homePageController.home(session, request, response, "123456", "WLIVE");
        verify(userPreferenceRepository, never()).save(Matchers.<UserPreference>any());
        assertThat(homePageResp, Is.is(SpaController.REDIRECT_APP_HOME));
        homePageResp = homePageController.home(session, request, response,
                "CAzL+vvXgBi82V8IM4TahdQIj4TDwpVildOSzy0LdUyB/Pu03lFRzfw0/tJTOUWF", "WLIVE");
        verify(userPreferenceRepository, never()).save(Matchers.<UserPreference>any());
        assertThat(homePageResp, Is.is(SpaController.REDIRECT_APP_HOME));
    }

    @Test
    public void testWPL_SSO_Enabled_Wrong_System() throws Exception {
        when(featureToggles.getFeatureToggle(eq("wpl.sso.encryptedaccount"))).thenReturn(true);
        when(togglesService.findOne(Matchers.any(ServiceErrors.class))).thenReturn(featureToggles);
        String homePageResp = homePageController.home(session, request, response, "", "WLIVE");
        verify(userPreferenceRepository, never()).save(Matchers.<UserPreference>any());
        assertThat(homePageResp, Is.is(SpaController.REDIRECT_APP_HOME));
    }

    @Test
    public void testWPL_SSO() throws Exception {
        when(featureToggles.getFeatureToggle(eq("wpl.sso.encryptedaccount"))).thenReturn(true);
        when(togglesService.findOne(Matchers.any(ServiceErrors.class))).thenReturn(featureToggles);
        String homePageResp = homePageController.home(session, request, response,
                "IfkITLGIo2mSwNYCJoWrhf8La8phe3ZNwpm0Zt2KLDwoRdjvC1DTnK0vGTbQA/Jo", "WLIVE");
        verify(userPreferenceRepository, times(1)).save(Matchers.<UserPreference>any());
        assertThat(homePageResp, Is.is(SpaController.REDIRECT_APP_HOME));
    }

    @Test
    public void testWPL_SSO_FeatureDisabled() throws Exception {
        when(featureToggles.getFeatureToggle(eq("wpl.sso.encryptedaccount"))).thenReturn(false);
        when(togglesService.findOne(Matchers.any(ServiceErrors.class))).thenReturn(featureToggles);
        String homePageResp = homePageController.home(session, request, response,
                "IfkITLGIo2mSwNYCJoWrhf8La8phe3ZNwpm0Zt2KLDwoRdjvC1DTnK0vGTbQA/Jo", "ABCD");
        UserPreferenceKey key = new UserPreferenceKey(GCM_ID, UserPreferenceEnum.LAST_ACCESSED_ACCOUNT.getPreferenceKey());
        UserPreference userPreference = new UserPreference(key, "120055835");
        verify(userPreferenceRepository, never()).save(eq(userPreference));
        assertThat(homePageResp, Is.is(SpaController.REDIRECT_APP_HOME));
    }

    @Test
    public void testAvaloqConnectionFailure() throws Exception {
        when(profileService.isAdmin()).thenThrow(new AvaloqConnectionException("AvaloqConnectionFailure", new ServiceErrorsImpl()));
        when(togglesService.findOne(Matchers.any(ServiceErrors.class))).thenReturn(featureToggles);
        String homePageResp = homePageController.home(session, request, response,
                "IfkITLGIo2mSwNYCJoWrhf8La8phe3ZNwpm0Zt2KLDwoRdjvC1DTnK0vGTbQA/Jo", "WLIVE");
        assertThat(homePageResp, Is.is(HomePageController.REDIRECT_AVALOQ_CONNECTION_FAILURE_LOGON));
    }
}

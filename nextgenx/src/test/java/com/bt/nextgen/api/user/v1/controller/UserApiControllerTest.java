package com.bt.nextgen.api.user.v1.controller;

import com.bt.nextgen.api.user.v1.model.TermsAndConditionsDto;
import com.bt.nextgen.api.user.v1.model.TermsAndConditionsDtoKey;
import com.bt.nextgen.api.user.v1.model.UserNoticesDto;
import com.bt.nextgen.api.user.v1.model.UserNoticesDtoKey;
import com.bt.nextgen.api.user.v1.service.TermsAndConditionsDtoService;
import com.bt.nextgen.api.user.v1.service.UserNoticesDtoService;
import com.btfin.panorama.core.security.profile.UserProfile;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.service.ServiceErrors;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.test.web.server.setup.MockMvcBuilders;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.status;


@RunWith(MockitoJUnitRunner.class)
public class UserApiControllerTest {

    @EnableWebMvc
    @ComponentScan(basePackages = {"com.bt.nextgen.api.user.v1.controller"})
    public static class TestConfiguration {

        @Bean(name = "PropertyPlaceholderConfigurer")
        PropertyPlaceholderConfigurer propertyPlaceholderConfigurerBean() {
            PropertyPlaceholderConfigurer pc = new PropertyPlaceholderConfigurer();
            pc.setLocation(new ClassPathResource("/com/bt/nextgen/api/user/v1/UriConfig.properties"));
            return pc;
        }

        @Bean(name = "TermsAndConditionsDtoService")
        TermsAndConditionsDtoService termsAndConditionsDtoService() {
            return termsAndConditionsDtoService;
        }

        @Bean(name = "UserProfileService")
        UserProfileService userProfileService() {
            return userProfileService;
        }

        @Bean(name = "UserUpdatesDtoServiceV1")
        UserNoticesDtoService UserUpdatesDtoServiceV1() {
            return UserUpdatesDtoServiceV1;
        }
    }

    private org.springframework.test.web.server.MockMvc mockMvc;

    @InjectMocks
    TermsAndConditionsApiController termsAndConditionsApiController;

    @Mock(name = "TermsAndConditionsDtoService")
    static TermsAndConditionsDtoService termsAndConditionsDtoService;

    @Mock
    static UserProfileService userProfileService;

    @Mock(name = "UserUpdatesDtoServiceV1")
    static UserNoticesDtoService UserUpdatesDtoServiceV1;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.annotationConfigSetup(TestConfiguration.class).build();

        UserProfile userProfile = mock(UserProfile.class);
        when(userProfile.getBankReferenceId()).thenReturn("myUserId");
        when(userProfileService.getActiveProfile()).thenReturn(userProfile);
        when(userProfileService.getGcmId()).thenReturn("gcmId123");

        TermsAndConditionsDto termsAndConditionsDto = new TermsAndConditionsDto(new TermsAndConditionsDtoKey("user123", "tnc123", Integer.valueOf(2)));
        when(termsAndConditionsDtoService.submit(any(TermsAndConditionsDto.class), any(ServiceErrors.class))).thenReturn(termsAndConditionsDto);

        UserNoticesDto userNoticesDto = new UserNoticesDto(new UserNoticesDtoKey("userid123", "noticeid123", Integer.valueOf(2)));
        when(UserUpdatesDtoServiceV1.update(any(UserNoticesDto.class), any(ServiceErrors.class))).thenReturn(userNoticesDto);
    }

    @Test
    public final void testGetTermsAndConditions() throws Exception {
        this.mockMvc.perform(
                get("/secure/api/user/v1_0/terms-and-conditions/").accept(MediaType.ALL))
                .andExpect(status().isOk());

    }

    @Test
    public final void testAcceptTermsAndConditions() throws Exception {
        this.mockMvc.perform(
                post("/secure/api/user/v1_0/terms-and-conditions/tncid123/2").accept(MediaType.ALL))
                .andExpect(status().isOk());
    }

    @Test
    public final void testGetUserUpdates() throws Exception {
        this.mockMvc.perform(
                get("/secure/api/user/v1_0/notices").accept(MediaType.ALL))
                .andExpect(status().isOk());
    }

    @Test
    public final void testModifyUserUpdates() throws Exception {
        this.mockMvc.perform(
                post("/secure/api/user/v1_0/notices/noticeid123/2").accept(MediaType.ALL))
                .andExpect(status().isOk());
    }
}


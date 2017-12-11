package com.bt.nextgen.api.logon.controller;

import com.bt.nextgen.api.logon.model.LogonUpdateUserNameDto;
import com.bt.nextgen.config.BaseSecureIntegrationTest;
import com.bt.nextgen.config.SecureTestContext;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.btfin.panorama.core.security.saml.SamlToken;
import com.bt.nextgen.core.security.api.service.PermissionBaseDtoService;
import com.btfin.panorama.core.security.profile.UserProfile;
import com.bt.nextgen.core.security.profile.UserProfileAdapterImpl;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.service.avaloq.userinformation.JobRole;
import com.bt.nextgen.service.avaloq.userinformation.UserExperience;
import com.bt.nextgen.service.avaloq.userinformation.UserInformationImpl;
import com.bt.nextgen.service.integration.client.ClientIntegrationService;
import com.bt.nextgen.service.integration.userinformation.ClientKey;
import com.bt.nextgen.service.integration.userinformation.JobKey;
import com.btfin.panorama.core.security.integration.userinformation.UserInformation;
import com.btfin.panorama.core.security.integration.userprofile.JobProfile;
import org.joda.time.DateTime;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class LogonApiControllerIntegrationTest extends BaseSecureIntegrationTest
{

    @Autowired
    private LogonApiController logonApiController;
    @Mock
    UserProfileService userProfileService;
    @Mock
    private PermissionBaseDtoService permissionBaseService;
    @Mock
    private ClientIntegrationService clientIntegrationService;

    private SamlToken samlToken;

    @Test
    @SecureTestContext(authorities =
            {
                    "ROLE_ADVISER"
            }, username = "adviser", customerId = "201601509" ,profileId ="711")
    public void testPasswordChangeForPostiveScenario() throws Exception
    {
        //logonApiController.update("newPassword","newPassword","oldPassword");
       // when(permissionBaseService.hasBasicPermission("feature.global.prmNonValueEvents")).thenReturn(true);
       // Mockito.when(permissionBaseService.hasBasicPermission("feature.global.prmNonValueEvents")).thenReturn(true);
       /* samlToken = new SamlToken(SamlUtil.loadWplSamlNewPanoramaCustomer());
        Mockito.when(userProfileService.getSamlToken()).thenReturn(samlToken);
        when(userProfileService.getUserId()).thenReturn("201603880");
       // when(permissionBaseService.hasBasicPermission("feature.global.prmNonValueEvents")).thenReturn(true);
        IndividualDetailImpl individualDetailImpl = Mockito.mock(IndividualDetailImpl.class);
        when(individualDetailImpl.getCISKey()).thenReturn(CISKey.valueOf("83971220010"));
        RequestContextHolder requestContextHolder = Mockito.mock(RequestContextHolder.class);
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        mockRequest.setScheme("http");
        mockRequest.setServerName("geomajas.org");
        mockRequest.setServerPort(80);
        mockRequest.setContextPath("/test");
   ServletRequestAttributes attributes = new ServletRequestAttributes(mockRequest);
        RequestContextHolder.setRequestAttributes(attributes);
           MockHttpSession session = mock(MockHttpSession.class);
        //HttpSession session = Mockito.mock(HttpSession.class);
          UserProfile activeProfile = getProfile(JobRole.ADVISER, "job id 1", "client1");
        //when(userProfileService.getActiveProfile()).thenReturn(activeProfile);
        when(userProfileService.getActiveProfile()).thenReturn(mock(UserProfile.class));
        when(userProfileService.getUserId()).thenReturn("201603880");
        when(userProfileService.isEmulating()).thenReturn(true);
        when(userProfileService.getEffectiveProfile()).thenReturn(mock(Profile.class));
        when(userProfileService.getEffectiveProfile().getUserId()).thenReturn("201603880");
      //  when(userProfileService.getActiveProfile().getUsername()).thenReturn("201603880");
        when(userProfileService.getUsername()).thenReturn("Darryl");*/

       /* ApiResponse response = logonApiController.update("50F159EF3C35B26125E561F6D51B6CC792293DD4CAC81E5F", "50F159EF3C35B26125E561F6D51B6CC792293DD4CAC81E5F", "E61EAFCCC4821504C5040C1D1DA7032C894404ECE7DDDB11");
        assertThat( response, is(notNullValue()));
        LogonUpdatePasswordDto logonUpdatePasswordDto=(LogonUpdatePasswordDto)response.getData();
        assertThat( logonUpdatePasswordDto.getNewPassword(), is(nullValue()));
        assertThat( logonUpdatePasswordDto.getCurrentPassword(), is(nullValue()));
        assertThat( logonUpdatePasswordDto.getHalgm(), is(nullValue()));
        assertThat( logonUpdatePasswordDto.isUpdateFlag(), is(true));*/
    }

    @Test
    @SecureTestContext(authorities =
            {
                    "ROLE_ADVISER"
            }, username = "adviser", customerId = "201601509" ,profileId ="711")
    public void testUserChangeScenario() throws Exception
    {
        //logonApiController.update("newPassword","newPassword","oldPassword");
         ApiResponse response = logonApiController.update("UserName", "UserName");
        // userProfileService.setNewUserNameProvidedByUserForChange(null);
        assertThat( response, is(notNullValue()));
        LogonUpdateUserNameDto logonUpdateUserNameDto=(LogonUpdateUserNameDto)response.getData();
        assertThat( logonUpdateUserNameDto.isUpdateFlag(), is(true));
        assertThat(logonUpdateUserNameDto.getNewUserName(), is(nullValue()));
        assertThat( logonUpdateUserNameDto.getUserName(), is(nullValue()));

    }






    @Test
    @SecureTestContext(authorities =
            {
                    "ROLE_ADVISER"
            }, username = "adviser", customerId = "201601509" ,profileId ="711" )
    public void testPasswordChangeForNegativeScenario() throws Exception
    {
      // Mockito.when(permissionBaseService.hasBasicPermission("feature.global.prmNonValueEvents")).thenReturn(false);
        ApiResponse response=null;
        try {
            response=logonApiController.update("Invalid_Password_1","Invalid_Password_1","E61EAFCCC4821504C5040C1D1DA7032C894404ECE7DDDB11");
        }
        catch(Exception e)
        {

        }
        assertThat( response, is(nullValue()));
    }

    public UserProfile getProfile(final JobRole role, final String jobId, final String customerId)
    {
        UserInformation user = new UserInformationImpl();
        user.setClientKey(ClientKey.valueOf(customerId));
        JobProfile job = getJobProfile(role, jobId);

        UserProfile profile = new UserProfileAdapterImpl(user, job);
        return profile;
    }

    private JobProfile getJobProfile(final JobRole role, final String jobId)
    {
        JobProfile job = new JobProfile()
        {
            @Override public JobRole getJobRole()
            {
                return role;
            }

            @Override public String getPersonJobId()
            {
                return null;
            }

            @Override public JobKey getJob()
            {
                return JobKey.valueOf(jobId);
            }

            @Override public String getProfileId()
            {
                return null;
            }

            @Override
            public DateTime getCloseDate() {
                return null;
            }

            @Override
            public UserExperience getUserExperience() {
                return null;
            }
        };
        return job;
    }
}

package com.bt.nextgen.api.profile.v1.service;

import com.bt.nextgen.api.profile.v1.model.ProfileDetailsUpdateDto;
import com.bt.nextgen.core.repository.User;
import com.bt.nextgen.core.repository.UserRepository;
import com.btfin.panorama.core.security.Roles;
import com.btfin.panorama.core.security.profile.UserProfile;
import com.bt.nextgen.core.security.profile.UserProfileAdapterImpl;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.core.util.Properties;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.userinformation.JobRole;
import com.bt.nextgen.service.avaloq.userinformation.UserInformationImpl;
import com.btfin.panorama.core.security.integration.customer.ChannelType;
import com.btfin.panorama.core.security.integration.customer.CredentialType;
import com.btfin.panorama.core.security.integration.customer.CustomerCredentialInformation;
import com.btfin.panorama.core.security.integration.customer.UserGroup;
import com.bt.nextgen.service.integration.user.CISKey;
import com.bt.nextgen.service.integration.user.UserKey;
import com.bt.nextgen.service.integration.userinformation.ClientKey;
import com.btfin.panorama.core.security.integration.userinformation.UserInformation;
import com.btfin.panorama.core.security.integration.userprofile.JobProfile;
import com.btfin.panorama.core.security.UserAccountStatus;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)

public class ProfileDetailsUpdateDtoServiceTest
{
    @InjectMocks
    ProfileDetailsUpdateDtoServiceImpl profileDetailsUpdateDtoService;

    @Mock
    UserProfileService profileService;

    @Mock
    UserRepository userRepository;

    private ProfileDetailsUpdateDto updateDto;
    private ServiceErrors serviceErrors;
    private User user;
    private UserProfile activeProfile;

    @Before
    public void setup()
    {
        user = getUser("user1");
        serviceErrors = new FailFastErrorsImpl();
        activeProfile = getProfile(JobRole.ADVISER, "job id 1", "client1", "user1");
        when(profileService.getActiveProfile()).thenReturn(activeProfile);
    }

    @Test
    public void testUpdateSuccess()
    {
        updateDto = new ProfileDetailsUpdateDto("read");
        when(userRepository.loadUser("user1")).thenReturn(user);
        ProfileDetailsUpdateDto result = profileDetailsUpdateDtoService.update(updateDto, serviceErrors);
        assertEquals(result.getKey(), "Success");
        assertEquals(user.getWhatsNewVersion(), Properties.getString("version"));
    }

    @Test
    public void testUpdateKeyWordFail()
    {
        updateDto = new ProfileDetailsUpdateDto("readfail");
        when(userRepository.loadUser("user1")).thenReturn(user);
        ProfileDetailsUpdateDto result = profileDetailsUpdateDtoService.update(updateDto, serviceErrors);
        assertEquals(result.getKey(), "Failed");
        assertNull(user.getWhatsNewVersion());
    }

    @Test
    public void testUpdateNoUserSuccess()
    {
        updateDto = new ProfileDetailsUpdateDto("read");
        when(userRepository.loadUser("user1")).thenReturn(null);
        when(userRepository.newUser("user1")).thenReturn(user);
        ProfileDetailsUpdateDto result = profileDetailsUpdateDtoService.update(updateDto, serviceErrors);
        assertEquals(result.getKey(), "Success");
        assertEquals(user.getWhatsNewVersion(), Properties.getString("version"));
    }

    @Test
    public void testUpdateNoUserKeyWordFail()
    {
        updateDto = new ProfileDetailsUpdateDto("read1");
        when(userRepository.loadUser("user1")).thenReturn(null);
        when(userRepository.newUser("user1")).thenReturn(user);
        ProfileDetailsUpdateDto result = profileDetailsUpdateDtoService.update(updateDto, serviceErrors);
        assertEquals(result.getKey(), "Failed");
        assertNull(user.getWhatsNewVersion());
    }

    private User getUser(String userName)
    {
        User user = new User();
        user.setUsername(userName);
        return user;
    }
    public UserProfile getProfile(final JobRole role, final String jobId, final String clientId, final String customerId)
    {
        UserInformation user = new UserInformationImpl();
        user.setClientKey(ClientKey.valueOf(clientId));
        JobProfile job = getJobProfile(role, jobId);

        CustomerCredentialInformation credential = getCredentialInformation(customerId);

        UserProfile profile = new UserProfileAdapterImpl(user, job, credential);
        return profile;
    }

    private JobProfile getJobProfile(final JobRole role, final String profileId)
    {
        JobProfile job = Mockito.mock(JobProfile.class);
        when(job.getJobRole()).thenReturn(role);
        when(job.getProfileId()).thenReturn(profileId);
        return job;
    }

    public CustomerCredentialInformation getCredentialInformation(final String customerId)
    {
        CustomerCredentialInformation credential = new CustomerCredentialInformation()
        {
            @Override
            public String getUserReferenceId() {
                return null;
            }
            @Override
            public List<UserGroup> getUserGroup() {
                return null;
            }
            @Override
            public UserAccountStatus getPrimaryStatus()
            {
                return null;
            }

            @Override
            public CredentialType getCredentialType()
            {
                return null;
            }

            @Override
            public List<Roles> getCredentialGroups()
            {
                return null;
            }

            @Override
            public ChannelType getChannelType()
            {
                return null;
            }

            @Override
            public String getLastUsed()
            {
                return null;
            }

            @Override
            public String getStartTimeStamp()
            {
                return null;
            }

            @Override
            public List<UserAccountStatus> getAllAccountStatusList()
            {
                return null;
            }

            @Override
            public String getServiceLevel()
            {
                return null;
            }

            @Override
            public String getServiceStatusErrorCode()
            {
                return null;
            }

            @Override
            public String getServiceStatusErrorDesc()
            {
                return null;
            }

            @Override
            public String getStatusInfo()
            {
                return null;
            }

            @Override
            public UserAccountStatus getUserAccountStatus()
            {
                return null;
            }

            @Override
            public DateTime getDate()
            {
                return null;
            }

            @Override
            public String getUsername()
            {
                return null;
            }

            @Override
            public String getCredentialId()
            {
                return null;
            }

            @Override public String getBankReferenceId()
            {
                return customerId;
            }

            @Override public UserKey getBankReferenceKey()
            {
                return null;
            }

            @Override
            public CISKey getCISKey() {
                return null;
            }

            @Override
            public String getNameId() { return null; }

            @Override
            public String getPpId() {
                return null;
            }
        };
        return credential;
    }

}

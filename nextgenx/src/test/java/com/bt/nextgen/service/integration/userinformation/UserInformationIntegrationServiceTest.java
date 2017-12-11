package com.bt.nextgen.service.integration.userinformation;

import com.btfin.panorama.core.security.integration.userinformation.JobPermission;
import com.btfin.panorama.core.security.integration.userinformation.UserInformation;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.avaloq.userinformation.AvaloqUserInformationIntegrationServiceImpl;
import com.bt.nextgen.service.avaloq.userinformation.UserInformationImpl;
import com.bt.nextgen.service.avaloq.userprofile.JobProfileImpl;
import com.bt.nextgen.service.integration.staticrole.StaticRoleIntegrationService;
import com.btfin.panorama.core.security.integration.userprofile.JobProfile;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserInformationIntegrationServiceTest
{

    @InjectMocks
    AvaloqUserInformationIntegrationServiceImpl userInformation;

    @Mock
    CacheManagedUserInformationService cacheService;

    @Mock
    StaticRoleIntegrationService staticRole;

    @Test
    public void testloadUserInformation() throws Exception
    {
        ServiceErrorsImpl serviceErrors = new ServiceErrorsImpl();
        JobProfile profile = new JobProfileImpl();
        UserInformationImpl user = new UserInformationImpl();
        user.setProfileId("1234");
        user.setClientKey(ClientKey.valueOf("23456"));

        List<String> role = new ArrayList<>();
        role.add("$UR_AVSR_BASIC");

        List<String> roles = new ArrayList<>();
        roles.add("$UR_AVSR_BASIC");
        user.setRoles(roles);

        when(cacheService.getUserInformation(any(JobProfile.class), any(ServiceErrorsImpl.class))).thenReturn(user);
        UserInformation userInfo = userInformation.loadUserInformation(profile, serviceErrors);

        assertThat(userInfo.getClientKey(), is(user.getClientKey()));
        assertThat(userInfo.getProfileId(), is(user.getProfileId()));
        assertNotNull(userInfo.getFunctionalRoles());

        assertThat(serviceErrors.isEmpty(), is(true));
    }

    @Test
    public void testGetLoggedInPerson() throws Exception
    {
        ServiceErrorsImpl serviceErrors = new ServiceErrorsImpl();
        JobProfile profile = new JobProfileImpl();

        UserInformationImpl user = new UserInformationImpl();
        user.setProfileId("1234");
        user.setClientKey(ClientKey.valueOf("23456"));

        List<String> roles = new ArrayList<>();
        roles.add("$UR_AVSR_BASIC");
        user.setRoles(roles);

        List<String> role = new ArrayList<>();
        role.add("$UR_AVSR_BASIC");

        when(cacheService.getUserInformation(any(JobProfile.class), any(ServiceErrorsImpl.class))).thenReturn(user);
        ClientIdentifier userInfo = userInformation.getLoggedInPerson(profile, serviceErrors);

        assertThat(userInfo.getClientKey(), is(user.getClientKey()));

        assertThat(serviceErrors.isEmpty(), is(true));
    }

    @Test
    public void testGetAvailableRoles() throws Exception
    {
        ServiceErrorsImpl serviceErrors = new ServiceErrorsImpl();
        JobProfile profile = new JobProfileImpl();

        UserInformationImpl user = new UserInformationImpl();
        user.setClientKey(ClientKey.valueOf("1234"));
        user.setProfileId("23456");

        List<String> roles = new ArrayList<>();
        roles.add("$UR_AVSR_BASIC");
        user.setRoles(roles);

        List<String> role = new ArrayList<>();
        role.add("$UR_AVSR_BASIC");

        when(cacheService.getUserInformation(any(JobProfile.class), any(ServiceErrorsImpl.class))).thenReturn(user);
        JobPermission userInfo = userInformation.getAvailableRoles(profile, serviceErrors);

        assertThat(serviceErrors.isEmpty(), is(true));
    }

    @Test
    public void testGetUserIdentifier() throws Exception
    {
        ServiceErrorsImpl serviceErrors = new ServiceErrorsImpl();
        JobProfile profile = new JobProfileImpl();

        UserInformationImpl user = new UserInformationImpl();
        user.setClientKey(ClientKey.valueOf("1234"));
        user.setProfileId("23456");

        List<String> roles = new ArrayList<>();
        roles.add("$UR_AVSR_BASIC");
        user.setRoles(roles);

        List<String> role = new ArrayList<>();
        role.add("$UR_AVSR_BASIC");

        when(cacheService.getUserInformation(any(JobProfile.class), any(ServiceErrorsImpl.class))).thenReturn(user);
        UserInformation userInfo = userInformation.getUserIdentifier(profile, serviceErrors);

        assertThat(userInfo.getClientKey(), is(user.getClientKey()));

        assertThat(serviceErrors.isEmpty(), is(true));
    }

}
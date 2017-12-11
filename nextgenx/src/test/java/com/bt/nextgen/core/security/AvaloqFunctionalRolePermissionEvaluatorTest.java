package com.bt.nextgen.core.security;

import com.btfin.panorama.core.security.profile.UserProfile;
import com.bt.nextgen.core.security.profile.UserProfileAdapterImpl;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.service.avaloq.userinformation.FunctionalRole;
import com.bt.nextgen.service.avaloq.userinformation.UserInformationImpl;
import com.bt.nextgen.service.avaloq.userprofile.JobProfileImpl;
import com.btfin.panorama.core.security.integration.userinformation.UserInformation;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.core.Authentication;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class AvaloqFunctionalRolePermissionEvaluatorTest
{
    @InjectMocks
    private AvaloqFunctionalRolePermissionEvaluator permissionEvaluator;

    @Mock
    Authentication authentication;

    @Mock
    UserProfileService userProfileService;

    @Test
    public void testHasPermission() throws Exception
    {
        UserProfile activeProfile = getActiveProfile(Arrays.asList(FunctionalRole.Make_a_BPAYPay_Anyone_Payment,
            FunctionalRole.Add_remove_update_adviser_role_on_user,
            FunctionalRole.View_intermediary_reports));
        Mockito.when(userProfileService.getActiveProfile()).thenReturn(activeProfile);
        assertTrue(permissionEvaluator.hasPermission(authentication, null, "View_intermediary_reports"));
    }

    @Test
    public void shouldReturnFalse_WhenEmulating_AndPermissionStringContains_IsNotEmulating() throws  Exception {
        UserProfile activeProfile = getActiveProfile(new ArrayList<FunctionalRole>());
        Mockito.when(userProfileService.getActiveProfile()).thenReturn(activeProfile);
        Mockito.when(userProfileService.isEmulating()).thenReturn(true);
        assertFalse(permissionEvaluator.hasPermission(authentication, null, "isNotEmulating"));
    }

    @Test
    public void shouldReturnTrue_WhenNotEmulating_AndPermissionStringContains_IsNotEmulating() throws  Exception {
        UserProfile activeProfile = getActiveProfile(new ArrayList<FunctionalRole>());
        Mockito.when(userProfileService.getActiveProfile()).thenReturn(activeProfile);
        Mockito.when(userProfileService.isEmulating()).thenReturn(false);
        assertTrue(permissionEvaluator.hasPermission(authentication, null, "isNotEmulating"));
    }

    @Test
    public void shouldReturnTrue_WhenNotEmulating_AndPermissionStringContains_IsNotEmulatingOnly() throws  Exception {
        UserProfile activeProfile = getActiveProfile(new ArrayList<FunctionalRole>());
        Mockito.when(userProfileService.getActiveProfile()).thenReturn(activeProfile);
        Mockito.when(userProfileService.isEmulating()).thenReturn(false);
        assertTrue(permissionEvaluator.hasPermission(authentication, null, "isNotEmulating"));
    }

    @Test
    public void shouldReturnFalse_WhenEmulating_AndPermissionStringContains_IsNotEmulatingOnly() throws  Exception {
        UserProfile activeProfile = getActiveProfile(new ArrayList<FunctionalRole>());
        Mockito.when(userProfileService.getActiveProfile()).thenReturn(activeProfile);
        Mockito.when(userProfileService.isEmulating()).thenReturn(false);
        assertTrue(permissionEvaluator.hasPermission(authentication, null, "isNotEmulating"));
    }

    @Test
    public void shouldReturnFalse_WhenNotEmulating_AndUserDoestNotHaveAllRequiredPermission() throws  Exception {
        UserProfile activeProfile = getActiveProfile(new ArrayList<FunctionalRole>());
        Mockito.when(userProfileService.getActiveProfile()).thenReturn(activeProfile);
        Mockito.when(userProfileService.isEmulating()).thenReturn(false);
        assertFalse(permissionEvaluator.hasPermission(authentication, null, "View_intermediary_reports"));
    }

    @Test
    public void testDoesNotHavePermission() throws Exception
    {
        UserProfile activeProfile = getActiveProfile(Arrays.asList(FunctionalRole.Make_a_BPAY_Payment_to_a_new_biller));
        Mockito.when(userProfileService.getActiveProfile()).thenReturn(activeProfile);
        assertFalse(permissionEvaluator.hasPermission(authentication, null, "View_intermediary_reports"));
    }

    public UserProfile getActiveProfile(List<FunctionalRole> roles)
    {
        UserInformation user = new UserInformationImpl();
        user.setFunctionalRoles(roles);
        UserProfile profile = new UserProfileAdapterImpl(user, new JobProfileImpl());
        return profile;
    }
}

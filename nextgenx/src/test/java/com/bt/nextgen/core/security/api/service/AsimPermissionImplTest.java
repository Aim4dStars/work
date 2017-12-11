package com.bt.nextgen.core.security.api.service;

import com.btfin.panorama.core.security.profile.UserProfile;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.service.avaloq.userinformation.JobRole;
import com.bt.nextgen.service.avaloq.userinformation.JobRoleType;
import com.bt.nextgen.service.avaloq.userinformation.UserExperience;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static com.bt.nextgen.service.avaloq.userinformation.JobRoleType.INTERMEDIARY;
import static com.bt.nextgen.service.avaloq.userinformation.JobRoleType.INVESTOR;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.when;


/**
 * Tests {@link AsimPermissionImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class AsimPermissionImplTest {
    private static final JobRoleType[] JOB_ROLE_TYPES_INVESTOR = { INVESTOR };
    private static final JobRoleType[] JOB_ROLE_TYPES_INTERMEDIARY = { INTERMEDIARY };
    private static final JobRoleType[] JOB_ROLE_TYPES_MULTI = { INTERMEDIARY, INVESTOR };

    @Mock
    private UserProfileService profileService;

    @Mock
    private UserProfile userProfile;

    @InjectMocks
    private AsimPermissionImpl asimPermission;


    @Test
    public void overrideValueForNonAsimUser() {
        when(profileService.getActiveProfile()).thenReturn(userProfile);
        when(userProfile.getUserExperience()).thenReturn(UserExperience.ADVISED);

        overrideValueForNonAsimUser(true);
        overrideValueForNonAsimUser(false);
    }

    @Test
    public void overrideValueForAsimUserWithoutJobRole() {
        when(profileService.getActiveProfile()).thenReturn(userProfile);
        when(userProfile.getUserExperience()).thenReturn(UserExperience.ASIM);
        when(userProfile.getJobRole()).thenReturn(null);

        overrideValueForAsimUserWithoutJobRole(true);
        overrideValueForAsimUserWithoutJobRole(false);
    }

    @Test
    public void overrideValueForAsimUserWithNullJobRoleTypes() {
        when(profileService.getActiveProfile()).thenReturn(userProfile);
        when(userProfile.getUserExperience()).thenReturn(UserExperience.ASIM);
        when(userProfile.getJobRole()).thenReturn(JobRole.INVESTOR);

        overrideValueForAsimUserWithNullJobRoleTypes(true);
        overrideValueForAsimUserWithNullJobRoleTypes(false);
    }

    @Test
    public void overrideValueWithIncludeJobRoleTypes() {
        when(profileService.getActiveProfile()).thenReturn(userProfile);
        when(userProfile.getUserExperience()).thenReturn(UserExperience.ASIM);
        when(userProfile.getJobRole()).thenReturn(JobRole.INVESTOR);

        overrideValueWithIncludeJobRoleTypes(true);
        overrideValueWithIncludeJobRoleTypes(false);
    }

    @Test
    public void overrideValueWithExcludeJobRoleTypes() {
        when(profileService.getActiveProfile()).thenReturn(userProfile);
        when(userProfile.getUserExperience()).thenReturn(UserExperience.ASIM);
        when(userProfile.getJobRole()).thenReturn(JobRole.INVESTOR);

        overrideValueWithExcludeJobRoleTypes(true);
        overrideValueWithExcludeJobRoleTypes(false);
    }


    private void overrideValueForNonAsimUser(boolean value) {
        assertThat(asimPermission.overrideValue(value, false, true, JOB_ROLE_TYPES_INVESTOR), equalTo(value));
        assertThat(asimPermission.overrideValue(value, false, false, JOB_ROLE_TYPES_INVESTOR), equalTo(value));
        assertThat(asimPermission.overrideValue(value, true, true, JOB_ROLE_TYPES_INVESTOR), equalTo(value));
        assertThat(asimPermission.overrideValue(value, true, false, JOB_ROLE_TYPES_INVESTOR), equalTo(value));
    }

    private void overrideValueForAsimUserWithoutJobRole(boolean value) {
        assertThat(asimPermission.overrideValue(value, false, true, JOB_ROLE_TYPES_INVESTOR), equalTo(value));
        assertThat(asimPermission.overrideValue(value, false, false, JOB_ROLE_TYPES_INVESTOR), equalTo(value));
        assertThat(asimPermission.overrideValue(value, true, true, JOB_ROLE_TYPES_INVESTOR), equalTo(value));
        assertThat(asimPermission.overrideValue(value, true, false, JOB_ROLE_TYPES_INVESTOR), equalTo(value));
    }

    private void overrideValueForAsimUserWithNullJobRoleTypes(boolean value) {
        assertThat(asimPermission.overrideValue(value, false, true, null), equalTo(value));
        assertThat(asimPermission.overrideValue(value, false, false, null), equalTo(value));
        assertThat(asimPermission.overrideValue(value, true, true, null), equalTo(value));
        assertThat(asimPermission.overrideValue(value, true, false, null), equalTo(value));
    }

    private void overrideValueWithIncludeJobRoleTypes(boolean value) {
        final boolean includeRoleTypes = true;
        boolean overrideValue;

        overrideValue = true;
        assertThat(asimPermission.overrideValue(value, overrideValue, includeRoleTypes, JOB_ROLE_TYPES_INVESTOR),
                equalTo(overrideValue));
        assertThat(asimPermission.overrideValue(value, overrideValue, includeRoleTypes, JOB_ROLE_TYPES_MULTI),
                equalTo(overrideValue));
        assertThat(asimPermission.overrideValue(value, overrideValue, includeRoleTypes, JOB_ROLE_TYPES_INTERMEDIARY),
                equalTo(value));

        overrideValue = false;
        assertThat(asimPermission.overrideValue(value, overrideValue, includeRoleTypes, JOB_ROLE_TYPES_INVESTOR),
                equalTo(overrideValue));
        assertThat(asimPermission.overrideValue(value, overrideValue, includeRoleTypes, JOB_ROLE_TYPES_MULTI),
                equalTo(overrideValue));
        assertThat(asimPermission.overrideValue(value, overrideValue, includeRoleTypes, JOB_ROLE_TYPES_INTERMEDIARY),
                equalTo(value));
    }

    private void overrideValueWithExcludeJobRoleTypes(boolean value) {
        final boolean includeRoleTypes = false;
        boolean overrideValue;

        overrideValue = true;
        assertThat(asimPermission.overrideValue(value, overrideValue, includeRoleTypes, JOB_ROLE_TYPES_INVESTOR),
                equalTo(value));
        assertThat(asimPermission.overrideValue(value, overrideValue, includeRoleTypes, JOB_ROLE_TYPES_MULTI),
                equalTo(value));
        assertThat(asimPermission.overrideValue(value, overrideValue, includeRoleTypes, JOB_ROLE_TYPES_INTERMEDIARY),
                equalTo(overrideValue));

        overrideValue = false;
        assertThat(asimPermission.overrideValue(value, overrideValue, includeRoleTypes, JOB_ROLE_TYPES_INVESTOR),
                equalTo(value));
        assertThat(asimPermission.overrideValue(value, overrideValue, includeRoleTypes, JOB_ROLE_TYPES_MULTI),
                equalTo(value));
        assertThat(asimPermission.overrideValue(value, overrideValue, includeRoleTypes, JOB_ROLE_TYPES_INTERMEDIARY),
                equalTo(overrideValue));
    }
}

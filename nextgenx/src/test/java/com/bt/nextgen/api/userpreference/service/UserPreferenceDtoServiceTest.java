package com.bt.nextgen.api.userpreference.service;

import com.bt.nextgen.api.userpreference.model.UserPreferenceDto;
import com.bt.nextgen.api.userpreference.model.UserPreferenceDtoKey;
import com.bt.nextgen.api.userpreference.model.UserPreferenceEnum;
import com.bt.nextgen.api.userpreference.model.UserTypeEnum;
import com.bt.nextgen.core.repository.UserPreference;
import com.bt.nextgen.core.repository.UserPreferenceKey;
import com.bt.nextgen.core.repository.UserPreferenceRepository;
import com.btfin.panorama.core.security.profile.UserProfile;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.integration.userinformation.JobKey;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserPreferenceDtoServiceTest {

    @InjectMocks
    private UserPreferenceDtoServiceImpl dtoService;

    @Mock
    private UserPreferenceRepository userPreferenceRepository;

    @Mock
    private UserProfileService profileService;

    @Mock
    private UserProfile activeProfile;

    private ServiceErrors serviceErrors;
    private UserPreferenceDto userPreferenceDto;
    private UserPreference preferenceDetails;

    @Before
    public void setup() {
        serviceErrors = new ServiceErrorsImpl();
        userPreferenceDto = new UserPreferenceDto();
        preferenceDetails = new UserPreference();
        when(activeProfile.getBankReferenceId()).thenReturn("gcm1");
        when(activeProfile.getJob()).thenReturn(JobKey.valueOf("job1"));
    }

    @Test
    public void testFindOneSuccess() {
        preferenceDetails.setKey(new UserPreferenceKey("job1", UserPreferenceEnum.DEFAULT_ROLE
            .getPreferenceKey()));
        preferenceDetails.setValue("pref details 1");
        when(profileService.getActiveProfile()).thenReturn(activeProfile);
        when(userPreferenceRepository.find(anyString(), anyString())).thenReturn
            (preferenceDetails);
        List<UserPreferenceDto> result = dtoService.search(new UserPreferenceDtoKey(UserTypeEnum.JOB.getUserType(),
            UserPreferenceEnum.DEFAULT_ROLE.getPreferenceKey()), serviceErrors);
        Assert.assertEquals(result.size(), 1);
        Assert.assertEquals(result.get(0).getKey().getUserType(), UserTypeEnum.JOB.getUserType());
        Assert.assertEquals(result.get(0).getKey().getPreferenceId(), UserPreferenceEnum.DEFAULT_ROLE
            .getPreferenceKey());
        Assert.assertEquals(result.get(0).getValue(), "pref details 1");
    }

    @Test
    public void testNoUserTypeEnumMatch() {
        List<UserPreferenceDto> result = dtoService.search(new UserPreferenceDtoKey("job1", UserPreferenceEnum
            .DEFAULT_ROLE.getPreferenceKey()), serviceErrors);
        Assert.assertEquals(result.size(), 0);
    }

    @Test
    public void testNoPreferenceTypeEnumMatch() {
        preferenceDetails.setKey(new UserPreferenceKey("job", "unknown"));
        preferenceDetails.setValue("pref details 1");
        when(profileService.getActiveProfile()).thenReturn(activeProfile);
        when(userPreferenceRepository.find(anyString(), anyString())).thenReturn
            (preferenceDetails);
        List<UserPreferenceDto> result = dtoService.search(new UserPreferenceDtoKey("job", "random"), serviceErrors);
        Assert.assertEquals(result.size(), 1);
        Assert.assertEquals(result.get(0).getKey().getPreferenceId(), UserPreferenceEnum.UNKNOWN
            .getPreferenceKey());
        Assert.assertEquals(result.get(0).getValue(), "pref details 1");
    }

    @Test
    public void testFindMultipleSuccess() {
        preferenceDetails.setKey(new UserPreferenceKey("user1",
            UserPreferenceEnum.LAST_ACCESSED_ACCOUNTS.getPreferenceKey()));
        preferenceDetails.setValue("pref details 1");
        when(profileService.getActiveProfile()).thenReturn(activeProfile);
        when(userPreferenceRepository.find(anyString(), anyString())).thenReturn
            (preferenceDetails);
        List<UserPreferenceDto> result = dtoService.search(new UserPreferenceDtoKey(UserTypeEnum.USER.getUserType(),
            "defaultrole,lastaccounts"), serviceErrors);
        Assert.assertEquals(result.size(), 2);
        Assert.assertEquals(result.get(1).getKey().getPreferenceId(), UserPreferenceEnum.LAST_ACCESSED_ACCOUNTS
            .getPreferenceKey());
        Assert.assertEquals(result.get(1).getValue(), "pref details 1");
    }

    @Test
    public void testFindNoMatch() {
        when(profileService.getActiveProfile()).thenReturn(activeProfile);
        when(userPreferenceRepository.find(anyString(), anyString())).thenReturn(null);
        List<UserPreferenceDto> result = dtoService.search(new UserPreferenceDtoKey("user", "pref1"), serviceErrors);
        Assert.assertEquals(result.size(), 0);
    }

    @Test
    public void testUpdateSuccess() {
        UserPreferenceDtoKey key = new UserPreferenceDtoKey();
        key.setUserType(UserTypeEnum.JOB.getUserType());
        key.setPreferenceId(UserPreferenceEnum.DEFAULT_ROLE.getPreferenceKey());
        userPreferenceDto.setKey(key);
        userPreferenceDto.setValue("value1");
        preferenceDetails.setKey(new UserPreferenceKey("job1", UserPreferenceEnum.DEFAULT_ROLE.getPreferenceKey()));
        preferenceDetails.setValue("value1");
        when(profileService.getActiveProfile()).thenReturn(activeProfile);
        when(userPreferenceRepository.save(any(UserPreference.class))).thenReturn(preferenceDetails);
        UserPreferenceDto result = dtoService.update(userPreferenceDto, serviceErrors);
        Assert.assertEquals(result.getKey().getPreferenceId(), UserPreferenceEnum.DEFAULT_ROLE.getPreferenceKey());
        Assert.assertEquals(result.getValue(), "value1");
    }

    @Test
    public void testUpdateFailed() {
        userPreferenceDto.setKey(new UserPreferenceDtoKey("job1", UserPreferenceEnum.DEFAULT_ROLE.getPreferenceKey()));
        userPreferenceDto.setValue("value1");
        when(profileService.getActiveProfile()).thenReturn(activeProfile);
        when(userPreferenceRepository.save(any(UserPreference.class))).thenReturn(null);
        UserPreferenceDto result = dtoService.update(userPreferenceDto, serviceErrors);
        Assert.assertNull(result);
    }

    @Test
    public void testUpdateSuccessUnknownPreferenceKey() {
        UserPreferenceDtoKey key = new UserPreferenceDtoKey();
        key.setUserType(UserTypeEnum.JOB.getUserType());
        key.setPreferenceId("random");
        userPreferenceDto.setKey(key);
        userPreferenceDto.setValue("value1");
        preferenceDetails.setKey(new UserPreferenceKey("job1", "unknown"));
        preferenceDetails.setValue("value1");
        when(profileService.getActiveProfile()).thenReturn(activeProfile);
        when(userPreferenceRepository.save(any(UserPreference.class))).thenReturn(preferenceDetails);
        UserPreferenceDto result = dtoService.update(userPreferenceDto, serviceErrors);
        Assert.assertEquals(result.getKey().getPreferenceId(), UserPreferenceEnum.UNKNOWN.getPreferenceKey());
        Assert.assertEquals(result.getValue(), "value1");
    }

    @Test
    public void testUpdateNoValues() {
        UserPreferenceDto result = dtoService.update(userPreferenceDto, serviceErrors);
        Assert.assertEquals(result, null);
    }

    @Test
    public void testFindNull() {
        UserPreferenceDto result = dtoService.find(new UserPreferenceDtoKey("job1", "pref1"), serviceErrors);
        Assert.assertEquals(result, null);
    }
}

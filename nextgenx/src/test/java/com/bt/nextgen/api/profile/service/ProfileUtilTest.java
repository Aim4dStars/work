package com.bt.nextgen.api.profile.service;

import com.bt.nextgen.api.profile.v1.service.ProfileUtil;
import com.bt.nextgen.core.repository.UserPreference;
import com.bt.nextgen.core.repository.UserPreferenceKey;
import com.bt.nextgen.core.repository.UserPreferenceRepository;
import com.btfin.panorama.core.security.profile.Profile;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.service.avaloq.UserCacheService;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Created by L075208 on 10/01/2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class ProfileUtilTest {
    @InjectMocks
    ProfileUtil profileUtil;

    @Mock
    UserProfileService userProfileService;

    @Mock
    UserPreferenceRepository userPreferenceRepository;

    @Mock
    UserCacheService userCacheService;


    UserPreference preference;

    @Test
    public void testGetProfileID() {
        preference = new UserPreference();
        UserPreferenceKey key = new UserPreferenceKey();
        key.setPreferenceId("12345");
        key.setUserId("1234");
        preference.setKey(key);
        preference.setValue("value");
        Profile profile = mock(Profile.class);
        Mockito.when(profile.getGcmId()).thenReturn("1234");
        Mockito.when(profile.getCurrentProfileId()).thenReturn("1234");
        Mockito.when(userPreferenceRepository.find(Mockito.anyString(), Mockito.anyString())).thenReturn(preference);
        Mockito.when(userProfileService.getEffectiveProfile()).thenReturn(profile);

        String profileId = profileUtil.getProfileId();

        Assert.assertNotNull(profileId);
        Assert.assertEquals(profileId, "1234");

    }

    @Test
    public void testGetActiveProfileCacheKey() {
        when(userCacheService.getActiveProfileCacheKey()).thenReturn("1234");
        final String key = profileUtil.getActiveProfileCacheKey();
        verify(userCacheService, times(1)).getActiveProfileCacheKey();
        assertEquals(key, "1234");
    }
}

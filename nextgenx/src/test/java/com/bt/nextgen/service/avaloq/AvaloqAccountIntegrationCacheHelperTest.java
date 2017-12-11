package com.bt.nextgen.service.avaloq;

import com.btfin.panorama.core.security.avaloq.AvaloqBankingAuthorityService;
import com.bt.nextgen.service.avaloq.account.AvaloqAccountIntegrationCacheHelper;
import static com.bt.nextgen.service.avaloq.account.AvaloqAccountIntegrationCacheHelper.Invoker;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.userprofile.JobProfileIdentifier;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.when;

/**
 * Tests {@link AvaloqAccountIntegrationCacheHelper}.
 */
@RunWith(MockitoJUnitRunner.class)
public class AvaloqAccountIntegrationCacheHelperTest {
    @InjectMocks
    private AvaloqAccountIntegrationCacheHelper cacheHelper;

    @Mock
    private AvaloqBankingAuthorityService userProfileService;

    @Mock
    private JobProfileIdentifier jobProfileIdentifier;

    @Mock
    private AccountKey accountKey;


    @Test
    public void getActiveProfileCacheKeyWithNullActiveProfile() {
        when(userProfileService.getActiveJobProfile()).thenReturn(null);
        assertThat(cacheHelper.getActiveProfileCacheKey(), equalTo(""));
    }

    @Test
    public void getActiveProfileCacheKey() {
        final String profileId = "profile_123";

        when(userProfileService.getActiveJobProfile()).thenReturn(jobProfileIdentifier);
        when(jobProfileIdentifier.getProfileId()).thenReturn(profileId);
        assertThat(cacheHelper.getActiveProfileCacheKey(), equalTo(profileId));
    }

    @Test
    public void loadWrapAccountDetail() {
        final String result = "abc123";
        final Invoker<String> invoker = new Invoker<String>() {
            @Override
            public String invoke() {
                return result;
            }
        };

        assertThat(cacheHelper.loadWrapAccountDetail(accountKey, invoker), equalTo(result));
    }
}

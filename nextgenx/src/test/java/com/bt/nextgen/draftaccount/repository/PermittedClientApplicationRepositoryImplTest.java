package com.bt.nextgen.draftaccount.repository;

import com.btfin.panorama.core.security.profile.UserProfile;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.bt.nextgen.service.integration.accountactivation.OnboardingApplicationKey;
import com.btfin.panorama.service.integration.broker.BrokerIdentifier;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collection;
import java.util.Date;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PermittedClientApplicationRepositoryImplTest {
    @Mock
    ClientApplicationRepository clientApplicationRepository;

    @Mock
    UserProfileService userProfileService;

    @Mock
    BrokerIntegrationService brokerService;

    @InjectMocks
    PermittedClientApplicationRepositoryImpl repository;

    @Test
    public void findNonActiveApplicationsBetweenDates_ShouldOnlyReturnApplicationsThatTheLoggedInUserCanAccess() throws Exception {
        UserProfile userProfile = mock(UserProfile.class);
        Collection<BrokerIdentifier> advisers = mock(Collection.class);

        when(userProfileService.getActiveProfile()).thenReturn(userProfile);
        when(brokerService.getAdvisersForUser(eq(userProfile), any(FailFastErrorsImpl.class))).thenReturn(advisers);

        Date from = new Date();
        Date to = new Date();
        repository.findNonActiveApplicationsBetweenDates(from, to);

        verify(clientApplicationRepository, times(1)).findNonActiveApplicationsBetweenDates(from, to, advisers);
    }

    @Test
    public void find_ShouldOnlyReturnApplicationIfTheLoggedInUserCanAccessIt() throws Exception {
        Long clientApplicationId = 1L;
        UserProfile userProfile = mock(UserProfile.class);
        Collection<BrokerIdentifier> advisers = mock(Collection.class);

        when(userProfileService.getActiveProfile()).thenReturn(userProfile);
        when(brokerService.getAdvisersForUser(eq(userProfile), any(FailFastErrorsImpl.class))).thenReturn(advisers);

        repository.find(clientApplicationId);

        verify(clientApplicationRepository, times(1)).find(clientApplicationId, advisers);
    }

    @Test
    public void save_ShouldSaveToTheClientApplicationRepository() {
        ClientApplication clientApplication = mock(ClientApplication.class);
        repository.save(clientApplication);

        verify(clientApplicationRepository, times(1)).save(clientApplication);
    }

    @Test
    public void findByOnboardingApplicationKey_ShouldOnlyReturnTheApplication_IfTheLoggedInUserCanAccessIt() throws Exception {
        OnboardingApplicationKey applicationKey = mock(OnboardingApplicationKey.class);
        UserProfile userProfile = mock(UserProfile.class);
        Collection<BrokerIdentifier> advisers = mock(Collection.class);

        when(userProfileService.getActiveProfile()).thenReturn(userProfile);
        when(brokerService.getAdvisersForUser(eq(userProfile), any(FailFastErrorsImpl.class))).thenReturn(advisers);

        repository.findByOnboardingApplicationKey(applicationKey);

        verify(clientApplicationRepository, times(1)).findByOnboardingApplicationKey(applicationKey, advisers);
    }
}
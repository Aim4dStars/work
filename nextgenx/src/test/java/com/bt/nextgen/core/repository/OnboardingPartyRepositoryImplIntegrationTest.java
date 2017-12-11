package com.bt.nextgen.core.repository;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import com.bt.nextgen.config.BaseSecureIntegrationTest;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class OnboardingPartyRepositoryImplIntegrationTest extends BaseSecureIntegrationTest {

    @Autowired
    private OnboardingApplicationRepository applicationRepository;

    @Autowired
    private OnboardingPartyRepository partyRepository;

    @Test
    @Transactional
    @Rollback(true)
    public void testLoadedOnboardingPartyIsSameAsPersisted() throws Exception {
        OnBoardingApplication application = new OnBoardingApplication(OnboardingApplicationStatus.processing, "TEST_AVLQ_ID"); // any status
        applicationRepository.save(application);
        OnboardingParty party = new OnboardingParty(1,application.getKey().getId(),"TEST_GCM");
        party.setStatus(OnboardingPartyStatus.NotificationSent);
        partyRepository.save(party);

        OnboardingParty savedParty = partyRepository.find(party.getOnboardingPartyKey());

        assertThat(savedParty.getStatus(), equalTo(party.getStatus()));
        assertThat(savedParty.getGcmPan(), equalTo(party.getGcmPan()));
    }

    @Test
    @Transactional
    @Rollback(true)
    public void findByGCMAndApplicationIdShouldReturnCorrectMatch() throws Exception {
        OnBoardingApplication application = new OnBoardingApplication(OnboardingApplicationStatus.processing, "TEST_AVLQ_ID"); // any status
        applicationRepository.save(application);
        OnboardingParty party = new OnboardingParty(1,application.getKey().getId(),"TEST_GCM");
        party.setStatus(OnboardingPartyStatus.NotificationSent);
        partyRepository.save(party);

        OnboardingParty onboardingParty = partyRepository.findByGCMAndApplicationId("TEST_GCM", application.getKey().getId());
        assertNotNull(onboardingParty);
        assertThat(onboardingParty.getStatus(), equalTo(OnboardingPartyStatus.NotificationSent));
    }

	@Test
	@Transactional
	@Rollback(true)
	public void findByGCMIdShouldReturnCorrectMatch() throws Exception
	{
	    OnBoardingApplication application = new OnBoardingApplication(OnboardingApplicationStatus.processing, "TEST_AVLQ_ID"); // any status
		applicationRepository.save(application);
		OnboardingParty party = new OnboardingParty(1, application.getKey().getId(), "TEST_GCM");
		party.setStatus(OnboardingPartyStatus.DeviceRegistrationFailed);
		party.setFailureMessage("Not able to Register the device");
		partyRepository.save(party);

	List<OnboardingParty> onboardingParties = partyRepository
		.findByGCMId("TEST_GCM");
	OnboardingParty onboardingParty = onboardingParties.get(0);
		assertNotNull(onboardingParty);
		assertThat(onboardingParty.getStatus(), equalTo(OnboardingPartyStatus.DeviceRegistrationFailed));
		assertThat(onboardingParty.getFailureMessage(), equalTo("Not able to Register the device"));
	}
}
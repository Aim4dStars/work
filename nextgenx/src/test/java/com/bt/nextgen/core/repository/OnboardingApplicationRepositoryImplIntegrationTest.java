package com.bt.nextgen.core.repository;

import com.bt.nextgen.config.BaseSecureIntegrationTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

@TransactionConfiguration(defaultRollback = true)
public class OnboardingApplicationRepositoryImplIntegrationTest extends BaseSecureIntegrationTest {

    @Autowired
    private OnboardingApplicationRepository applicationRepository;

    @Test
    @Transactional(value = "springJpaTransactionManager")
    @Rollback(true)
    public void loadedOnboardingApplicationShouldHaveSameAttributeValuesAsPersistedOne() throws Exception {

        OnBoardingApplication onboardingApplication = new OnBoardingApplication(OnboardingApplicationStatus.draft, "order_id");
        applicationRepository.save(onboardingApplication);
        assertNotNull(onboardingApplication.getKey());
        OnBoardingApplication savedOnBoardingApplication = applicationRepository.find(onboardingApplication.getKey());
        assertThat(savedOnBoardingApplication.getKey(), is(onboardingApplication.getKey()));
        assertThat(savedOnBoardingApplication.getStatus(), is(OnboardingApplicationStatus.draft));
        assertThat(savedOnBoardingApplication.getAvaloqOrderId(), is("order_id"));
        savedOnBoardingApplication.setStatus(OnboardingApplicationStatus.active);
        applicationRepository.update(savedOnBoardingApplication);
        OnBoardingApplication updateOnBoardingApplication = applicationRepository.find(onboardingApplication.getKey());
        assertThat(updateOnBoardingApplication.getStatus(), is(OnboardingApplicationStatus.active));
    }

}
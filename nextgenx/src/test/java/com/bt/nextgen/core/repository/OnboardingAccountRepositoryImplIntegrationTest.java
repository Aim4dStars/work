package com.bt.nextgen.core.repository;


import com.bt.nextgen.config.BaseSecureIntegrationTest;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.accountactivation.OnboardingApplicationKey;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

@TransactionConfiguration(defaultRollback = true)

public class OnboardingAccountRepositoryImplIntegrationTest extends BaseSecureIntegrationTest {

    @Autowired
    OnboardingApplicationRepositoryImpl applicationRepository;

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    OnboardingAccountRepositoryImpl repository;

    @Test
    @Transactional(value = "springJpaTransactionManager")
    @Rollback(true)
    public void saveShouldUpdateOnboardingAccountRepository() throws Exception {

        OnBoardingApplication onBoardingApplication = new OnBoardingApplication();
        applicationRepository.save(onBoardingApplication);

        OnboardingApplicationKey onboardingApplicationKey = onBoardingApplication.getKey();
        long onboardingAccountSeq = 222L;

        OnboardingAccount onboardingAccount = new OnboardingAccount(onboardingAccountSeq, onboardingApplicationKey);
        String accountNumber = "MyAccountNumber";
        onboardingAccount.setAccountNumber(accountNumber);

        entityManager.persist(onboardingAccount);

        OnboardingAccount savedOnboardingAccount = repository.findByAccountNumber(accountNumber);
        assertEquals(onboardingApplicationKey.getId(), savedOnboardingAccount.getOnboardingKey().getOnboardingApplicationId());
        assertEquals((Long) onboardingAccountSeq, savedOnboardingAccount.getOnboardingKey().getOnboardingAccountSeq());
    }

    @Test
    @Transactional(value = "springJpaTransactionManager")
    @Rollback(true)
    public void shouldReturnOnboardingAccountByOnboardingAppId() throws Exception {
        storeRecordInDbAndGetOnboardingAppId("First Key");
        OnboardingApplicationKey appKey2 = storeRecordInDbAndGetOnboardingAppId("Second Key");

        OnboardingAccount savedOnboardingAccount = repository.findByOnboardingApplicationId(appKey2);
        assertEquals(appKey2.getId(), savedOnboardingAccount.getOnboardingKey().getOnboardingApplicationId());
        assertEquals("Second Key", savedOnboardingAccount.getAccountNumber());
    }

    @Test
    @Transactional(value = "springJpaTransactionManager")
    @Rollback(true)
    public void shouldReturnAllOnboardingAccountByOnboardingAppId() throws Exception {
        OnboardingApplicationKey appKey1 = storeRecordInDbAndGetOnboardingAppId("First Key");
        OnboardingApplicationKey appKey2 = storeRecordInDbAndGetOnboardingAppId("Second Key");
        OnboardingApplicationKey appKey3 = storeRecordInDbAndGetOnboardingAppId("Third Key");
        List<Long> ids = Arrays.asList(appKey1.getId(), appKey2.getId(), appKey3.getId());

        List<OnboardingAccount> accounts = repository.findByOnboardingApplicationIds(ids);

        assertThat("Should return a account for each key", accounts.size(), is(3));
        assertTrue("Should contain all the ids", filterIds(accounts).containsAll(ids));
    }

    @Test
    @Transactional(value = "springJpaTransactionManager")
    @Rollback(true)
    public void shouldReturnAllOnboardingAccountByOnboardingAppIdWhichIsNoneWhenNoIds() throws Exception {
        List<Long> ids = Collections.emptyList();
        List<OnboardingAccount> accounts = repository.findByOnboardingApplicationIds(ids);
        assertThat("Should return an empty list", accounts.size(), is(0));
    }

    private List<Long> filterIds(List<OnboardingAccount> accounts) {
        List<Long> ids = new ArrayList<>();
        for (OnboardingAccount account : accounts) {
            ids.add(account.getOnboardingKey().getOnboardingApplicationId());
        }
        return ids;
    }

    private OnboardingApplicationKey storeRecordInDbAndGetOnboardingAppId(String accountNumberValue){
        OnBoardingApplication onBoardingApplication = new OnBoardingApplication();
        applicationRepository.save(onBoardingApplication);

        AccountKey accountkey = AccountKey.valueOf(accountNumberValue);
        OnboardingApplicationKey onboardingApplicationKey = onBoardingApplication.getKey();
        long onboardingAccountSeq = 222L;

        OnboardingAccount onboardingAccount = new OnboardingAccount(onboardingAccountSeq, onboardingApplicationKey);
        onboardingAccount.setAccountNumber(accountkey.getId());

        entityManager.persist(onboardingAccount);
        return onBoardingApplication.getKey();
    }

}

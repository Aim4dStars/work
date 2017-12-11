package com.bt.nextgen.core.repository;

import com.bt.nextgen.service.integration.accountactivation.OnboardingApplicationKey;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public class OnboardingApplicationRepositoryImpl implements OnboardingApplicationRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public OnBoardingApplication find(OnboardingApplicationKey key) {
        return entityManager.find(OnBoardingApplication.class, key.getId());
    }

    @Override
    public OnBoardingApplication save(OnBoardingApplication onBoardingApplication) {
        entityManager.persist(onBoardingApplication);
        return onBoardingApplication;
    }
    @Transactional(value = "springJpaTransactionManager")
    @Override
    public OnBoardingApplication update(OnBoardingApplication onBoardingApplication) {
        OnBoardingApplication mergeOnBoardingApplication = entityManager.merge(onBoardingApplication);
    entityManager.flush();
    return mergeOnBoardingApplication;
    }
}

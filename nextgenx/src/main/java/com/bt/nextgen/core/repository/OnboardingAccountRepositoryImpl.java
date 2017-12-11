package com.bt.nextgen.core.repository;

import com.bt.nextgen.service.integration.accountactivation.OnboardingApplicationKey;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.Collections;
import java.util.List;

@Repository
public class OnboardingAccountRepositoryImpl implements OnboardingAccountRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public OnboardingAccount findByAccountNumber(String accountNumber) {
        TypedQuery<OnboardingAccount> query = entityManager.createQuery(
                "SELECT a FROM OnboardingAccount a WHERE a.accountNumber = :id", OnboardingAccount.class);
        return query.setParameter("id", accountNumber).getSingleResult();
    }

    @Override
    public OnboardingAccount findByOnboardingApplicationId(OnboardingApplicationKey key) {
        TypedQuery<OnboardingAccount> query = entityManager.createQuery(
                "SELECT a FROM OnboardingAccount a WHERE a.onboardingKey.onboardingApplicationId = :id", OnboardingAccount.class);
        return query.setParameter("id", key.getId()).getSingleResult();
    }

    @Override
    public List<OnboardingAccount> findByOnboardingApplicationIds(List<Long> keys) {
        Assert.notNull(keys);
        if (keys.isEmpty()) {
            return Collections.emptyList();
        }
        TypedQuery<OnboardingAccount> query = entityManager.createQuery(
                "SELECT a FROM OnboardingAccount a WHERE a.onboardingKey.onboardingApplicationId in :id", OnboardingAccount.class);
        return query.setParameter("id", keys).getResultList();
    }
}

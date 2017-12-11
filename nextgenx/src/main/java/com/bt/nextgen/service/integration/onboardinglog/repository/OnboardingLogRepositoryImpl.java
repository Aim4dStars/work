package com.bt.nextgen.service.integration.onboardinglog.repository;


import com.bt.nextgen.service.integration.onboardinglog.model.OnboardingLog;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import java.util.List;

@Repository("OnboardingLogRepository")
@Transactional(value = "springJpaTransactionManager")
public class OnboardingLogRepositoryImpl implements OnboardingLogRepository
{
	@PersistenceContext
	private EntityManager entityManager;

    @Override
    public List<OnboardingLog> findAll() {
        // Return all events that require logging.
        TypedQuery<OnboardingLog> query = entityManager.createQuery("SELECT o FROM OnboardingLog o WHERE o.hasBeenLogged = 0",
                OnboardingLog.class);
        query.setLockMode(LockModeType.PESSIMISTIC_WRITE);
        return query.getResultList();
	}
}
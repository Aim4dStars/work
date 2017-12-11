package com.bt.nextgen.core.repository;

import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import java.util.List;

@Repository
public class OnboardingPartyRepositoryImpl implements OnboardingPartyRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public OnboardingParty find(OnboardingParty.OnboardingPartyKey key){
        return entityManager.find(OnboardingParty.class, key);
    }

    @Override
    public OnboardingParty save(OnboardingParty party){
        entityManager.persist(party);
        return party;
    }

    @Override
    public OnboardingParty update(OnboardingParty onboardingParty) {
	entityManager.merge(onboardingParty);
	entityManager.flush();
	return onboardingParty;
    }

    @Override
    public OnboardingParty findByGCMAndApplicationId(String gcmId, Long applicationId) {
        TypedQuery<OnboardingParty> query = entityManager.createQuery(
                "SELECT a FROM OnboardingParty a WHERE a.gcmPan = :gcm and a.onboardingPartyKey.onboardingApplicationId = :applicationId", OnboardingParty.class);
        return query.setParameter("gcm", gcmId).setParameter("applicationId",applicationId).getSingleResult();
    }

    @Override
    public List<OnboardingParty> findOnboardingPartiesByApplicationIds(List<Long> applicationIds) {

        TypedQuery<OnboardingParty> query = entityManager.createQuery(
                "SELECT a FROM OnboardingParty a WHERE a.onboardingPartyKey.onboardingApplicationId in :applicationIds", OnboardingParty.class);
        return query.setParameter("applicationIds",applicationIds).getResultList();
    }

	@Override
    public List<OnboardingParty> findByGCMId(String gcmId)
	{
		TypedQuery <OnboardingParty> query = entityManager.createQuery("SELECT a FROM OnboardingParty a WHERE a.gcmPan = :gcm ",
			OnboardingParty.class);
	return query.setParameter("gcm", gcmId).getResultList();
	}
}

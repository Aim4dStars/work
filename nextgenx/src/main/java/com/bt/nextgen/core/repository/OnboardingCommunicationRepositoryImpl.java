package com.bt.nextgen.core.repository;

import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import java.util.Date;
import java.util.List;

@Repository
public class OnboardingCommunicationRepositoryImpl implements OnboardingCommunicationRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public OnboardingCommunication save(OnboardingCommunication communication) {
        communication.setLastModifiedDate(new Date());
        entityManager.persist(communication);
        return communication;
    }

    @Override
    public OnboardingCommunication find(String communicationId) {
        return entityManager.find(OnboardingCommunication.class, communicationId);
    }

    @Override
    public void flush() {
        entityManager.flush();
    }

    @Override
    public List<OnboardingCommunication> findByApplicationIdAndGCMId(Long onboardingApplicationId, String gcmPan) {
        TypedQuery<OnboardingCommunication> query = entityManager.createQuery(
                "SELECT a FROM OnboardingCommunication a WHERE a.onboardingApplicationId =:id and a.gcmPan =:gcmPan", OnboardingCommunication.class);
        return query.setParameter("id", onboardingApplicationId).setParameter("gcmPan", gcmPan).getResultList();
    }

	@Override
	public List <OnboardingCommunication> findCommunicationsByGcmId(String gcmPan)
	{
		TypedQuery <OnboardingCommunication> query = entityManager.createQuery("SELECT a FROM OnboardingCommunication a WHERE  a.gcmPan =:gcmPan",
			OnboardingCommunication.class);
		return query.setParameter("gcmPan", gcmPan).getResultList();
	}
}

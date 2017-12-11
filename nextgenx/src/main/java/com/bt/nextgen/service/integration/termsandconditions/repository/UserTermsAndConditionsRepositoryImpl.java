package com.bt.nextgen.service.integration.termsandconditions.repository;


import com.bt.nextgen.service.integration.termsandconditions.model.UserTermsAndConditions;
import com.bt.nextgen.service.integration.termsandconditions.model.UserTermsAndConditionsKey;
import com.bt.nextgen.service.integration.user.UserKey;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import java.util.List;

@Repository("UserTermsAndConditionsRepository")
public class UserTermsAndConditionsRepositoryImpl implements UserTermsAndConditionsRepository
{
	@PersistenceContext
	private EntityManager entityManager;


	@Override
    public List<UserTermsAndConditions> search(UserKey userKey)
	{
        TypedQuery<UserTermsAndConditions> query = entityManager.createQuery(
                "SELECT c FROM UserTermsAndConditions c WHERE c.userTermsAndConditionsKey.gcmId in :id",
                UserTermsAndConditions.class);
        return query.setParameter("id", userKey.getId()).getResultList();
	}

	@Override
	@Transactional(value = "springJpaTransactionManager")
    public void save(UserTermsAndConditions userTermsAndConditions)
	{
        entityManager.merge(userTermsAndConditions);
		entityManager.flush();
	}

    @Override
    public UserTermsAndConditions find(UserTermsAndConditionsKey key) {
        return entityManager.find(UserTermsAndConditions.class, key);
    }
}
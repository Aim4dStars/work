package com.bt.nextgen.service.integration.termsandconditions.repository;


import com.bt.nextgen.service.integration.termsandconditions.model.TermsAndConditions;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import java.util.List;

@Repository("TermsAndConditionsRepository")
public class TermsAndConditionsRepositoryImpl implements TermsAndConditionsRepository
{
	@PersistenceContext
	private EntityManager entityManager;

	@Override
    public List<TermsAndConditions> findAll()
	{
        TypedQuery<TermsAndConditions> query = entityManager.createQuery("SELECT c FROM TermsAndConditions c",
                TermsAndConditions.class);
        return query.getResultList();
	}
}
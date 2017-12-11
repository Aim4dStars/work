package com.bt.nextgen.payments.repository;

import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public class BsbCodeRepositoryImpl implements BsbCodeRepository
{
	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public Bsb load(final String bsbCode)
	{
		return entityManager.find(Bsb.class, bsbCode);
	}
}

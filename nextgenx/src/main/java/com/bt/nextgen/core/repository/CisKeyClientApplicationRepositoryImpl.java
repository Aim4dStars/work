package com.bt.nextgen.core.repository;

import ch.lambdaj.Lambda;
import ch.lambdaj.function.convert.Converter;
import com.bt.nextgen.draftaccount.repository.ClientApplication;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

import static ch.lambdaj.Lambda.on;

/**
 * Created by F058391 on 19/11/2015.
 */
@Repository
public class CisKeyClientApplicationRepositoryImpl implements CisKeyClientApplicationRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<ClientApplication> findClientApplicationsForCisKey(String cisKey) {
        TypedQuery<CisKeyClientApplication> query = entityManager.createQuery(
                "SELECT c FROM CisKeyClientApplication c WHERE c.cisClientApplicationId.cisKey in :id", CisKeyClientApplication.class);
        List<CisKeyClientApplication> cisKeyClientApplications = query.setParameter("id", cisKey).getResultList();
        return Lambda.collect(cisKeyClientApplications, on(CisKeyClientApplication.class).getCisClientApplicationId().getClientApplication());
    }

    @Override
    public void save(CisKeyClientApplication cisKeyClientApplication) {
        entityManager.persist(cisKeyClientApplication);
    }
}

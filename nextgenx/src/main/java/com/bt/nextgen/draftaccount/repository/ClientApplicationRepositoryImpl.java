package com.bt.nextgen.draftaccount.repository;

import static ch.lambdaj.Lambda.convert;
import static com.bt.nextgen.core.toggle.FeatureToggles.FILTER_DIRECT_ACCTS;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.bt.nextgen.core.toggle.FeatureToggles;
import com.bt.nextgen.core.toggle.FeatureTogglesService;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.bt.nextgen.service.integration.accountactivation.OnboardingApplicationKey;
import com.btfin.panorama.service.integration.broker.BrokerIdentifier;

import ch.lambdaj.function.convert.Converter;

/**
 * Do not use this repository directly. Use the PermittedClientApplicationRepository, it will ensure that you can access the
 * records that you are supposed to be able to access. This interface is the 'raw' interface where you have to supply
 * the correct broker identifiers.
 */
@Repository
public class ClientApplicationRepositoryImpl implements ClientApplicationRepository {

    /** Convert BrokerIdentifier instances to their key values. */
    private static final Converter<BrokerIdentifier, String> TO_BROKER_ID = new Converter<BrokerIdentifier, String>() {
        @Override
        public String convert(BrokerIdentifier identifier) {
            return identifier.getKey().getId();
        }
    };

    @Autowired
    private FeatureTogglesService togglesService;


    // TODO: Remove this Feature toggle for the onboardingFilterDirectAccounts, due in MAY 2017.
    private static final String FILTER_OUT_DIRECT_ACCTS =
            " AND NOT (a.formData like '%\"applicationOrigin\":\"WestpacLive\"%') ";


    /*
        This method  is created for testing purpose.
     */
    public void setTogglesService(FeatureTogglesService togglesService) {
        this.togglesService = togglesService;
    }


    /*
        As Direct accounts are active once they are submitted. They  need to filtered from unapproved application report.
        TODO: To be removed once the  onboardingFilterDirectAccounts goes live
        @return direct account filter
     */
    private String getDirectAccountsFilter(){
        final FeatureToggles toggles = togglesService.findOne(new FailFastErrorsImpl());
        final StringBuilder filter = new StringBuilder();
        if(toggles.getFeatureToggle(FILTER_DIRECT_ACCTS)){
            filter.append(FILTER_OUT_DIRECT_ACCTS);
        }
        return filter.toString();
    }

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Long save(ClientApplication draftAccount) {
        entityManager.persist(draftAccount);
        return draftAccount.getId();
    }

    @Override
    public ClientApplication find(Long id, Collection<? extends BrokerIdentifier> adviserIds) {

        TypedQuery<ClientApplication> query = entityManager.createQuery(
                "SELECT a FROM ClientApplication a " +
                        "WHERE a.status <> :status " +
                        "and a.id = :id " +
                        "AND a.adviserPositionId IN :advisers ", ClientApplication.class);
        return query
                .setParameter("id", id)
                .setParameter("status", ClientApplicationStatus.deleted)
                .setParameter("advisers", getBrokerIds(adviserIds))
                .getSingleResult();
    }

    /**
     * To find the ClientApplication based on id only. So that a service operator user can search ClientApplication.
     * Method added for US3953.
     */
    @Override
    public ClientApplication find(Long id)
    {
    	  TypedQuery<ClientApplication> query = entityManager.createQuery(
              "SELECT a FROM ClientApplication a " +
                      "WHERE a.status <> :status " +
                      "and a.id = :id "
                     , ClientApplication.class);
      return query
              .setParameter("id", id)
              .setParameter("status", ClientApplicationStatus.deleted)
              .getSingleResult();
    }

    /**
     * To find the latest draft ClientApplications and return a @count of it.
     */
    @Override
    public List<ClientApplication> findCertainNumberOfMostRecentDraftAccounts(Integer count, Collection<? extends BrokerIdentifier> adviserIds)
    {
        TypedQuery<ClientApplication> query = entityManager.createQuery(
                "SELECT a FROM ClientApplication a " +
                        "WHERE a.status = :status " +
                        "AND a.adviserPositionId IN :advisers " +
                        "ORDER BY a.lastModifiedAt desc"
                , ClientApplication.class);
        return query
                .setParameter("status", ClientApplicationStatus.draft)
                .setParameter("advisers", getBrokerIds(adviserIds))
                .setMaxResults(count)
                .getResultList();
    }

    /**
     * To find the applications that are not active or deleted.
     */
    @Override
    public List<ClientApplication> findNonActiveApplicationsBetweenDates(Date from, Date to) {
        TypedQuery<ClientApplication> query = entityManager.createQuery(
                "SELECT a FROM ClientApplication a LEFT JOIN FETCH a.onboardingApplication " +
                        "WHERE a.lastModifiedAt >= :fromDate " +
                        "AND a.lastModifiedAt < :toDate " +
                        getDirectAccountsFilter() +
                        "AND a.status NOT IN :statuses " , ClientApplication.class);
        return query
                .setParameter("fromDate", from, TemporalType.TIMESTAMP)
                .setParameter("toDate", to, TemporalType.TIMESTAMP)
                .setParameter("statuses", EnumSet.of(ClientApplicationStatus.deleted, ClientApplicationStatus.active))
                .getResultList();
    }

    @Override
    public List<ClientApplication> findNonActiveApplicationsBetweenDates(Date from, Date to, Collection<? extends BrokerIdentifier> adviserIds) {
        if (adviserIds.isEmpty()) {
            return Collections.emptyList();
        }
        TypedQuery<ClientApplication> query = entityManager.createQuery(
                "SELECT a FROM ClientApplication a LEFT JOIN FETCH a.onboardingApplication " +
                        "WHERE a.lastModifiedAt >= :fromDate " +
                        "AND a.lastModifiedAt < :toDate " +
                        "AND a.status NOT IN :statuses " +
                        "AND a.adviserPositionId IN :advisers", ClientApplication.class);
        return query
                .setParameter("fromDate", from, TemporalType.TIMESTAMP)
                .setParameter("toDate", to, TemporalType.TIMESTAMP)
                .setParameter("statuses", EnumSet.of(ClientApplicationStatus.deleted, ClientApplicationStatus.active))
                .setParameter("advisers",getBrokerIds(adviserIds))
                .getResultList();
    }

    /**
     * To get count of draft ClientApplications for the adviser.
     */
    @Override
    public Long getNumberOfDraftAccounts(Collection<? extends BrokerIdentifier> adviserIds)
    {
        TypedQuery<Long> query = entityManager.createQuery(
                "SELECT count(a) FROM ClientApplication a " +
                        "WHERE a.status = :status " +
                        "AND a.adviserPositionId IN :advisers "
                , Long.class);
        return query
                .setParameter("status", ClientApplicationStatus.draft)
                .setParameter("advisers", getBrokerIds(adviserIds))
                .getSingleResult();
    }

    @Override
    public ClientApplication findByOnboardingApplicationKey(OnboardingApplicationKey key, Collection<? extends BrokerIdentifier> adviserIds) {
        TypedQuery<ClientApplication> query = entityManager.createQuery(
                "SELECT a FROM ClientApplication a " +
                        "WHERE a.onboardingApplication.id = :id " +
                        "AND a.adviserPositionId IN :advisers", ClientApplication.class);

        return query.setParameter("id", key.getId())
                .setParameter("advisers", getBrokerIds(adviserIds))
                .getSingleResult();
    }

    @Override
    public ClientApplication findByOnboardingApplicationKey(OnboardingApplicationKey key) {
        TypedQuery<ClientApplication> query = entityManager.createQuery(
                "SELECT a FROM ClientApplication a " +
                        "WHERE a.onboardingApplication.id = :id ", ClientApplication.class);

        return query.setParameter("id", key.getId())
                .getSingleResult();
    }

    private static Collection<String> getBrokerIds(Collection<? extends BrokerIdentifier> advisers){
        return convert(advisers, TO_BROKER_ID);
    }
}


package com.bt.nextgen.core.repository;

import ch.lambdaj.function.convert.Converter;
import com.btfin.panorama.core.security.avaloq.accountactivation.ApplicationStatus;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.accountactivation.ApplicationDocument;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Repository("subscriptionsRepository")
public class SubscriptionsRepositoryImpl implements SubscriptionsRepository {

    //private static final Logger logger = LoggerFactory.getLogger(SubscriptionsRepositoryImpl.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public SubscriptionDetails find(String docId) {
        return entityManager.find(SubscriptionDetails.class, docId);
    }

    @Override
    public List<SubscriptionDetails> findAll(AccountKey accountKey) {
        TypedQuery<SubscriptionDetails> query = entityManager.createQuery(
                "SELECT a FROM SubscriptionDetails a WHERE a.accountId =:accountid", SubscriptionDetails.class);
        return query.setParameter("accountid", accountKey.getId()).getResultList();
    }

    @Override
    public List<SubscriptionDetails> findAll() {
        Query query = entityManager.createQuery("SELECT a FROM SubscriptionDetails a");
        return query.getResultList();
    }

    /**
     * Partitions the accountID list to subset list of count 999 if more than 1000
     * ListSize is the number of times the accountList has to be split
     * List Size calculation is to avoid the div and mod to handle the end index of the list
     *
     * @param accountIdsMap
     * @param accountIds
     *
     * @return String
     */
    protected String getInnerQuery(final Map<String, List<String>> accountIdsMap, List<String> accountIds) {
        final int size = 999;
        final int listSize = (accountIds.size() + size - 1) / size; //AccountIds is mandatory
        StringBuilder innerQueryStatement = new StringBuilder(" ( (");

        for (int i = 0; i < listSize; i++) {
            int start = i * size;
            int end = Math.min(start + size, accountIds.size());
            accountIdsMap.put("accountIds" + i, accountIds.subList(start, end));
            if (i != 0) {
                innerQueryStatement.append(") or (");
            }
            innerQueryStatement.append(" a.accountId in (:accountIds" + i + ")");
        }
        innerQueryStatement.append(" ) ) ");

        return innerQueryStatement.toString();
    }

    /**
     * Adding accountIds in where clause dynamically to resolve the oracle issue of not able to handle more than 1000 objects
     * @param status
     * @param accountIds
     * @return
     */
    @Override
    public List<String> findAllByStatus(SubscriptionStatus status, List<String> accountIds) {
        String queryStatement = "SELECT a.docId FROM SubscriptionDetails a WHERE a.status =:status and ";
        final Map<String, List<String>> accountIdsMap = new HashMap<>();
        String innerQueryStatement = getInnerQuery(accountIdsMap, accountIds);
        TypedQuery<String> query = entityManager.createQuery(
                queryStatement + innerQueryStatement,
                String.class);

        query.setParameter("status", status.name());

        Iterator<String> accountIdIterator = accountIdsMap.keySet().iterator();
        while (accountIdIterator.hasNext()) {
            String key = accountIdIterator.next();
            query.setParameter(key, accountIdsMap.get(key));
        }

        return query.getResultList();
    }

    public SubscriptionDetails save(ApplicationDocument subscriptionDetails) {
        return save(getConverter().convert(subscriptionDetails));
    }

    @Override
    @Transactional("springJpaTransactionManager")
    public SubscriptionDetails save(SubscriptionDetails subscriptionDetails) {
        entityManager.persist(subscriptionDetails);
        entityManager.flush();
        return subscriptionDetails;
    }

    @Override
    @Transactional("springJpaTransactionManager")
    public SubscriptionDetails update(SubscriptionDetails subscriptionDetails) {
        SubscriptionDetails updatedDetails = entityManager.merge(subscriptionDetails);
        entityManager.flush();
        return updatedDetails;
    }

    @Transactional("springJpaTransactionManager")
    public List<SubscriptionDetails> update(List<SubscriptionDetails> subscriptionDetails) {
        List<SubscriptionDetails> details = new ArrayList<>();
        for (SubscriptionDetails detail : subscriptionDetails) {
            details.add(update(detail));
        }
        return details;
    }

    private static SubscriptionStatus getStatus(ApplicationDocument applicationDocument) {
        if (ApplicationStatus.RUN_CANCEL.equals(applicationDocument.getAppState()) ||
                ApplicationStatus.DISCARDED.equals(applicationDocument.getAppState())) {
            return SubscriptionStatus.CANCELLED;
        }
        else if (ApplicationStatus.DONE.equals(applicationDocument.getAppState())) {
            return SubscriptionStatus.SUBSCRIBED;
        }
        else {
            return SubscriptionStatus.INPROGRESS;
        }
    }

    private Converter<ApplicationDocument, SubscriptionDetails> getConverter() {
        return new Converter<ApplicationDocument, SubscriptionDetails>() {
            @Override
            public SubscriptionDetails convert(ApplicationDocument from) {
                SubscriptionDetails subscriptionDetails = new SubscriptionDetails();
                subscriptionDetails.setDocId(from.getAppNumber());
                subscriptionDetails.setStatus(getStatus(from).name());
                subscriptionDetails.setSubscriptionType(from.getOrderType());
                subscriptionDetails.setAccountId(from.getBpid().getId());
                return subscriptionDetails;
            }
        };
    }
}
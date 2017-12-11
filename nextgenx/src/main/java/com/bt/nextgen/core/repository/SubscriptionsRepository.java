package com.bt.nextgen.core.repository;

import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.accountactivation.ApplicationDocument;

import java.util.List;

/**
 *
 */
public interface SubscriptionsRepository {

    SubscriptionDetails find(String docId);

    List<SubscriptionDetails> findAll();

    List<SubscriptionDetails> findAll(AccountKey accountKey);

    List<String> findAllByStatus(SubscriptionStatus status, List<String> accountIds);

    SubscriptionDetails save(ApplicationDocument subscriptionsDetails);

    SubscriptionDetails save(SubscriptionDetails subscriptionsDetails);

    SubscriptionDetails update(SubscriptionDetails subscriptionsDetails);

    List<SubscriptionDetails>  update(List<SubscriptionDetails> subscriptionsDetails);
}
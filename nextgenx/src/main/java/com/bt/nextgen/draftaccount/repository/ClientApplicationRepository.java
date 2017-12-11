package com.bt.nextgen.draftaccount.repository;


import com.bt.nextgen.service.integration.accountactivation.OnboardingApplicationKey;
import com.btfin.panorama.service.integration.broker.BrokerIdentifier;

import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Do not use this repository directly. Use the PermittedClientApplicationRepository, it will ensure that you can access the
 * records that you are supposed to be able to access. This interface is the 'raw' interface where you have to supply
 * the correct broker identifiers.
 */
public interface ClientApplicationRepository {

    Long save(ClientApplication draftAccount);

    ClientApplication find(Long id, Collection<? extends BrokerIdentifier> adviserIds);

    Long getNumberOfDraftAccounts(Collection<? extends BrokerIdentifier> adviserIds);

    List<ClientApplication> findNonActiveApplicationsBetweenDates(Date from, Date to, Collection<? extends BrokerIdentifier> adviserIds);

    List<ClientApplication> findNonActiveApplicationsBetweenDates(Date fromDate, Date toDate);

    ClientApplication findByOnboardingApplicationKey(OnboardingApplicationKey key, Collection<? extends BrokerIdentifier> adviserIds);

    ClientApplication findByOnboardingApplicationKey(OnboardingApplicationKey key);

    ClientApplication find(Long id);

    List<ClientApplication> findCertainNumberOfMostRecentDraftAccounts(Integer count, Collection<? extends BrokerIdentifier> adviserIds);
}

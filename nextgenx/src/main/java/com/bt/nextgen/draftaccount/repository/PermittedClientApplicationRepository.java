package com.bt.nextgen.draftaccount.repository;

import com.bt.nextgen.service.integration.accountactivation.OnboardingApplicationKey;

import java.util.Date;
import java.util.List;

public interface PermittedClientApplicationRepository {
    ClientApplication find(Long id);

    List<ClientApplication> findNonActiveApplicationsBetweenDates(Date date, Date date1);

    ClientApplication findByOnboardingApplicationKey(OnboardingApplicationKey key);

    ClientApplication findByOnboardingApplicationKeyWithoutPermissionCheck(OnboardingApplicationKey key);

    Long save(ClientApplication draftAccount);

    List<ClientApplication> findCertainNumberOfLatestDraftAccounts(Integer count);

    Long getNumberOfDraftAccounts();

    ClientApplication findByClientApplicationId(Long id);
}

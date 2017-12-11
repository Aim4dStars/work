package com.bt.nextgen.service.avaloq.contributionhistory;

import com.bt.nextgen.api.account.v3.model.AccountKey;
import org.joda.time.DateTime;

/**
 * Interface for contribution history service.
 */
public interface ContributionHistoryIntegrationService {
    /**
     * Get contribution history for an account for a range of dates.
     *
     * @param accountKey    Account key.
     * @param financialYearStartDate    Start of the financial year.
     * @param financialYearEndDate  End of the financial year.
     * @return Contribution history for an account for a range of dates
     */
    ContributionHistory getContributionHistory(AccountKey accountKey, DateTime financialYearStartDate, DateTime financialYearEndDate);
}

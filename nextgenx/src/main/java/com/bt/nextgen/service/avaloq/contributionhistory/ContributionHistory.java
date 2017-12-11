package com.bt.nextgen.service.avaloq.contributionhistory;

import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.util.List;

/**
 * Interface for contribution history.
 */
public interface ContributionHistory {
    /**
     * Get the start date of the financial year.
     *
     * @return Start date of the financial year.
     */
    DateTime getFinancialYearStartDate();

    /**
     * Get the Maximum Amount
     *
     * @return maximum contribution amount
     */
    public BigDecimal getMaxAmount();


    /**
     * Get contribution summaries by type.
     *
     * @return Contribution summaries by type.
     */
    List<ContributionSummaryByType> getContributionSummariesByType();
}

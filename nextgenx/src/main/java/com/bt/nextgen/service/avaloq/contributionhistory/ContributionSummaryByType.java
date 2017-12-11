package com.bt.nextgen.service.avaloq.contributionhistory;

import com.bt.nextgen.service.integration.cashcategorisation.model.ContributionClassification;
import org.joda.time.DateTime;

import java.math.BigDecimal;

/**
 * Contribution summary for a apecific type.
 */
public interface ContributionSummaryByType {
    /**
     * Get the document id.
     *
     * @return Document id.
     */
    Long getDocId();

    /**
     * Get the contribution classification.
     *
     * @return Contribution classification.
     */
    ContributionClassification getContributionClassification();

    /**
     * Get the contribution type.
     *
     * @return Contribution type.
     */
    ContributionType getContributionType();

    /**
     * Get the date of the last contribution for the contribution type represented by this summary.
     *
     * @return Date of the last contribution for the contribution type represented by this summary.
     */
    DateTime getLastContributionDate();

    /**
     * Get the total amount of contribution for the contribution type represented by this summary.
     *
     * @return Total amount of contribution for the contribution type represented by this summary.
     */
    BigDecimal getAmount();
}

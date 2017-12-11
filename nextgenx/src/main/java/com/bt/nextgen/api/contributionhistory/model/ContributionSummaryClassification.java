package com.bt.nextgen.api.contributionhistory.model;

import java.math.BigDecimal;

/**
 * Summary of a contribution classification.
 */
public class ContributionSummaryClassification {
    /**
     * Contribution classification.
     */
    private String contributionClassification;

    /**
     * Label fot contribution classification.
     */
    private String contributionClassificationLabel;

    /**
     * Total of contribution.
     */
    private BigDecimal total;

    /**
     * Available balance for the classification before it reaches its cap amount.
     */
    private BigDecimal availableBalance;


    public String getContributionClassification() {
        return contributionClassification;
    }

    public void setContributionClassification(String contributionClassification) {
        this.contributionClassification = contributionClassification;
    }

    public String getContributionClassificationLabel() {
        return contributionClassificationLabel;
    }

    public void setContributionClassificationLabel(String contributionClassificationLabel) {
        this.contributionClassificationLabel = contributionClassificationLabel;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public BigDecimal getAvailableBalance() {
        return availableBalance;
    }

    public void setAvailableBalance(BigDecimal availableBalance) {
        this.availableBalance = availableBalance;
    }
}

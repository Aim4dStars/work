package com.bt.nextgen.api.contributionhistory.model;

import java.math.BigDecimal;

/**
 * Contribution for a specified classification.
 */
public class ContributionByClassification {
    /**
     * Type of contribution.
     */
    private String contributionType;

    /**
     * Label  for type of contribution.
     */
    private String contributionTypeLabel;

    /**
     * Amount of contribution.
     */
    private BigDecimal amount;


    public String getContributionType() {
        return contributionType;
    }

    public void setContributionType(String contributionType) {
        this.contributionType = contributionType;
    }

    public String getContributionTypeLabel() {
        return contributionTypeLabel;
    }

    public void setContributionTypeLabel(String contributionTypeLabel) {
        this.contributionTypeLabel = contributionTypeLabel;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}

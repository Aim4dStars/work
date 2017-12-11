package com.bt.nextgen.api.contributionhistory.model;

import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.util.List;

/**
 * Summary of contributions.
 */
public class ContributionSummary {
    /**
     * Date and time of the last contribution.
     */
    private DateTime lastContributionTime;

    /**
     * Amount of the last contribution.
     */
    private BigDecimal lastContributionAmount;

    /**
     * Label for the last contribution type
     */
    private String lastContributionType;

    /**
     * Total amount of contributions.
     */
    private BigDecimal totalContributions;

    /**
     * List of contribution summaries by classification.
     */
    private List<ContributionSummaryClassification> contributionSummaryClassifications;

    /**
     * Total Notified
     */
    private BigDecimal totalNotifiedTaxDeductionAmount;


    public DateTime getLastContributionTime() {
        return lastContributionTime;
    }

    public void setLastContributionTime(DateTime lastContributionTime) {
        this.lastContributionTime = lastContributionTime;
    }

    public BigDecimal getLastContributionAmount() {
        return lastContributionAmount;
    }

    public void setLastContributionAmount(BigDecimal lastContributionAmount) {
        this.lastContributionAmount = lastContributionAmount;
    }

    public String getLastContributionType() {
        return lastContributionType;
    }

    public void setLastContributionType(String lastContributionType) {
        this.lastContributionType = lastContributionType;
    }

    public BigDecimal getTotalContributions() {
        return totalContributions;
    }

    public void setTotalContributions(BigDecimal totalContributions) {
        this.totalContributions = totalContributions;
    }

    public List<ContributionSummaryClassification> getContributionSummaryClassifications() {
        return contributionSummaryClassifications;
    }

    public void setContributionSummaryClassifications(List<ContributionSummaryClassification> contributionSummaryClassifications) {
        this.contributionSummaryClassifications = contributionSummaryClassifications;
    }


    public BigDecimal getTotalNotifiedTaxDeductionAmount() {
        return totalNotifiedTaxDeductionAmount;
    }

    public void setTotalNotifiedTaxDeductionAmount(BigDecimal totalNotifiedTaxDeductionAmount) {
        this.totalNotifiedTaxDeductionAmount = totalNotifiedTaxDeductionAmount;
    }
}

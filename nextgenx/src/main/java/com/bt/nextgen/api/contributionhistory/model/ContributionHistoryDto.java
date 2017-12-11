package com.bt.nextgen.api.contributionhistory.model;

import com.bt.nextgen.core.api.model.BaseDto;
import org.joda.time.LocalDate;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO for contribution history.
 */
public class ContributionHistoryDto extends BaseDto {
    /**
     * Start date of the financial year.
     */
    private LocalDate financialYearStartDate;

    /**
     * Maximum Amount
     */
    private BigDecimal maxAmount;
    /**
     * Summary of contributions.
     */
    private ContributionSummary contributionSummary;

    /**
     * List of contributions by classification.
     */
    private List<ContributionByClassification> contributionByClassifications;


    public LocalDate getFinancialYearStartDate() {
        return financialYearStartDate;
    }

    public void setFinancialYearStartDate(LocalDate financialYearStartDate) {
        this.financialYearStartDate = financialYearStartDate;
    }

    public ContributionSummary getContributionSummary() {
        return contributionSummary;
    }

    public void setContributionSummary(ContributionSummary contributionSummary) {
        this.contributionSummary = contributionSummary;
    }

    public List<ContributionByClassification> getContributionByClassifications() {
        return contributionByClassifications;
    }

    public void setContributionByClassifications(List<ContributionByClassification> contributionByClassifications) {
        this.contributionByClassifications = contributionByClassifications;
    }

    public BigDecimal getMaxAmount() {
        return maxAmount;
    }

    public void setMaxAmount(BigDecimal maxAmount) {
        this.maxAmount = maxAmount;
    }

}

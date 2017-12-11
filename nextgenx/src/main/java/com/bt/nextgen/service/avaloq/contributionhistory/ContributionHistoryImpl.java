package com.bt.nextgen.service.avaloq.contributionhistory;

import com.bt.nextgen.core.conversion.BigDecimalConverter;
import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;
import com.bt.nextgen.service.integration.externalasset.builder.IsoDateTimeConverter;
import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Avaloq business object for contribution history..
 */
@ServiceBean(xpath = "//bp_list/bp")
public class ContributionHistoryImpl implements ContributionHistory {
    /**
     * Start date of the financial year.
     */
    @ServiceElement(xpath = "bp_head_list/bp_head/fy_date/val", converter = IsoDateTimeConverter.class)
    private DateTime financialYearStartDate;

    /**
     * Maximum Amount
     */
    @ServiceElement(xpath = "bp_head_list/bp_head/remn_contri_tc/val", converter = BigDecimalConverter.class)
    private BigDecimal maxAmount;

    /**
     * List of contribution summaries by type.
     */
    @ServiceElement(xpath = "buc_list/buc/buc_head_list/buc_head", type = ContributionSummaryByTypeImpl.class)
    private List<ContributionSummaryByType> contributionSummariesByType = new ArrayList<>();


    @Override
    public DateTime getFinancialYearStartDate() {
        return financialYearStartDate;
    }

    @Override
    public List<ContributionSummaryByType> getContributionSummariesByType() {
        return contributionSummariesByType;
    }

    public BigDecimal getMaxAmount() {
        return maxAmount;
    }

    public void setMaxAmount(BigDecimal maxAmount) {
        this.maxAmount = maxAmount;
    }

}

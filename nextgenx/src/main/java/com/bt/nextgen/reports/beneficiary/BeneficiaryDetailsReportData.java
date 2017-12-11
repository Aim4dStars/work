package com.bt.nextgen.reports.beneficiary;

import static org.hamcrest.core.IsEqual.equalTo;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bt.nextgen.core.reporting.ReportFormat;
import com.bt.nextgen.core.reporting.ReportFormatter;

import ch.lambdaj.Lambda;


public class BeneficiaryDetailsReportData {

    private static final int ONE_HUNDRED = 100;

    private final DateTime beneficiariesLastUpdatedTime;

    private final List<BeneficiaryData> beneficiaryDataList;

    private static final Integer SCALE = 2;

    private static final Logger logger = LoggerFactory.getLogger(BeneficiaryDetailsReportData.class);

    /**
     * Overloaded constructor for populating Beneficiary details
     *
     * @param beneficiariesLastUpdatedTime last updated time for beneficiaries
     * @param totalAllocationPercent       total allocation percent
     * @param beneficiaryDataList          beneficiary list
     */
    public BeneficiaryDetailsReportData(@Nonnull final DateTime beneficiariesLastUpdatedTime,
                                        @Nonnull final List<BeneficiaryData> beneficiaryDataList) {
        this.beneficiariesLastUpdatedTime = beneficiariesLastUpdatedTime;
        this.beneficiaryDataList = beneficiaryDataList;
    }

    public String getBeneficiariesLastUpdatedTime() {
        return ReportFormatter.format(ReportFormat.SHORT_DATE, beneficiariesLastUpdatedTime);
    }

    public String getTotalAllocationPercentPrimary() {
        return getTotalAllocationPercent(true);
    }

    public String getTotalAllocationPercentSecondary() {
        return getTotalAllocationPercent(false);
    }

    /**
     * "1.23%"
     */
    private @Nonnull String getTotalAllocationPercent(boolean isPrimary) {
        BigDecimal percent = new BigDecimal(BigInteger.ZERO, SCALE);
        List<BeneficiaryData> groupedBeneficiaries = getGroupedBeneficiaries(isPrimary);
        for (BeneficiaryData aBenificiary : groupedBeneficiaries) {
            percent = percent.add(percentageFrom(aBenificiary));
        }
        return ReportFormatter.format(ReportFormat.PERCENTAGE, true, percent);
    }

    /**
     * TODO: get rid of aBenificiary.getAllocationPercent() returning "100.00%".
     * Can't touch it for now as the mandate is no/minimum retesting for Jan17.
     * "100.00%" returns BD(1.0000)
     */
    private @Nonnull BigDecimal percentageFrom(BeneficiaryData aBenificiary) {
        BigDecimal returnValue = BigDecimal.ZERO;
        returnValue.setScale(SCALE + new Double(Math.log10(ONE_HUNDRED)).intValue()); // thats 2 + 2; to capture the full precision we are getting
        try {
            returnValue = new BigDecimal(StringUtils.removeEnd(aBenificiary.getAllocationPercent(), "%")).divide(new BigDecimal(ONE_HUNDRED));;
        } catch (Exception e) {
            logger.info("bad allocation percentage, defaulting to 0", e);
        }
        return returnValue;
    }

    private @Nonnull List<BeneficiaryData> getGroupedBeneficiaries(boolean isPrimary) {
        List<BeneficiaryData> groupedBeneficiaries = new ArrayList<>();
        if (isPrimary) {
            groupedBeneficiaries.addAll(getPrimaryBeneficiaries());
        } else {
            List<BeneficiaryData> groupedSecondaryBeneficiaries = getSecondaryBeneficiaries();
            if (groupedSecondaryBeneficiaries != null) {
                groupedBeneficiaries.addAll(groupedSecondaryBeneficiaries);
            }
        }
        return groupedBeneficiaries;
    }

    /**
     * This effectively replaces getBeneficiaries() when we have no auto discretionary beneficiaries.
     * In other words, if we have no auto discretionaries, return as primary, all beneficiaries.
     * In that circumstance we don't care to separate out secondaries.
     */
    public @Nonnull List<BeneficiaryData> getPrimaryBeneficiaries() {
        List<BeneficiaryData> primaryBeneficiaries = beneficiaryDataList;
        if (getHasAutoDiscretionaryBeneficiaries()) {
            primaryBeneficiaries = getBeneficiaries(true);
        }
        return primaryBeneficiaries;
    }

    /**
     * null indicates not to display the secondary section, and to turn the header off in the primary.
     */
    public @Nullable List<BeneficiaryData> getSecondaryBeneficiaries() {
        List<BeneficiaryData> secondaryBeneficiaries = getBeneficiaries(false);
        if (secondaryBeneficiaries.isEmpty()) {
            secondaryBeneficiaries = null;
        }
        return secondaryBeneficiaries;
    }

    /**
     * @return Boolean (sic).
     */
    public Boolean getHasAutoDiscretionaryBeneficiaries() {
        return !getBeneficiaries(true).isEmpty();
    }

    private List<BeneficiaryData> getBeneficiaries(boolean isPrimary) {
        return Lambda.filter(Lambda.having(Lambda.on(BeneficiaryData.class).isPrimary(), equalTo(isPrimary)),
                beneficiaryDataList);
    }

    public Boolean getHasNoBeneficiaries() {
        return beneficiaryDataList.isEmpty();
    }

    public Boolean getHasNoSecondaryBeneficiaries() {
        return getSecondaryBeneficiaries() == null;
    }

    public Boolean getHasNoPrimaryBeneficiaries() {
        return getPrimaryBeneficiaries().isEmpty();
    }

}

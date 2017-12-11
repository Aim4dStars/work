package com.bt.nextgen.api.contributionhistory.builder;

import com.bt.nextgen.api.contributionhistory.model.ContributionByClassification;
import com.bt.nextgen.api.contributionhistory.model.ContributionHistoryDto;
import com.bt.nextgen.api.contributionhistory.model.ContributionSummary;
import com.bt.nextgen.api.contributionhistory.model.ContributionSummaryClassification;
import com.bt.nextgen.service.avaloq.contributionhistory.ContributionHistory;
import com.bt.nextgen.service.avaloq.contributionhistory.ContributionSummaryByType;
import com.bt.nextgen.service.avaloq.contributionhistory.ContributionType;
import com.bt.nextgen.service.integration.cashcategorisation.model.ContributionClassification;
import com.bt.nextgen.service.integration.code.Code;
import com.bt.nextgen.service.integration.code.Field;
import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * Converter for contribution history.
 */
public class ContributionHistoryDtoConverter {
    /**
     * Number of decimal points for amount and percentages.
     */
    private static final int DECIMAL_POINTS = 2;

    /**
     * Rounding mode for decimal points.
     */
    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;

    private static final String EXTL_FLD_MODAL_NAME = "btfg$is_calc_psdn";
    private static final String EXTL_FLD_MODAL_VALUE = "+";

    /**
     * Get a DTO version of contribution history.
     *
     * @param contributionHistory Contribution history to convert to DTO.
     *
     * @return DTO version of contribution history.
     */
    public ContributionHistoryDto getAsContributionHistoryDto(final ContributionHistory contributionHistory, Collection<Code> categoryCodes) {
        final ContributionHistoryDto retval = new ContributionHistoryDto();

        if (contributionHistory != null) {
            retval.setMaxAmount(contributionHistory.getMaxAmount());
            processContributionSummaries(contributionHistory, retval, categoryCodes);
        }

        return retval;
    }

    /**
     * Process contribution summaries and update its DTO.
     *
     * @param contributionHistory Contribution history.
     * @param dto                 DTO for contribution history.
     */
    private void processContributionSummaries(ContributionHistory contributionHistory, ContributionHistoryDto dto, Collection<Code> categoryCodes) {
        final SummaryBuilder summaryBuilder = new SummaryBuilder();
        final Map<String, ContributionByClassification> contributionMap = new HashMap<>();
        final List<ContributionByClassification> contributions = new ArrayList<>();
        BigDecimal totalNotifiedAmount = BigDecimal.ZERO;
        for (ContributionSummaryByType summaryByType : contributionHistory.getContributionSummariesByType()) {
            final ContributionType contributionType = summaryByType.getContributionType();
            final String contributionId = contributionType.getId();
            final BigDecimal amount = summaryByType.getAmount().setScale(DECIMAL_POINTS, ROUNDING_MODE);
            ContributionByClassification contribution = contributionMap.get(contributionType.getId());

            if (contribution == null) {
                contribution = new ContributionByClassification();

                contribution.setContributionType(contributionId);
                contribution.setContributionTypeLabel(contributionType.getLabel());
                contribution.setAmount(amount);

                contributionMap.put(contributionId, contribution);
            }
            else {
                contribution.setAmount(contribution.getAmount().add(amount));
            }

            summaryBuilder.updateSummary(summaryByType);
            totalNotifiedAmount = totalNotifiedAmount.add(getTotalNotifiedTaxDeductionAmount(contributionType, amount, categoryCodes));
        }

        contributions.addAll(contributionMap.values());
        sortContributions(contributions);
        dto.setContributionByClassifications(contributions);
        //Set totalNotifiedTaxDeductionAmount in contribution summary

        dto.setContributionSummary(summaryBuilder.getContributionSummary());
        dto.getContributionSummary().setTotalNotifiedTaxDeductionAmount(totalNotifiedAmount);
    }

    //Set TotalNotifiedTaxDeductionAmount based on btfg$is_calc_psdn flag
    private BigDecimal getTotalNotifiedTaxDeductionAmount(ContributionType contributionType, BigDecimal amount, Collection<Code> categoryCodes){
        //check flag btfg$is_calc_psdn in code table
        BigDecimal totalAmount = BigDecimal.ZERO;
        for(Code code : categoryCodes) {
            if(code.getIntlId().equals(contributionType.getId())){
                for (Iterator<Field> iterator = code.getFields().iterator(); iterator.hasNext(); ) {
                    Field field = iterator.next();
                    if(field.getName().equals(EXTL_FLD_MODAL_NAME) && field.getValue()!=null && field.getValue().equals(EXTL_FLD_MODAL_VALUE)){
                        totalAmount = totalAmount.add(amount);
                    }
                }
            }
        }
        return totalAmount;
    }



    /**
     * Sort the contributions by ascending order or label.
     *
     * @param contributions contributions to sort.
     */
    private void sortContributions(List<ContributionByClassification> contributions) {
        Collections.sort(contributions, new Comparator<ContributionByClassification>() {
            @Override
            public int compare(ContributionByClassification o1, ContributionByClassification o2) {
                return o1.getContributionTypeLabel().compareTo(o2.getContributionTypeLabel());
            }
        });
    }


    /**
     * Builder for summary of contribution history.
     */
    private class SummaryBuilder {
        /**
         * Avaloq guarantees the allocation of docId to be in the sequence of contributions processing.
         * As the contribution date does not contain the time component, docId is used to find the
         * latest contribution.
         */
        private Long latestDocId;

        /**
         * Date of last contribution.
         */
        private DateTime latestContributionDate;

        /**
         * Amount of last contribution.
         */
        private BigDecimal latestAmount;

        /**
         * Label for the last contribution.
         */
        private String lastContributionType;

        /**
         * Total contribution amount.
         */
        private BigDecimal totalContributions = BigDecimal.ZERO.setScale(DECIMAL_POINTS, ROUNDING_MODE);

        /**
         * Amounts for each contribution classification.
         */
        private Map<String, BigDecimal> classificationAmounts = new HashMap<>();


        /**
         * Update the  contribution summary using the specified type of summary.
         *
         * @param summaryByType Contribution summary for a specified type.
         */
        public void updateSummary(final ContributionSummaryByType summaryByType) {
            setLatestContributionSummary(summaryByType);
            accumulateContributions(summaryByType);
            accumulateClassificationAmounts(summaryByType);
        }

        /**
         * Update the total contribution.
         *
         * @param summaryByType Contribution summary for a specified type.
         */
        private void accumulateContributions(final ContributionSummaryByType summaryByType) {
            totalContributions = totalContributions.add(summaryByType.getAmount().setScale(DECIMAL_POINTS, ROUNDING_MODE));
        }

        /**
         * Get the built up contribution summary.
         *
         * @return Summary of contributions.
         */
        public ContributionSummary getContributionSummary() {
            final ContributionSummary retval = new ContributionSummary();
            final List<ContributionSummaryClassification> contributionSummaryClassifications = new ArrayList<>();

            retval.setLastContributionTime(latestContributionDate);
            retval.setLastContributionAmount(latestAmount);
            retval.setTotalContributions(totalContributions);
            retval.setContributionSummaryClassifications(contributionSummaryClassifications);
            retval.setLastContributionType(lastContributionType);

            for (final ContributionClassification classification : ContributionClassification.values()) {
                final ContributionSummaryClassification summaryClassification = new ContributionSummaryClassification();

                summaryClassification.setContributionClassification(classification.getAvaloqInternalId());
                summaryClassification.setContributionClassificationLabel(classification.getName());
                summaryClassification.setTotal(classificationAmounts.get(classification.name()));

                contributionSummaryClassifications.add(summaryClassification);
            }

            return retval;
        }

        /**
         * Accumulate contribution classification amounts using the specified type of summary..
         *
         * @param summaryByType Contribution summary for a specified type.
         */
        private void accumulateClassificationAmounts(final ContributionSummaryByType summaryByType) {
            final ContributionClassification classification = summaryByType.getContributionClassification();
            BigDecimal totalAmount = classificationAmounts.get(classification.name());
            final BigDecimal amount = summaryByType.getAmount().setScale(DECIMAL_POINTS, ROUNDING_MODE);

            if (totalAmount == null) {
                totalAmount = amount;
            }
            else {
                totalAmount = totalAmount.add(amount);
            }

            classificationAmounts.put(classification.name(), totalAmount);
        }

        /**
         * Set the latest contribution summary using the specified type of summary.
         *
         * @param summaryByType Contribution summary for a specified type.
         */
        private void setLatestContributionSummary(final ContributionSummaryByType summaryByType) {
            // use docId to check the sequence of contributions
            if ((summaryByType.getDocId()!=null && latestDocId == null )|| (summaryByType.getDocId()!=null && latestDocId.compareTo(summaryByType.getDocId()) < 0)) {
                latestDocId = summaryByType.getDocId();
                latestContributionDate = summaryByType.getLastContributionDate();
                latestAmount = summaryByType.getAmount().setScale(DECIMAL_POINTS, ROUNDING_MODE);
                lastContributionType = summaryByType.getContributionType() != null ? summaryByType.getContributionType().getLabel() : null;
            }
            // sum up amounts for contributions made in the same transaction
            else if (summaryByType.getDocId() !=null && latestDocId.compareTo(summaryByType.getDocId()) == 0) {
                latestAmount = latestAmount.add(summaryByType.getAmount().setScale(DECIMAL_POINTS, ROUNDING_MODE));
            }
        }
    }
}

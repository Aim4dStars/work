package com.bt.nextgen.service.avaloq.contributionhistory;

import com.bt.nextgen.service.integration.cashcategorisation.model.ContributionClassification;
import com.bt.nextgen.service.integration.code.Code;
import com.bt.nextgen.service.integration.code.Field;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.btfin.panorama.core.conversion.CodeCategory;
import com.btfin.panorama.core.util.StringUtil;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.btfin.panorama.wrap.model.Contribution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Converter for Wrap contribution type.
 */

@Component
public class WrapContributionTypeConverter {
    /**

     * Avaloq field name for contribution type.
     */
    private static final String FIELD_CONTRIBUTION_LABEL = "btfg$ui_name";

    private static Logger logger = LoggerFactory.getLogger(WrapContributionTypeConverter.class);
    /**
     * Convert Wrap contribution type to a {@link ContributionType} instance.
     *
     * @param source Value of Wrap contribution type.
     * @return An instance of {@link ContributionType}
     */

    public ContributionType convert(final String source, StaticIntegrationService staticService) {
            String intlId = StringUtil.isNotNullorEmpty(source) ? WrapContributionTypeMapping.getIntlIdByString(source) : null;
            if(intlId !=null) {
                final Code code = staticService.loadCodeByAvaloqId(CodeCategory.SUPER_CONTRIBUTIONS_TYPE, intlId, new FailFastErrorsImpl());
                final Field labelField = code.getField(FIELD_CONTRIBUTION_LABEL);
                final String label = (labelField != null && labelField.getValue() != null) ? labelField.getValue() : code.getName();
                return new ContributionType(intlId, label);
            }
            return null;
    }

    /**
     * Convert Wrap contributions to a {@link ContributionHistory} instance.
     *
     * @param wrapContributions
     * @param staticService
     * @return An instance of {@link ContributionHistory}
     */

    public ContributionHistory toContributionHistory(final List<Contribution> wrapContributions, final StaticIntegrationService staticService) {
        final ContributionHistory contributionHistory = new ContributionHistoryImpl();
        for(Contribution wrapContribution : wrapContributions){
            List<ContributionSummaryByType> contributionSummaryByTypes = new ArrayList<>();
            if (wrapContribution != null) {
                ContributionType contributionType = convert(wrapContribution.getContributionSubType(), staticService);
                if (BigDecimal.ZERO.compareTo(wrapContribution.getContributionAmount())!=0  && contributionType!=null) {
                    ContributionSummaryByTypeImpl contributionSummaryByType = new ContributionSummaryByTypeImpl();
                    contributionSummaryByType.setContributionClassification(ContributionClassification.forName(wrapContribution.getContributionType()));
                    contributionSummaryByType.setContributionType(contributionType);
                    contributionSummaryByType.setAmount(wrapContribution.getContributionAmount());
                    contributionSummaryByTypes.add(contributionSummaryByType);
                }
            }
            contributionHistory.getContributionSummariesByType().addAll(contributionSummaryByTypes);
            logger.debug("Contribution summaries list size", contributionHistory.getContributionSummariesByType().size());
        }
        return contributionHistory;

    }

}
package com.bt.nextgen.service.avaloq.contributionhistory;

import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.integration.cashcategorisation.model.ContributionClassification;
import com.bt.nextgen.service.integration.code.Code;
import com.btfin.panorama.core.conversion.CodeCategory;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Converter for contribution classification.
 */
@Component
public class ContributionClassificationConverter implements Converter<String, ContributionClassification> {
    /**
     * Service for static code.
     */
    @Autowired
    private StaticIntegrationService staticIntegrationService;

    /**
     * Convert a contribution cap type code to a contribution classification.
     *
     * @param source Code for contribution cap type.
     * @return Contribution classification for the cap type.
     */
    @Override
    public ContributionClassification convert(final String source) {
        final Code code = staticIntegrationService.loadCode(CodeCategory.SUPER_CONTRIBUTIONS_CONC_TYPE, source, new ServiceErrorsImpl());
        final ContributionClassification classification = ContributionClassification.forAvaloqInternalId(code.getIntlId());

        if (classification == null) {
            return ContributionClassification.OTHER;
        }

        return classification;
    }
}

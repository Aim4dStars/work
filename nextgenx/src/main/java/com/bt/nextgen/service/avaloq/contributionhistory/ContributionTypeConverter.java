package com.bt.nextgen.service.avaloq.contributionhistory;

import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.integration.cashcategorisation.model.ContributionClassification;
import com.bt.nextgen.service.integration.code.Code;
import com.btfin.panorama.core.conversion.CodeCategory;
import com.bt.nextgen.service.integration.code.Field;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Converter for Avaloq contribution type.
 */
@Component
public class ContributionTypeConverter implements Converter<String, ContributionType> {
    /**
     * Avaloq field name for contribution type.
     */
    private static final String FIELD_CONTRIBUTION_LABEL = "btfg$ui_name";

    /**
     * Static code integration service.
     */
    @Autowired
    private StaticIntegrationService staticIntegrationService;


    /**
     * Convert Avaloq contribution type to a {@link ContributionType} instance.
     * @param source    Value of Avaloq contribution type.
     * @return  An instance of {@link ContributionType}
     */
    @Override
    public ContributionType convert(final String source) {
        final Code code = staticIntegrationService.loadCode(CodeCategory.SUPER_CONTRIBUTIONS_TYPE, source, new ServiceErrorsImpl());
        final Field labelField = code.getField(FIELD_CONTRIBUTION_LABEL);
        final String label = (labelField != null && labelField.getValue() != null)? labelField.getValue() : code.getName();

        return new ContributionType(code.getIntlId(), label);
    }
}

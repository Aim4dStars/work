package com.bt.nextgen.api.corporateaction.v1.service.converter;

import java.math.BigDecimal;

import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionContext;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionSelectedOptionDto;
import com.bt.nextgen.api.corporateaction.v1.service.stereotype.CorporateActionRequestConverter;

@CorporateActionRequestConverter("CA_MULTI_BLOCK_PUBLIC_OFFER_REQUEST")
public class MultiBlockPublicOfferRequestConverterServiceImpl extends MultiBlockRequestConverterServiceImpl {
    @Override
    protected String getTakeUpPercent(CorporateActionContext context, CorporateActionSelectedOptionDto electionDto) {
        if (useMaxTakeUpPercent(context)) {
            return context.getCorporateActionDetails().getTakeoverLimit().toPlainString();
        }

        return super.getTakeUpPercent(context, electionDto);
    }

    private boolean useMaxTakeUpPercent(CorporateActionContext context) {
        return context.getCorporateActionDetails().getTakeoverLimit() != null &&
                BigDecimal.ZERO.compareTo(context.getCorporateActionDetails().getTakeoverLimit()) < 0 &&
                CorporateActionConverterConstants.DECIMAL_ONE_HUNDRED.compareTo(context.getCorporateActionDetails().getTakeoverLimit()) > 0;
    }
}

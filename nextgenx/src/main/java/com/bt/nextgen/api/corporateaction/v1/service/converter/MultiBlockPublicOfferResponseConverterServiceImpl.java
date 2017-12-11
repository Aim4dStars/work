package com.bt.nextgen.api.corporateaction.v1.service.converter;

import java.math.BigDecimal;

import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionContext;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionDetailsDtoParams;
import com.bt.nextgen.api.corporateaction.v1.service.stereotype.CorporateActionResponseConverter;


@CorporateActionResponseConverter("CA_MULTI_BLOCK_PUBLIC_OFFER_RESPONSE")
public class MultiBlockPublicOfferResponseConverterServiceImpl extends MultiBlockResponseConverterServiceImpl {
    @Override
    public CorporateActionDetailsDtoParams setCorporateActionDetailsDtoParams(CorporateActionContext context,
                                                                              CorporateActionDetailsDtoParams params) {
        super.setCorporateActionDetailsDtoParams(context, params);

        if (useMaxTakeUpPercent(context)) {
            params.setMaxTakeUpPercent(context.getCorporateActionDetails().getTakeoverLimit());
        }

        return params;
    }

    private boolean useMaxTakeUpPercent(CorporateActionContext context) {
        return context.getCorporateActionDetails().getTakeoverLimit() != null &&
                BigDecimal.ZERO.compareTo(context.getCorporateActionDetails().getTakeoverLimit()) < 0 &&
                CorporateActionConverterConstants.DECIMAL_ONE_HUNDRED.compareTo(context.getCorporateActionDetails().getTakeoverLimit()) > 0;
    }
}

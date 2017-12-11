package com.bt.nextgen.api.corporateaction.v1.service.converter;

import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionAccountDetailsDtoParams;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionContext;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionDetailsDtoParams;
import com.bt.nextgen.api.corporateaction.v1.service.stereotype.CorporateActionResponseConverter;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionAccount;

@CorporateActionResponseConverter("CA_BUY_BACK_RESPONSE")
public class BuyBackResponseConverterServiceImpl extends BuyBackResponseConverterServiceBaseImpl {

    @Override
    public CorporateActionDetailsDtoParams setCorporateActionDetailsDtoParams(CorporateActionContext context,
                                                                              CorporateActionDetailsDtoParams params) {
        params.setMinPrices(super.toCorporateActionPriceOptionDtos(context.getCorporateActionDetails(),
                super.getMinimumPriceIndex(context.getCorporateActionDetails().getDecisions())));

        return params;
    }

    @Override
    public CorporateActionAccountDetailsDtoParams setCorporateActionAccountDetailsDtoParams(CorporateActionContext context,
                                                                                            CorporateActionAccount account,
                                                                                            CorporateActionAccountDetailsDtoParams params) {
        // If pay date is after today then hide the transaction number and description
        if (context.getCorporateActionDetails().getPayDate() != null && context.getCorporateActionDetails().getPayDate().isAfterNow()) {
            params.setTransactionNumber(null);
            params.setTransactionDescription(null);
        }

        return params;
    }
}
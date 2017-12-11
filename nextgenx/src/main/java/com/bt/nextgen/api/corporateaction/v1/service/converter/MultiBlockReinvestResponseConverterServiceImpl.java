package com.bt.nextgen.api.corporateaction.v1.service.converter;

import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionContext;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionDetailsDtoParams;
import com.bt.nextgen.api.corporateaction.v1.service.stereotype.CorporateActionResponseConverter;

@CorporateActionResponseConverter("CA_MULTI_BLOCK_REINVEST_RESPONSE")
public class MultiBlockReinvestResponseConverterServiceImpl extends MultiBlockResponseConverterServiceImpl {
    @Override
    public CorporateActionDetailsDtoParams setCorporateActionDetailsDtoParams(CorporateActionContext context,
                                                                              CorporateActionDetailsDtoParams params) {
        super.setCorporateActionDetailsDtoParams(context, params);

        if (isTakeUpByUnits(context)) {
            params.setPartialElection(Boolean.TRUE);
        }

        return params;
    }

    @Override
    protected boolean isTakeUpByUnits(CorporateActionContext context) {
        return !context.isDealerGroupOrInvestmentManager();
    }
}

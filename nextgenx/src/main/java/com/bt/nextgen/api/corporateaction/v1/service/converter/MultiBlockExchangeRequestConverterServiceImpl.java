package com.bt.nextgen.api.corporateaction.v1.service.converter;

import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionContext;
import com.bt.nextgen.api.corporateaction.v1.service.stereotype.CorporateActionRequestConverter;

@CorporateActionRequestConverter("CA_MULTI_BLOCK_EXCHANGE_REQUEST")
public class MultiBlockExchangeRequestConverterServiceImpl extends MultiBlockRequestConverterServiceImpl {
    @Override
    protected boolean isTakeUpByUnits(CorporateActionContext context) {
        return !context.isDealerGroupOrInvestmentManager();
    }
}

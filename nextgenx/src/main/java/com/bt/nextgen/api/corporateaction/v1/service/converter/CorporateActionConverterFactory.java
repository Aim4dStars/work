package com.bt.nextgen.api.corporateaction.v1.service.converter;

import com.bt.nextgen.service.integration.corporateaction.CorporateActionDetails;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionOfferType;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionType;

public interface CorporateActionConverterFactory {
    CorporateActionResponseConverterService getResponseConverterService(CorporateActionDetails corporateActionDetails);

    CorporateActionRequestConverterService getRequestConverterService(CorporateActionDetails corporateActionDetails);

    Object getCorporateActionBean(CorporateActionType corporateActionType,
                                  CorporateActionOfferType corporateActionOfferType, String suffix,
                                  Class corporateActionConverterClass);
}

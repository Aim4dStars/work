package com.bt.nextgen.api.modelportfolio.v2.service.defaultparams;

import com.bt.nextgen.api.modelportfolio.v2.model.detail.ModelOfferDto;
import com.bt.nextgen.core.api.dto.SearchByCriteriaDtoService;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.modelportfolio.common.ModelType;

import java.util.List;

/**
 * 
 * @deprecated Service interface to be removed due to changes in Packaging. Targeting April '18 release.
 * 
 */
@Deprecated
public interface TailoredPortfolioOfferDtoService extends SearchByCriteriaDtoService<ModelOfferDto> {

    public List<ModelOfferDto> getModelOffers(final BrokerKey dealerKey, ModelType modelEnum, final ServiceErrors errors);
}

package com.bt.nextgen.api.portfolio.v3.service.valuation;

import com.bt.nextgen.api.portfolio.v3.model.valuation.DatedValuationKey;
import com.bt.nextgen.api.portfolio.v3.model.valuation.ValuationDto;
import com.bt.nextgen.core.api.dto.CacheableFindByKeyDtoService;

public interface CacheableValuationDtoService extends ValuationDtoService,
        CacheableFindByKeyDtoService<DatedValuationKey, ValuationDto>
{

}

package com.bt.nextgen.api.account.v2.service.valuation;

import com.bt.nextgen.api.account.v2.model.DatedValuationKey;
import com.bt.nextgen.api.account.v2.model.ValuationDto;
import com.bt.nextgen.core.api.dto.CacheableFindByKeyDtoService;
import com.bt.nextgen.core.api.dto.FindByKeyDtoService;

@Deprecated
public interface ValuationDtoService extends FindByKeyDtoService<DatedValuationKey, ValuationDto>,
        CacheableFindByKeyDtoService<DatedValuationKey, ValuationDto>
{

}

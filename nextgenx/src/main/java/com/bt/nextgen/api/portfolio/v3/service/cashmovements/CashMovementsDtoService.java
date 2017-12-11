package com.bt.nextgen.api.portfolio.v3.service.cashmovements;

import com.bt.nextgen.api.portfolio.v3.model.cashmovements.CashMovementsDto;
import com.bt.nextgen.api.portfolio.v3.model.valuation.DatedValuationKey;
import com.bt.nextgen.core.api.dto.FindByKeyDtoService;

public interface CashMovementsDtoService extends FindByKeyDtoService<DatedValuationKey, CashMovementsDto>
{

}

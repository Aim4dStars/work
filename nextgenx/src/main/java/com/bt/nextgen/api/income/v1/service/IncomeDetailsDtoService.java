package com.bt.nextgen.api.income.v1.service;

import com.bt.nextgen.api.income.v1.model.IncomeDetailsDto;
import com.bt.nextgen.api.income.v1.model.IncomeDetailsKey;
import com.bt.nextgen.core.api.dto.FindByKeyDtoService;
import com.bt.nextgen.service.ServiceErrors;

@Deprecated
public interface IncomeDetailsDtoService extends FindByKeyDtoService <IncomeDetailsKey, IncomeDetailsDto>
{
	public IncomeDetailsDto find(IncomeDetailsKey key, ServiceErrors serviceErrors);
}

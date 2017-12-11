package com.bt.nextgen.api.income.v2.service;

import com.bt.nextgen.api.income.v2.model.IncomeDetailsKey;
import com.bt.nextgen.api.income.v2.model.IncomeValuesDto;
import com.bt.nextgen.core.api.dto.FindByKeyDtoService;
import com.bt.nextgen.service.ServiceErrors;

public interface IncomeDetailsDtoService extends FindByKeyDtoService <IncomeDetailsKey, IncomeValuesDto>
{
	public IncomeValuesDto find(IncomeDetailsKey key, ServiceErrors serviceErrors);
}

package com.bt.nextgen.api.fees.service;

import com.bt.nextgen.api.account.v1.model.AccountKey;

import com.bt.nextgen.api.fees.model.FeeScheduleDto;
import com.bt.nextgen.api.fees.model.FeesTypeDto;
import com.bt.nextgen.api.fees.model.InvestmentMgmtFeesDto;
import com.bt.nextgen.core.api.dto.FindByKeyDtoService;
import com.bt.nextgen.core.api.dto.SubmitDtoService;
import com.bt.nextgen.core.api.dto.ValidateDtoService;
import com.bt.nextgen.service.ServiceErrors;

import java.util.List;

public interface FeeScheduleDtoService extends FindByKeyDtoService <AccountKey, FeeScheduleDto>,
	ValidateDtoService <AccountKey, FeeScheduleDto>, SubmitDtoService <AccountKey, FeeScheduleDto>
{
	//public List<FeesTypeDto> getFeesScheduleComponents(String accountId, ServiceErrors serviceErrors);

	//public List <InvestmentMgmtFeesDto> getInvestmentMgmtFee(String accountId, ServiceErrors serviceErrors);

}

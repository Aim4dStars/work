package com.bt.nextgen.api.account.v1.service;

import com.bt.nextgen.api.account.v1.model.AccountKey;
import com.bt.nextgen.api.account.v1.model.DepositDto;
import com.bt.nextgen.core.api.dto.SearchByCriteriaDtoService;
import com.bt.nextgen.core.api.dto.SubmitDtoService;
import com.bt.nextgen.core.api.dto.ValidateDtoService;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.PortfolioRequest;
import com.bt.nextgen.service.integration.MoneyAccountIdentifier;

import java.util.List;
import java.util.Map;

/**
 * @deprecated Use V2
 */
@Deprecated
public interface DepositDtoService extends SearchByCriteriaDtoService <DepositDto>, ValidateDtoService <AccountKey, DepositDto>,
	SubmitDtoService <AccountKey, DepositDto>
{

	public List <DepositDto> loadPayeesForDeposits(PortfolioRequest portfolioRequest);

	public DepositDto validateDeposit(DepositDto keyedObject, ServiceErrors serviceErrors);

	public DepositDto submitDeposit(DepositDto keyedObject, ServiceErrors serviceErrors);

	Map <String, DepositDto> loadDepositReciepts();

        public MoneyAccountIdentifier getMoneyAccountIdentifier(DepositDto keyedObject, ServiceErrors serviceErrors);
}

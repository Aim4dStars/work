package com.bt.nextgen.service.integration.cashcategorisation.service;

import com.bt.nextgen.core.api.dto.FindByPartialKeyDtoService;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.cashcategorisation.model.CategorisableCashTransactionDto;
import com.bt.nextgen.service.integration.cashcategorisation.model.DepositKey;

import java.util.List;

public interface RetrieveCashContributionDtoService extends
	FindByPartialKeyDtoService <DepositKey, CategorisableCashTransactionDto>
{
    public CategorisableCashTransactionDto getCashTransactionsForAccounts(List<ApiSearchCriteria> criteriaList, ServiceErrors serviceErrors);

}

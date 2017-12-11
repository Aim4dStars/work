package com.bt.nextgen.api.cashcategorisation.service;

import com.bt.nextgen.api.cashcategorisation.builder.CashContributionConverter;
import com.bt.nextgen.api.cashcategorisation.model.CategorisableCashTransactionDto;
import com.bt.nextgen.api.transactioncategorisation.model.TransactionCategoryDto;
import com.bt.nextgen.api.transactioncategorisation.service.TransactionCategoryDtoService;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.TransactionStatus;
import com.bt.nextgen.service.integration.cashcategorisation.model.CashCategorisationAction;
import com.bt.nextgen.service.integration.cashcategorisation.service.CashCategorisationIntegrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * API layer service to retrieve and save member contributions on a cash transaction<p>
  */
@Service
public class CashContributionDtoServiceImpl implements CashContributionDtoService
{
	@Autowired
	@Qualifier("CashCategorisationIntegrationServiceImpl")
	private CashCategorisationIntegrationService cashCatIntegrationService;

	@Autowired
	private TransactionCategoryDtoService transactionCategoryDtoService;


	@Override
	public CategorisableCashTransactionDto submit(CategorisableCashTransactionDto keyedObject, ServiceErrors serviceErrors)
	{
		List<TransactionCategoryDto> transactionCategoryDtoList = transactionCategoryDtoService.search(new ArrayList<ApiSearchCriteria>(), serviceErrors);

		TransactionStatus transactionStatus = cashCatIntegrationService.saveOrUpdate(CashCategorisationAction.ADD,
												CashContributionConverter.toCategorisableCashTransaction(transactionCategoryDtoList, keyedObject));

		CategorisableCashTransactionDto response = CashContributionConverter.toCategorisationResponseDto(transactionStatus, keyedObject);

		return response;
	}
}
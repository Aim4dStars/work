package com.bt.nextgen.api.cashcategorisation.service;


import com.bt.nextgen.api.cashcategorisation.builder.CategorisedTransactionValuationBuilder;
import com.bt.nextgen.api.cashcategorisation.model.MemberCategorisationValuationDto;
import com.bt.nextgen.api.transactioncategorisation.model.TransactionCategoryDto;
import com.bt.nextgen.api.transactioncategorisation.service.TransactionCategoryDtoService;
import com.bt.nextgen.api.transactionhistory.model.SmsfMembersDto;
import com.bt.nextgen.api.transactionhistory.service.RetrieveSmsfMembersDtoService;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.cashcategorisation.model.CashCategorisationType;
import com.bt.nextgen.service.integration.cashcategorisation.model.Contribution;
import com.bt.nextgen.service.integration.cashcategorisation.service.CashCategorisationIntegrationService;
import com.bt.nextgen.service.integration.cashcategorisation.service.CashCategorisationIntegrationServiceFactory;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@Service
public class CategorisedTransactionValuationDtoServiceImpl implements CategorisedTransactionValuationDtoService
{
	@Autowired
	@Qualifier("CashCategorisationIntegrationServiceImpl")
	private CashCategorisationIntegrationService cashCategorisationIntegrationService;

	@Autowired
	private TransactionCategoryDtoService transactionCategoryDtoService;

	@Autowired
	private RetrieveSmsfMembersDtoService retrieveSmsfMembersDtoService;

	@Autowired
	private CashCategorisationIntegrationServiceFactory cashCategorisationIntegrationServiceFactory;


	/**
	 * Retrieve a summary of categorised transactions for a specific account
	 *
	 * @param criteriaList
	 * @param serviceErrors
	 * @return
	 */
	@Override
	public List<MemberCategorisationValuationDto> search(List<ApiSearchCriteria> criteriaList, ServiceErrors serviceErrors)
	{
		String accountId = null;
		Date financialYearDate = null;
		String strDateVal = null;
		String mode = null;
		//String category = null;

		for (ApiSearchCriteria criteria : criteriaList)
		{
			if ("accountId".equalsIgnoreCase(criteria.getProperty()))
			{
				accountId = criteria.getValue();
			}
			else if ("financialYearDate".equalsIgnoreCase(criteria.getProperty()))
			{
				strDateVal = criteria.getValue();
				financialYearDate = Date.valueOf(strDateVal);
			}
			else if ("useCache".equalsIgnoreCase(criteria.getProperty()) && "true".equalsIgnoreCase(criteria.getValue()))
			{
				mode = "CACHE";
			}
			/*else if ("category".equalsIgnoreCase(criteria.getProperty()))
			{
				category = criteria.getValue();
			}*/
		}

		//CashCategorisationType categoryType = CashCategorisationType.getByAvaloqInternalId(category);

		List<Contribution> contributions = cashCategorisationIntegrationServiceFactory.getInstance(mode).loadCashContributionsForAccount(AccountKey.valueOf(accountId), financialYearDate, CashCategorisationType.PENSION, serviceErrors);

		List <ApiSearchCriteria> searchCriteriaList = new ArrayList<>();
		List<TransactionCategoryDto> categoryList = transactionCategoryDtoService.search(searchCriteriaList, serviceErrors);

		List <ApiSearchCriteria> criteriaListForSmsfMembers = new ArrayList <ApiSearchCriteria>();
		criteriaListForSmsfMembers.add(new ApiSearchCriteria(Attribute.ACCOUNT_ID, ApiSearchCriteria.SearchOperation.EQUALS, EncodedString.fromPlainText(accountId).toString(), ApiSearchCriteria.OperationType.STRING));
		List <SmsfMembersDto> smsfList = retrieveSmsfMembersDtoService.search(criteriaListForSmsfMembers, serviceErrors);


		return CategorisedTransactionValuationBuilder.toMemberCategorisationValuationDto(contributions, categoryList, smsfList);

	}
}
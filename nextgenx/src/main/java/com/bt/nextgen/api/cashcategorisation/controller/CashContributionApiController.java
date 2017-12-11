package com.bt.nextgen.api.cashcategorisation.controller;

import com.bt.nextgen.api.contributioncaps.service.AccountContributionSummaryDtoService;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.api.operation.SearchByCriteria;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bt.nextgen.api.account.v2.model.AccountKey;
import com.bt.nextgen.api.cashcategorisation.model.CategorisableCashTransactionDto;
import com.bt.nextgen.api.cashcategorisation.service.CashContributionDtoService;
import com.bt.nextgen.api.cashcategorisation.service.CategorisableCashTransactionDtoValidator;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.UriMappingConstants;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.model.KeyedApiResponse;
import com.bt.nextgen.core.api.operation.FindByPartialKey;
import com.bt.nextgen.core.api.operation.Submit;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.integration.cashcategorisation.model.CashCategorisationAction;
import com.bt.nextgen.service.integration.cashcategorisation.model.DepositKey;
import com.bt.nextgen.service.integration.cashcategorisation.service.RetrieveCashContributionDtoService;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import java.math.BigDecimal;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


@SuppressWarnings("squid:UnusedProtectedMethod")
@Controller
@RequestMapping(value = UriMappingConstants.CURRENT_VERSION_API, produces = "application/json")
public class CashContributionApiController
{
	@Autowired
	private CashContributionDtoService cashContributionDtoService;

	@Autowired
	private RetrieveCashContributionDtoService contributionDtoService;

	@Autowired
	private AccountContributionSummaryDtoService accountContributionSummaryDtoService;

	@Autowired
	private CategorisableCashTransactionDtoValidator categorisableCashTransactionDtoValidator;

	private static final Logger logger = LoggerFactory.getLogger(com.bt.nextgen.api.cashcategorisation.controller.CashContributionApiController.class);


	@SuppressWarnings("unchecked")
	@RequestMapping(method = RequestMethod.GET, value = UriMappingConstants.CASH_CATEGORY)
	@PreAuthorize("isAuthenticated()")
	public @ResponseBody
	ApiResponse getContributions(@PathVariable(UriMappingConstants.ACCOUNT_ID_URI_MAPPING) String accId,
		@PathVariable(UriMappingConstants.DOCUMENT_ID_URI_MAPPING) String depositId,
		@RequestParam(value = "date", required = false) String date)
	{

		DepositKey key = new DepositKey(EncodedString.toPlainText(depositId), date);
		key.setAccountId(accId);
		if (StringUtils.isEmpty(accId))
		{
			throw new IllegalArgumentException("Account id is not valid");
		}

		return new FindByPartialKey <>(ApiVersion.CURRENT_VERSION, contributionDtoService, key).performOperation();
	}


	@SuppressWarnings("unchecked")
	@RequestMapping(method = RequestMethod.GET, value = UriMappingConstants.MEMBER_CONTRIBUTION_SUMMARY)
	@PreAuthorize("isAuthenticated()")
	public @ResponseBody
	ApiResponse getContributionSummary(@PathVariable(UriMappingConstants.ACCOUNT_ID_URI_MAPPING) String accId,
										@RequestParam(value = "date", required = true) String date,
									   	@RequestParam(value = "cache", required = false) String useCache)
	{
		if (StringUtils.isEmpty(accId))
		{
			throw new IllegalArgumentException("Account id is not valid");
		}

		if (StringUtils.isEmpty(date))
		{
			throw new IllegalArgumentException("Dateis not valid");
		}

		ApiSearchCriteria accountIdCriteria = new ApiSearchCriteria("accountId", ApiSearchCriteria.SearchOperation.EQUALS, EncodedString.toPlainText(accId), ApiSearchCriteria.OperationType.STRING);
		ApiSearchCriteria dateCriteria = new ApiSearchCriteria("financialYearDate", ApiSearchCriteria.SearchOperation.EQUALS, date, ApiSearchCriteria.OperationType.STRING);

		List<ApiSearchCriteria> searchCriteriaList = new ArrayList<>();
		searchCriteriaList.add(accountIdCriteria);
		searchCriteriaList.add(dateCriteria);

		if (StringUtils.isNotEmpty(useCache) && "true".equalsIgnoreCase(useCache))
		{
			ApiSearchCriteria useCacheCriteria = new ApiSearchCriteria("useCache", ApiSearchCriteria.SearchOperation.EQUALS, "true", ApiSearchCriteria.OperationType.STRING);
			searchCriteriaList.add(useCacheCriteria);
		}

		return new SearchByCriteria<>(ApiVersion.CURRENT_VERSION, accountContributionSummaryDtoService, searchCriteriaList).performOperation();
	}

	@RequestMapping(method = RequestMethod.POST, value = UriMappingConstants.SAVE_CONTRIBUTION_SPLIT)
	public @ResponseBody
	KeyedApiResponse<AccountKey> saveCashContributionSplit(@PathVariable("account-id") String accountId,
										  @PathVariable("deposit-id") String depositId,
										  @RequestBody CategorisableCashTransactionDto transaction,
										  BindingResult result,
										  HttpSession session)
	{
		logger.info("Saving cash contribution split for account: {} and docId: {}", accountId, depositId);

		validateContributionSplitParams(accountId, depositId, transaction);

		// TODO: Ensure that depositId being passed in from both move money and transaction history screens are using the same encoded id.
		if (depositId.length() > 15)
		{
			transaction.setDepositId(EncodedString.toPlainText(depositId));
		}

		if(transaction.getAction().equals(CashCategorisationAction.ADD.toString()))
		{
			//validate
			if (getDepositAmountFromSession(EncodedString.toPlainText(depositId), session) != null)
			{
				transaction.setAmount(getDepositAmountFromSession(EncodedString.toPlainText(depositId), session));
			}

			categorisableCashTransactionDtoValidator.validate(transaction, result);
			//validateTotalContributionsMatchesDepositAmount(getDepositAmountFromSession(depositId, session), transaction);
		}
		else if(transaction.getAction().equals(CashCategorisationAction.REMOVE.toString())){
			//do nothing
		}
		else if (StringUtils.isEmpty(transaction.getAction())){
			//throw exception
			throw new IllegalArgumentException("Action is not defined");
		}
		

		if (result.hasErrors())
		{
			throw new IllegalArgumentException("Request payload is invalid");
		}

		return new Submit<AccountKey, CategorisableCashTransactionDto>(ApiVersion.CURRENT_VERSION, cashContributionDtoService, null, transaction).performOperation();
	}

	private BigDecimal getDepositAmountFromSession(String docId, HttpSession session)
	{
		ConcurrentMap<String, BigDecimal> transactionHistory = (ConcurrentHashMap<String, BigDecimal>) session.getAttribute("transactionhistory");
		BigDecimal amount = null;

		if (transactionHistory != null)
		{
			amount = transactionHistory.get(docId);
		}

		return amount;
	}

	private void validateContributionSplitParams(String accountId, String depositId, CategorisableCashTransactionDto transaction)
	{
	if (transaction == null)
	{
		throw new IllegalArgumentException("Member contribution cannot be null");
	}

	if (StringUtils.isNotEmpty(accountId))
	{
		transaction.setKey(new AccountKey(EncodedString.toPlainText(accountId)));
	}
	else
	{
		throw new IllegalArgumentException("Account id cannot be null");
	}

	if (StringUtils.isNotEmpty(depositId))
	{
		transaction.setDepositId(depositId);
	}
	else
	{
		throw new IllegalArgumentException("Deposit id cannot be null");
	}
}

	/*private void validateTotalContributionsMatchesDepositAmount(BigDecimal depositAmount, CategorisableCashTransactionDto transaction)
	{
		BigDecimal amountToCompare;

		if (depositAmount == null)
		{
			throw new IllegalStateException("Deposit amount does not exist");
		}
		else
		{
			amountToCompare = depositAmount.setScale(2, RoundingMode.HALF_UP);
		}

		BigDecimal totalContributions = new BigDecimal(0);

		for (CategorisedTransactionDto contribution : transaction.getMemberContributionDtoList())
		{
			BigDecimal contributionAmount = contribution.getAmount().setScale(2, RoundingMode.HALF_UP);
			totalContributions = totalContributions.add(contributionAmount);
		}

		if (!amountToCompare.abs().equals(totalContributions.abs()))
		{
			logger.error("Deposit (doc id {}) with amount {} does not add up to total contributions ({})",
											transaction.getDepositId(), depositAmount, totalContributions);
			throw new IllegalStateException("Deposit amount does not equal to deposit amount");
		}
	}*/





	@InitBinder
	protected void initBinder(WebDataBinder binder)
	{
		binder.setValidator(categorisableCashTransactionDtoValidator);
	}
	

}
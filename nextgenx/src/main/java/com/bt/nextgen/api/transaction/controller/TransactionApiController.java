package com.bt.nextgen.api.transaction.controller;

import com.bt.nextgen.api.transaction.model.TransactionKey;
import com.bt.nextgen.api.transaction.service.TransactionDtoService;
import com.bt.nextgen.api.transaction.util.TransactionUtil;
import com.bt.nextgen.api.transactionhistory.model.CashTransactionHistoryDto;
import com.bt.nextgen.api.transactionhistory.model.SmsfMembersDto;
import com.bt.nextgen.api.transactionhistory.service.CashTransactionHistoryDtoService;
import com.bt.nextgen.api.transactionhistory.service.RetrieveSmsfMembersDtoService;
import com.bt.nextgen.api.transactionhistory.service.TransactionHistoryDtoService;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.UriMappingConstants;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.model.ResultListDto;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria.OperationType;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria.SearchOperation;
import com.bt.nextgen.core.api.operation.FindByKey;
import com.bt.nextgen.core.api.operation.Group;
import com.bt.nextgen.core.api.operation.SearchByCriteria;
import com.bt.nextgen.core.api.operation.Sort;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import com.btfin.panorama.core.security.avaloq.Constants;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping(method = RequestMethod.GET, value = UriMappingConstants.CURRENT_VERSION_API, produces = "application/json")
public class TransactionApiController
{
	@Autowired
	private TransactionDtoService transactionDtoService;

	@Autowired
	private TransactionHistoryDtoService transactionHistoryDtoService;

	@Autowired
	private CashTransactionHistoryDtoService cashTransactionHistoryDtoService;

	@Autowired
	private RetrieveSmsfMembersDtoService retrieveSmsfMembersDtoService;


	final String START_DATE = "startdate";
	final String END_DATE = "enddate";
	final String AVALOQ_DATE_FORMAT = "yyyy-MM-dd";

	/*
		// XXX This is removed because it stops the code from compiling please review
		@RequestMapping(method = RequestMethod.GET, value = UriMappingConstants.INVESTMENT_TRANSACTIONS, produces = "application/json")
		public @ResponseBody
		ApiResponse getTransactions(@PathVariable(UriMappingConstants.CLIENT_ID_URI_MAPPING) String clientId,
			@PathVariable(UriMappingConstants.PORTFOLIO_ID_URI_MAPPING) String portfolioId,
			@PathVariable(UriMappingConstants.INVESTMENT_ID_URI_MAPPING) String investmentId,
			@RequestParam(value = BeanFilter.QUERY_PARAMETER, required = false) String queryString) throws Exception
		{
			InvestmentKey key = new InvestmentKey(clientId, portfolioId, investmentId);
			return new SearchByKeyedCriteria <InvestmentKey, TransactionDto>(ApiVersion.CURRENT_VERSION,
				transactionDtoService,
				key,
				queryString).performOperation();
		}
	*/
	@RequestMapping(method = RequestMethod.GET, value = UriMappingConstants.TRANSACTION_RECENT_TEN)
	@PreAuthorize("isAuthenticated() and hasPermission(null, 'View_account_reports')")
	public @ResponseBody
	ApiResponse getRecentTenTransactions(@PathVariable(UriMappingConstants.ACCOUNT_ID_URI_MAPPING) String accountId,
		@RequestParam(value = Sort.SORT_PARAMETER, required = false) String sortOrder,
		@RequestParam(value = "cache", required = false) String useCache) throws Exception
	{
		List <ApiSearchCriteria> criteria = new ArrayList <ApiSearchCriteria>();
		criteria.add(new ApiSearchCriteria(Attribute.ACCOUNT_ID, SearchOperation.EQUALS, accountId, OperationType.STRING));
		criteria.add(new ApiSearchCriteria(Attribute.TRANSACTION_TYPE,
			SearchOperation.EQUALS,
			Attribute.RECENT_TRANSACTIONS,
			OperationType.STRING));
		if (StringUtils.isNotEmpty(useCache) && "true".equalsIgnoreCase(useCache))
		{
			ApiSearchCriteria useCacheCriteria = new ApiSearchCriteria("serviceType", ApiSearchCriteria.SearchOperation.EQUALS, "cache", ApiSearchCriteria.OperationType.STRING);
			criteria.add(useCacheCriteria);
		}
		return new Sort <>(new SearchByCriteria <>(ApiVersion.CURRENT_VERSION, transactionDtoService, criteria), sortOrder).performOperation();
	}

	/**
	 * Returns cash deposit transactions <p>
	 * Note: Performance is dire (from avaloq) when requesting large transaction set.
	 *
	 * @param accountId Encoded account key
	 * @param startDate
	 * @param endDate
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method = RequestMethod.GET, value = UriMappingConstants.PAST_TRANSACTION)
	@PreAuthorize("isAuthenticated() and @acctPermissionService.canTransact(#accountId, 'account.report.view')")
	public @ResponseBody
	ApiResponse getCashDepositTransactionHistory(@PathVariable(UriMappingConstants.ACCOUNT_ID_URI_MAPPING) String accountId,
		@RequestParam(value = START_DATE, required = false) String startDate,
		@RequestParam(value = END_DATE, required = false) String endDate, HttpSession session) throws Exception
	{
		String formattedStartDate = formatInputDate(startDate);
		String formattedEndDate = formatInputDate(endDate);

		List <ApiSearchCriteria> criteria = new ArrayList <ApiSearchCriteria>();
		criteria.add(new ApiSearchCriteria(Attribute.ACCOUNT_ID, SearchOperation.EQUALS, accountId, OperationType.STRING));
		criteria.add(new ApiSearchCriteria(Attribute.FROM_DATE, SearchOperation.EQUALS, formattedStartDate, OperationType.STRING));
		criteria.add(new ApiSearchCriteria(Attribute.TO_DATE, SearchOperation.EQUALS, formattedEndDate, OperationType.STRING));

		String sortOrder = "valDate,desc;coprSeqNr,desc;evtId,desc";

		ApiResponse response = new Sort <>(new SearchByCriteria <>(ApiVersion.CURRENT_VERSION, cashTransactionHistoryDtoService, criteria),
				sortOrder).performOperation();


		ResultListDto <CashTransactionHistoryDto> resultList = (ResultListDto <CashTransactionHistoryDto>)response.getData();
		// Add cash transactions to session for validation
		List <CashTransactionHistoryDto> cashDtoList = resultList.getResultList();
		TransactionUtil.updateCashDepositAmountsToSession(cashDtoList, session);

		return response;
	}

	private String formatInputDate(String inputDate) {
        String resultDate = inputDate;
		if (inputDate.contains("(")) {
            resultDate = inputDate.substring(0, inputDate.indexOf("("));
		}
		DateTimeFormatter df = DateTimeFormat.forPattern("EEE MMM dd yyyy HH:mm:ss 'GMT'Z");
		DateTime dtStartDate = df.withOffsetParsed().parseDateTime(resultDate.trim());
		DateTimeFormatter fmt = DateTimeFormat.forPattern(AVALOQ_DATE_FORMAT);
		return fmt.print(dtStartDate);
	}

	@RequestMapping(method = RequestMethod.GET, value = UriMappingConstants.TRANSACTION_SCHEDULED)
	@PreAuthorize("isAuthenticated() and @acctPermissionService.canTransact(#accountId, 'account.report.view')")
	public @ResponseBody
	ApiResponse getScheduledTransactions(@PathVariable(UriMappingConstants.ACCOUNT_ID_URI_MAPPING) String accountId,
		@RequestParam(value = Group.GROUP_PARAMETER, required = false) String groupBy,
		@RequestParam(value = Sort.SORT_PARAMETER, required = false) String sortOrder,
		@RequestParam(value = "payeeRequired", required = false) String payeeRequired,
		@RequestParam(value = "cache", required = false) String useCache) throws Exception
	{
		List <ApiSearchCriteria> criteria = new ArrayList <ApiSearchCriteria>();
		criteria.add(new ApiSearchCriteria(Attribute.ACCOUNT_ID, SearchOperation.EQUALS, accountId, OperationType.STRING));
		criteria.add(new ApiSearchCriteria(Attribute.TRANSACTION_TYPE,
			SearchOperation.EQUALS,
			Attribute.SCHEDULED_TRANSACTIONS,
			OperationType.STRING));
		//added to for performance issue on account overview page

		if (payeeRequired != null)
		{
			criteria.add(new ApiSearchCriteria(Constants.PAYEE_DETAIL_REQUIRED,
				SearchOperation.EQUALS,
				payeeRequired,
				OperationType.STRING));
		}

		if (StringUtils.isNotEmpty(useCache) && "true".equalsIgnoreCase(useCache))
		{
			ApiSearchCriteria useCacheCriteria = new ApiSearchCriteria("serviceType", ApiSearchCriteria.SearchOperation.EQUALS, "cache", ApiSearchCriteria.OperationType.STRING);
			criteria.add(useCacheCriteria);
		}

		return new Group <>(ApiVersion.CURRENT_VERSION, new Sort <>(new SearchByCriteria <>(ApiVersion.CURRENT_VERSION,
			transactionDtoService,
			criteria), sortOrder), groupBy).performOperation();
	}

	@RequestMapping(method = RequestMethod.GET, value = UriMappingConstants.TRANSACTION_SCHEDULED_SINGLE)
	@PreAuthorize("isAuthenticated() and @acctPermissionService.canTransact(#accountId, 'account.report.view')")
	public @ResponseBody
	ApiResponse getScheduledTransaction(@PathVariable(UriMappingConstants.ACCOUNT_ID_URI_MAPPING) String accountId,
										@PathVariable(UriMappingConstants.POSITION_ID_URI_MAPPING) String positionId) {
		return new FindByKey<>(ApiVersion.CURRENT_VERSION, transactionDtoService, new TransactionKey(accountId, positionId))
				.performOperation();
	}

	@RequestMapping(method = RequestMethod.GET, value = UriMappingConstants.STOP_SCHEDULED_TRANSACTION)
	@PreAuthorize("isAuthenticated() and ( (@acctPermissionService.canTransact(#accountId, 'account.payment.linked.create')) OR "
		+ "(@acctPermissionService.canTransact(#accountId, 'account.payment.anyone.create')) )")
	public @ResponseBody
	ApiResponse getScheduledTransactionsStopPayment(@PathVariable(UriMappingConstants.ACCOUNT_ID_URI_MAPPING) String accountId,
		@RequestParam(value = Attribute.PAYMENT_ID, required = false) String paymentId,
		@RequestParam(value = Attribute.TRANSACTION_ID, required = true) String transactionId,
		@RequestParam(value = Constants.META_TYPE, required = false) String metaType) throws Exception
	{
		List <ApiSearchCriteria> criteria = new ArrayList <ApiSearchCriteria>();
		criteria.add(new ApiSearchCriteria(Attribute.ACCOUNT_ID, SearchOperation.EQUALS, accountId, OperationType.STRING));
		criteria.add(new ApiSearchCriteria(Attribute.TRANSACTION_TYPE,
			SearchOperation.EQUALS,
			Attribute.STOP_SCHEDULED_TRANSACTIONS,
			OperationType.STRING));
		criteria.add(new ApiSearchCriteria(Attribute.PAYMENT_ID, SearchOperation.EQUALS, paymentId, OperationType.STRING));
		criteria.add(new ApiSearchCriteria(Attribute.TRANSACTION_ID, SearchOperation.EQUALS, transactionId, OperationType.STRING));
		if (metaType != null)
		{
			criteria.add(new ApiSearchCriteria(Constants.META_TYPE, SearchOperation.EQUALS, metaType, OperationType.STRING));
		}

		return new SearchByCriteria <>(ApiVersion.CURRENT_VERSION, transactionDtoService, criteria).performOperation();
	}

	@RequestMapping(method = RequestMethod.GET, value = UriMappingConstants.TRANSACTION_HISTORY)
	@PreAuthorize("isAuthenticated() and hasPermission(null, 'View_account_reports')")
	public @ResponseBody
	ApiResponse getTransactionHistory(@PathVariable(UriMappingConstants.ACCOUNT_ID_URI_MAPPING) String accountId,
		@RequestParam(value = UriMappingConstants.ASSET_CODE, required = false) String assetCode,
		@RequestParam(value = UriMappingConstants.START_DATE_PARAMETER_MAPPING, required = false) String startDateString,
		@RequestParam(value = UriMappingConstants.END_DATE_PARAMETER_MAPPING, required = false) String endDateString)
	{
		List <ApiSearchCriteria> criteria = new ArrayList <ApiSearchCriteria>();

		DateTime startDate = StringUtils.isEmpty(startDateString) ? new DateTime() : new DateTime(startDateString);
		DateTime endDate = StringUtils.isEmpty(endDateString) ? new DateTime() : new DateTime(endDateString);

		criteria.add(new ApiSearchCriteria(Attribute.ACCOUNT_ID, SearchOperation.EQUALS, accountId, OperationType.STRING));
		criteria.add(new ApiSearchCriteria(Attribute.START_DATE, SearchOperation.EQUALS, startDate.toString(), OperationType.DATE));
		criteria.add(new ApiSearchCriteria(Attribute.END_DATE, SearchOperation.EQUALS, endDate.toString(), OperationType.DATE));

		if (StringUtils.isNotEmpty(assetCode))
		{
			criteria.add(new ApiSearchCriteria(Attribute.ASSET_CODE, SearchOperation.CONTAINS, assetCode, OperationType.STRING));
		}

		return new SearchByCriteria <>(ApiVersion.CURRENT_VERSION, transactionHistoryDtoService, criteria).performOperation();
	}

	@RequestMapping(method = RequestMethod.GET, value = UriMappingConstants.SMSF_MEMBERS)
	@PreAuthorize("isAuthenticated()")
	public @ResponseBody
	ApiResponse retrieveSmsfMembers(@PathVariable(UriMappingConstants.ACCOUNT_ID_URI_MAPPING) String accId)
	{
		if (StringUtils.isEmpty(accId))
		{
			throw new IllegalArgumentException("Account id is not valid");
		}
		List <ApiSearchCriteria> criteria = new ArrayList <ApiSearchCriteria>();
		criteria.add(new ApiSearchCriteria(Attribute.ACCOUNT_ID, SearchOperation.EQUALS, accId, OperationType.STRING));

		return new SearchByCriteria <SmsfMembersDto>(ApiVersion.CURRENT_VERSION, retrieveSmsfMembersDtoService, criteria).performOperation();

	}
}

package com.bt.nextgen.api.contributioncaps.service;

import com.bt.nextgen.api.contributioncaps.builder.MemberContributionSummaryConverter;
import com.bt.nextgen.api.contributioncaps.model.MemberContributionCapValuationDto;
import com.bt.nextgen.api.contributioncaps.model.MemberContributionsCapDto;
import com.bt.nextgen.api.staticdata.model.StaticCodeDto;
import com.bt.nextgen.api.transactioncategorisation.model.TransactionCategoryDto;
import com.bt.nextgen.api.transactioncategorisation.service.TransactionCategoryDtoService;
import com.bt.nextgen.api.transactionhistory.model.SmsfMembersDto;
import com.bt.nextgen.api.transactionhistory.service.RetrieveSmsfMembersDtoService;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.cashcategorisation.model.*;
import com.bt.nextgen.service.integration.cashcategorisation.service.CashCategorisationIntegrationServiceFactory;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * Constructs a report on an SMSF account describing its members, their contribution caps, and their contribution transactions,
 * for any given financial year.
 * <p>
 * This report is generated from the following avaloq services:
 * <ul>
 *     <li>BTFG$UI_CASH_CAT.BP#CAT</li>
 *     <li>BTFG$UI_BP.BP#BP_DET</li>
 *     <li>BTFG$UI_CASH_CAT.BP#CAPS</li>
 * </ul>
 */
@Service
public class AccountContributionSummaryDtoServiceImpl implements AccountContributionSummaryDtoService
{
	private static final Logger logger = LoggerFactory.getLogger(AccountContributionSummaryDtoServiceImpl.class);

	@Autowired
	private CashCategorisationIntegrationServiceFactory cashCategorisationIntegrationServiceFactory;

	@Autowired
	private RetrieveSmsfMembersDtoService retrieveSmsMembersDtoService;

	@Autowired
	private ContributionCapDtoService contributionCapDtoService;

	@Autowired
	private TransactionCategoryDtoService transactionCategoryDtoService;

	/**
	 * Facade that will return a report of smsf account members, their contribution caps, and contribution summaries.
	 *
	 * @param criteriaList accountId: un-encoded account id
	 * @param serviceErrors
	 * @return
	 */
	public List<MemberContributionCapValuationDto> search(List <ApiSearchCriteria> criteriaList, ServiceErrors serviceErrors)
	{
		String accountId = null;
		Date financialYearDate = null;
		String strDateVal = null;
		String mode = null;

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
		}

		//ExecutorService executor = Executors.newSingleThreadExecutor();
		//ContributionCapDtoServiceCallable contributionCapCallable = new ContributionCapDtoServiceCallable(contributionCapDtoService, accountId, strDateVal);
		//logger.debug("Submitting contribution cap callable for execution");
		//Future<List<MemberContributionsCapDto>> future = executor.submit(contributionCapCallable);

		logger.debug("Retrieving cash contributions for smsf account {} for fy period {}", accountId, financialYearDate);
		final List<Contribution> contributionList = cashCategorisationIntegrationServiceFactory.getInstance(mode).loadCashContributionsForAccount(
				AccountKey.valueOf(accountId), financialYearDate, null, serviceErrors);

		List<ApiSearchCriteria> memberCriteriaList = new ArrayList<>();
		ApiSearchCriteria criteria = new ApiSearchCriteria(Attribute.ACCOUNT_ID, ApiSearchCriteria.SearchOperation.EQUALS,
									EncodedString.fromPlainText(accountId).toString(), ApiSearchCriteria.OperationType.STRING);
		memberCriteriaList.add(criteria);

		logger.debug("Retrieve smsf members");
		List<SmsfMembersDto> memberDtoList = retrieveSmsMembersDtoService.search(memberCriteriaList, serviceErrors);
		List<MemberContributionsCapDto> memberCapList = getMemberContributionCaps(accountId, strDateVal);

		//List<MemberContributionsCapDto> memberCapList = new ArrayList<>();

/*		try
		{
			memberCapList = future.get();
		}
		catch (ExecutionException | InterruptedException ee)
		{
			logger.error("Error trying to fetch account contribution caps", ee);
			serviceErrors.addError(new ServiceErrorImpl("Error trying to fetch account contribution caps"));
		}*/
		List<TransactionCategoryDto> transactionCategoryDtoList = transactionCategoryDtoService.search(new ArrayList<ApiSearchCriteria>(), serviceErrors);

		//Setting only the contribution category
		List <StaticCodeDto> contributionSubCategories = new ArrayList<StaticCodeDto>();
		for(TransactionCategoryDto categoryDto:transactionCategoryDtoList){
			if(categoryDto.getIntlId().equals(CashCategorisationType.CONTRIBUTION.getValue())){
				contributionSubCategories.addAll(categoryDto.getSubCategories());
			}
		}
		MemberContributionSummaryConverter converter = new MemberContributionSummaryConverter();
		return converter.createMemberContributionSummary(contributionList, memberDtoList, memberCapList,contributionSubCategories);
	}


	/**
	 * Get the contribution cap limits for members of this account
	 *
	 * @param accountId
	 * @param financialYearDate
	 * @return
	 */
	private List<MemberContributionsCapDto> getMemberContributionCaps(String accountId, String financialYearDate)
	{
		List<ApiSearchCriteria> capCriteriaList = new ArrayList<>();

		ApiSearchCriteria capSearchCriteria1 = new ApiSearchCriteria(Attribute.ACCOUNT_ID, ApiSearchCriteria.SearchOperation.EQUALS,
				EncodedString.fromPlainText(accountId).toString(), ApiSearchCriteria.OperationType.STRING);
		ApiSearchCriteria capSearchCriteria2 = new ApiSearchCriteria("date", ApiSearchCriteria.SearchOperation.EQUALS,
				financialYearDate, ApiSearchCriteria.OperationType.STRING);

		capCriteriaList.add(capSearchCriteria1);
		capCriteriaList.add(capSearchCriteria2);

		List<MemberContributionsCapDto> memberCapDtoList = contributionCapDtoService.search(capCriteriaList, new ServiceErrorsImpl());
		return memberCapDtoList;
	}
}

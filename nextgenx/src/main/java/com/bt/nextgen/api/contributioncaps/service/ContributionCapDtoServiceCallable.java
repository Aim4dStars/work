package com.bt.nextgen.api.contributioncaps.service;


import com.bt.nextgen.api.contributioncaps.model.MemberContributionsCapDto;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.web.controller.cash.util.Attribute;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class ContributionCapDtoServiceCallable implements Callable<List<MemberContributionsCapDto>>
{
	private String accountId;

	private String financialYearDate;

	private ContributionCapDtoService contributionCapDtoService;


	public ContributionCapDtoServiceCallable(ContributionCapDtoService contributionCapDtoService, String accountId, String financialYearDate)
	{
		this.contributionCapDtoService = contributionCapDtoService;
		this.accountId = accountId;
		this.financialYearDate = financialYearDate;
	}

	@Override
	public List<MemberContributionsCapDto> call() throws Exception
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

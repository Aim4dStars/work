package com.bt.nextgen.api.contributioncaps.service;

import java.util.ArrayList;
import java.sql.Date;
import java.util.List;

import com.bt.nextgen.api.contributioncaps.builder.MemberContributionCapConverter;
import com.bt.nextgen.service.integration.contributioncaps.service.ContributionCapIntegrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bt.nextgen.service.integration.contributioncaps.model.MemberContributionsCap;
import com.bt.nextgen.api.contributioncaps.model.MemberContributionsCapDto;
import com.bt.nextgen.api.transactionhistory.model.SmsfMembersDto;
import com.bt.nextgen.api.transactionhistory.service.RetrieveSmsfMembersDtoServiceImpl;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria.OperationType;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria.SearchOperation;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.web.controller.cash.util.Attribute;

@Service
@SuppressWarnings({"squid:S1481"})
public class ContributionCapDtoServiceImpl implements ContributionCapDtoService
{

	@Autowired
	private ContributionCapIntegrationService contributionCapIntegrationService;
	
	@Autowired
	private RetrieveSmsfMembersDtoServiceImpl retrieveSmsfMembersDtoServiceImpl;


	/**
	 *
	 * @param criteriaList date: java.sql.Date representing the financial year to retrieve data for
	 * @param serviceErrors
	 * @return
	 */
	@Override
	public List <MemberContributionsCapDto> search(List <ApiSearchCriteria> criteriaList, ServiceErrors serviceErrors)
	{
		EncodedString accountId = null;
		String accId = null;
		Date financialYearDate = null;
		List <MemberContributionsCapDto> contriCapList = new ArrayList <MemberContributionsCapDto>();

		if (!criteriaList.isEmpty())
		{
			for (ApiSearchCriteria parameter : criteriaList)
			{
				if (Attribute.ACCOUNT_ID.equals(parameter.getProperty()))
				{
					accountId = new EncodedString(parameter.getValue());
					accId = parameter.getValue();
				}
				else if ("date".equals(parameter.getProperty()))
				{
					financialYearDate = Date.valueOf(parameter.getValue());
				}
				else
				{
					throw new IllegalArgumentException("Unsupported search");
				}
			}
			if (accountId == null)
			{
				throw new IllegalArgumentException("Unsupported search");
			}
			List <MemberContributionsCap> capList = contributionCapIntegrationService.loadMemberContributionsCap(accountId.plainText(),
					financialYearDate,
				serviceErrors);

			List <ApiSearchCriteria> criteriaListForSmsfMembers = new ArrayList <ApiSearchCriteria>();
			
			criteriaListForSmsfMembers.add(new ApiSearchCriteria(Attribute.ACCOUNT_ID, SearchOperation.EQUALS, accId, OperationType.STRING));
			
			List <SmsfMembersDto> smsfList = retrieveSmsfMembersDtoServiceImpl.search(criteriaListForSmsfMembers, serviceErrors);
			
			contriCapList = toContributionCapList(capList, smsfList);

		}
		return contriCapList;
	}

	private List <MemberContributionsCapDto> toContributionCapList(List <MemberContributionsCap> capList, List<SmsfMembersDto> smsfList )
	{
		return MemberContributionCapConverter.toMemberContributionDtoList(capList, smsfList);
	}

}

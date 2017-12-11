package com.bt.nextgen.api.contributioncaps.builder;

import com.bt.nextgen.api.contributioncaps.model.ContributionSubtypeValuationDto;
import com.bt.nextgen.api.contributioncaps.model.MemberContributionCapValuationDto;
import com.bt.nextgen.api.contributioncaps.model.MemberContributionsCapDto;
import com.bt.nextgen.api.staticdata.model.StaticCodeDto;
import com.bt.nextgen.api.transactionhistory.model.SmsfMembersDto;
import com.bt.nextgen.service.integration.cashcategorisation.model.CashCategorisationSubtype;
import com.bt.nextgen.service.integration.cashcategorisation.model.Contribution;
import com.bt.nextgen.service.integration.transactionhistory.BTOrderType;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.*;

/**
 * Create List of MemberContributionCapValuationDto models (DTO) based on list of Contribution integration objects.
 */
@SuppressWarnings({"checkstyle:com.puppycrawl.tools.checkstyle.checks.sizes.ExecutableStatementCountCheck"})
public final class MemberContributionSummaryConverter
{

	public List<MemberContributionCapValuationDto> createMemberContributionSummary(List<Contribution> contributionList,
																				   List<SmsfMembersDto> smsfMemberList, List<MemberContributionsCapDto> memberCapList,List <StaticCodeDto> contriSubCategories)
	{
		// List of smsf members to display on contribution report taken from contribution cap service.
		// i.e. if a smsf member does not have any cap values, the member will not appear on the report at all,
		// regardless of whether they have contribution tranasction data or not
		String[] memberIndex = Arrays.copyOf(getMembersFromCapList(memberCapList).toArray(), getMembersFromCapList(memberCapList).toArray().length, String[].class);

		// List of categorisation subtypes
		CashCategorisationSubtype[] contributionSubCatIndex = Arrays.copyOf(CashCategorisationSubtype.getSortedCashCategorisationSubtypeListForOrderType(BTOrderType.DEPOSIT).toArray(),
																			CashCategorisationSubtype.getSortedCashCategorisationSubtypeListForOrderType(BTOrderType.DEPOSIT).toArray().length,
																			CashCategorisationSubtype[].class);

		// final member dto list
		List<MemberContributionCapValuationDto> memberContributionSummaryList = new ArrayList<>();


		for (String personId : memberIndex)
		{
			List<ContributionSubtypeValuationDto> subtypeValList = new ArrayList<>();

			SmsfMembersDto memberDto = findSmsfMemberById(personId, smsfMemberList);
			MemberContributionCapValuationDto memberSummaryDto = new MemberContributionCapValuationDto();
			memberSummaryDto.setPersonId(personId);

			if (memberDto != null)
			{
				memberSummaryDto.setFirstName(memberDto.getFirstName());
				memberSummaryDto.setLastName(memberDto.getLastName());
			}

			for (CashCategorisationSubtype subtype :  contributionSubCatIndex)
			{
				List<Contribution> contributionSubtypeList = getAllContributionSubtypesForMember(personId, subtype, contributionList);
				BigDecimal totalValue = aggregateContributionSubtypes(contributionSubtypeList);
				ContributionSubtypeValuationDto subTypeValDto = createContributionSubtypeModel(subtype.getClassification().getAvaloqInternalId(), subtype.getAvaloqInternalId(), totalValue, contributionSubtypeList);
				subtypeValList.add(subTypeValDto);
			}
			//Set contribution categories in list
			List<ContributionSubtypeValuationDto> contriSubtypeValList = new ArrayList<>();
			setContributionCategories(contriSubCategories,contriSubtypeValList,subtypeValList);
			setTotalNoOfTransactionsInSummaryDto(contriSubtypeValList, memberSummaryDto);

			memberSummaryDto.setContributionSubtypeValuationDto(contriSubtypeValList);
			memberSummaryDto.setMemberContributionsCapDto(findMemberCapbyPersonId(personId, memberCapList));
			memberContributionSummaryList.add(memberSummaryDto);
		}

		
		return memberContributionSummaryList;
	}

	
	

	private BigDecimal aggregateContributionSubtypes(List<Contribution> contributionList)
	{
		BigDecimal retVal = BigDecimal.ZERO;

		for (Contribution contribution : contributionList)
		{
			retVal = retVal.add(contribution.getAmount());
		}

		return retVal;
	}


	private List<Contribution> getAllContributionSubtypesForMember(String personId, CashCategorisationSubtype subtype, List<Contribution> contributionList)
	{
		List<Contribution> retList = new ArrayList<>();

		for (Contribution contribution : contributionList)
		{
			if (contribution.getPersonKey()!=null && contribution.getPersonKey().getId().equalsIgnoreCase(personId) && contribution.getCashCategorisationSubtype() == subtype)
			{
				retList.add(contribution);
			}
		}

		return retList;
	}


	/**
	 * Returns a list of person ids that are part of the cap list
	 * @param memberCapList
	 * @return
	 */
	private List<String> getMembersFromCapList(List<MemberContributionsCapDto> memberCapList)
	{
		List<String> memberList = new ArrayList<>();

		for (MemberContributionsCapDto memberCapDto : memberCapList)
		{
			memberList.add(memberCapDto.getPersonId());
		}

		return memberList;
	}


	private static ContributionSubtypeValuationDto createContributionSubtypeModel(String contributionClassification, String categorisationSubtype, BigDecimal amount, List <Contribution>  contributionSubtypeList)
	{
		ContributionSubtypeValuationDto subtypeValuationDto = new ContributionSubtypeValuationDto();
		subtypeValuationDto.setContributionClassification(contributionClassification);
		subtypeValuationDto.setContributionSubtype(categorisationSubtype);
		subtypeValuationDto.setAmount(amount);
		subtypeValuationDto.setNumberOfTransactions(contributionSubtypeList.size());

		return subtypeValuationDto;
	}


	private SmsfMembersDto findSmsfMemberById(String personId, List<SmsfMembersDto> smsfMemberList)
	{
		SmsfMembersDto retVal = null;

		if (smsfMemberList != null)
		{
			for (SmsfMembersDto memberDto : smsfMemberList)
			{
				if (memberDto.getPersonId().equalsIgnoreCase(personId))
				{
					retVal = memberDto;
				}
			}
		}

		return retVal;
	}

	private MemberContributionsCapDto findMemberCapbyPersonId(String personId, List<MemberContributionsCapDto> memberCapList)
	{
		MemberContributionsCapDto retVal = null;

		for (MemberContributionsCapDto memberCapDto : memberCapList)
		{
			if (memberCapDto.getPersonId().equalsIgnoreCase(personId))
			{
				retVal = memberCapDto;
			}
		}

		return retVal;
	}

	private void setContributionCategories(List <StaticCodeDto> contriSubCategories, List<ContributionSubtypeValuationDto> contriSubtypeValList, List<ContributionSubtypeValuationDto> subtypeValList)
	{
		//For contribution category
		for(StaticCodeDto codeDto : contriSubCategories)
		{
			for(ContributionSubtypeValuationDto valuationDto : subtypeValList)
			{
				if (codeDto.getIntlId().equals(valuationDto.getContributionSubtype()))
				{
					contriSubtypeValList.add(valuationDto);
				}
			}
		}
	}

	private void setTotalNoOfTransactionsInSummaryDto(List <ContributionSubtypeValuationDto>  contriSubtypeValList, MemberContributionCapValuationDto memberSummaryDto)
	{
		BigDecimal noOfTransactions = new BigDecimal(0);
		for(ContributionSubtypeValuationDto valuationDto : contriSubtypeValList)
		{
			noOfTransactions = noOfTransactions.add(new BigDecimal(valuationDto.getNumberOfTransactions()));
		}
		memberSummaryDto.setNumberOfTransactions(noOfTransactions.intValue());
	}
}
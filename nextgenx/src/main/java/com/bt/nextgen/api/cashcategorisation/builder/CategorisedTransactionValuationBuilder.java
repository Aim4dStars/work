package com.bt.nextgen.api.cashcategorisation.builder;


import com.bt.nextgen.api.cashcategorisation.model.CategorisedTransactionDto;
import com.bt.nextgen.api.cashcategorisation.model.CategorisedTransactionValuationDto;
import com.bt.nextgen.api.cashcategorisation.model.MemberCategorisationValuationDto;
import com.bt.nextgen.api.staticdata.model.StaticCodeDto;
import com.bt.nextgen.api.transactioncategorisation.model.TransactionCategoryDto;
import com.bt.nextgen.api.transactionhistory.model.SmsfMembersDto;
import com.btfin.panorama.core.security.integration.account.PersonKey;
import com.bt.nextgen.service.integration.cashcategorisation.model.*;

import java.io.Serializable;
import java.util.*;

/**
 * Utility class to convert from list of categorised transactions (integration layer)
 * to <code>MemberCategorisationValuationDto</code> (dto)
 */
public final class CategorisedTransactionValuationBuilder
{
	private CategorisedTransactionValuationBuilder()
	{
	}

	private static CategorisedTransactionDto toCategorisedTransaction(Contribution transaction)
	{
		CategorisedTransactionDto categorisedTransactionDto = new CategorisedTransactionDto();
		categorisedTransactionDto.setAmount(transaction.getAmount());
		categorisedTransactionDto.setDescription(transaction.getDescription());
		categorisedTransactionDto.setPersonId(transaction.getPersonKey().getId());
		categorisedTransactionDto.setContributionSubType(transaction.getCashCategorisationSubtype().getAvaloqInternalId());

		if (transaction.getTransactionDate() != null)
		{
			categorisedTransactionDto.setDate(transaction.getTransactionDate().toString("yyyy-MM-dd"));
		}

		return categorisedTransactionDto;
	}

	private static CategorisedTransactionValuationDto toCategorisedTransactionValuationDto(String categoryType)
	{
		CategorisedTransactionValuationDto categorisedTransactionValuationDto = new CategorisedTransactionValuationDto();
		categorisedTransactionValuationDto.setCategory(categoryType);
		categorisedTransactionValuationDto.setCategorisedTransactions(new ArrayList());

		return categorisedTransactionValuationDto;
	}

	public static List<MemberCategorisationValuationDto> toMemberCategorisationValuationDto(List<Contribution> transactions, List<TransactionCategoryDto> categoriesDto, List<SmsfMembersDto> members)
	{
		//Map<String, CategorisedTransactionValuationDto> catValuationDtoMap = new HashMap<>();
		Map<PersonKey, MemberCategorisationValuationDto> memberValuationDtoMap = new HashMap<>();

		for (Contribution transaction : transactions)
		{
			if (transaction.getPersonKey() != null)
			{
				MemberCategorisationValuationDto memberCatValuationDto = memberValuationDtoMap.get(transaction.getPersonKey());

				if (memberCatValuationDto == null)
				{
					SmsfMembersDto member = lookupMember(members, transaction.getPersonKey().getId());

					memberCatValuationDto = new MemberCategorisationValuationDto();
					memberCatValuationDto.setPersonId(transaction.getPersonKey().getId());

					if (member != null)
					{
						memberCatValuationDto.setFirstName(member.getFirstName());
						memberCatValuationDto.setLastName(member.getLastName());
					}

					memberValuationDtoMap.put(transaction.getPersonKey(), memberCatValuationDto);
				}

				TransactionCategoryDto category = lookupTransactionCategoryFromSubCategory(categoriesDto, transaction.getCashCategorisationSubtype());
				CategorisedTransactionValuationDto categoryValuation = getCategorisedTransactionValuation(memberCatValuationDto.getCategorisedTransactionValuation(), category);

				// category not instantiated yet -- create a new CategorisedTransactionValuationDto
				if (category != null && categoryValuation == null)
				{
					categoryValuation = toCategorisedTransactionValuationDto(category.getIntlId());
					memberValuationDtoMap.get(transaction.getPersonKey()).getCategorisedTransactionValuation().add(categoryValuation);
				}

				categoryValuation.getCategorisedTransactionDto().add(toCategorisedTransaction(transaction));
			}
		}
		List<MemberCategorisationValuationDto> valuationList = new ArrayList<MemberCategorisationValuationDto>();
		setValuationListForNonPensionMembers(valuationList, members, memberValuationDtoMap);
		List<MemberCategorisationValuationDto> valuationDtoList = new ArrayList(memberValuationDtoMap.values());
		if(!valuationList.isEmpty())
		{
			valuationDtoList.addAll(valuationList);
		}
		sortValuationDtoList(valuationDtoList);
		//set noOfTransactions in valuationDto for account overview
		for(MemberCategorisationValuationDto valuationDto: valuationDtoList){
			List<CategorisedTransactionValuationDto> catTranvaluationList = valuationDto.getCategorisedTransactionValuation();
			if(catTranvaluationList!=null && !catTranvaluationList.isEmpty() && catTranvaluationList.get(0)!=null && catTranvaluationList.get(0).getCategorisedTransactionDto()!=null) {
				valuationDto.setNumberOfTransactions(valuationDto.getCategorisedTransactionValuation().get(0).getCategorisedTransactionDto().size());
			}
		}
		
		return valuationDtoList;
	}

	private static void setValuationListForNonPensionMembers(List<MemberCategorisationValuationDto> valuationList,List<SmsfMembersDto> members,Map<PersonKey, MemberCategorisationValuationDto> memberValuationDtoMap)
	{
		for(SmsfMembersDto member : members)
		{
			if(!(memberValuationDtoMap.containsKey(PersonKey.valueOf(member.getPersonId()))))
			{
				MemberCategorisationValuationDto memberCatDto=new MemberCategorisationValuationDto();
				memberCatDto.setPersonId(member.getPersonId());
				memberCatDto.setFirstName(member.getFirstName());
				memberCatDto.setLastName(member.getLastName());
				valuationList.add(memberCatDto);
			}
		}
	}
	private static void sortValuationDtoList(List<MemberCategorisationValuationDto> valuationDtoList)
	{
		for(MemberCategorisationValuationDto valuationDto:valuationDtoList)
		{
			if(valuationDto.getCategorisedTransactionValuation()!=null)
			{
				for(CategorisedTransactionValuationDto catTranDto :valuationDto.getCategorisedTransactionValuation())
				{
					if(catTranDto.getCategorisedTransactionDto()!=null && catTranDto.getCategorisedTransactionDto().size()>1)
					{
						Collections.sort(catTranDto.getCategorisedTransactionDto(), new CategorisationTransactionComparator());
					}
				}
			}
		}

		Collections.sort(valuationDtoList, new MemberCategorisationValuationDtoComparator());
	}

	static class CategorisationTransactionComparator implements Comparator <CategorisedTransactionDto>, Serializable
	{
		private static final long serialVersionUID = 1L;
		@Override
		public int compare(CategorisedTransactionDto catDto1, CategorisedTransactionDto catDto2)
		{
			return catDto2.getDate().compareTo(catDto1.getDate());
		}
	}

	static class MemberCategorisationValuationDtoComparator implements Comparator <MemberCategorisationValuationDto>, Serializable
	{
		private static final long serialVersionUID = 1L;
		@Override
		public int compare(MemberCategorisationValuationDto memberValDto1, MemberCategorisationValuationDto memberValDto2)
		{
			StringBuilder memberFullName1 = new StringBuilder();
			StringBuilder memberFullName2 = new StringBuilder();
			memberFullName1.append(memberValDto1.getFirstName()).append(memberValDto1.getLastName());
			memberFullName2.append(memberValDto2.getFirstName()).append(memberValDto2.getLastName());
			return memberFullName1.toString().toUpperCase().compareTo(memberFullName2.toString().toUpperCase());
		}
	}
	private static TransactionCategoryDto lookupTransactionCategoryFromSubCategory(List<TransactionCategoryDto> categoriesDto, CashCategorisationSubtype subType)
	{
		for (TransactionCategoryDto categoryDto : categoriesDto)
		{
			List<StaticCodeDto> staticCodes = categoryDto.getSubCategories();

			for (StaticCodeDto code : staticCodes)
			{
				if (subType.getAvaloqInternalId().equalsIgnoreCase(code.getIntlId()))
				{
					return categoryDto;
				}
			}
		}

		return null;
	}

	private static SmsfMembersDto lookupMember(List<SmsfMembersDto> members, String personId)
	{
		for (SmsfMembersDto member : members)
		{
			if (member.getPersonId().equalsIgnoreCase(personId))
			{
				return member;
			}
		}

		return null;
	}

	private static CategorisedTransactionValuationDto getCategorisedTransactionValuation(List<CategorisedTransactionValuationDto> transactions, TransactionCategoryDto category)
	{
		if (transactions != null)
		{
			for (CategorisedTransactionValuationDto catTransactionValDto : transactions)
			{
				if (catTransactionValDto.getCategory().equalsIgnoreCase(category.getIntlId()))
				{
					return catTransactionValDto;
				}
			}
		}

		return null;
	}
}

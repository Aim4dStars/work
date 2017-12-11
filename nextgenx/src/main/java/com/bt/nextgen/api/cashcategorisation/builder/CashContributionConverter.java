package com.bt.nextgen.api.cashcategorisation.builder;


import com.bt.nextgen.api.cashcategorisation.model.CategorisableCashTransactionDto;
import com.bt.nextgen.api.cashcategorisation.model.CategorisedTransactionDto;
import com.bt.nextgen.api.transactioncategorisation.model.TransactionCategoryDto;
import com.bt.nextgen.service.integration.TransactionStatus;
import com.btfin.panorama.core.security.integration.account.PersonKey;
import com.bt.nextgen.service.integration.cashcategorisation.model.*;

import java.util.List;

/**
 * Utility class to convert to Cash Categorisation domain classes and dto
 */
public final class CashContributionConverter
{
	private CashContributionConverter()
	{

	}

	// Convert from {@link CategorisableCashTransactionDto} to {@link CategorisableCashTransaction}
	public static CategorisableCashTransaction toCategorisableCashTransaction(List<TransactionCategoryDto> transactionCategoryList, CategorisableCashTransactionDto cashTransactionDto)
	{
		CategorisableCashTransaction transaction = new CategorisableCashTransactionImpl();

		transaction.setAccountKey(cashTransactionDto.getKey());
		transaction.setDocId(cashTransactionDto.getDepositId());
		transaction.setTransactionCategory(cashTransactionDto.getTransactionCategory());

		Contribution memberContribution;

		for (CategorisedTransactionDto memberContributionDto : cashTransactionDto.getMemberContributionDtoList())
		{
			// Member level categorisations don't have sub-type selections from the ui
			// Need to inject sub-types for these here
			if ("member".equalsIgnoreCase(cashTransactionDto.getCategorisationLevel()))
			{
				setContributionSubCategoryOnMember(memberContributionDto, cashTransactionDto.getTransactionCategory(), transactionCategoryList);
			}

			memberContribution = toContribution(memberContributionDto);
			transaction.getContributionSplit().add(memberContribution);
		}

		return transaction;
	}

	private static void setContributionSubCategoryOnMember(CategorisedTransactionDto memberContributionDto, String transactionCategory,
															List<TransactionCategoryDto> transactionCategoryList)
	{
		for (TransactionCategoryDto category : transactionCategoryList)
		{
			if (category.getIntlId().equalsIgnoreCase(transactionCategory) && category.getSubCategories() != null && category.getSubCategories().size() == 1)
			{	
				memberContributionDto.setContributionSubType(category.getSubCategories().get(0).getIntlId());
			}
		}
	}

	private static Contribution toContribution(CategorisedTransactionDto memberContributionDto)
	{
		Contribution contribution = new MemberContributionImpl();
		contribution.setPersonKey(PersonKey.valueOf(memberContributionDto.getPersonId()));
		contribution.setAmount(memberContributionDto.getAmount());
		contribution.setCashCategorisationSubtype(CashCategorisationSubtype.getByAvaloqInternalId(memberContributionDto.getContributionSubType()));

		return contribution;
	}

	public static CategorisableCashTransactionDto toCategorisationResponseDto(TransactionStatus status, CategorisableCashTransactionDto cashTransactionDto)
	{
		cashTransactionDto.setStatus(status.isSuccessful() == true ? "success" : "error");

		return cashTransactionDto;
	}
}
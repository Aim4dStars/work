package com.bt.nextgen.api.cashcategorisation.builder;

import com.bt.nextgen.api.account.v2.model.AccountKey;
import com.bt.nextgen.api.cashcategorisation.model.CategorisableCashTransactionDto;
import com.bt.nextgen.api.cashcategorisation.model.CategorisedTransactionDto;
import com.bt.nextgen.api.staticdata.model.StaticCodeDto;
import com.bt.nextgen.api.transactioncategorisation.model.TransactionCategoryDto;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.integration.cashcategorisation.model.CashCategorisationSubtype;
import com.bt.nextgen.service.integration.cashcategorisation.model.CategorisableCashTransaction;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(MockitoJUnitRunner.class)
public class CashContributionConverterTest
{
	@Test
	public void convertFromCashCatDtoToDomain()
	{
		CategorisableCashTransaction cashTransaction = CashContributionConverter.toCategorisableCashTransaction(getTransactionCategoryList(), createStandardCashTransactionDto());
		assertNotNull(cashTransaction);
		//assertEquals(new BigDecimal("270.00"), cashTransaction.get)
		assertEquals("4444444", EncodedString.toPlainText(cashTransaction.getAccountKey().getAccountId()));
		assertEquals("333333", cashTransaction.getDocId());
		//assertEquals(CashCategorisationType.CONTRIBUTION, cashTransaction.getCategorisationType());
		assertEquals(1, cashTransaction.getContributionSplit().size());
		assertEquals("678900", cashTransaction.getContributionSplit().get(0).getPersonKey().getId());
		assertEquals(CashCategorisationSubtype.EMPLOYER, cashTransaction.getContributionSplit().get(0).getCashCategorisationSubtype());

	}

	private CategorisableCashTransactionDto createStandardCashTransactionDto()
	{
		CategorisableCashTransactionDto categorisableCashTransactionDto = new CategorisableCashTransactionDto();
		categorisableCashTransactionDto.setDepositId("333333");
		categorisableCashTransactionDto.setKey(new AccountKey(EncodedString.fromPlainText("4444444").toString()));

		ArrayList<CategorisedTransactionDto> memberContributionList = new ArrayList<>();
		memberContributionList.add(createStandardMemberContributionDto());
		categorisableCashTransactionDto.setMemberContributionDtoList(memberContributionList);

		return categorisableCashTransactionDto;
	}

	private CategorisedTransactionDto createStandardMemberContributionDto()
	{
		CategorisedTransactionDto contributionDto = new CategorisedTransactionDto();
		contributionDto.setAmount(new BigDecimal("275.00"));
		contributionDto.setContributionSubType("empl");
		contributionDto.setPersonId("678900");

		return contributionDto;
	}

	private List<TransactionCategoryDto> getTransactionCategoryList()
	{
		StaticCodeDto subCat1 = new StaticCodeDto();
		subCat1.setIntlId("pension");
		subCat1.setLabel("Pension");
		subCat1.setListName("pension");
		subCat1.setId("pension");

		List<StaticCodeDto> subCatList = new ArrayList<>();
		subCatList.add(subCat1);

		TransactionCategoryDto transactionCategoryDto1 = new TransactionCategoryDto();
		transactionCategoryDto1.setIntlId("pension");
		transactionCategoryDto1.setTransactionMetaType("payment");
		transactionCategoryDto1.setCategorisationLevel("member");
		transactionCategoryDto1.setLabel("Pension");
		//transactionCategoryDto1.setType("transactionCategory");
		transactionCategoryDto1.setSubCategories(subCatList);

		List<TransactionCategoryDto> transactionCategoryDtoList = new ArrayList<>();
		transactionCategoryDtoList.add(transactionCategoryDto1);

		return transactionCategoryDtoList;
	}

}
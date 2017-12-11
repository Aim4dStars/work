package com.bt.nextgen.api.cashcategorisation.service;

import com.bt.nextgen.api.account.v2.model.AccountKey;
import com.bt.nextgen.api.cashcategorisation.model.CategorisableCashTransactionDto;
import com.bt.nextgen.api.cashcategorisation.model.CategorisedTransactionDto;
import com.bt.nextgen.api.staticdata.model.StaticCodeDto;
import com.bt.nextgen.api.transactioncategorisation.model.TransactionCategoryDto;
import com.bt.nextgen.api.transactioncategorisation.service.TransactionCategoryDtoServiceImpl;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.avaloq.TransactionStatusImpl;
import com.bt.nextgen.service.integration.TransactionStatus;
import com.bt.nextgen.service.integration.cashcategorisation.model.CashCategorisationAction;
import com.bt.nextgen.service.integration.cashcategorisation.model.CategorisableCashTransactionImpl;
import com.bt.nextgen.service.integration.cashcategorisation.service.CashCategorisationIntegrationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.any;

@RunWith(MockitoJUnitRunner.class)
public class CashContributionDtoServiceImplTest
{
	@InjectMocks
	CashContributionDtoServiceImpl cashContributionDtoService;

	@Mock
	CashCategorisationIntegrationService cashCatIntegrationService;

	@Mock
	TransactionCategoryDtoServiceImpl transactionCategoryDtoServiceImpl;

	List <TransactionCategoryDto> tranCatDtoList;

	@Before
	public void setUp()
	{
		TransactionStatus status = new TransactionStatusImpl();
		status.setSuccessful(true);


		tranCatDtoList = new ArrayList <TransactionCategoryDto>();
		TransactionCategoryDto dto1 = new TransactionCategoryDto();
		dto1.setIntlId("contri");
		List <StaticCodeDto> subCategories1 = new ArrayList <StaticCodeDto>();
		StaticCodeDto staticDto1 = new StaticCodeDto();
		staticDto1.setId("1");
		staticDto1.setIntlId("empl");

		StaticCodeDto staticDto2 = new StaticCodeDto();
		staticDto2.setId("4");
		staticDto2.setIntlId("spouse_chld_contri");
		subCategories1.add(staticDto1);
		subCategories1.add(staticDto2);
		dto1.setSubCategories(subCategories1);

		TransactionCategoryDto dto2 = new TransactionCategoryDto();
		dto2.setIntlId("purch");
		List <StaticCodeDto> subCategories2 = new ArrayList <StaticCodeDto>();
		StaticCodeDto staticDto3 = new StaticCodeDto();
		staticDto3.setId("27");
		staticDto3.setIntlId("td");
		subCategories2.add(staticDto3);
		dto2.setSubCategories(subCategories2);

		tranCatDtoList.add(dto1);
		tranCatDtoList.add(dto2);

		Mockito.when(cashCatIntegrationService.saveOrUpdate(Mockito.eq(CashCategorisationAction.ADD),
															Mockito.any(CategorisableCashTransactionImpl.class))).thenReturn(status);

		Mockito.when(transactionCategoryDtoServiceImpl.search(Mockito.any(ArrayList.class), Mockito.any(ServiceErrors.class)))
				.thenReturn(tranCatDtoList);
	}

	@Test
	public void testSubmitCategorisedCashTransaction()
	{
		CategorisedTransactionDto memberContributionDto = new CategorisedTransactionDto();
		memberContributionDto.setPersonId("12345");
		memberContributionDto.setContributionSubType("empl");
		memberContributionDto.setAmount(new BigDecimal("500.00"));

		List<CategorisedTransactionDto> contributionSplitList = new ArrayList<>();
		contributionSplitList.add(memberContributionDto);

		CategorisableCashTransactionDto cashTransactionDto = new CategorisableCashTransactionDto();
		cashTransactionDto.setMemberContributionDtoList(contributionSplitList);
		cashTransactionDto.setKey(new AccountKey(EncodedString.fromPlainText("98765").toString()));
		cashTransactionDto.setDepositId("66666");

		CategorisableCashTransactionDto responseDto = cashContributionDtoService.submit(cashTransactionDto, new ServiceErrorsImpl());
		assertEquals("success", responseDto.getStatus());
		assertEquals("66666", responseDto.getDepositId());
		assertEquals("98765", EncodedString.toPlainText(responseDto.getKey().getAccountId()));
	}
}
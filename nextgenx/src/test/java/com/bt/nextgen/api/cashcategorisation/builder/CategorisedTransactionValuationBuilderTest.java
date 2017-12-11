package com.bt.nextgen.api.cashcategorisation.builder;

import com.bt.nextgen.api.account.v2.model.AccountKey;
import com.bt.nextgen.api.cashcategorisation.model.MemberCategorisationValuationDto;
import com.bt.nextgen.api.staticdata.model.StaticCodeDto;
import com.bt.nextgen.api.transactioncategorisation.model.TransactionCategoryDto;
import com.bt.nextgen.api.transactionhistory.model.SmsfMembersDto;
import com.bt.nextgen.service.integration.cashcategorisation.model.CashCategorisationSubtype;
import com.bt.nextgen.service.integration.cashcategorisation.model.Contribution;
import com.bt.nextgen.service.integration.cashcategorisation.model.ContributionClassification;
import com.bt.nextgen.service.integration.cashcategorisation.model.MemberContributionImpl;
import com.btfin.panorama.core.security.integration.account.PersonKey;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertNotNull;

@RunWith(MockitoJUnitRunner.class)
public class CategorisedTransactionValuationBuilderTest
{
	@Test
	public void testConvertOfSingleTransactionRecord()
	{
		List<Contribution> contributionList = new ArrayList<>();
		contributionList.add(createSingleContribution());

		List<MemberCategorisationValuationDto> memberCatValDto = CategorisedTransactionValuationBuilder.toMemberCategorisationValuationDto(contributionList, getTransactionCategoryList(), getSmsfMemberList());
	}

	@Test
	public void convertSingleTransactionRecordWithZeroAmountAndPersonId()
	{
		//To check that the members are added inspite of zero pension amount
		List<Contribution> contributionList = new ArrayList<>();
		List<MemberCategorisationValuationDto> memberCatValDto = CategorisedTransactionValuationBuilder.toMemberCategorisationValuationDto(contributionList, new ArrayList<TransactionCategoryDto>(), getSmsfMemberList());
		assertThat(memberCatValDto.size(), equalTo(1));
		assertNotNull(memberCatValDto.get(0).getPersonId());
		assertThat(memberCatValDto.get(0).getTotalAmount(), equalTo(BigDecimal.ZERO));
	}

	private Contribution createSingleContribution()
	{
		Contribution contribution1 = new MemberContributionImpl();
		contribution1.setAccountKey(new AccountKey("12345"));
		contribution1.setPersonKey(PersonKey.valueOf("666789"));
		contribution1.setCashCategorisationSubtype(CashCategorisationSubtype.PENSION);
		contribution1.setContributionClassification(ContributionClassification.OTHER);
		contribution1.setAmount(new BigDecimal("150.50"));
		contribution1.setDescription("First pension payment");
		contribution1.setDocId("999091");
		//contribution1.setTransactionDate(new DateTime("2015-01-15"));

		return contribution1;
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

	private List<SmsfMembersDto> getSmsfMemberList()
	{
		SmsfMembersDto member1 = new SmsfMembersDto();
		member1.setPersonId("666789");
		member1.setLastName("Lee");
		member1.setFirstName("Eric");
		member1.setDateOfBirth("2010-01-30");

		List<SmsfMembersDto> memberList = new ArrayList<>();
		memberList.add(member1);

		return memberList;
	}
}
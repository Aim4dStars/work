package com.bt.nextgen.api.contributioncaps.service;

import com.bt.nextgen.api.contributioncaps.model.MemberContributionCapValuationDto;
import com.bt.nextgen.api.contributioncaps.model.MemberContributionsCapDto;
import com.bt.nextgen.api.staticdata.model.StaticCodeDto;
import com.bt.nextgen.api.transactioncategorisation.model.TransactionCategoryDto;
import com.bt.nextgen.api.transactioncategorisation.service.TransactionCategoryDtoServiceImpl;
import com.bt.nextgen.api.transactionhistory.model.SmsfMembersDto;
import com.bt.nextgen.api.transactionhistory.service.RetrieveSmsfMembersDtoService;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.btfin.panorama.core.security.integration.account.PersonKey;
import com.bt.nextgen.service.integration.cashcategorisation.model.*;
import com.bt.nextgen.service.integration.cashcategorisation.service.CashCategorisationIntegrationService;
import com.bt.nextgen.service.integration.cashcategorisation.service.CashCategorisationIntegrationServiceFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;

@RunWith(MockitoJUnitRunner.class)
public class AccountContributionSummaryDtoServiceImplTest
{
	@Mock
	private CashCategorisationIntegrationService cashCategorisationIntegrationService;

	@Mock
	private CashCategorisationIntegrationServiceFactory cashCategorisationIntegrationServiceFactory;

	@Mock
	private RetrieveSmsfMembersDtoService retrieveSmsMembersDtoService;

	@Mock
	private ContributionCapDtoService contributionCapDtoService;

	@Mock
	private TransactionCategoryDtoServiceImpl transactionCategoryDtoServiceImpl;


	@InjectMocks
	private AccountContributionSummaryDtoServiceImpl accountContributionSummaryDto;

	List <TransactionCategoryDto> tranCatDtoList;

	@Before
	public void initTestData()
	{
		tranCatDtoList = new ArrayList <TransactionCategoryDto>();
		TransactionCategoryDto dto1 = new TransactionCategoryDto();
		dto1.setIntlId("contri");
		List <StaticCodeDto> subCategories1 = new ArrayList <StaticCodeDto>();
		StaticCodeDto staticDto1 = new StaticCodeDto();
		staticDto1.setId("1");
		staticDto1.setIntlId("empl");

		StaticCodeDto staticDto2 = new StaticCodeDto();
		staticDto2.setId("3");
		staticDto2.setIntlId("prsnl_nconc");

		StaticCodeDto staticDto4 = new StaticCodeDto();
		staticDto4.setId("2");
		staticDto4.setIntlId("prsnl_conc");

		StaticCodeDto staticDto5 = new StaticCodeDto();
		staticDto5.setId("9");
		staticDto5.setIntlId("prsnl_injury_elect");

		StaticCodeDto staticDto6 = new StaticCodeDto();
		staticDto6.setId("8");
		staticDto6.setIntlId("frn_super_non_assble");


		subCategories1.add(staticDto1);
		subCategories1.add(staticDto2);
		subCategories1.add(staticDto4);
		subCategories1.add(staticDto5);
		subCategories1.add(staticDto6);
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


		Mockito.when(cashCategorisationIntegrationService.loadCashContributionsForAccount(any(AccountKey.class),
				any(Date.class), any(CashCategorisationType.class), any(ServiceErrorsImpl.class))).thenReturn(createMemberContributionCapValuationDto());

		Mockito.when(retrieveSmsMembersDtoService.search(any(List.class), any(ServiceErrorsImpl.class)))
				.thenReturn(createSmsfMemberDtoList());

		Mockito.when(contributionCapDtoService.search(any(List.class), any(ServiceErrorsImpl.class)))
				.thenReturn(createMemberContributionCapDtoList());

		Mockito.when(transactionCategoryDtoServiceImpl.search(any(ArrayList.class), any(ServiceErrors.class)))
				.thenReturn(tranCatDtoList);

		Mockito.when(cashCategorisationIntegrationServiceFactory.getInstance(any(String.class))).thenReturn(cashCategorisationIntegrationService);
	}


	@Test
	public void testContributionSummary()
	{
		ApiSearchCriteria accountIdCriteria = new ApiSearchCriteria("accountId", ApiSearchCriteria.SearchOperation.EQUALS, "1234566", ApiSearchCriteria.OperationType.STRING);
		ApiSearchCriteria dateCriteria = new ApiSearchCriteria("financialYearDate", ApiSearchCriteria.SearchOperation.EQUALS, "2015-07-30", ApiSearchCriteria.OperationType.STRING);

		List<ApiSearchCriteria> criteriaList = new ArrayList<>();
		criteriaList.add(accountIdCriteria);
		criteriaList.add(dateCriteria);

		List<MemberContributionCapValuationDto> accountContributionSummaryDtoList = accountContributionSummaryDto.search(criteriaList, new ServiceErrorsImpl());

		assertNotNull(accountContributionSummaryDtoList);
		assertEquals(3, accountContributionSummaryDtoList.size());

		assertEquals("23456", accountContributionSummaryDtoList.get(0).getPersonId());
		assertEquals("Satayam", accountContributionSummaryDtoList.get(0).getFirstName());
		assertEquals("Singh", accountContributionSummaryDtoList.get(0).getLastName());
		assertEquals(new BigDecimal(2000), accountContributionSummaryDtoList.get(0).getConcessionalTotal());
		assertEquals(new BigDecimal(5000), accountContributionSummaryDtoList.get(0).getNonConcessionalTotal());
		assertEquals(new BigDecimal(0), accountContributionSummaryDtoList.get(0).getOtherContributionsTotal());
		assertEquals(new BigDecimal(7000), accountContributionSummaryDtoList.get(0).getTotalContributions());
		assertEquals(new BigDecimal(198000), accountContributionSummaryDtoList.get(0).getConcessionalAvailableBalance());
		assertEquals(new BigDecimal(245000), accountContributionSummaryDtoList.get(0).getNonConcessionalAvailableBalance());

		assertEquals(5, accountContributionSummaryDtoList.get(0).getContributionSubtypeValuationDto().size());
		assertEquals("empl", accountContributionSummaryDtoList.get(0).getContributionSubtypeValuationDto().get(0).getContributionSubtype());

		assertEquals("12345", accountContributionSummaryDtoList.get(1).getPersonId());
		assertEquals("Albert", accountContributionSummaryDtoList.get(1).getFirstName());
		assertEquals("Hirawan", accountContributionSummaryDtoList.get(1).getLastName());
		assertEquals(new BigDecimal(1500), accountContributionSummaryDtoList.get(1).getConcessionalTotal());
		assertEquals(new BigDecimal(4000), accountContributionSummaryDtoList.get(1).getNonConcessionalTotal());
		assertEquals(new BigDecimal(9000), accountContributionSummaryDtoList.get(1).getOtherContributionsTotal());
		assertEquals(new BigDecimal(14500), accountContributionSummaryDtoList.get(1).getTotalContributions());
		assertEquals(new BigDecimal(198500), accountContributionSummaryDtoList.get(1).getConcessionalAvailableBalance());
		assertEquals(new BigDecimal(296000), accountContributionSummaryDtoList.get(1).getNonConcessionalAvailableBalance());
		assertEquals(5, accountContributionSummaryDtoList.get(1).getContributionSubtypeValuationDto().size());

		assertEquals("34676", accountContributionSummaryDtoList.get(2).getPersonId());
		assertEquals("Eric", accountContributionSummaryDtoList.get(2).getFirstName());
		assertEquals("Lee", accountContributionSummaryDtoList.get(2).getLastName());
		assertEquals(new BigDecimal(0), accountContributionSummaryDtoList.get(2).getConcessionalTotal());
		assertEquals(new BigDecimal(6455), accountContributionSummaryDtoList.get(2).getNonConcessionalTotal());
		assertEquals(new BigDecimal(0), accountContributionSummaryDtoList.get(2).getOtherContributionsTotal());
		assertEquals(new BigDecimal(6455), accountContributionSummaryDtoList.get(2).getTotalContributions());
		assertEquals(new BigDecimal(200000), accountContributionSummaryDtoList.get(2).getConcessionalAvailableBalance());
		assertEquals(new BigDecimal(343545), accountContributionSummaryDtoList.get(2).getNonConcessionalAvailableBalance());
		assertEquals(5, accountContributionSummaryDtoList.get(2).getContributionSubtypeValuationDto().size());
	}

	private List<Contribution> createMemberContributionCapValuationDto()
	{
		MemberContributionImpl contribution1 = createMemberContributionImpl(ContributionClassification.CONCESSIONAL, CashCategorisationSubtype.EMPLOYER, 1500, "12345");
		MemberContributionImpl contribution2 = createMemberContributionImpl(ContributionClassification.CONCESSIONAL, CashCategorisationSubtype.EMPLOYER, 2000, "23456");
		MemberContributionImpl contribution3 = createMemberContributionImpl(ContributionClassification.OTHER, CashCategorisationSubtype.PERSONAL_INJURY_ELECTION, 9000, "12345");
		MemberContributionImpl contribution4 = createMemberContributionImpl(ContributionClassification.NON_CONCESSIONAL, CashCategorisationSubtype.FOREIGN_SUPER_NON_ASSESSABLE, 455, "34676");
		MemberContributionImpl contribution5 = createMemberContributionImpl(ContributionClassification.NON_CONCESSIONAL, CashCategorisationSubtype.PERSONAL_NON_CONCESSIONAL, 4000, "12345");
		MemberContributionImpl contribution6 = createMemberContributionImpl(ContributionClassification.NON_CONCESSIONAL, CashCategorisationSubtype.PERSONAL_NON_CONCESSIONAL, 5000, "23456");
		MemberContributionImpl contribution7 = createMemberContributionImpl(ContributionClassification.NON_CONCESSIONAL, CashCategorisationSubtype.PERSONAL_NON_CONCESSIONAL, 6000, "34676");
		List<Contribution> contributionSubTypeList = new ArrayList<>();

		contributionSubTypeList.add(contribution1);
		contributionSubTypeList.add(contribution2);
		contributionSubTypeList.add(contribution3);
		contributionSubTypeList.add(contribution4);
		contributionSubTypeList.add(contribution5);
		contributionSubTypeList.add(contribution6);
		contributionSubTypeList.add(contribution7);


		return contributionSubTypeList;
	}

	private MemberContributionImpl createMemberContributionImpl(ContributionClassification classification, CashCategorisationSubtype subtype, int amount, String personId)
	{
		MemberContributionImpl contribution = new MemberContributionImpl();
		contribution.setAmount(new BigDecimal(amount));
		contribution.setCashCategorisationSubtype(subtype);
		contribution.setContributionClassification(classification);
		contribution.setPersonKey(PersonKey.valueOf(personId));
		return contribution;
	}

	private List<SmsfMembersDto> createSmsfMemberDtoList()
	{
		SmsfMembersDto member1 = new SmsfMembersDto(new com.bt.nextgen.api.account.v2.model.AccountKey("64567"), "34676", "Eric", "Lee");
		SmsfMembersDto member2 = new SmsfMembersDto(new com.bt.nextgen.api.account.v2.model.AccountKey("64567"), "12345", "Albert", "Hirawan");
		SmsfMembersDto member3 = new SmsfMembersDto(new com.bt.nextgen.api.account.v2.model.AccountKey("64567"), "23456", "Satayam", "Singh");

		List<SmsfMembersDto> resultList = new ArrayList<>();
		resultList.add(member1);
		resultList.add(member2);
		resultList.add(member3);

		return resultList;
	}

	private List<MemberContributionsCapDto> createMemberContributionCapDtoList()
	{
		MemberContributionsCapDto memberCapDto1 = new MemberContributionsCapDto();
		memberCapDto1.setPersonId("23456");
		memberCapDto1.setFinancialYear("2015-07-01");
		memberCapDto1.setDateOfBirth("1990-01-01");
		memberCapDto1.setAge(30);
		memberCapDto1.setNonConcessionalCap(new BigDecimal("250000"));
		memberCapDto1.setConcessionalCap(new BigDecimal("200000"));

		MemberContributionsCapDto memberCapDto2 = new MemberContributionsCapDto();
		memberCapDto2.setPersonId("12345");
		memberCapDto2.setFinancialYear("2015-07-01");
		memberCapDto2.setDateOfBirth("1990-01-01");
		memberCapDto2.setAge(30);
		memberCapDto2.setNonConcessionalCap(new BigDecimal("300000"));
		memberCapDto2.setConcessionalCap(new BigDecimal("200000"));

		MemberContributionsCapDto memberCapDto3 = new MemberContributionsCapDto();
		memberCapDto3.setPersonId("34676");
		memberCapDto3.setFinancialYear("2015-07-01");
		memberCapDto3.setDateOfBirth("1990-01-01");
		memberCapDto3.setAge(30);
		memberCapDto3.setNonConcessionalCap(new BigDecimal("350000"));
		memberCapDto3.setConcessionalCap(new BigDecimal("200000"));

		List<MemberContributionsCapDto> memberCapList = new ArrayList<>();
		memberCapList.add(memberCapDto1);
		memberCapList.add(memberCapDto2);
		memberCapList.add(memberCapDto3);

		return memberCapList;
	}
}

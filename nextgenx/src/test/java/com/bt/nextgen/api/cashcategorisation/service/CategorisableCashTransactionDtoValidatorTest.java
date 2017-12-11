package com.bt.nextgen.api.cashcategorisation.service;

import com.bt.nextgen.api.account.v2.model.AccountKey;
import com.bt.nextgen.api.cashcategorisation.model.CategorisableCashTransactionDto;
import com.bt.nextgen.api.cashcategorisation.model.CategorisedTransactionDto;
import com.bt.nextgen.api.transactionhistory.model.SmsfMembersDto;
import com.bt.nextgen.api.transactionhistory.service.RetrieveSmsfMembersDtoService;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.integration.cashcategorisation.model.CashCategorisationSubtype;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.validation.Errors;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class CategorisableCashTransactionDtoValidatorTest
{
	@Mock
	RetrieveSmsfMembersDtoService smsfMemberDtoService;

	@InjectMocks
	CategorisableCashTransactionDtoValidator validator;


	@Before
	public void setup()
	{

	}

	@Test
	public void testCategorisationAtMemberLevelWithInvalidSmsfMembers()
	{
		ArrayList members = new ArrayList<>();
		members.add(createNonExistingSmsfMember());
		Mockito.when(smsfMemberDtoService.search(any(ArrayList.class), any(ServiceErrorsImpl.class))).thenReturn(members);

		CategorisableCashTransactionDto transaction = createCategorisedTransactionAtMemberLevel();

		Errors errors = Mockito.mock(Errors.class);
		validator.validate(transaction, errors);

		assertThat(errors.getAllErrors().size() == 0);
		verify(errors, times(1)).reject("One or more person id's nominated do not belong to this smsf account");
		verify(errors, times(0)).rejectValue(Mockito.anyString(), Mockito.anyString());
	}

	@Test
	public void testCategorisationAtMemberLevelWithInvalidCashCatType()
	{
		ArrayList members = new ArrayList<>();
		members.addAll(createExistingSmsfMember());
		Mockito.when(smsfMemberDtoService.search(any(ArrayList.class), any(ServiceErrorsImpl.class))).thenReturn(members);

		CategorisableCashTransactionDto transaction = createCategorisedTransactionAtMemberLevel();
		transaction.setTransactionCategory("random_category");

		Errors errors = Mockito.mock(Errors.class);
		validator.validate(transaction, errors);

		assertThat(errors.getAllErrors().size() == 0);
		verify(errors, times(1)).reject("categorisation type is not valid");
		verify(errors, times(0)).rejectValue(Mockito.anyString(), Mockito.anyString());
	}

	@Test
	public void testCategorisationAtMemberLevelWithGoodArguments()
	{
		ArrayList members = new ArrayList<>();
		members.addAll(createExistingSmsfMember());
		Mockito.when(smsfMemberDtoService.search(any(ArrayList.class), any(ServiceErrorsImpl.class))).thenReturn(members);

		CategorisableCashTransactionDto transaction = createCategorisedTransactionAtMemberLevel();

		Errors errors = Mockito.mock(Errors.class);
		validator.validate(transaction, errors);

		assertThat(errors.getAllErrors().size() == 0);
		verify(errors, times(0)).reject(Mockito.anyString());
		verify(errors, times(0)).rejectValue(Mockito.anyString(), Mockito.anyString());
	}

	@Test
	public void testCategorisationAtFundLevelWithGoodArguments()
	{
		ArrayList members = new ArrayList<>();
		members.addAll(createExistingSmsfMember());
		Mockito.when(smsfMemberDtoService.search(any(ArrayList.class), any(ServiceErrorsImpl.class))).thenReturn(members);

		CategorisableCashTransactionDto transaction = createCategorisedTransactionAtFundLevel();

		Errors errors = Mockito.mock(Errors.class);
		validator.validate(transaction, errors);

		assertThat(errors.getAllErrors().size() == 0);
		verify(errors, times(0)).reject(Mockito.anyString());
		verify(errors, times(0)).rejectValue(Mockito.anyString(), Mockito.anyString());
	}

	@Test
	public void testCategorisationAtFundLevelWithInvalidSubtypes()
	{
		ArrayList members = new ArrayList<>();
		members.addAll(createExistingSmsfMember());
		Mockito.when(smsfMemberDtoService.search(any(ArrayList.class), any(ServiceErrorsImpl.class))).thenReturn(members);

		CategorisableCashTransactionDto transaction = createCategorisedTransactionAtFundLevel();
		transaction.getMemberContributionDtoList().get(0).setContributionSubType("random_subtype");
		transaction.getMemberContributionDtoList().get(1).setContributionSubType("random_subtype2");

		Errors errors = Mockito.mock(Errors.class);
		validator.validate(transaction, errors);

		assertThat(errors.getAllErrors().size() == 0);
		verify(errors, times(0)).reject(Mockito.anyString());
		verify(errors, times(1)).rejectValue(Mockito.anyString(), Mockito.anyString());
	}

	@Test
	public void testCategorisationAtFundLevelWithAmountsNotAddingUp()
	{
		ArrayList members = new ArrayList<>();
		members.addAll(createExistingSmsfMember());
		Mockito.when(smsfMemberDtoService.search(any(ArrayList.class), any(ServiceErrorsImpl.class))).thenReturn(members);

		CategorisableCashTransactionDto transaction = createCategorisedTransactionAtFundLevel();
		transaction.setAmount(new BigDecimal(20000));

		Errors errors = Mockito.mock(Errors.class);
		validator.validate(transaction, errors);

		assertThat(errors.getAllErrors().size() == 0);
		verify(errors, times(1)).reject(Mockito.anyString());
		verify(errors, times(0)).rejectValue(Mockito.anyString(), Mockito.anyString());
	}


	private CategorisableCashTransactionDto createCategorisedTransactionAtFundLevel()
	{
		CategorisedTransactionDto memberContributionDto1 = new CategorisedTransactionDto();
		memberContributionDto1.setAmount(new BigDecimal("7000"));
		memberContributionDto1.setContributionSubType(CashCategorisationSubtype.PRTY_INSURANCE.getAvaloqInternalId());

		CategorisedTransactionDto memberContributionDto2 = new CategorisedTransactionDto();
		memberContributionDto2.setAmount(new BigDecimal("9000"));
		memberContributionDto2.setContributionSubType(CashCategorisationSubtype.PRTY_OTHER.getAvaloqInternalId());

		List<CategorisedTransactionDto> memberContributionList = new ArrayList<>();
		memberContributionList.add(memberContributionDto1);
		memberContributionList.add(memberContributionDto2);

		CategorisableCashTransactionDto transaction = new CategorisableCashTransactionDto();
		transaction.setDepositId("12345");
		transaction.setKey(new AccountKey("234435"));
		transaction.setAction("add");
		transaction.setAmount(new BigDecimal(16000));
		transaction.setCategorisationLevel("fund");
		transaction.setTransactionCategory("prty");
		transaction.setMemberContributionDtoList(memberContributionList);

		return transaction;
	}


	private CategorisableCashTransactionDto createCategorisedTransactionAtMemberLevel()
	{
		CategorisedTransactionDto memberContributionDto1 = new CategorisedTransactionDto();
		memberContributionDto1.setAmount(new BigDecimal("5000"));
		memberContributionDto1.setPersonId("223344");
		memberContributionDto1.setContributionSubType(CashCategorisationSubtype.PENSION.getAvaloqInternalId());

		CategorisedTransactionDto memberContributionDto2 = new CategorisedTransactionDto();
		memberContributionDto2.setAmount(new BigDecimal("6500"));
		memberContributionDto2.setPersonId("556677");
		memberContributionDto2.setContributionSubType(CashCategorisationSubtype.PENSION.getAvaloqInternalId());

		List<CategorisedTransactionDto> memberContributionList = new ArrayList<>();
		memberContributionList.add(memberContributionDto1);
		memberContributionList.add(memberContributionDto2);

		CategorisableCashTransactionDto transaction = new CategorisableCashTransactionDto();
		transaction.setDepositId("12345");
		transaction.setKey(new AccountKey("234435"));
		transaction.setAction("add");
		transaction.setAmount(new BigDecimal(11500));
		transaction.setCategorisationLevel("member");
		transaction.setTransactionCategory("pension");
		transaction.setMemberContributionDtoList(memberContributionList);

		return transaction;
	}

	private List<SmsfMembersDto> createExistingSmsfMember()
	{
		SmsfMembersDto member1 = new SmsfMembersDto();
		member1.setKey(new AccountKey("223344"));
		member1.setPersonId("223344");
		member1.setDateOfBirth("2015-09-09");
		member1.setFirstName("Lee");
		member1.setLastName("Eric");

		SmsfMembersDto member2 = new SmsfMembersDto();
		member2.setKey(new AccountKey("556677"));
		member2.setPersonId("556677");
		member2.setDateOfBirth("2015-09-09");
		member2.setFirstName("Albert");
		member2.setLastName("Hirawan");

		List<SmsfMembersDto> members = new ArrayList<>();
		members.add(member1);
		members.add(member2);
		return members;
	}

	private SmsfMembersDto createNonExistingSmsfMember()
	{
		SmsfMembersDto member1 = new SmsfMembersDto();
		member1.setKey(new AccountKey("33333"));
		member1.setPersonId("33333");
		member1.setDateOfBirth("2015-09-09");
		member1.setFirstName("Lee");
		member1.setLastName("Eric");

		return member1;
	}
}

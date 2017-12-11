package com.bt.nextgen.web.conversion;

import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.hamcrest.core.Is;
import org.joda.time.DateTime;
import org.junit.Test;

import com.bt.nextgen.addressbook.PayeeModel;
import com.bt.nextgen.payments.domain.PayeeType;
import com.bt.nextgen.payments.domain.Payment;
import com.bt.nextgen.payments.domain.PaymentFrequency;
import com.bt.nextgen.payments.domain.PaymentRepeatsEnd;
import com.bt.nextgen.payments.repository.Bsb;
import com.bt.nextgen.payments.web.model.ConfirmDepositConversation;
import com.bt.nextgen.payments.web.model.MoveMoneyModel;
import com.bt.nextgen.portfolio.web.model.CashAccountModel;
import com.bt.nextgen.reports.domain.IncomeTransaction;


public class DepositConversationToPaymentRequestTest
{
	DepositConversationToPaymentRequest converter = new DepositConversationToPaymentRequest();

	@Test
	public void testDepositConversationToPaymentInstruction()
	{
		MoveMoneyModel conversation = new MoveMoneyModel();
		conversation.setAmount(BigDecimal.valueOf(123.45));
		conversation.setDate("14 Feb 2013");
		conversation.setDescription("description");
		conversation.setPayeeId("177");
		conversation.setRecurring(false);
		conversation.setEndRepeat(PaymentRepeatsEnd.REPEAT_END_DATE);
		conversation.setFrequency(PaymentFrequency.MONTHLY);
		conversation.setRepeatEndDate("14 Mar 2013");
		conversation.setRepeatNumber("1");

		PayeeModel payeeModel = new PayeeModel();
		payeeModel.setName("Testing Account");
		payeeModel.setReference("519798645");	
		Bsb bsb = new Bsb();
		bsb.setBsbCode("012055");
		payeeModel.setCode("012055");
		payeeModel.setNickname("Nick Nam");
		payeeModel.setPayeeType(PayeeType.PAY_ANYONE);

		CashAccountModel cashAcc = new CashAccountModel();
		cashAcc.setIdpsAccountName("Dennis Beecham");
		cashAcc.setBsb("205456");
		cashAcc.setCashAccountNumber("234568876");
        DateTime bankDate= new DateTime("2015-02-27");
		
		ConfirmDepositConversation confirmDepositConversation = new ConfirmDepositConversation(conversation,
			payeeModel,
			cashAcc,
            bankDate,
			"53692674-fbcb-4205-af36-38ff65c25fc1");

		Payment pi = converter.convert(confirmDepositConversation);
		assertThat(pi.getToAccount().getReference(),Is.is(confirmDepositConversation.getToAccount()));
		assertThat(pi.getToAccount().getCode(),Is.is(confirmDepositConversation.getToBsb()));
		assertThat(pi.getToAccount().getName(),Is.is(confirmDepositConversation.getToName()));

	}

@Test
public void testConvert()
{	
	ConfirmDepositConversation source = new ConfirmDepositConversation();
	CashAccountModel account = new CashAccountModel();
	account.setIdpsAccountName("Account 123");
	account.setCashAccountNumber("123456789");
	account.setBillerCode("012055");
	account.setBsb("012055");
	List <IncomeTransaction> cashTransactions = new ArrayList<IncomeTransaction>();
	IncomeTransaction incomeTransaction = new IncomeTransaction();	
	cashTransactions.add(incomeTransaction);
	account.setCrn("159");
	account.setInterestRate("5.25");
	source.setAccount(account);
	source.setAmount(new BigDecimal(150000));
	MoveMoneyModel conversation = new MoveMoneyModel();
	conversation.setCustomerRefNo("123");
	conversation.setDescription("View Details");
	conversation.setRepeatNumber("5");
	conversation.setRepeatEndDate("15/03/2013");
	conversation.setRecurring(false);
	conversation.setPaymentId("125");
	conversation.setExceedLimit(true);
	conversation.setFrequency(PaymentFrequency.MONTHLY);
	conversation.setDate("30 May 2020");
	source.setConversation(conversation);
	PayeeModel depositAccount = new PayeeModel();
	depositAccount.setName("Testing Account");
	depositAccount.setReference("519798645");	
	Bsb bsb = new Bsb();
	bsb.setBsbCode("012055");
	depositAccount.setCode("012055");
	depositAccount.setNickname("Nick Nam");
	depositAccount.setPayeeType(PayeeType.PAY_ANYONE);
	source.setDepositAccount(depositAccount);
	source.setPaymentState("Active");
	source.setPaymentToken("Est");
	source.setPayType("PayAnyOne");
	source.setToAccount("123456789");
	source.setToBsb("012055");
	source.setToName("Test Name");
	Payment paymentRequest = converter.convert(source);
	assertNotNull("paymentRequest should not be null", paymentRequest);	
 }

@Test
public void testDepositConversationToPaymentInstruction_recurring()
{
	MoveMoneyModel conversation = new MoveMoneyModel();
	conversation.setAmount(BigDecimal.valueOf(123.45));
	conversation.setDate("14 Feb 2013");
	conversation.setDescription("description");
	conversation.setPayeeId("177");
	conversation.setRecurring(true);
	conversation.setEndRepeat(PaymentRepeatsEnd.REPEAT_NUMBER);
	conversation.setFrequency(PaymentFrequency.MONTHLY);
	conversation.setRepeatEndDate("14 Mar 2013");
	conversation.setRepeatNumber("1");

	PayeeModel payeeModel = new PayeeModel();
	payeeModel.setName("Testing Account");
	payeeModel.setReference("519798645");	
	Bsb bsb = new Bsb();
	bsb.setBsbCode("012055");
	payeeModel.setCode("012055");
	payeeModel.setNickname("Nick Nam");
	payeeModel.setPayeeType(PayeeType.PAY_ANYONE);

	CashAccountModel cashAcc = new CashAccountModel();
	cashAcc.setIdpsAccountName("Dennis Beecham");
	cashAcc.setBsb("205456");
	cashAcc.setCashAccountNumber("234568876");
    DateTime bankDate= new DateTime("2015-02-27");
	conversation.setEndRepeat(PaymentRepeatsEnd.REPEAT_NUMBER);ConfirmDepositConversation confirmDepositConversation = new ConfirmDepositConversation(conversation,
		payeeModel,
		cashAcc,
        bankDate,
		"53692674-fbcb-4205-af36-38ff65c25fc1");

	Payment pi = converter.convert(confirmDepositConversation);
	assertThat(pi.getToAccount().getReference(),Is.is(confirmDepositConversation.getToAccount()));
	assertThat(pi.getToAccount().getCode(),Is.is(confirmDepositConversation.getToBsb()));
	assertThat(pi.getToAccount().getName(),Is.is(confirmDepositConversation.getToName()));
	
}

@Test
public void testDepositConversationToPaymentInstruction_ParseException()
{
	MoveMoneyModel conversation = new MoveMoneyModel();
	conversation.setAmount(BigDecimal.valueOf(123.45));
	conversation.setDate("14 Feb-2013");
	conversation.setDescription("description");
	conversation.setPayeeId("177");
	conversation.setRecurring(true);
	conversation.setEndRepeat(PaymentRepeatsEnd.REPEAT_NUMBER);
	conversation.setFrequency(PaymentFrequency.MONTHLY);
	conversation.setRepeatEndDate("14 Mar 2013");
	conversation.setRepeatNumber("1");

	PayeeModel payeeModel = new PayeeModel();
	payeeModel.setName("Testing Account");
	payeeModel.setReference("519798645");	
	Bsb bsb = new Bsb();
	bsb.setBsbCode("012055");
	payeeModel.setCode("012055");
	payeeModel.setNickname("Nick Nam");
	payeeModel.setPayeeType(PayeeType.PAY_ANYONE);
    DateTime bankDate= new DateTime("2015-02-27");
	CashAccountModel cashAcc = new CashAccountModel();
	cashAcc.setIdpsAccountName("Dennis Beecham");
	cashAcc.setBsb("205456");
	cashAcc.setCashAccountNumber("234568876");
	conversation.setEndRepeat(PaymentRepeatsEnd.REPEAT_NUMBER);ConfirmDepositConversation confirmDepositConversation = new ConfirmDepositConversation(conversation,
		payeeModel,
		cashAcc,
        bankDate,
		"53692674-fbcb-4205-af36-38ff65c25fc1");

	Payment pi = converter.convert(confirmDepositConversation);
	assertThat(pi,notNullValue());
	
}
}

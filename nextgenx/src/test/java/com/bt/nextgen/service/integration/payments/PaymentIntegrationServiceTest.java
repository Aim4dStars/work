package com.bt.nextgen.service.integration.payments;

import com.bt.nextgen.config.BaseSecureIntegrationTest;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.avaloq.MoneyAccountIdentifierImpl;
import com.bt.nextgen.service.avaloq.PayAnyoneAccountDetailsImpl;
import com.bt.nextgen.service.avaloq.payments.BpayBillerImpl;
import com.bt.nextgen.service.avaloq.payments.PaymentDetailsImpl;
import com.bt.nextgen.service.avaloq.payments.RecurringPaymentDetailsImpl;
import com.bt.nextgen.service.integration.CurrencyType;
import com.bt.nextgen.service.integration.IntegrationServiceUtil;
import com.bt.nextgen.service.integration.MoneyAccountIdentifier;
import com.bt.nextgen.service.integration.PayAnyoneAccountDetails;
import com.btfin.panorama.service.integration.RecurringFrequency;
import com.bt.nextgen.service.integration.TransactionStatus;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class PaymentIntegrationServiceTest extends BaseSecureIntegrationTest
{
	private static final Logger logger = LoggerFactory.getLogger(PaymentIntegrationServiceTest.class);

	@Autowired
	PaymentIntegrationService paymentService;

	ServiceErrors serviceErrors = new ServiceErrorsImpl();

	@Test
	public void testOneoffValidateBPAYPayments() throws Exception
	{
		PaymentDetails paymentrequest = createBPAY_PaymentDetails();
		PaymentDetails paymentValidated = paymentService.validatePayment(paymentrequest, serviceErrors);

		assertThat(paymentValidated, is(notNullValue()));
		assertThat(paymentValidated.getTransactionDate(), is(IntegrationServiceUtil.toDate(getGegorianDate("2014-06-02"))));

	}

	@Test
	public void testOneoffSubmitBPAYPayments() throws Exception
	{
		PaymentDetails paymentrequest = createBPAY_PaymentDetails();
		PaymentDetails paymentSubmitted = paymentService.submitPayment(paymentrequest, serviceErrors);

		assertThat(paymentSubmitted, notNullValue());
		assertThat(paymentSubmitted.getReceiptNumber(), equalTo("20160"));
	}

	@Test
	public void testOneoffValidatePayanyonePayments() throws Exception
	{

		PaymentDetails paymentrequest = createPAYANYONE_PaymentDetails();
		PaymentDetails paymentValidated = paymentService.validatePayment(paymentrequest, serviceErrors);

		assertThat(paymentValidated, is(notNullValue()));
		assertThat(paymentValidated.getTransactionDate(), is(IntegrationServiceUtil.toDate(getGegorianDate("2014-02-06"))));

	}

	@Test
	public void testOneoffSubmitPayanyonePayments() throws Exception
	{

		PaymentDetails paymentrequest = createPAYANYONE_PaymentDetails();
		PaymentDetails paymentSubmitted = paymentService.submitPayment(paymentrequest, serviceErrors);

		assertThat(paymentSubmitted, notNullValue());
		assertThat(paymentSubmitted.getReceiptNumber(), equalTo("20169"));

	}

	@Test
	public void testRecurringValidateBPAYPayments() throws Exception
	{

		RecurringPaymentDetails paymentrequest = createBPAY_PaymentDetails_Recurring();
		PaymentDetails paymentValidated = paymentService.validatePayment(paymentrequest, serviceErrors);

		assertThat(paymentValidated, is(notNullValue()));
		assertThat(paymentValidated.getTransactionDate(), is(IntegrationServiceUtil.toDate(getGegorianDate("2014-06-02"))));

	}

	@Test
	public void testRecurringSubmitBPAYPayments() throws Exception
	{

		RecurringPaymentDetails paymentrequest = createBPAY_PaymentDetails_Recurring();
		PaymentDetails paymentSubmitted = paymentService.submitPayment(paymentrequest, serviceErrors);

		assertThat(paymentSubmitted, notNullValue());
		assertThat(paymentSubmitted.getReceiptNumber(), equalTo("20160"));
	}

	@Test
	public void testRecurringValidatePayanyonePayments() throws Exception
	{

		RecurringPaymentDetails paymentrequest = createPAYANYONE_PaymentDetails_Recurring();
		PaymentDetails paymentValidated = paymentService.validatePayment(paymentrequest, serviceErrors);

		assertThat(paymentValidated, is(notNullValue()));
		assertThat(paymentValidated.getTransactionDate(), is(IntegrationServiceUtil.toDate(getGegorianDate("2014-02-06"))));

	}

	@Test
	public void testRecurringSubmitPayanyonePayments() throws Exception
	{

		RecurringPaymentDetails paymentrequest = createPAYANYONE_PaymentDetails_Recurring();
		PaymentDetails paymentSubmitted = paymentService.submitPayment(paymentrequest, serviceErrors);

		assertThat(paymentSubmitted, notNullValue());
		assertThat(paymentSubmitted.getReceiptNumber(), equalTo("20169"));

	}

	@Test
	public void stopPayments() throws Exception
	{
		PaymentDetails paymentrequest = createPAYANYONE_PaymentDetails_Recurring();
		paymentrequest.setPositionId("73098");

		TransactionStatus status = paymentService.stopPayment(paymentrequest, serviceErrors);

		assertThat(status, is(notNullValue()));
	}

	PaymentDetails createBPAY_PaymentDetails() throws ParseException
	{
		PaymentDetails payment = new PaymentDetailsImpl();
		MoneyAccountIdentifier moneyAccId = new MoneyAccountIdentifierImpl();
		moneyAccId.setMoneyAccountId("69952");
		payment.setMoneyAccount(moneyAccId);
        Date date = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse("2015-02-27");
        payment.setTransactionDate(date);
		BpayBiller bpaybiller = new BpayBillerImpl();
		bpaybiller.setBillerCode("1008");
		bpaybiller.setCustomerReferenceNo("4557016834016904");
		payment.setBpayBiller(bpaybiller);

		payment.setAmount(new BigDecimal(123));
		CurrencyType currency = CurrencyType.AustralianDollar;
		payment.setCurrencyType(currency);
		payment.setBenefeciaryInfo("Rent 123");

		return payment;
	}

    RecurringPaymentDetails createBPAY_PaymentDetails_Recurring() throws ParseException
    {
        RecurringPaymentDetails payment = new RecurringPaymentDetailsImpl();
        MoneyAccountIdentifier moneyAccId = new MoneyAccountIdentifierImpl();
        moneyAccId.setMoneyAccountId("69952");
        payment.setMoneyAccount(moneyAccId);

        BpayBiller bpaybiller = new BpayBillerImpl();
        bpaybiller.setBillerCode("1008");
        bpaybiller.setCustomerReferenceNo("4557016834016904");
        payment.setBpayBiller(bpaybiller);

        payment.setAmount(new BigDecimal(123));
        CurrencyType currency = CurrencyType.AustralianDollar;
        payment.setCurrencyType(currency);
        payment.setBenefeciaryInfo("Rent 123");
        Date date = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse("2015-02-27");
        payment.setTransactionDate(date);
        payment.setRecurringFrequency(RecurringFrequency.Monthly);
        payment.setStartDate(new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse("2015-02-27"));
        payment.setEndDate(new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse("2016-01-26"));

        return payment;
    }

	PaymentDetails createPAYANYONE_PaymentDetails() throws ParseException
	{
		PaymentDetails payment = new PaymentDetailsImpl();
		MoneyAccountIdentifier moneyAccId = new MoneyAccountIdentifierImpl();
		moneyAccId.setMoneyAccountId("69952");
		payment.setMoneyAccount(moneyAccId);
        Date date = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse("2015-02-27");
        payment.setTransactionDate(date);
		PayAnyoneAccountDetails accDetails = new PayAnyoneAccountDetailsImpl();
		accDetails.setAccount("12345678");
		accDetails.setBsb("012003");
		payment.setPayAnyoneBeneficiary(accDetails);

		payment.setAmount(new BigDecimal(123));
		CurrencyType currency = CurrencyType.AustralianDollar;
		payment.setCurrencyType(currency);
		payment.setBenefeciaryInfo("Rent 123");

		return payment;
	}

	RecurringPaymentDetails createPAYANYONE_PaymentDetails_Recurring() throws ParseException
	{
		RecurringPaymentDetails payment = new RecurringPaymentDetailsImpl();
		MoneyAccountIdentifier moneyAccId = new MoneyAccountIdentifierImpl();
		moneyAccId.setMoneyAccountId("69952");
		payment.setMoneyAccount(moneyAccId);

		PayAnyoneAccountDetails accDetails = new PayAnyoneAccountDetailsImpl();
		accDetails.setAccount("12345678");
		accDetails.setBsb("012003");
		payment.setPayAnyoneBeneficiary(accDetails);

		payment.setAmount(new BigDecimal(123));
		CurrencyType currency = CurrencyType.AustralianDollar;
		payment.setCurrencyType(currency);
		payment.setBenefeciaryInfo("Rent 123");
		Date date = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse("2015-02-27");
		payment.setTransactionDate(date);
		payment.setRecurringFrequency(RecurringFrequency.Monthly);
		payment.setStartDate(new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse("2015-02-27"));
		payment.setEndDate(new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse("2016-01-26"));

		return payment;
	}

	public XMLGregorianCalendar getGegorianDate(String date) throws Exception
	{
		GregorianCalendar c = new GregorianCalendar();
		c.setTime(new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(date));
		c.setTimeZone(TimeZone.getTimeZone("Australia/Sydney"));
		XMLGregorianCalendar trnxDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);

		return trnxDate;
	}
}

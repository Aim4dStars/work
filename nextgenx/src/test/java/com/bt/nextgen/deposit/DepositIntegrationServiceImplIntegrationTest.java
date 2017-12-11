package com.bt.nextgen.deposit;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.bt.nextgen.config.BaseSecureIntegrationTest;
import com.bt.nextgen.integration.xml.parser.ParsingContext;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.avaloq.MoneyAccountIdentifierImpl;
import com.bt.nextgen.service.avaloq.PayAnyoneAccountDetailsImpl;
import com.bt.nextgen.service.avaloq.PositionIdentifierImpl;
import com.bt.nextgen.service.avaloq.deposit.DepositDetailsImpl;
import com.bt.nextgen.service.avaloq.deposit.RecurringDepositDetailsImpl;
import com.bt.nextgen.service.integration.CurrencyType;
import com.bt.nextgen.service.integration.MoneyAccountIdentifier;
import com.bt.nextgen.service.integration.PayAnyoneAccountDetails;
import com.bt.nextgen.service.integration.PositionIdentifier;
import com.btfin.panorama.service.integration.RecurringFrequency;
import com.bt.nextgen.service.integration.TransactionStatus;
import com.bt.nextgen.service.integration.deposit.DepositDetails;
import com.bt.nextgen.service.integration.deposit.DepositIntegrationService;
import com.bt.nextgen.service.integration.deposit.RecurringDepositDetails;

/**
 * @deprecated Use V2
 */
@Deprecated
public class DepositIntegrationServiceImplIntegrationTest extends BaseSecureIntegrationTest {
    @Autowired
    DepositIntegrationService depositService;

    @Autowired
    ParsingContext context;

    ServiceErrors serviceErrors = new ServiceErrorsImpl();

    @Test
    public void testValidDeposit_PayOnce() throws ParseException, Exception {

        DepositDetails deposit = createDepositDetailsObject();
        Date date = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse("2015-06-30");
        deposit.setTransactionDate(date);
        DepositDetails response = depositService.validateDeposit(deposit, serviceErrors);
        assertThat(response, is(notNullValue()));

        /*
         * GregorianCalendar c = new GregorianCalendar(); c.setTime(new SimpleDateFormat("yyyy-MM-dd",
         * Locale.ENGLISH).parse("2015-02-26")); XMLGregorianCalendar date2 =
         * DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
         */
        assertThat(response.getTransactionDate(), is(notNullValue()));
        assertThat(serviceErrors.hasErrors(), is(false));
    }

    @Test
    public void testValidDeposit_Scheduled() throws ParseException, Exception {

        DepositDetails deposit = createDepositDetailsObject();
        Date date = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse("2015-06-30");
        deposit.setTransactionDate(date);

        DepositDetails response = depositService.validateDeposit(deposit, serviceErrors);
        assertThat(response, is(notNullValue()));

        assertThat(response.getTransactionDate(), is(notNullValue()));
        assertThat(serviceErrors.hasErrors(), is(false));
    }

    @Test
    public void testValidDeposit_Recurring() throws ParseException, Exception {

        RecurringDepositDetails deposit = createDepositDetailsObject_Recurring();
        DepositDetails response = depositService.validateDeposit(deposit, serviceErrors);
        assertThat(response, is(notNullValue()));

        /*
         * GregorianCalendar c = new GregorianCalendar(); c.setTime(new SimpleDateFormat("yyyy-MM-dd",
         * Locale.ENGLISH).parse("2015-01-27")); XMLGregorianCalendar date2 =
         * DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
         */

        assertThat(response.getTransactionDate(), is(notNullValue()));
        assertThat(serviceErrors.hasErrors(), is(false));
    }

    @Test
    public void testSubmitDeposit_PayOnce() throws ParseException {

        DepositDetails deposit = createDepositDetailsObject();
        Date date = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse("2015-06-30");
        deposit.setTransactionDate(date);
        DepositDetails response = depositService.submitDeposit(deposit, serviceErrors);
        assertThat(response, is(notNullValue()));
        assertThat(response.getRecieptNumber(), is(notNullValue()));
        assertThat(serviceErrors.hasErrors(), is(false));

    }

    @Test
    public void testSubmitDeposit_Scheduled() throws ParseException {

        DepositDetails deposit = createDepositDetailsObject();
        Date date = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse("2015-06-30");
        deposit.setTransactionDate(date);

        DepositDetails response = depositService.submitDeposit(deposit, serviceErrors);
        assertThat(response, is(notNullValue()));
        assertThat(response.getRecieptNumber(), is(notNullValue()));
        assertThat(serviceErrors.hasErrors(), is(false));

    }

    @Test
    public void testSubmitDeposit_Recurring() throws ParseException {

        RecurringDepositDetails deposit = createDepositDetailsObject_Recurring();
        DepositDetails response = depositService.submitDeposit(deposit, serviceErrors);
        assertThat(response, is(notNullValue()));
        assertThat(response.getRecieptNumber(), is(notNullValue()));
        assertThat(serviceErrors.hasErrors(), is(false));

    }

    @Test
    public void testStopDeposit() throws ParseException {
        PositionIdentifier posIdentifier = new PositionIdentifierImpl();
        posIdentifier.setPositionId("73082");
        TransactionStatus response = depositService.stopDeposit(posIdentifier, serviceErrors);
        assertThat(response, is(notNullValue()));
        assertThat(response.isSuccessful(), is(true));
        assertThat(serviceErrors.hasErrors(), is(false));

    }

    DepositDetails createDepositDetailsObject() throws ParseException {
        DepositDetails deposit = new DepositDetailsImpl();
        MoneyAccountIdentifier moneyAccId = new MoneyAccountIdentifierImpl();
        moneyAccId.setMoneyAccountId("69952");
        deposit.setMoneyAccountIdentifier(moneyAccId);

        PayAnyoneAccountDetails accDetails = new PayAnyoneAccountDetailsImpl();
        accDetails.setAccount("12345678");
        accDetails.setBsb("012003");
        deposit.setPayAnyoneAccountDetails(accDetails);

        deposit.setDepositAmount(new BigDecimal(123));
        CurrencyType currency = CurrencyType.AustralianDollar;
        deposit.setCurrencyType(currency);
        deposit.setDescription("Rent 123");
        /*
         * Date date = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse("2015-02-27"); deposit.setTransactionDate(date);
         */
        // deposit.setRecurring(false);
        /*
         * RecurringTransaction recurDetails = new RecurringTransactionImpl();
         * recurDetails.setRecurringFrequency(RecurringFrequency.Once); deposit.setRecurringTransaction(recurDetails);
         */

        return deposit;
    }

    RecurringDepositDetails createDepositDetailsObject_Recurring() throws ParseException {
        RecurringDepositDetails deposit = new RecurringDepositDetailsImpl();
        MoneyAccountIdentifier moneyAccId = new MoneyAccountIdentifierImpl();
        moneyAccId.setMoneyAccountId("69952");
        deposit.setMoneyAccountIdentifier(moneyAccId);

        PayAnyoneAccountDetails accDetails = new PayAnyoneAccountDetailsImpl();
        accDetails.setAccount("12345678");
        accDetails.setBsb("012003");
        deposit.setPayAnyoneAccountDetails(accDetails);

        deposit.setDepositAmount(new BigDecimal(123));
        CurrencyType currency = CurrencyType.AustralianDollar;
        deposit.setCurrencyType(currency);
        deposit.setDescription("Rent 123");
        Date date = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse("2015-02-27");
        deposit.setTransactionDate(date);
        // deposit.setRecurring(true);
        deposit.setRecurringFrequency(RecurringFrequency.Monthly);
        deposit.setStartDate(new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse("2015-02-27"));
        deposit.setEndDate(new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse("2016-01-26"));
        // deposit.setRecurringTransaction(recurDetails);

        return deposit;
    }
}

package com.bt.nextgen.service.avaloq.movemoney;

import com.bt.nextgen.config.BaseSecureIntegrationTest;
import com.bt.nextgen.integration.xml.parser.ParsingContext;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.MoneyAccountIdentifierImpl;
import com.bt.nextgen.service.avaloq.PayAnyoneAccountDetailsImpl;
import com.bt.nextgen.service.avaloq.PositionIdentifierImpl;
import com.bt.nextgen.service.avaloq.wrapaccount.WrapAccountIdentifierImpl;
import com.bt.nextgen.service.integration.CurrencyType;
import com.bt.nextgen.service.integration.MoneyAccountIdentifier;
import com.bt.nextgen.service.integration.PayAnyoneAccountDetails;
import com.bt.nextgen.service.integration.PositionIdentifier;
import com.btfin.panorama.service.integration.RecurringFrequency;
import com.bt.nextgen.service.integration.TransactionStatus;
import com.bt.nextgen.service.integration.movemoney.ContributionType;
import com.bt.nextgen.service.integration.movemoney.DepositDetails;
import com.bt.nextgen.service.integration.movemoney.DepositIntegrationService;
import com.bt.nextgen.service.integration.movemoney.DepositStatus;
import com.bt.nextgen.service.integration.movemoney.RecurringDepositDetails;
import com.btfin.panorama.service.integration.wrapaccount.WrapAccountIdentifier;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class DepositDelegateServiceImplTest extends BaseSecureIntegrationTest {
    private final DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd");

    @Autowired
    DepositIntegrationService depositService;

    @Autowired
    ParsingContext context;

    ServiceErrors serviceErrors = new ServiceErrorsImpl();

    @Test
    public void testLoadSavedDeposits_whenValidRequest_thenResponseReturned() throws Exception {
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        WrapAccountIdentifier identifier = new WrapAccountIdentifierImpl();
        identifier.setBpId("73351");
        List<DepositDetails> deposits = depositService.loadSavedDeposits(identifier, serviceErrors);
        assertNotNull(deposits);
        RecurringDepositDetails deposit = (RecurringDepositDetails) deposits.get(0);

        assertEquals("6440061", deposit.getDepositId());
        assertEquals("5", deposit.getTransactionSeq());
        assertEquals(DepositStatus.NOT_SUBMITTED, deposit.getStatus());
        assertEquals(BigDecimal.valueOf(250), deposit.getDepositAmount());
        assertEquals("description test", deposit.getDescription());
        assertEquals("2017-01-27", formatter.print(deposit.getTransactionDate()));
        assertEquals("2017-10-24", formatter.print(deposit.getEndDate()));
        assertEquals(4, deposit.getMaxCount().intValue());
        assertEquals(RecurringFrequency.Quarterly, deposit.getRecurringFrequency());
        assertEquals(ContributionType.PERSONAL, deposit.getContributionType());
        assertEquals("036081", deposit.getPayerBsb());
        assertEquals("123456782", deposit.getPayerAccount());
    }

    @Test
    public void testDeleteDeposit_whenValidRequest_thenNoErrors() throws ParseException {
        depositService.deleteDeposit("123456", serviceErrors);
        assertThat(serviceErrors.hasErrors(), is(false));
    }

    @Test
    public void testDeleteRecurringDeposit_whenValidRequest_thenNoErrors() throws ParseException {
        depositService.deleteRecurringDeposit("123456", serviceErrors);
        assertThat(serviceErrors.hasErrors(), is(false));
    }

    @Test
    public void testValidDeposit_PayOnce() throws ParseException, Exception {
        DepositDetails deposit = createDepositDetailsObject();
        DepositDetails response = depositService.validateDeposit(deposit, serviceErrors);
        assertThat(response, is(notNullValue()));
        assertThat(response.getTransactionDate(), is(notNullValue()));
        assertThat(serviceErrors.hasErrors(), is(false));
    }

    @Test
    public void testValidDeposit_Scheduled() throws ParseException, Exception {
        DepositDetails deposit = createDepositDetailsObject();
        DepositDetails response = depositService.validateDeposit(deposit, serviceErrors);
        assertThat(response, is(notNullValue()));
        assertThat(response.getTransactionDate(), is(notNullValue()));
        assertThat(serviceErrors.hasErrors(), is(false));
    }

    @Test
    public void testValidDeposit_Recurring() throws ParseException, Exception {
        RecurringDepositDetails deposit = createDepositDetailsObject_Recurring();
        RecurringDepositDetails response = depositService.validateDeposit(deposit, serviceErrors);
        assertThat(response, is(notNullValue()));
        assertThat(response.getTransactionDate(), is(notNullValue()));
        assertThat(serviceErrors.hasErrors(), is(false));
    }

    @Test
    public void testSubmitDeposit_PayOnce() throws ParseException {
        DepositDetails deposit = createDepositDetailsObject();
        DepositDetails response = depositService.submitDeposit(deposit, serviceErrors);
        assertThat(response, is(notNullValue()));
        assertThat(response.getReceiptNumber(), is(notNullValue()));
        assertThat(serviceErrors.hasErrors(), is(false));
    }

    @Test
    public void testSubmitDeposit_Scheduled() throws ParseException {
        DepositDetails deposit = createDepositDetailsObject();
        DepositDetails response = depositService.submitDeposit(deposit, serviceErrors);
        assertThat(response, is(notNullValue()));
        assertThat(response.getReceiptNumber(), is(notNullValue()));
        assertThat(serviceErrors.hasErrors(), is(false));
    }

    @Test
    public void testSubmitDeposit_Recurring() throws ParseException {
        RecurringDepositDetails deposit = createDepositDetailsObject_Recurring();
        RecurringDepositDetails response = depositService.submitDeposit(deposit, serviceErrors);
        assertThat(response, is(notNullValue()));
        assertThat(response.getReceiptNumber(), is(notNullValue()));
        assertThat(serviceErrors.hasErrors(), is(false));
    }

    @Test
    public void testCreateDeposit_PayOnce() throws ParseException {
        DepositDetails deposit = createDepositDetailsObject();
        DepositDetails response = depositService.createDeposit(deposit, serviceErrors);
        assertThat(response, is(notNullValue()));
        assertThat(response.getReceiptNumber(), is(notNullValue()));
        assertThat(serviceErrors.hasErrors(), is(false));
    }

    @Test
    public void testCreateDeposit_Recurring() throws ParseException {
        RecurringDepositDetails deposit = createDepositDetailsObject_Recurring();
        RecurringDepositDetails response = depositService.createDeposit(deposit, serviceErrors);
        assertThat(response, is(notNullValue()));
        assertThat(response.getReceiptNumber(), is(notNullValue()));
        assertThat(serviceErrors.hasErrors(), is(false));
    }

    @Test
    public void testUpdateDeposit_PayOnce() throws ParseException {
        DepositDetails deposit = createDepositDetailsObject();
        DepositDetails response = depositService.updateDeposit(deposit, serviceErrors);
        assertThat(response, is(notNullValue()));
        assertThat(response.getReceiptNumber(), is(notNullValue()));
        assertThat(serviceErrors.hasErrors(), is(false));
    }

    @Test
    public void testUpdateDeposit_Recurring() throws ParseException {
        RecurringDepositDetails deposit = createDepositDetailsObject_Recurring();
        RecurringDepositDetails response = depositService.updateDeposit(deposit, serviceErrors);
        assertThat(response, is(notNullValue()));
        assertThat(response.getReceiptNumber(), is(notNullValue()));
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
        MoneyAccountIdentifier moneyAccId = new MoneyAccountIdentifierImpl();
        moneyAccId.setMoneyAccountId("69952");

        PayAnyoneAccountDetails accDetails = new PayAnyoneAccountDetailsImpl();
        accDetails.setAccount("12345678");
        accDetails.setBsb("012003");
        DateTime transactionDate = new DateTime("2015-06-30");

        return new DepositDetailsImpl(moneyAccId, accDetails, new BigDecimal(123), CurrencyType.AustralianDollar, "Rent 123",
                transactionDate, null, null, ContributionType.SPOUSE, null, null);
    }

    RecurringDepositDetails createDepositDetailsObject_Recurring() throws ParseException {
        MoneyAccountIdentifier moneyAccId = new MoneyAccountIdentifierImpl();
        moneyAccId.setMoneyAccountId("69952");

        PayAnyoneAccountDetails accDetails = new PayAnyoneAccountDetailsImpl();
        accDetails.setAccount("12345678");
        accDetails.setBsb("012003");

        DateTime transactionDate = new DateTime("2015-02-27");
        DateTime startDate = new DateTime("2015-02-28");
        DateTime endDate = new DateTime("2015-02-26");

        return new RecurringDepositDetailsImpl(moneyAccId, accDetails, new BigDecimal(123), CurrencyType.AustralianDollar,
                "Rent 123", transactionDate, null, null, ContributionType.SPOUSE, RecurringFrequency.Monthly, startDate, endDate,
                null, null, null);
    }
}

package com.bt.nextgen.api.movemoney.v2.util;

import com.bt.nextgen.api.movemoney.v2.model.DepositDto;
import com.bt.nextgen.service.avaloq.MoneyAccountIdentifierImpl;
import com.bt.nextgen.service.avaloq.PayAnyoneAccountDetailsImpl;
import com.bt.nextgen.service.avaloq.movemoney.RecurringDepositDetailsImpl;
import com.bt.nextgen.service.integration.MoneyAccountIdentifier;
import com.bt.nextgen.service.integration.PayAnyoneAccountDetails;
import com.bt.nextgen.service.integration.movemoney.ContributionType;
import com.bt.nextgen.service.integration.movemoney.RecurringDepositDetails;
import com.btfin.panorama.service.integration.RecurringFrequency;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@RunWith(MockitoJUnitRunner.class)
public class DepositUtilTest {

    private static final String RECEIPT = "receipt";
    private static final Integer END_REPEAT_NUMBER = 1;
    private static final String DESCRIPTION = "description";
    private static final String END_REPEAT_SET_DATE = "setDate";
    private static final String END_REPEAT_SET_NUMBER = "setNumber";
    private static final BigDecimal AMOUNT = new BigDecimal(10);
    private static final ContributionType DEPOSIT_TYPE = ContributionType.PERSONAL;
    private static final RecurringFrequency FREQUENCY = RecurringFrequency.Monthly;
    private static final SimpleDateFormat FORMAT = new SimpleDateFormat("dd MMM yyyy");
    private static final DateTime END_REPEAT_DATE = new DateTime(2017, 1, 1, 0, 0);
    private static final DateTime TRANSACTION_DATE = new DateTime(2017, 1, 1, 0, 0);

    @Test
    public void testPopulateRecurDepositDetailsReq_whenRepeatNumberOfTimes_thenRecurringDepositDetailsPopulated() {
        DepositDto depositDto = createDummyDepositDto(END_REPEAT_SET_NUMBER);
        PayAnyoneAccountDetails payAnyOneAccounts = new PayAnyoneAccountDetailsImpl();
        MoneyAccountIdentifier moneyAccountIdentifier = new MoneyAccountIdentifierImpl();

        RecurringDepositDetails recurringDepositDetails = DepositUtil.populateRecurDepositDetailsReq(depositDto, payAnyOneAccounts, moneyAccountIdentifier);

        assertThat(recurringDepositDetails.getDepositAmount(), is(AMOUNT));
        assertThat(recurringDepositDetails.getRecurringFrequency(), is(FREQUENCY));
        assertThat(recurringDepositDetails.getDescription(), is(DESCRIPTION));
        assertThat(recurringDepositDetails.getTransactionDate(), is(TRANSACTION_DATE));
        assertThat(recurringDepositDetails.getMaxCount(), is(END_REPEAT_NUMBER));
        assertThat(recurringDepositDetails.getEndDate(), is(nullValue()));
        assertThat(recurringDepositDetails.getContributionType(), is(DEPOSIT_TYPE));
    }

    @Test
    public void testPopulateRecurDepositDetailsReq_whenRepeatUntilDate_thenRecurringDepositDetailsPopulated() {
        DepositDto depositDto = createDummyDepositDto(END_REPEAT_SET_DATE);
        PayAnyoneAccountDetails payAnyOneAccounts = new PayAnyoneAccountDetailsImpl();
        MoneyAccountIdentifier moneyAccountIdentifier = new MoneyAccountIdentifierImpl();

        RecurringDepositDetails recurringDepositDetails = DepositUtil.populateRecurDepositDetailsReq(depositDto, payAnyOneAccounts, moneyAccountIdentifier);

        assertThat(recurringDepositDetails.getDepositAmount(), is(AMOUNT));
        assertThat(recurringDepositDetails.getRecurringFrequency(), is(FREQUENCY));
        assertThat(recurringDepositDetails.getDescription(), is(DESCRIPTION));
        assertThat(recurringDepositDetails.getTransactionDate(), is(TRANSACTION_DATE));
        assertThat(recurringDepositDetails.getMaxCount(), is(nullValue()));
        assertThat(recurringDepositDetails.getEndDate(), is(END_REPEAT_DATE));
        assertThat(recurringDepositDetails.getContributionType(), is(DEPOSIT_TYPE));
    }

    @Test
    public void testPopulateRecurDepositDetailsReq_whenNoData_thenRecurringDepositDetailsEmpty() {
        DepositDto depositDto = new DepositDto();
        PayAnyoneAccountDetails payAnyOneAccounts = new PayAnyoneAccountDetailsImpl();
        MoneyAccountIdentifier moneyAccountIdentifier = new MoneyAccountIdentifierImpl();

        RecurringDepositDetails recurringDepositDetails = DepositUtil.populateRecurDepositDetailsReq(depositDto, payAnyOneAccounts, moneyAccountIdentifier);

        assertThat(recurringDepositDetails.getDepositAmount(), is(nullValue()));
        assertThat(recurringDepositDetails.getRecurringFrequency(), is(nullValue()));
        assertThat(recurringDepositDetails.getDescription(), is(nullValue()));
        assertThat(recurringDepositDetails.getTransactionDate(), is(nullValue()));
        assertThat(recurringDepositDetails.getMaxCount(), is(nullValue()));
        assertThat(recurringDepositDetails.getEndDate(), is(nullValue()));
        assertThat(recurringDepositDetails.getContributionType(), is(nullValue()));

        // for coverage
        depositDto.setEndRepeat(END_REPEAT_SET_NUMBER);
        DepositUtil.populateRecurDepositDetailsReq(depositDto, payAnyOneAccounts, moneyAccountIdentifier);

        depositDto.setEndRepeat(END_REPEAT_SET_DATE);
        DepositUtil.populateRecurDepositDetailsReq(depositDto, payAnyOneAccounts, moneyAccountIdentifier);
    }

    @Test
    public void testToDepositDto_whenAllDataPresent_thenDepositDtoHasAllFieldsMapped() {
        RecurringDepositDetailsImpl deposit = new RecurringDepositDetailsImpl();
        DepositDto depositDtoKeyedObj = new DepositDto();

        deposit.setDepositAmount(AMOUNT);
        deposit.setRecurringFrequency(FREQUENCY);
        deposit.setDescription(DESCRIPTION);
        deposit.setReceiptNumber(RECEIPT);
        deposit.setTransactionDate(TRANSACTION_DATE);
        deposit.setStartDate(TRANSACTION_DATE);
        deposit.setMaxCount(END_REPEAT_NUMBER);
        deposit.setEndDate(END_REPEAT_DATE);
        deposit.setContributionType(DEPOSIT_TYPE);

        DepositDto mappedDepositDto = DepositUtil.toDepositDto(deposit, depositDtoKeyedObj);

        assertThat(mappedDepositDto.getAmount(), is(AMOUNT));
        assertThat(mappedDepositDto.getFrequency(), is(FREQUENCY.getDescription()));
        assertThat(mappedDepositDto.getDescription(), is(DESCRIPTION));
        assertThat(mappedDepositDto.getReceiptNumber(), is(RECEIPT));
        assertThat(mappedDepositDto.getReceiptId(), is(notNullValue()));
        assertThat(mappedDepositDto.getTransactionDate(), is(FORMAT.format(TRANSACTION_DATE.toDate())));
        assertThat(mappedDepositDto.getEndRepeatNumber(), is(END_REPEAT_NUMBER.toString()));
        assertThat(mappedDepositDto.getRepeatEndDate(), is(FORMAT.format(END_REPEAT_DATE.toDate())));
        assertThat(mappedDepositDto.getDepositType(), is(DEPOSIT_TYPE.getDisplayName()));
    }

    @Test
    public void testToDepositDto_whenAllDataMissing_thenDepositDtoHasAllFieldsNull() {
        RecurringDepositDetailsImpl deposit = new RecurringDepositDetailsImpl();
        DepositDto depositDtoKeyedObj = new DepositDto();

        DepositDto mappedDepositDto = DepositUtil.toDepositDto(deposit, depositDtoKeyedObj);
        assertNullDepositDto(mappedDepositDto);
        mappedDepositDto = DepositUtil.toDepositDto(null, null);
        assertNullDepositDto(mappedDepositDto);
    }

    private void assertNullDepositDto(DepositDto mappedDepositDto) {
        assertThat(mappedDepositDto.getAmount(), is(nullValue()));
        assertThat(mappedDepositDto.getFrequency(), is(nullValue()));
        assertThat(mappedDepositDto.getDescription(), is(nullValue()));
        assertThat(mappedDepositDto.getReceiptNumber(), is(nullValue()));
        assertThat(mappedDepositDto.getTransactionDate(), is(nullValue()));
        assertThat(mappedDepositDto.getEndRepeatNumber(), is(nullValue()));
        assertThat(mappedDepositDto.getRepeatEndDate(), is(nullValue()));
        assertThat(mappedDepositDto.getDepositType(), is(nullValue()));
    }

    private DepositDto createDummyDepositDto(String endRepeat) {
        DepositDto depositDto = new DepositDto();
        depositDto.setFrequency(FREQUENCY.getDescription());
        depositDto.setEndRepeat(endRepeat);
        depositDto.setEndRepeatNumber(END_REPEAT_NUMBER.toString());
        depositDto.setRepeatEndDate(FORMAT.format(END_REPEAT_DATE.toDate()));
        depositDto.setTransactionDate(FORMAT.format(TRANSACTION_DATE.toDate()));
        depositDto.setDepositType(DEPOSIT_TYPE.getName());
        depositDto.setDescription(DESCRIPTION);
        depositDto.setAmount(AMOUNT);

        return depositDto;
    }

}
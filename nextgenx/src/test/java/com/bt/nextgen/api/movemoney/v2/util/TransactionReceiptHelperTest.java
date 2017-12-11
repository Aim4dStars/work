package com.bt.nextgen.api.movemoney.v2.util;

import com.bt.nextgen.api.movemoney.v2.model.DepositDto;
import com.bt.nextgen.api.movemoney.v2.model.PayeeDto;
import com.bt.nextgen.api.movemoney.v2.model.PaymentDto;
import com.bt.nextgen.reports.account.movemoney.TransactionReceiptReportData;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TransactionReceiptHelperTest {

    @InjectMocks
    private TransactionReceiptHelper transactionReceiptHelper;

    @Test
    public void getReceiptData() {
        PaymentDto data = mock(PaymentDto.class);
        when(data.getReceiptNumber()).thenReturn("1234");
        when(data.getFromPayDto()).thenReturn(mock(PayeeDto.class));
        when(data.getToPayeeDto()).thenReturn(mock(PayeeDto.class));
        when(data.getRepeatEndDate()).thenReturn(new DateTime("2017-05-05"));
        when(data.getEndRepeatNumber()).thenReturn(BigInteger.valueOf(2));

        transactionReceiptHelper.storeReceiptData(data);
        TransactionReceiptReportData receiptData = transactionReceiptHelper.getReceiptData("1234");
        assertNotNull(receiptData);
        assertEquals(receiptData.getTransactionType(), "Payment");
        assertEquals(receiptData.getRepeatEndDate(), "Ends after 2 repeats on 05 May 2017");
    }

    @Test
    public void storeReceiptData_payment() {
        PaymentDto data = mock(PaymentDto.class);
        when(data.getReceiptNumber()).thenReturn("5678");
        when(data.getFromPayDto()).thenReturn(mock(PayeeDto.class));
        when(data.getToPayeeDto()).thenReturn(mock(PayeeDto.class));

        transactionReceiptHelper.storeReceiptData(data);
        TransactionReceiptReportData receiptData = transactionReceiptHelper.getReceiptData("5678");
        assertNotNull(receiptData);
        assertEquals(receiptData.getTransactionType(), "Payment");
        assertEquals(receiptData.getRepeatEndDate(), "No end date");
    }

    @Test
    public void storeReceiptData_deposit_noTransactionDate() {
        DepositDto data = mock(DepositDto.class);
        when(data.getReceiptNumber()).thenReturn("1234");
        when(data.getFromPayDto()).thenReturn(mock(PayeeDto.class));
        when(data.getToPayeeDto()).thenReturn(mock(PayeeDto.class));
        when(data.getRepeatEndDate()).thenReturn("05 May 2017");

        transactionReceiptHelper.storeReceiptData(data);
        TransactionReceiptReportData receiptData = transactionReceiptHelper.getReceiptData("1234");
        assertNotNull(receiptData);
        assertEquals(receiptData.getTransactionType(), "Deposit");
        assertEquals(receiptData.getRepeatEndDate(), "Ends on 05 May 2017");
    }

    @Test
    public void storeReceiptData_deposit_noRepeatDate() {
        DepositDto data = mock(DepositDto.class);
        when(data.getReceiptNumber()).thenReturn("1234");
        when(data.getFromPayDto()).thenReturn(mock(PayeeDto.class));
        when(data.getToPayeeDto()).thenReturn(mock(PayeeDto.class));
        when(data.getTransactionDate()).thenReturn("07 May 2017");

        transactionReceiptHelper.storeReceiptData(data);
        TransactionReceiptReportData receiptData = transactionReceiptHelper.getReceiptData("1234");
        assertNotNull(receiptData);
        assertEquals(receiptData.getTransactionType(), "Deposit");
        assertEquals(receiptData.getTransactionDate(), "07 May 2017");
    }

    @Test
    public void storeReceiptData_invalid() {
        TransactionReceiptReportData receiptData = transactionReceiptHelper.getReceiptData("1111");
        assertNull(receiptData);
    }
}
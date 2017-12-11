package com.bt.nextgen.api.transactionhistory.service;

import com.bt.nextgen.api.transactionhistory.model.CashTransactionHistoryDto;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.service.avaloq.transactionhistory.TransactionHistoryImpl;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CashTransactionHistoryReportServiceTest {

    private static final String ACCOUNT_ID = "975AF9B7FE27CF0A2A7134DC4BBC3EDD510AAB21532150E0";
    private static final String DIRECTION = "CREDIT";
    private static final DateTime START_DATE = new DateTime("2017-01-01");
    private static final DateTime END_DATE = new DateTime("2017-01-02");
    private static final String DOC_ID = "198074";

    @InjectMocks
    private CashTransactionHistoryReportServiceImpl reportService;

    @Mock
    private CashTransactionHistoryDtoServiceImpl cashTransactionDtoService;


    @Test
    public void testRetrievePastTransaction() throws ParseException
    {
        List <CashTransactionHistoryDto> transactionDtos = new ArrayList <>();
        TransactionHistoryImpl transaction = CreateDummyTransactionHistory(DOC_ID, DIRECTION);
        CashTransactionHistoryDto transactionDto = new CashTransactionHistoryDto(transaction);
        transactionDtos.add(transactionDto);

        when(cashTransactionDtoService.search((Matchers.anyListOf(ApiSearchCriteria.class)), any(ServiceErrorsImpl.class))).thenReturn(transactionDtos);

        CashTransactionHistoryDto pastTransactionDto = reportService.retrievePastTransaction(ACCOUNT_ID, DIRECTION, START_DATE, END_DATE, DOC_ID);

        assertNotNull(pastTransactionDto);
        assertNotNull(pastTransactionDto.getDocId());
        assertEquals(DOC_ID, pastTransactionDto.getDocId());
        assertEquals(pastTransactionDto, transactionDto);
    }

    @Test
    public void shouldReturnTransactionForNullDirection() throws ParseException
    {
        List <CashTransactionHistoryDto> transactionDtos = new ArrayList <>();
        TransactionHistoryImpl transaction = CreateDummyTransactionHistory(DOC_ID, null);
        CashTransactionHistoryDto transactionDto = new CashTransactionHistoryDto(transaction);
        transactionDtos.add(transactionDto);

        when(cashTransactionDtoService.search((Matchers.anyListOf(ApiSearchCriteria.class)), any(ServiceErrorsImpl.class))).thenReturn(transactionDtos);

        CashTransactionHistoryDto pastTransactionDto = reportService.retrievePastTransaction(ACCOUNT_ID, DIRECTION, START_DATE, END_DATE, DOC_ID);

        assertNotNull(pastTransactionDto);
        assertNotNull(pastTransactionDto.getDocId());
        assertEquals(DOC_ID, pastTransactionDto.getDocId());
        assertEquals(pastTransactionDto, transactionDto);
    }

    @Test
    public void shouldNotReturnTransactionForInvalidReceiptNumber() throws ParseException
    {
        List <CashTransactionHistoryDto> transactionDtos = new ArrayList <>();
        TransactionHistoryImpl transaction = CreateDummyTransactionHistory("invalidId", DIRECTION);
        CashTransactionHistoryDto transactionDto = new CashTransactionHistoryDto(transaction);
        transactionDtos.add(transactionDto);

        when(cashTransactionDtoService.search((Matchers.anyListOf(ApiSearchCriteria.class)), any(ServiceErrorsImpl.class))).thenReturn(transactionDtos);

        CashTransactionHistoryDto pastTransactionDto = reportService.retrievePastTransaction(ACCOUNT_ID, DIRECTION, START_DATE, END_DATE, DOC_ID);

        assertNull(pastTransactionDto);
    }

    @Test
    public void shouldNotReturnTransactionForInvalidDirection() throws ParseException
    {
        List <CashTransactionHistoryDto> transactionDtos = new ArrayList <>();
        TransactionHistoryImpl transaction = CreateDummyTransactionHistory(DOC_ID, DIRECTION);
        CashTransactionHistoryDto transactionDto = new CashTransactionHistoryDto(transaction);
        transactionDtos.add(transactionDto);

        when(cashTransactionDtoService.search((Matchers.anyListOf(ApiSearchCriteria.class)), any(ServiceErrorsImpl.class))).thenReturn(transactionDtos);

        CashTransactionHistoryDto pastTransactionDto = reportService.retrievePastTransaction(ACCOUNT_ID, "invalidDirection", START_DATE, END_DATE, DOC_ID);

        assertNull(pastTransactionDto);
    }

    private TransactionHistoryImpl CreateDummyTransactionHistory(String docId, String direction) {
        TransactionHistoryImpl transaction = new TransactionHistoryImpl();
        transaction.setEvtId(99);
        transaction.setAccountId("69949");
        transaction.setDocId(docId);
        transaction.setMetaType("inpay");
        transaction.setOrderType("inpay.inpay#dd");
        transaction.setTransactionType(direction);
        transaction.setEffectiveDate(new DateTime("2015-02-26"));
        transaction.setValDate(new DateTime("2015-02-27"));
        transaction.setClearDate(new DateTime("2015-03-03"));
        transaction.setBookingText("Direct Debit Deposit from deepshikha");
        transaction.setTransactionDescription("Rent 123");
        transaction.setPayerName("deepshikha");
        transaction.setPayeeName("Tom Demo Bertrand");
        transaction.setPayerBsb("012-003");
        transaction.setPayeeBsb("262-786");
        transaction.setPayerAccount("12345678");
        transaction.setPayeeAccount("120009311");
        transaction.setBalance(new BigDecimal(50045406.87));
        transaction.setAmount(new BigDecimal(123));
        transaction.setCleared(true);
        transaction.setSystemTransaction(false);

        return transaction;
    }
}

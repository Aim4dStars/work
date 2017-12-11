package com.bt.nextgen.transactions.service;

import com.bt.nextgen.config.BaseSecureIntegrationTest;
import com.bt.nextgen.config.SecureTestContext;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.avaloq.pasttransaction.TransactionType;
import com.bt.nextgen.service.avaloq.transaction.TransactionFrequency;
import com.bt.nextgen.service.avaloq.wrapaccount.WrapAccountIdentifierImpl;
import com.bt.nextgen.service.integration.transaction.Transaction;
import com.bt.nextgen.service.integration.transaction.TransactionIntegrationService;
import com.bt.nextgen.service.integration.transactionhistory.TransactionHistory;
import com.btfin.panorama.service.integration.wrapaccount.WrapAccountIdentifier;
import org.hamcrest.core.Is;
import org.joda.time.DateTime;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class TransactionIntegrationServiceIntegrationTest extends BaseSecureIntegrationTest
{

        @Autowired
        @Qualifier("AvaloqTransactionIntegrationServiceImpl")
        TransactionIntegrationService transactionService;

        @Test
        @SecureTestContext
        public void testScheduledTransactionService() throws Exception
        {
                ServiceErrors serviceErrors = new ServiceErrorsImpl();
                WrapAccountIdentifier identifier = new WrapAccountIdentifierImpl();
                identifier.setBpId("73351");

                List <Transaction> transactions = transactionService.loadScheduledTransactions(identifier, serviceErrors);
                assertNotNull(transactions);

                assertEquals("pay", transactions.get(0).getMetaType().toString());
                assertEquals("Yearly", transactions.get(0).getFrequency().name());
                assertEquals("2000", transactions.get(0).getAmount().toString());
                assertEquals(TransactionType.PAY, transactions.get(0).getMetaType());
                assertEquals(TransactionFrequency.Yearly, transactions.get(0).getFrequency());
                assertEquals("New Standing Order (Pay Anyone)", transactions.get(0).getOrderType());
                assertEquals("217126", transactions.get(0).getTransactionId());
                assertEquals("73815", transactions.get(0).getStordPos());
                assertEquals("Rental Payment", transactions.get(0).getDescription());
                assertEquals("2000", transactions.get(0).getNetAmount().toString());

                assertEquals("73351", transactions.get(0).getPayer());
                assertEquals(null, transactions.get(0).getPayee());
                assertEquals("123456789", transactions.get(0).getPayerAccount());
                assertEquals("036081", transactions.get(0).getPayerBsb());
                assertEquals(null, transactions.get(0).getPayeeBillerCode());
                assertEquals("427033", transactions.get(0).getPayeeBsb());
                assertEquals(null, transactions.get(0).getPayeeCustrRef());
                assertEquals("123312312", transactions.get(0).getPayeeAccount());
                assertEquals(3, transactions.get(0).getMaxPeriodCnt());

                assertEquals("10 Sep 2014 14:00:00 GMT", transactions.get(0).getFirstDate().toDate().toGMTString());
                assertEquals("16 Sep 2014 14:00:00 GMT", transactions.get(0).getValDate().toDate().toGMTString());
                assertEquals("16 Sep 2014 14:00:00 GMT", transactions.get(0).getEffectiveDate().toDate().toGMTString());
                assertEquals("10 Sep 2015 14:00:00 GMT", transactions.get(0).getNextDue().toDate().toGMTString());

                assertEquals("2000", transactions.get(0).getAmount().toString());
        }

        @Test
        @SecureTestContext
        public void testPastTransactionService() throws Exception
        {
                DateTime dateFrom =  new DateTime("2015-02-25");
                DateTime dateTo =  new DateTime("2015-02-26");
                ServiceErrors serviceErrors = new ServiceErrorsImpl();

                DateTime date = new DateTime("2015-02-25");
                DateTime valDate = new DateTime("2015-02-26");
                DateTime clearDate = new DateTime("2015-03-03");

                List <TransactionHistory> transactions = transactionService.loadCashTransactionHistory("69949",
                        dateTo,
                        dateFrom,
                        serviceErrors);
                assertNotNull(transactions);

                assertEquals("inpay", transactions.get(3).getMetaType());
                assertEquals("123", transactions.get(0).getAmount().toString());
                assertEquals("198074", transactions.get(0).getDocId());
                assertEquals(valDate, transactions.get(0).getValDate());
                assertEquals(valDate, transactions.get(0).getEffectiveDate());
                assertEquals("Direct Debit Deposit from deepshikha", transactions.get(0).getBookingText());
                assertEquals(clearDate, transactions.get(0).getClearDate());
                assertEquals("120009311", transactions.get(0).getPayeeAccount());
                assertEquals("262786", transactions.get(0).getPayeeBsb());
                assertEquals("Tom Demo Bertrand", transactions.get(0).getPayeeName());
                assertEquals("12345678", transactions.get(0).getPayerAccount());
                assertEquals("012003", transactions.get(0).getPayerBsb());
                assertEquals("deepshikha", transactions.get(0).getPayerName());
                assertEquals("50045406.87", transactions.get(0).getClosingBalance().toString());
        }

        @Test
        @SecureTestContext
        public void testPastTransactionEvaluatedBalance() throws Exception
        {
                DateTime dateFrom =  new DateTime("2015-02-25");
                DateTime dateTo =  new DateTime("2015-02-26");
                ServiceErrors serviceErrors = new ServiceErrorsImpl();
                List <TransactionHistory> transactions = transactionService.loadCashTransactionHistory("69949",
                        dateTo,
                        dateFrom,
                        serviceErrors);
                assertNotNull(transactions);

                //Evaluate balance
                assertEquals("50045406.87", transactions.get(0).getBalance().toString());
                assertEquals("50045283.87", transactions.get(1).getBalance().toString());
                assertEquals("50050283.87", transactions.get(2).getBalance().toString());
                assertEquals("50050160.87", transactions.get(3).getBalance().toString());
                assertEquals("50050037.87", transactions.get(4).getBalance().toString());
                assertEquals("50050047.87", transactions.get(5).getBalance().toString());
                assertEquals("50000047.87", transactions.get(6).getBalance().toString());
                assertEquals("49999924.87", transactions.get(7).getBalance().toString());
                assertEquals("50000124.87", transactions.get(8).getBalance().toString());
                assertEquals("50000224.87", transactions.get(9).getBalance().toString());
                assertEquals("50000234.87", transactions.get(10).getBalance().toString());
                assertEquals("50000111.87", transactions.get(11).getBalance().toString());
                assertEquals("50000125.00", transactions.get(12).getBalance().toString());
                assertEquals("50000002.00", transactions.get(13).getBalance().toString());
                assertEquals("49999990.00", transactions.get(14).getBalance().toString());
                assertEquals("50000000.00", transactions.get(15).getBalance().toString());
        }

        @Test
        @SecureTestContext
        public void testPastTransactionSystemTransaction() throws Exception
        {
                DateTime dateFrom = new DateTime("2015-02-25");
                DateTime dateTo = new DateTime("2015-02-26");
                ServiceErrors serviceErrors = new ServiceErrorsImpl();
                List <TransactionHistory> transactions = transactionService.loadCashTransactionHistory("69949",
                        dateTo,
                        dateFrom,
                        serviceErrors);
                assertNotNull(transactions);

                //Evaluate balance
                assertEquals(false, transactions.get(0).isSystemTransaction());
                assertEquals(true, transactions.get(1).isSystemTransaction());
                assertEquals(false, transactions.get(2).isSystemTransaction());
                assertEquals(false, transactions.get(3).isSystemTransaction());
                assertEquals(false, transactions.get(4).isSystemTransaction());
                assertEquals(false, transactions.get(15).isSystemTransaction());

        }

        @Test
        @SecureTestContext
        public void testPastTransactionClearingStatus() throws Exception
        {
                DateTime dateFrom = new DateTime("2015-02-25");
                DateTime dateTo = new DateTime("2015-02-26");
                ServiceErrors serviceErrors = new ServiceErrorsImpl();
                List <TransactionHistory> transactions = transactionService.loadCashTransactionHistory("69949",
                        dateTo,
                        dateFrom,
                        serviceErrors);
                assertNotNull(transactions);

                assertEquals(true, transactions.get(0).isCleared());
                assertEquals(true, transactions.get(1).isCleared());
                assertEquals(true, transactions.get(5).isCleared());

        }

        @Test
        @SecureTestContext
        public void testPastTransactionOrdering() throws Exception
        {
                DateTime dateFrom = new DateTime("2015-02-25");
                DateTime dateTo = new DateTime("2015-02-26");
                ServiceErrors serviceErrors = new ServiceErrorsImpl();
                List <TransactionHistory> transactions = transactionService.loadCashTransactionHistory("69949",
                        dateTo,
                        dateFrom,
                        serviceErrors);
                assertNotNull(transactions);

                assertEquals("99", transactions.get(0).getEvtId().toString());
                assertEquals("79", transactions.get(1).getEvtId().toString());
                assertEquals("72", transactions.get(2).getEvtId().toString());
                assertEquals("56", transactions.get(3).getEvtId().toString());
                assertEquals("32", transactions.get(4).getEvtId().toString());
                assertEquals("21", transactions.get(5).getEvtId().toString());
                assertEquals("11", transactions.get(6).getEvtId().toString());
                assertEquals("10", transactions.get(7).getEvtId().toString());
                assertEquals("9", transactions.get(8).getEvtId().toString());
                assertEquals("8", transactions.get(9).getEvtId().toString());
                assertEquals("7", transactions.get(10).getEvtId().toString());
                assertEquals("6", transactions.get(11).getEvtId().toString());
                assertEquals("5", transactions.get(12).getEvtId().toString());
                assertEquals("2", transactions.get(13).getEvtId().toString());
                assertEquals("1", transactions.get(14).getEvtId().toString());
                assertEquals("4", transactions.get(15).getEvtId().toString());
        }

        @SecureTestContext(username = "explode", customerId = "201601388")
        @Test
        public void testPastTransactionClearingStatusError() throws Exception
        {
                DateTime dateFrom = new DateTime("2015-02-25");
                DateTime dateTo = new DateTime("2015-02-26");
                ServiceErrors serviceErrors = new ServiceErrorsImpl();
                List <TransactionHistory> transactions = transactionService.loadCashTransactionHistory("69949",
                        dateTo,
                        dateFrom,
                        serviceErrors);
                assertNotNull(transactions);
                assertThat(serviceErrors.hasErrors(), Is.is(true));
        }

        @SecureTestContext(username = "explode", customerId = "201101101")
        @Test
        public void testScheduledTransactionServiceError() throws Exception
        {
                ServiceErrors serviceErrors = new ServiceErrorsImpl();
                WrapAccountIdentifier identifier = new WrapAccountIdentifierImpl();
                identifier.setBpId("73351");
                List <Transaction> transactions = transactionService.loadScheduledTransactions(identifier, serviceErrors);
                assertThat(serviceErrors.hasErrors(), Is.is(true));
        }
}
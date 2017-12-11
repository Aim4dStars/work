package com.bt.nextgen.api.transaction.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Qualifier;

import com.bt.nextgen.api.transaction.model.TransactionDto;
import com.bt.nextgen.api.transaction.model.TransactionKey;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria.OperationType;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria.SearchOperation;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.TransactionStatusImpl;
import com.bt.nextgen.service.avaloq.pasttransaction.TransactionType;
import com.bt.nextgen.service.avaloq.payeedetails.CashAccountDetailsImpl;
import com.bt.nextgen.service.avaloq.payeedetails.PayeeDetailsImpl;
import com.bt.nextgen.service.avaloq.transaction.TransactionFrequency;
import com.bt.nextgen.service.avaloq.transaction.TransactionImpl;
import com.bt.nextgen.service.avaloq.transaction.TransactionWorkflowStatus;
import com.bt.nextgen.service.avaloq.transactionhistory.TransactionHistoryImpl;
import com.bt.nextgen.service.avaloq.wrapaccount.WrapAccountIdentifierImpl;
import com.bt.nextgen.service.integration.PositionIdentifier;
import com.bt.nextgen.service.integration.TransactionStatus;
import com.bt.nextgen.service.integration.cashcategorisation.model.CashCategorisationType;
import com.bt.nextgen.service.integration.movemoney.DepositIntegrationService;
import com.bt.nextgen.service.integration.order.OrderType;
import com.bt.nextgen.service.integration.payeedetails.PayeeDetailsIntegrationService;
import com.bt.nextgen.service.integration.transaction.Transaction;
import com.bt.nextgen.service.integration.transaction.TransactionIntegrationService;
import com.bt.nextgen.service.integration.transaction.TransactionIntegrationServiceFactory;
import com.bt.nextgen.service.integration.transactionhistory.BTOrderType;
import com.bt.nextgen.service.integration.transactionhistory.TransactionHistory;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import com.btfin.panorama.core.security.avaloq.Constants;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.btfin.panorama.service.integration.wrapaccount.WrapAccountIdentifier;

@RunWith(MockitoJUnitRunner.class)
public class TransactionDtoServiceImplTest {
    @InjectMocks
    TransactionDtoServiceImpl transactionDtoServiceImpl;

    @Mock
    @Qualifier("AvaloqTransactionIntegrationServiceImpl")
    TransactionIntegrationService transactionIntegrationService;

    @Mock
    DepositIntegrationService depositIntegrationService;

    @Mock
    PayeeDetailsIntegrationService payeeDetailsIntegrationService;

    @Mock
    private TransactionIntegrationServiceFactory transactionIntegrationServiceFactory;

    ServiceErrors serviceErrors;

    WrapAccountIdentifier identifier;

    @Before
    public void setup() {
        serviceErrors = new ServiceErrorsImpl();

        identifier = new WrapAccountIdentifierImpl();
        identifier.setBpId(EncodedString.fromPlainText("11861").toString());

        mockTransactionServices();
        mockDepositService();
        mockPayeeService();

    }

    private void mockTransactionServices() {
        List<Transaction> transactions = new ArrayList<Transaction>();
        Transaction transaction = new TransactionImpl();
        transaction.setDescription("Direct Credit to the rental payment");
        transaction.setNetAmount(new BigDecimal("5000"));
        transaction.setWorkFlowStatus(TransactionWorkflowStatus.SCHEDULED);
        transaction.setOrderType("Direct Credit");
        transaction.setOrderTypeCode(OrderType.STORD_NEW_PENSION_PAYMENT);
        transaction.setFrequency(TransactionFrequency.Monthly);
        transaction.setMetaType(TransactionType.INPAY);
        transaction.setRepeatInstr("until");
        transaction.setRecentTrxDate(new DateTime("2016-04-01"));
        transaction.setTransactionId("123456");

        transactions.add(transaction);

        List<TransactionHistory> transactionHistories = new ArrayList<TransactionHistory>();

        transactionHistories.add(makeTransactionHistory(null,
                "Direct Credit", new BigDecimal("5000"),
                "Advise description", BTOrderType.DEPOSIT, null, "Test transaction"));
        transactionHistories.add(makeTransactionHistory(null,
                "Insurance payment", new BigDecimal("2300"),
                "doc2", BTOrderType.PAYMENT, null, null));
        transactionHistories.add(makeTransactionHistory(null,
                "Reversed transaction", new BigDecimal("123"),
                "doc3", null, "-36", "Test transaction"));
        transactionHistories.add(makeTransactionHistory(CashCategorisationType.ADMINISTRATION,
                "original transaction", new BigDecimal("223"),
                "doc4", null, "36", "Test transaction"));

        when(transactionIntegrationServiceFactory.getInstance(any(String.class)))
                .thenReturn(transactionIntegrationService);

        when(transactionIntegrationService.loadScheduledTransactions(any(WrapAccountIdentifier.class),
                any(ServiceErrors.class))).thenReturn(transactions);

        when(transactionIntegrationService.loadRecentCashTransactions(any(WrapAccountIdentifier.class),
                any(ServiceErrors.class))).thenReturn(transactionHistories);
    }

    private TransactionHistoryImpl makeTransactionHistory(CashCategorisationType categorisationType, String bookingText, BigDecimal amount,
                                                          String docDescription, BTOrderType btOrderType, String status, String transactionDescription) {
        final TransactionHistoryImpl transactionHistory = new TransactionHistoryImpl();

        transactionHistory.setCashCategorisationType(categorisationType);
        transactionHistory.setBookingText(bookingText);
        transactionHistory.setTransactionDescription(transactionDescription);
        transactionHistory.setAmount(amount);
        transactionHistory.setDocDescription(docDescription);
        transactionHistory.setBTOrderType(btOrderType);
        transactionHistory.setStatus(status);

        return transactionHistory;
    }

    private void mockDepositService() {
        TransactionStatus mockStatus = new TransactionStatusImpl();
        mockStatus.setSuccessful(true);

        when(depositIntegrationService.stopDeposit(any(PositionIdentifier.class), any(ServiceErrors.class)))
                .thenReturn(mockStatus);
    }

    private void mockPayeeService() {
        CashAccountDetailsImpl cashAccount = new CashAccountDetailsImpl();
        cashAccount.setAccountName("Test Cash");

        List<CashAccountDetailsImpl> accounts = new ArrayList<>();
        accounts.add(cashAccount);

        PayeeDetailsImpl payeeDetails = new PayeeDetailsImpl();
        payeeDetails.setCashAccount(accounts);

        when(payeeDetailsIntegrationService.loadPayeeDetails(any(WrapAccountIdentifier.class), any(ServiceErrors.class)))
                .thenReturn(payeeDetails);
    }

    @Test
    public void testSearchWithScheduledTransactionCriteria() {
        List<ApiSearchCriteria> scheduledTransactionCriteria = new ArrayList<ApiSearchCriteria>();

        scheduledTransactionCriteria.add(new ApiSearchCriteria(Attribute.PORTFOLIO_ID, SearchOperation.EQUALS,
                identifier.getAccountIdentifier(), OperationType.STRING));

        scheduledTransactionCriteria.add(new ApiSearchCriteria(Attribute.TRANSACTION_TYPE, SearchOperation.EQUALS,
                Attribute.SCHEDULED_TRANSACTIONS, OperationType.STRING));

        scheduledTransactionCriteria.add(
                new ApiSearchCriteria(Constants.PAYEE_DETAIL_REQUIRED, SearchOperation.EQUALS, "true", OperationType.STRING));

        List<TransactionDto> transactionDto = transactionDtoServiceImpl.search(scheduledTransactionCriteria, serviceErrors);

        assertNotNull(transactionDto);
        assertEquals(1, transactionDto.size());
        assertEquals(new DateTime("2016-04-01"), transactionDto.get(0).getRecentTrxDate());
        assertEquals(transactionDto.get(0).getOrderTypeCode(), OrderType.STORD_NEW_PENSION_PAYMENT.name());
    }

    @Test
    public void testSearchWithStopScheduledTransactionCriteria() {
        List<ApiSearchCriteria> stopSchedTransactionCriteria = new ArrayList<ApiSearchCriteria>();

        stopSchedTransactionCriteria.add(new ApiSearchCriteria(Attribute.ACCOUNT_ID, SearchOperation.EQUALS,
                identifier.getAccountIdentifier(), OperationType.STRING));

        stopSchedTransactionCriteria.add(new ApiSearchCriteria(Attribute.TRANSACTION_TYPE, SearchOperation.EQUALS,
                Attribute.STOP_SCHEDULED_TRANSACTIONS, OperationType.STRING));

        stopSchedTransactionCriteria
                .add(new ApiSearchCriteria(Constants.META_TYPE, SearchOperation.EQUALS, Constants.DEPOSIT, OperationType.STRING));

        stopSchedTransactionCriteria.add(new ApiSearchCriteria(Attribute.TRANSACTION_ID, SearchOperation.EQUALS,
                EncodedString.fromPlainText("10").toString(), OperationType.STRING));

        List<TransactionDto> transactionDto = transactionDtoServiceImpl.search(stopSchedTransactionCriteria, serviceErrors);

        assertNotNull(transactionDto);
        // Updating test scenario as stopped transactions will be removed from display as per new requirement
        assertEquals(0, transactionDto.size());
    }

    @Test
    public void testSearchWithRecentTransactionCriteria() {
        List<ApiSearchCriteria> recentTransactionCriteria = new ArrayList<ApiSearchCriteria>();

        recentTransactionCriteria.add(new ApiSearchCriteria(Attribute.ACCOUNT_ID, SearchOperation.EQUALS,
                identifier.getAccountIdentifier(), OperationType.STRING));
        recentTransactionCriteria.add(new ApiSearchCriteria(Attribute.TRANSACTION_TYPE, SearchOperation.EQUALS,
                Attribute.RECENT_TRANSACTIONS, OperationType.STRING));

        List<TransactionDto> transactionDto = transactionDtoServiceImpl.search(recentTransactionCriteria, serviceErrors);

        assertNotNull(transactionDto);
        assertEquals(4, transactionDto.size());
        assertEquals("Advise description", transactionDto.get(0).getDocDescription());
        assertEquals("Direct Credit. Test transaction", transactionDto.get(0).getDescription());
        assertEquals("Insurance payment", transactionDto.get(1).getDescription());
    }

    @Test
    public void testFindWithTransactionKey() {
        TransactionDto transactionDto = transactionDtoServiceImpl
                .find(new TransactionKey(EncodedString.fromPlainText("11861").toString(),
                        EncodedString.fromPlainText("123456").toString()), serviceErrors);
        assertNotNull(transactionDto);
        assertEquals(transactionDto.getFrequency(), "Monthly");
    }
}

package com.bt.nextgen.service.wrap.integration.transaction;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.transactionhistory.TransactionHistoryConverter;
import com.bt.nextgen.service.avaloq.transactionhistory.TransactionHistoryImpl;
import com.bt.nextgen.service.avaloq.transactionhistory.WrapTransactionHistoryConverter;
import com.bt.nextgen.service.avaloq.transactionhistory.WrapTransactionHistoryImpl;
import com.bt.nextgen.service.avaloq.wrapaccount.WrapAccountIdentifierImpl;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.bankdate.BankDateIntegrationService;
import com.bt.nextgen.service.integration.base.AvaloqAccountIntegrationService;
import com.bt.nextgen.service.integration.base.SystemType;
import com.bt.nextgen.service.integration.base.ThirdPartyDetails;
import com.bt.nextgen.service.integration.transactionhistory.BTOrderType;
import com.bt.nextgen.service.integration.transactionhistory.TransactionHistory;
import com.btfin.panorama.service.client.error.ServiceErrorsImpl;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.btfin.panorama.service.integration.wrapaccount.WrapAccountIdentifier;
import com.btfin.panorama.wrap.model.CashStatement;
import com.btfin.panorama.wrap.service.TransactionService;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Qualifier;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class WrapTransactionIntegrationServiceTest {

    @InjectMocks
    @Qualifier("wrapTransactionIntegrationServiceImpl")
    WrapTransactionIntegrationService dashboardTransactionIntegrationService;

    @Mock
    @Qualifier("ThirdPartyAvaloqAccountIntegrationService")
    private AvaloqAccountIntegrationService avaloqAccountIntegrationService;

    @Mock
    private TransactionService transactionService;

    @Mock
    private TransactionHistoryConverter transactionHistoryConverter;

    @Mock
    private WrapTransactionHistoryConverter wrapTransactionHistoryConverter;

    @Mock
    private BankDateIntegrationService bankDate;

    @Before
    public void setUp() {
        DateTime date1 = new DateTime(2017, Calendar.FEBRUARY, 11, 0, 0, 0);
        Mockito.when(bankDate.getBankDate(Mockito.any(ServiceErrors.class))).thenReturn(date1);
        List<CashStatement> cashStatements = getCashStatements();
        Mockito.when(transactionService.getCashStatementForClient(Mockito.any(String.class), Mockito.any(DateTime.class),
                Mockito.any(DateTime.class), Mockito.any(ServiceErrors.class))).thenReturn(cashStatements);
        List<TransactionHistory> pastTransactions = getPastTransactions();
        Mockito.when(transactionHistoryConverter.evaluateBalanceAndSystemTransaction(
                Mockito.anyListOf(TransactionHistory.class), Mockito.any(DateTime.class))).thenReturn(pastTransactions);
    }

    private List<CashStatement> getCashStatements() {
        List<CashStatement> cashStatements = new ArrayList<>();

        CashStatement cashStatement = new CashStatement();
        cashStatement.setId("480343763");
        cashStatement.setClientId("M00533624");
        cashStatement.setOpeningBalance(new BigDecimal("1650.15"));
        cashStatement.setClosingBalance(new BigDecimal("1522.21"));
        cashStatement.setServiceType("Interest");
        cashStatement.setServiceSubType("Interest");
        cashStatement.setEffectiveDate("2016-01-01 00:00:00.0");
        cashStatement.setAmount(new BigDecimal("3.37"));
        cashStatement.setNote(null);
        cashStatement.setStockTransTp(" ");
        cashStatement.setPanoTxnType("income");

        CashStatement cashStatement2 = new CashStatement();
        cashStatement2.setId("480875325");
        cashStatement2.setClientId("M00533624");
        cashStatement2.setOpeningBalance(new BigDecimal("1650.15"));
        cashStatement2.setClosingBalance(new BigDecimal("1522.21"));
        cashStatement2.setServiceType("Fee");
        cashStatement2.setServiceSubType("Ongoing Adviser Fee");
        cashStatement2.setEffectiveDate("2016-01-04 00:00:00.0");
        cashStatement2.setAmount(new BigDecimal("-41.67"));
        cashStatement2.setNote(null);
        cashStatement2.setStockTransTp("for the period 01 Dec 2015  to 31 Dec 2015");
        cashStatement2.setPanoTxnType("expns");

        CashStatement cashStatement3 = new CashStatement();
        cashStatement3.setId("485397183");
        cashStatement3.setClientId("M00533624");
        cashStatement3.setOpeningBalance(new BigDecimal("1650.15"));
        cashStatement3.setClosingBalance(new BigDecimal("1522.21"));
        cashStatement3.setServiceType("Fee");
        cashStatement3.setServiceSubType("Ongoing Adviser Fee");
        cashStatement3.setEffectiveDate("2016-02-01 00:00:00.0");
        cashStatement3.setAmount(new BigDecimal("-40.75"));
        cashStatement3.setNote(null);
        cashStatement3.setStockTransTp("for the period 01 Jan 2016  to 31 Jan 2016 ");
        cashStatement3.setPanoTxnType("expns");

        cashStatements.add(cashStatement);
        cashStatements.add(cashStatement2);
        cashStatements.add(cashStatement3);

        return cashStatements;
    }

    private List<TransactionHistory> getPastTransactions() {
        List<TransactionHistory> transactions = new ArrayList<>();

        TransactionHistoryImpl transaction = new TransactionHistoryImpl();
        transaction.setEvtId(111);
        transaction.setDocId("485397183");
        DateTime effDate = new DateTime(2016, 2, 01, 0, 0, 0);
        DateTime valDate = new DateTime(2016, 2, 01, 0, 0, 0);
        transaction.setValDate(valDate);
        transaction.setEffectiveDate(effDate);
        transaction.setBookingText("Ongoing Adviser Fee for the period 01 Jan 2016  to 31 Jan 2016 ");
        transaction.setAmount(new BigDecimal("-40.75"));
        transaction.setClosingBalance(new BigDecimal(1522.21));
        transaction.setBalance(new BigDecimal("1609.40"));
        transaction.setBTOrderType(BTOrderType.EXPENSES);
        transaction.setMetaType("xferfee");
        transaction.setOrderType("xferfee.platform_adm");
        transaction.setSystemTransaction(false);
        transactions.add(transaction);


        TransactionHistoryImpl transaction1 = new TransactionHistoryImpl();
        transaction1.setEvtId(111);
        transaction1.setDocId("480875325");
        DateTime effDate1 = new DateTime(2016, 1, 04, 0, 0, 0);
        DateTime valDate1 = new DateTime(2016, 1, 04, 0, 0, 0);
        transaction1.setValDate(valDate1);
        transaction1.setEffectiveDate(effDate1);
        transaction1.setBookingText("Ongoing Adviser Fee for the period 01 Dec 2015  to 31 Dec 2015");
        transaction1.setAmount(new BigDecimal("-41.67"));
        transaction1.setClosingBalance(new BigDecimal(1522.21));
        transaction1.setBalance(new BigDecimal("1608.48"));
        transaction1.setBTOrderType(BTOrderType.EXPENSES);
        transaction1.setMetaType("xferfee");
        transaction1.setOrderType("xferfee.platform_adm");
        transaction1.setSystemTransaction(false);
        transactions.add(transaction1);

        TransactionHistoryImpl transaction2 = new TransactionHistoryImpl();
        transaction2.setEvtId(111);
        transaction2.setDocId("480343763");
        DateTime effDate2 = new DateTime(2016, 1, 01, 0, 0, 0);
        DateTime valDate2 = new DateTime(2016, 1, 01, 0, 0, 0);
        transaction2.setValDate(valDate2);
        transaction2.setEffectiveDate(effDate2);
        transaction2.setBookingText("Interest");
        transaction2.setAmount(new BigDecimal("3.37"));
        transaction2.setClosingBalance(new BigDecimal(1522.21));
        transaction2.setBalance(new BigDecimal("1653.52"));
        transaction2.setBTOrderType(BTOrderType.INCOME);
        transaction2.setMetaType("xferfee");
        transaction2.setOrderType("xferfee.platform_adm");
        transaction2.setSystemTransaction(false);
        transactions.add(transaction2);

        return transactions;
    }

    @Test
    public void loadCashTransactionHistoryTest() throws Exception {
        WrapAccountIdentifier accountIdentifier = new WrapAccountIdentifierImpl();
        accountIdentifier.setBpId("1234567");
        List<TransactionHistory> pastTransactions = dashboardTransactionIntegrationService.loadCashTransactionHistory(
                "1234567", new DateTime("2016-02-11"), new DateTime("2016-02-11"), new FailFastErrorsImpl());
        assertNotNull(pastTransactions);
        assertThat(pastTransactions.size(), equalTo(3));
        TransactionHistory transactionHistory = pastTransactions.get(0);
        assertThat(transactionHistory.getEvtId(), equalTo(new Integer(111)));
        assertThat(transactionHistory.getDocId(), equalTo("485397183"));
        assertThat(transactionHistory.getAmount(), equalTo(new BigDecimal("-40.75")));
        assertThat(transactionHistory.getBookingText(), equalTo("Ongoing Adviser Fee for the period 01 Jan 2016  to 31 Jan 2016 "));
        assertThat(transactionHistory.getBalance(), equalTo(new BigDecimal("1609.40")));
        assertThat(transactionHistory.getBTOrderType(), equalTo(BTOrderType.EXPENSES));
        assertThat(transactionHistory.getMetaType(), equalTo("xferfee"));
        assertThat(transactionHistory.getOrderType(), equalTo("xferfee.platform_adm"));
        assertThat(transactionHistory.isSystemTransaction(), equalTo(false));

        transactionHistory = pastTransactions.get(1);
        assertThat(transactionHistory.getEvtId(), equalTo(new Integer(111)));
        assertThat(transactionHistory.getDocId(), equalTo("480875325"));
        assertThat(transactionHistory.getAmount(), equalTo(new BigDecimal("-41.67")));
        assertThat(transactionHistory.getBookingText(), equalTo("Ongoing Adviser Fee for the period 01 Dec 2015  to 31 Dec 2015"));
        assertThat(transactionHistory.getBalance(), equalTo(new BigDecimal("1608.48")));
        assertThat(transactionHistory.getBTOrderType(), equalTo(BTOrderType.EXPENSES));
        assertThat(transactionHistory.getMetaType(), equalTo("xferfee"));
        assertThat(transactionHistory.getOrderType(), equalTo("xferfee.platform_adm"));
        assertThat(transactionHistory.isSystemTransaction(), equalTo(false));

        transactionHistory = pastTransactions.get(2);
        assertThat(transactionHistory.getEvtId(), equalTo(new Integer(111)));
        assertThat(transactionHistory.getDocId(), equalTo("480343763"));
        assertThat(transactionHistory.getAmount(), equalTo(new BigDecimal("3.37")));
        assertThat(transactionHistory.getBookingText(), equalTo("Interest"));
        assertThat(transactionHistory.getBalance(), equalTo(new BigDecimal("1653.52")));
        assertThat(transactionHistory.getBTOrderType(), equalTo(BTOrderType.INCOME));
        assertThat(transactionHistory.getMetaType(), equalTo("xferfee"));
        assertThat(transactionHistory.getOrderType(), equalTo("xferfee.platform_adm"));
        assertThat(transactionHistory.isSystemTransaction(), equalTo(false));

    }

    @Test
    public void loadCashTransactionHistoryTest_emptyResponse() throws Exception {
        WrapAccountIdentifier accountIdentifier = new WrapAccountIdentifierImpl();
        accountIdentifier.setBpId("1234567");
        Mockito.when(transactionService.getCashStatementForClient(Mockito.any(String.class), Mockito.any(DateTime.class),
                Mockito.any(DateTime.class), Mockito.any(ServiceErrors.class))).thenReturn(null);
        Mockito.when(transactionHistoryConverter.evaluateBalanceAndSystemTransaction(
                Mockito.anyListOf(TransactionHistory.class), Mockito.any(DateTime.class))).thenReturn(new ArrayList<TransactionHistory>());
        List<TransactionHistory> pastTransactions = dashboardTransactionIntegrationService.loadCashTransactionHistory(
                "1234567", new DateTime("2016-02-11"), new DateTime("2016-02-11"), new FailFastErrorsImpl());
        assertNotNull(pastTransactions);
        assertThat(pastTransactions.size(), equalTo(0));
    }

    @Test
    public void testLoadTransactionHistory() {
        List<TransactionHistory> wrapTransactionHistories = new ArrayList<>();

        WrapTransactionHistoryImpl transactionHistory = new WrapTransactionHistoryImpl();

        transactionHistory.setDocId("30577963");
        transactionHistory.setEffectiveDate(new DateTime("2015-01-01T00:00:00.000+05:30"));
        transactionHistory.setValDate(new DateTime("2015-01-01T00:00:00.000+05:30"));
        transactionHistory.setQuantity(new BigDecimal("23"));
        transactionHistory.setAmount(new BigDecimal("2365.14"));
        transactionHistory.setAssetCode("BGL0034AU");
        transactionHistory.setAssetName("Security 1");
        transactionHistory.setTransactionType("Buy");
        transactionHistory.setBookingText("Booking text1");

        wrapTransactionHistories.add(transactionHistory);

        List<com.btfin.panorama.wrap.model.TransactionHistory> transactionHistories = new ArrayList<>();
        com.btfin.panorama.wrap.model.TransactionHistory wrapTransactionHistory = new com.btfin.panorama.wrap.model.TransactionHistory();
        wrapTransactionHistory.setMovementId("30577963");
        wrapTransactionHistory.setTradeDate("2015-01-01T00:00:00.000+05:30");
        wrapTransactionHistory.setSettlementDate("2015-01-01T00:00:00.000+05:30");
        wrapTransactionHistory.setQuantity(new BigDecimal("23"));
        wrapTransactionHistory.setAmount(new BigDecimal("2365.14"));
        wrapTransactionHistory.setSecurityCode("BGL0034AU");
        wrapTransactionHistory.setSecurityName("Security 1");
        wrapTransactionHistory.setTransactionType("Buy");
        wrapTransactionHistory.setBookingText("Booking text1");
        transactionHistories.add(wrapTransactionHistory);

        ThirdPartyDetails thirdPartyDetails = new ThirdPartyDetails();
        thirdPartyDetails.setSystemType(SystemType.WRAP);
        thirdPartyDetails.setMigrationKey("M02744476");
        thirdPartyDetails.setMigrationDate(new DateTime());
        Mockito.when(avaloqAccountIntegrationService.getThirdPartySystemDetails(Mockito.any(AccountKey.class), Mockito.any(ServiceErrors.class))).thenReturn(thirdPartyDetails);

        Mockito.when(
                transactionService.getTransactionHistoryForClient(
                        Mockito.any(String.class), Mockito.any(DateTime.class), Mockito.any(DateTime.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(transactionHistories);

        Mockito.when(wrapTransactionHistoryConverter.convertWrapTransactionsToPanorama(Matchers.anyList(), (ServiceErrors) Matchers.anyObject()))
                .thenReturn(wrapTransactionHistories);

        List<TransactionHistory> transactionHistories1 =
                dashboardTransactionIntegrationService.loadTransactionHistory("12365", new DateTime("2016-02-11"), new DateTime("2016-02-11"), new ServiceErrorsImpl());
        Assert.assertNotNull(transactionHistories1);
    }

    @Test
    public void testLoadTransactionHistoryWithNoResponse() {
        List<TransactionHistory> wrapTransactionHistories = new ArrayList<>();

        Mockito.when(
                transactionService.getTransactionHistoryForClient(
                        Mockito.any(String.class), Mockito.any(DateTime.class), Mockito.any(DateTime.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(null);

        Mockito.when(wrapTransactionHistoryConverter.convertWrapTransactionsToPanorama(Matchers.anyList(), (ServiceErrors) Matchers.anyObject()))
                .thenReturn(wrapTransactionHistories);

        List<TransactionHistory> transactionHistories1 =
                dashboardTransactionIntegrationService.loadTransactionHistory("12365", new DateTime("2016-02-11"), new DateTime("2016-02-11"), new ServiceErrorsImpl());
        Assert.assertNotNull(transactionHistories1);
    }
}
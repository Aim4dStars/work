package com.bt.nextgen.service.wrap.integration;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.transactionhistory.TransactionHistoryImpl;
import com.bt.nextgen.service.avaloq.transactionhistory.WrapTransactionHistoryImpl;
import com.bt.nextgen.service.avaloq.wrapaccount.WrapAccountIdentifierImpl;
import com.bt.nextgen.service.integration.Origin;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.account.ContainerType;
import com.bt.nextgen.service.integration.base.AvaloqAccountIntegrationService;
import com.bt.nextgen.service.integration.base.SystemType;
import com.bt.nextgen.service.integration.base.ThirdPartyDetails;
import com.bt.nextgen.service.integration.transaction.TransactionIntegrationService;
import com.bt.nextgen.service.integration.transaction.DashboardTransactionIntegrationService;
import com.bt.nextgen.service.integration.transactionhistory.BTOrderType;
import com.bt.nextgen.service.integration.transactionhistory.TransactionHistory;
import com.btfin.panorama.service.client.error.ServiceErrorsImpl;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.btfin.panorama.service.integration.asset.AssetType;
import com.btfin.panorama.service.integration.wrapaccount.WrapAccountIdentifier;
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
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * TransactionIntegrationService
 */
@RunWith(MockitoJUnitRunner.class)
public class TransactionIntegrationServiceImplTest {

    @InjectMocks
    @Qualifier("ThirdPartyTransactionIntegrationService")
    TransactionIntegrationServiceImpl transactionIntegrationService;

    @Mock
    @Qualifier("wrapTransactionIntegrationServiceImpl")
    private DashboardTransactionIntegrationService wrapTransactionIntegrationService;

    @Mock
    @Qualifier("AvaloqTransactionIntegrationServiceImpl")
    private TransactionIntegrationService avaloqTransactionIntegrationService;

    @Mock
    @Qualifier("ThirdPartyAvaloqAccountIntegrationService")
    private AvaloqAccountIntegrationService avaloqAccountIntegrationService;

    @Before
    public void setUp() {
        ThirdPartyDetails thirdPartyDetails = new ThirdPartyDetails();
        thirdPartyDetails.setSystemType(SystemType.WRAP);
        thirdPartyDetails.setMigrationKey("M02744476");
        thirdPartyDetails.setMigrationDate(new DateTime(2017, 1, 04, 0, 0, 0));
        Mockito.when(
                avaloqAccountIntegrationService.getThirdPartySystemDetails(Mockito.any(AccountKey.class),
                        Mockito.any(ServiceErrors.class))).thenReturn(thirdPartyDetails);
    }

    private List<TransactionHistory> getWrapTransactions() {
        List<TransactionHistory> transactions = new ArrayList<>();

        TransactionHistoryImpl transaction1 = new TransactionHistoryImpl();
        transaction1.setEvtId(480875325);
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
        transaction2.setEvtId(480343763);
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

    private List<TransactionHistory> getAvaloqTransactions() {
        List<TransactionHistory> transactions = new ArrayList<>();

        TransactionHistoryImpl transaction = new TransactionHistoryImpl();
        transaction.setEvtId(485397183);
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

        return transactions;
    }


    @Test
    public void loadScheduledTransactions() throws Exception {
        assertTrue(true);
    }

    @Test
    public void testLoadTransactionHistoryBeforeMigrationDate() {
        List<TransactionHistory> transactionHistories = new ArrayList<>();
        List<TransactionHistory> wrapTransactionHistories = new ArrayList<>();

        TransactionHistoryImpl transactionHistory = new TransactionHistoryImpl();
        transactionHistory.setPosAssetId("2234");
        transactionHistory.setBookingText("buy");
        transactionHistory.setTransactionDescription("Optional desc");
        transactionHistory.setDocId("1234");
        transactionHistory.setAmount(new BigDecimal(2344));
        transactionHistory.setPosName("BT0001 BT Managed Account");
        transactionHistory.setStatus("Approved");
        transactionHistory.setEffectiveDate(new DateTime());
        transactionHistory.setValDate(new DateTime());
        transactionHistory.setContType(ContainerType.MANAGED_PORTFOLIO);
        transactionHistory.setOrigin(Origin.WEB_UI);
        transactionHistories.add(transactionHistory);

        WrapTransactionHistoryImpl wrapTransactionHistory = new WrapTransactionHistoryImpl();
        wrapTransactionHistory.setDocId("30577963");
        wrapTransactionHistory.setEffectiveDate(new DateTime("2015-01-01T00:00:00.000+05:30"));
        wrapTransactionHistory.setValDate(new DateTime("2015-01-01T00:00:00.000+05:30"));
        wrapTransactionHistory.setQuantity(new BigDecimal("23"));
        wrapTransactionHistory.setAssetCode("BGL0034AU");
        wrapTransactionHistory.setAssetName("Security 1");
        wrapTransactionHistory.setAssetType(AssetType.MANAGED_FUND);
        wrapTransactionHistory.setTransactionType("Buy");
        wrapTransactionHistory.setBookingText("Booking text1");
        wrapTransactionHistories.add(wrapTransactionHistory);

        Mockito.when(avaloqTransactionIntegrationService.loadTransactionHistory(Matchers.anyString(), Mockito.any(DateTime.class), Mockito.any(DateTime.class),
                Mockito.any(ServiceErrors.class))).thenReturn(transactionHistories);

        Mockito.when(wrapTransactionIntegrationService.loadTransactionHistory(Matchers.anyString(), Mockito.any(DateTime.class), Mockito.any(DateTime.class),
                Mockito.any(ServiceErrors.class))).thenReturn(wrapTransactionHistories);

        DateTime fromDate = new DateTime(2016, 1, 04, 0, 0, 0);
        DateTime toDate = new DateTime(2016, 1, 04, 0, 0, 0);
        List<TransactionHistory> transactionHistoryList = transactionIntegrationService.loadTransactionHistory("36598", toDate, fromDate, new ServiceErrorsImpl());
        Assert.assertNotNull(transactionHistoryList);
        Assert.assertTrue(transactionHistoryList.size() == 2);
    }

    @Test
    public void testLoadTransactionHistoryAfterMigrationDate() {
        List<TransactionHistory> transactionHistories = new ArrayList<>();

        TransactionHistoryImpl transactionHistory = new TransactionHistoryImpl();
        transactionHistory.setPosAssetId("2234");
        transactionHistory.setBookingText("buy");
        transactionHistory.setTransactionDescription("Optional desc");
        transactionHistory.setDocId("1234");
        transactionHistory.setAmount(new BigDecimal(2344));
        transactionHistory.setPosName("BT0001 BT Managed Account");
        transactionHistory.setStatus("Approved");
        transactionHistory.setEffectiveDate(new DateTime());
        transactionHistory.setValDate(new DateTime());
        transactionHistory.setContType(ContainerType.MANAGED_PORTFOLIO);
        transactionHistory.setOrigin(Origin.WEB_UI);
        transactionHistories.add(transactionHistory);

        Mockito.when(avaloqTransactionIntegrationService.loadTransactionHistory(Matchers.anyString(), Mockito.any(DateTime.class), Mockito.any(DateTime.class),
                Mockito.any(ServiceErrors.class))).thenReturn(transactionHistories);

        DateTime fromDate = new DateTime(2017, 1, 06, 0, 0, 0);
        DateTime toDate = new DateTime(2016, 1, 04, 0, 0, 0);
        List<TransactionHistory> transactionHistoryList = transactionIntegrationService.loadTransactionHistory("36598", toDate, fromDate, new ServiceErrorsImpl());
        Assert.assertNotNull(transactionHistoryList);
        Assert.assertTrue(transactionHistoryList.size() == 1);
    }

    @Test
    public void testLoadTransactionHistoryIfMigrationDateNull() {
        List<TransactionHistory> transactionHistories = new ArrayList<>();

        ThirdPartyDetails thirdPartyDetails = new ThirdPartyDetails();
        thirdPartyDetails.setSystemType(SystemType.WRAP);
        thirdPartyDetails.setMigrationKey("M02744476");
        thirdPartyDetails.setMigrationDate(null);
        Mockito.when(
                avaloqAccountIntegrationService.getThirdPartySystemDetails(Mockito.any(AccountKey.class),
                        Mockito.any(ServiceErrors.class))).thenReturn(thirdPartyDetails);

        TransactionHistoryImpl transactionHistory = new TransactionHistoryImpl();
        transactionHistory.setPosAssetId("2234");
        transactionHistory.setBookingText("buy");
        transactionHistory.setTransactionDescription("Optional desc");
        transactionHistory.setDocId("1234");
        transactionHistory.setAmount(new BigDecimal(2344));
        transactionHistory.setPosName("BT0001 BT Managed Account");
        transactionHistory.setStatus("Approved");
        transactionHistory.setEffectiveDate(new DateTime());
        transactionHistory.setValDate(new DateTime());
        transactionHistory.setContType(ContainerType.MANAGED_PORTFOLIO);
        transactionHistory.setOrigin(Origin.WEB_UI);
        transactionHistories.add(transactionHistory);

        Mockito.when(avaloqTransactionIntegrationService.loadTransactionHistory(Matchers.anyString(), Mockito.any(DateTime.class), Mockito.any(DateTime.class),
                Mockito.any(ServiceErrors.class))).thenReturn(transactionHistories);

        DateTime fromDate = new DateTime(2017, 1, 06, 0, 0, 0);
        DateTime toDate = new DateTime(2016, 1, 04, 0, 0, 0);
        List<TransactionHistory> transactionHistoryList = transactionIntegrationService.loadTransactionHistory("36598", toDate, fromDate, new ServiceErrorsImpl());
        Assert.assertNotNull(transactionHistoryList);
        Assert.assertTrue(transactionHistoryList.size() == 1);
    }

    @Test
    public void loadCashTransactionHistoryTest_WrapDataExists_AvaloqDataExists() throws Exception {

        List<TransactionHistory> wrapTransactions = getWrapTransactions();
        List<TransactionHistory> avaloqTransactions = getAvaloqTransactions();

        Mockito.when(wrapTransactionIntegrationService.loadCashTransactionHistory(Mockito.any(String.class), Mockito.any(DateTime.class),
                Mockito.any(DateTime.class), Mockito.any(ServiceErrors.class))).thenReturn(wrapTransactions);
        Mockito.when(avaloqTransactionIntegrationService.loadCashTransactionHistory(Mockito.any(String.class), Mockito.any(DateTime.class),
                Mockito.any(DateTime.class), Mockito.any(ServiceErrors.class))).thenReturn(avaloqTransactions);

        List<TransactionHistory> pastTransactions = transactionIntegrationService.loadCashTransactionHistory(
                "1234567", new DateTime("2016-02-11"), new DateTime("2016-02-11"), new FailFastErrorsImpl());
        assertNotNull(pastTransactions);
        assertThat(pastTransactions.size(), equalTo(3));
    }

    @Test
    public void loadCashTransactionHistoryTest_WrapDataEmpty_AvaloqDataExists() throws Exception {

        List<TransactionHistory> wrapTransactions = new ArrayList<>();
        List<TransactionHistory> avaloqTransactions = getAvaloqTransactions();

        Mockito.when(wrapTransactionIntegrationService.loadCashTransactionHistory(Mockito.any(String.class), Mockito.any(DateTime.class),
                Mockito.any(DateTime.class), Mockito.any(ServiceErrors.class))).thenReturn(wrapTransactions);
        Mockito.when(avaloqTransactionIntegrationService.loadCashTransactionHistory(Mockito.any(String.class), Mockito.any(DateTime.class),
                Mockito.any(DateTime.class), Mockito.any(ServiceErrors.class))).thenReturn(avaloqTransactions);

        WrapAccountIdentifier accountIdentifier = new WrapAccountIdentifierImpl();
        accountIdentifier.setBpId("1234567");
        List<TransactionHistory> pastTransactions = transactionIntegrationService.loadCashTransactionHistory(
                "1234567", new DateTime("2016-02-11"), new DateTime("2016-02-11"), new FailFastErrorsImpl());
        assertNotNull(pastTransactions);
        assertThat(pastTransactions.size(), equalTo(1));
    }


    @Test
    public void loadCashTransactionHistoryTest_WrapDataExists_AvaloqDataEmpty() throws Exception {

        List<TransactionHistory> wrapTransactions = getWrapTransactions();
        List<TransactionHistory> avaloqTransactions = new ArrayList<>();

        Mockito.when(wrapTransactionIntegrationService.loadCashTransactionHistory(Mockito.any(String.class), Mockito.any(DateTime.class),
                Mockito.any(DateTime.class), Mockito.any(ServiceErrors.class))).thenReturn(wrapTransactions);
        Mockito.when(avaloqTransactionIntegrationService.loadCashTransactionHistory(Mockito.any(String.class), Mockito.any(DateTime.class),
                Mockito.any(DateTime.class), Mockito.any(ServiceErrors.class))).thenReturn(avaloqTransactions);

        WrapAccountIdentifier accountIdentifier = new WrapAccountIdentifierImpl();
        accountIdentifier.setBpId("1234567");
        List<TransactionHistory> pastTransactions = transactionIntegrationService.loadCashTransactionHistory(
                "1234567", new DateTime("2016-02-11"), new DateTime("2016-02-11"), new FailFastErrorsImpl());
        assertNotNull(pastTransactions);
        assertThat(pastTransactions.size(), equalTo(2));
    }


    @Test
    public void loadCashTransactionHistoryTest_WrapDataEmpty_AvaloqDataEmpty() throws Exception {

        List<TransactionHistory> wrapTransactions = new ArrayList<>();
        List<TransactionHistory> avaloqTransactions = new ArrayList<>();

        Mockito.when(wrapTransactionIntegrationService.loadCashTransactionHistory(Mockito.any(String.class), Mockito.any(DateTime.class),
                Mockito.any(DateTime.class), Mockito.any(ServiceErrors.class))).thenReturn(wrapTransactions);
        Mockito.when(avaloqTransactionIntegrationService.loadCashTransactionHistory(Mockito.any(String.class), Mockito.any(DateTime.class),
                Mockito.any(DateTime.class), Mockito.any(ServiceErrors.class))).thenReturn(avaloqTransactions);

        WrapAccountIdentifier accountIdentifier = new WrapAccountIdentifierImpl();
        accountIdentifier.setBpId("1234567");
        List<TransactionHistory> pastTransactions = transactionIntegrationService.loadCashTransactionHistory(
                "1234567", new DateTime("2016-02-11"), new DateTime("2016-02-11"), new FailFastErrorsImpl());
        assertNotNull(pastTransactions);
        assertThat(pastTransactions.size(), equalTo(0));
    }

    @Test
    public void loadCashTransactionHistoryTest_AfterMigrationDate() throws Exception {

        List<TransactionHistory> avaloqTransactions = getAvaloqTransactions();

        Mockito.when(avaloqTransactionIntegrationService.loadCashTransactionHistory(Mockito.any(String.class), Mockito.any(DateTime.class),
                Mockito.any(DateTime.class), Mockito.any(ServiceErrors.class))).thenReturn(avaloqTransactions);

        WrapAccountIdentifier accountIdentifier = new WrapAccountIdentifierImpl();
        accountIdentifier.setBpId("1234567");
        List<TransactionHistory> pastTransactions = transactionIntegrationService.loadCashTransactionHistory(
                "1234567", new DateTime("2016-02-11"), new DateTime("2017-02-11"), new FailFastErrorsImpl());
        assertNotNull(pastTransactions);
        assertThat(pastTransactions.size(), equalTo(1));
    }

    @Test
    public void loadCashTransactionHistoryTest_ifMigrationDateNull() throws Exception {

        List<TransactionHistory> avaloqTransactions = getAvaloqTransactions();

        ThirdPartyDetails thirdPartyDetails = new ThirdPartyDetails();
        thirdPartyDetails.setSystemType(SystemType.WRAP);
        thirdPartyDetails.setMigrationKey("M02744476");
        thirdPartyDetails.setMigrationDate(null);
        Mockito.when(
                avaloqAccountIntegrationService.getThirdPartySystemDetails(Mockito.any(AccountKey.class),
                        Mockito.any(ServiceErrors.class))).thenReturn(thirdPartyDetails);

        Mockito.when(avaloqTransactionIntegrationService.loadCashTransactionHistory(Mockito.any(String.class), Mockito.any(DateTime.class),
                Mockito.any(DateTime.class), Mockito.any(ServiceErrors.class))).thenReturn(avaloqTransactions);

        WrapAccountIdentifier accountIdentifier = new WrapAccountIdentifierImpl();
        accountIdentifier.setBpId("1234567");
        List<TransactionHistory> pastTransactions = transactionIntegrationService.loadCashTransactionHistory(
                "1234567", new DateTime("2016-02-11"), new DateTime("2017-02-11"), new FailFastErrorsImpl());
        assertNotNull(pastTransactions);
        assertThat(pastTransactions.size(), equalTo(1));
    }

}
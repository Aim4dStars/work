package com.bt.nextgen.service.avaloq.transactionhistory;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.asset.AssetImpl;
import com.bt.nextgen.service.avaloq.code.CodeImpl;
import com.bt.nextgen.service.avaloq.pasttransaction.TransactionOrderType;
import com.bt.nextgen.service.avaloq.pasttransaction.TransactionType;
import com.bt.nextgen.service.integration.account.ContainerType;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.bt.nextgen.service.integration.code.CodeCategoryInterface;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.bt.nextgen.service.integration.transactionhistory.TransactionHistory;
import com.bt.nextgen.service.integration.transactionhistory.TransactionSubType;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@RunWith(MockitoJUnitRunner.class)
public class TransactionHistoryConverterTest {
    @InjectMocks
    private TransactionHistoryConverter transactionHistoryConverter;

    @Mock
    private AssetIntegrationService assetIntegrationService;

    @Mock
    private StaticIntegrationService staticIntegrationService;

    private DateTime bankDate;
    private List<TransactionHistory> transactions = new ArrayList<>();

    @Before
    public void setUp() {
        TransactionHistoryImpl transaction = new TransactionHistoryImpl();
        transaction.setEvtId(2);
        transaction.setValDate(new DateTime("2015-03-02"));
        transaction.setMetaType(TransactionType.INPAY.name());
        transaction.setOrderType(TransactionOrderType.BPAY_FILE.getName());
        transaction.setClosingBalance(new BigDecimal(5000000.00));
        transaction.setAmount(new BigDecimal("500"));
        transaction.setClearDate(new DateTime("2014-03-02"));
        transactions.add(transaction);

        TransactionHistoryImpl transaction1 = new TransactionHistoryImpl();
        transaction1.setEvtId(1);
        transaction1.setValDate(new DateTime("2015-03-02"));
        transaction1.setMetaType(TransactionType.PAY.name());
        transaction1.setOrderType(TransactionOrderType.BPAY_FILE.getName());
        transaction1.setClosingBalance(new BigDecimal(5000000.00));
        transaction1.setAmount(new BigDecimal("500"));
        transaction1.setClearDate(new DateTime("2000-03-02"));
        transactions.add(transaction1);

        TransactionHistoryImpl transaction2 = new TransactionHistoryImpl();
        transaction2.setEvtId(12);
        transaction2.setValDate(new DateTime("2015-03-02"));
        transaction2.setMetaType(TransactionType.INPAY.name());
        transaction2.setOrderType("");
        transaction2.setClosingBalance(new BigDecimal(5000000.00));
        transaction2.setAmount(new BigDecimal("500"));
        transaction2.setClearDate(new DateTime("2035-03-02"));
        transactions.add(transaction2);

        TransactionHistoryImpl transaction3 = new TransactionHistoryImpl();
        transaction3.setEvtId(77);
        transaction3.setValDate(new DateTime("2015-03-02"));
        transaction3.setMetaType(TransactionType.UNKNOWN.name());
        transaction3.setOrderType("");
        transaction3.setClosingBalance(new BigDecimal(5000000.00));
        transaction3.setAmount(new BigDecimal("500"));
        transaction3.setClearDate(new DateTime("2035-03-21"));
        transactions.add(transaction3);

        TransactionHistoryImpl transaction4 = new TransactionHistoryImpl();
        transaction4.setEvtId(177);
        transaction4.setValDate(new DateTime("2011-03-21"));
        transaction4.setMetaType(TransactionType.UNKNOWN.name());
        transaction4.setOrderType("");
        transaction4.setClosingBalance(new BigDecimal(5000000.00));
        transaction4.setAmount(new BigDecimal("500"));
        transaction4.setClearDate(new DateTime("2011-03-21"));
        transactions.add(transaction4);

        TransactionHistoryImpl transaction5 = new TransactionHistoryImpl();
        transaction5.setEvtId(-177);
        transaction5.setValDate(new DateTime("2011-03-21"));
        transaction5.setMetaType(TransactionType.UNKNOWN.name());
        transaction5.setOrderType("");
        transaction5.setClosingBalance(new BigDecimal(5000000.00));
        transaction5.setAmount(new BigDecimal("500"));
        transaction5.setClearDate(new DateTime("2011-03-01"));
        transactions.add(transaction5);

        TransactionHistoryImpl transaction6 = new TransactionHistoryImpl();
        transaction6.setEvtId(10);
        transaction6.setValDate(new DateTime("2011-03-12"));
        transaction6.setMetaType(TransactionType.UNKNOWN.name());
        transaction6.setOrderType("");
        transaction6.setClosingBalance(new BigDecimal(5000000.00));
        transaction6.setAmount(new BigDecimal("500"));
        transaction6.setClearDate(new DateTime("2011-03-12"));
        transactions.add(transaction6);

        TransactionHistoryImpl transaction7 = new TransactionHistoryImpl();
        transaction7.setEvtId(-10);
        transaction7.setValDate(new DateTime("2011-03-12"));
        transaction7.setMetaType(TransactionType.UNKNOWN.name());
        transaction7.setOrderType("");
        transaction7.setClosingBalance(new BigDecimal(5000000.00));
        transaction7.setAmount(new BigDecimal("500"));
        transaction7.setClearDate(new DateTime("2011-03-02"));
        transactions.add(transaction7);

        bankDate = new DateTime("2014-03-02");

        Map<String, Asset> assetMap = new HashMap<>();
        AssetImpl asset = new AssetImpl();
        asset.setAssetId("1");
        assetMap.put("1", asset);

        AssetImpl asset1 = new AssetImpl();
        asset1.setAssetId("2");
        assetMap.put("2", asset1);

        AssetImpl asset2 = new AssetImpl();
        asset2.setAssetId("3");
        assetMap.put("3", asset2);

        Mockito.when(assetIntegrationService.loadAssets(Mockito.anyCollectionOf(String.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(assetMap);

        Mockito.when(assetIntegrationService.loadAssets(Mockito.anyCollectionOf(String.class), Mockito.any(DateTime.class),
                Mockito.any(ServiceErrors.class))).thenReturn(assetMap);

        Mockito.when(
                staticIntegrationService.loadCode(Mockito.any(CodeCategoryInterface.class), Mockito.anyString(),
                        Mockito.any(ServiceErrors.class))).thenReturn(new CodeImpl("X", "X", "X", "X"));
    }

    @Test
    public void testEvaluateClearedAndSystemTransactionFlag() throws Exception {
        List<TransactionHistory> transactionsList = transactionHistoryConverter.evaluateBalanceAndSystemTransaction(transactions,
                bankDate);

        assertEquals("5000000", transactionsList.get(0).getBalance().toString());
        assertEquals("4999500", transactionsList.get(1).getBalance().toString());
        assertEquals("4999000", transactionsList.get(2).getBalance().toString());
        assertEquals("4998500", transactionsList.get(3).getBalance().toString());

        assertEquals(true, transactionsList.get(0).isCleared());
        assertEquals(false, transactionsList.get(1).isCleared());
        assertEquals(true, transactionsList.get(2).isCleared());
        assertEquals(true, transactionsList.get(3).isCleared());

        assertEquals(true, transactionsList.get(0).isSystemTransaction());
        assertEquals(false, transactionsList.get(1).isSystemTransaction());
        assertEquals(true, transactionsList.get(2).isSystemTransaction());
        assertEquals(false, transactionsList.get(3).isSystemTransaction());
    }

    @Test
    public void testEvaluateBalanceAndSystemTransaction() throws Exception {
        List<TransactionHistory> transactionsList = transactionHistoryConverter.evaluateBalanceAndSystemTransaction(transactions,
                bankDate);

        TransactionHistory transaction = transactionHistoryConverter.evaluateClearedAndSystemTransactionFlag(
                transactionsList.get(0), bankDate);
        assertEquals(true, transaction.isCleared());

        transaction = transactionHistoryConverter.evaluateClearedAndSystemTransactionFlag(transactionsList.get(1), bankDate);
        assertEquals(false, transaction.isCleared());

        transaction = transactionHistoryConverter.evaluateClearedAndSystemTransactionFlag(transactionsList.get(2), bankDate);
        assertEquals(true, transaction.isCleared());

        transaction = transactionHistoryConverter.evaluateClearedAndSystemTransactionFlag(transactionsList.get(3), bankDate);
        assertEquals(true, transaction.isCleared());
    }

    @Test
    public void testEvaluateBalanceEmptyList() throws Exception {
        List<TransactionHistory> transactionsList = transactionHistoryConverter.evaluateBalanceAndSystemTransaction(
                new ArrayList<TransactionHistory>(), bankDate);
        assertNotNull(transactionsList);
        assertEquals(0, transactionsList.size());
    }

    @Test
    public void test_SortTransactions() throws Exception {
        List<TransactionHistory> test = transactions;

        List<TransactionHistory> transactionsList = transactionHistoryConverter.sortBy(test);

        assertEquals(transactions.get(3).getValDate(), transactionsList.get(0).getValDate());
        assertEquals(77, transactionsList.get(0).getEvtId().intValue());

        assertEquals(transactions.get(2).getValDate(), transactionsList.get(1).getValDate());
        assertEquals(12, transactionsList.get(1).getEvtId().intValue());

        assertEquals(transactions.get(0).getValDate(), transactionsList.get(2).getValDate());
        assertEquals(2, transactionsList.get(2).getEvtId().intValue());

        assertEquals(transactions.get(1).getValDate(), transactionsList.get(3).getValDate());
        assertEquals(1, transactionsList.get(3).getEvtId().intValue());

        assertEquals(transactions.get(4).getValDate(), transactionsList.get(4).getValDate());
        assertEquals(-177, transactionsList.get(4).getEvtId().intValue());

        assertEquals(transactions.get(5).getValDate(), transactionsList.get(5).getValDate());
        assertEquals(177, transactionsList.get(5).getEvtId().intValue());

        assertEquals(transactions.get(6).getValDate(), transactionsList.get(6).getValDate());
        assertEquals(-10, transactionsList.get(6).getEvtId().intValue());

        assertEquals(transactions.get(7).getValDate(), transactionsList.get(7).getValDate());
        assertEquals(10, transactionsList.get(7).getEvtId().intValue());

    }

    @Test
    public void test_SortAbsoluteEvtId() throws Exception {
        List<TransactionHistory> transactionsList = transactionHistoryConverter.sortAbsoluteEvtId(transactions);

        assertThat(transactionsList.get(5).getEvtId(), greaterThan(transactionsList.get(4).getEvtId()));
        assertThat(transactionsList.get(7).getEvtId(), greaterThan(transactionsList.get(6).getEvtId()));
    }

    @Test
    public void test_setExtraDetails_WhenRefAssetIdIsSet_thenTransactionHistoryShouldHaveRefAssetObjectSet() {
        List<TransactionHistory> transactions = new ArrayList<>();

        TransactionHistoryImpl transaction = new TransactionHistoryImpl();
        transaction.setEvtId(2);
        transaction.setValDate(new DateTime("2015-03-02"));
        transaction.setMetaType(TransactionType.INPAY.name());
        transaction.setOrderType(TransactionOrderType.BPAY_FILE.getName());
        transaction.setClosingBalance(new BigDecimal(5000000.00));
        transaction.setAmount(new BigDecimal("500"));
        transaction.setClearDate(new DateTime("2014-03-02"));
        transaction.setRefAssetId("1");
        transactions.add(transaction);

        List<TransactionHistory> transList = transactionHistoryConverter.setExtraDetails(transactions, new FailFastErrorsImpl());

        assertNotNull(transList.get(0).getRefAsset());
    }

    @Test
    public void test_setExtraDetails_WhenContAssetIdIsSet_thenTransactionHistoryShouldHaveContAssetObjectSet() {
        List<TransactionHistory> transactions = new ArrayList<>();

        TransactionHistoryImpl transaction = new TransactionHistoryImpl();
        transaction.setEvtId(2);
        transaction.setValDate(new DateTime("2015-03-02"));
        transaction.setMetaType(TransactionType.INPAY.name());
        transaction.setOrderType(TransactionOrderType.BPAY_FILE.getName());
        transaction.setClosingBalance(new BigDecimal(5000000.00));
        transaction.setAmount(new BigDecimal("500"));
        transaction.setClearDate(new DateTime("2014-03-02"));
        transaction.setRefAssetId("1");
        transaction.setContAssetId("2");
        transaction.setPosAssetId("3");
        transaction.setContType(ContainerType.MANAGED_PORTFOLIO);
        transactions.add(transaction);

        List<TransactionHistory> transList = transactionHistoryConverter.setExtraDetails(transactions, new FailFastErrorsImpl());

        assertNotNull(transList.get(0).getContAsset());
        assertNotNull(transList.get(0).getRefAsset());
    }

    @Test
    public void test_setExtraDetails_WhenRefAssetIdIsNotSet_thenTransactionHistoryRefAssetObjectShouldBeNull() {
        List<TransactionHistory> transactions = new ArrayList<>();

        TransactionHistoryImpl transaction = new TransactionHistoryImpl();
        transaction.setEvtId(2);
        transaction.setValDate(new DateTime("2015-03-02"));
        transaction.setMetaType(TransactionType.INPAY.name());
        transaction.setOrderType(TransactionOrderType.BPAY_FILE.getName());
        transaction.setClosingBalance(new BigDecimal(5000000.00));
        transaction.setAmount(new BigDecimal("500"));
        transaction.setClearDate(new DateTime("2014-03-02"));
        transactions.add(transaction);

        List<TransactionHistory> transList = transactionHistoryConverter.setExtraDetails(transactions, new FailFastErrorsImpl());

        assertNull(transList.get(0).getRefAsset());
    }

    @Test
    public void test_setSubTypes() {
        TransactionHistoryImpl transaction = new TransactionHistoryImpl();
        List<TransactionHistory> transactionsWithSubTypes = new ArrayList<>();
        transaction.setEvtId(2);
        transaction.setValDate(new DateTime("2015-03-02"));
        transaction.setMetaType(TransactionType.INPAY.name());
        transaction.setOrderType(TransactionOrderType.BPAY_FILE.getName());
        transaction.setClosingBalance(new BigDecimal(5000000.00));
        transaction.setClearDate(new DateTime("2014-03-02"));
        List<TransactionSubType> transactionSubTypes = new ArrayList<>();
        TransactionSubTypeImpl transactionSubType = new TransactionSubTypeImpl();
        transactionSubType.setTransactionSubTypeDescription("Third Party Contribution");
        transactionSubType.setTransactionSubTypeAmount(new BigDecimal(500));
        transactionSubType.setTransactionSubType("sa_family_friend");
        transactionSubType.setTransactionType("12");
        transactionSubTypes.add(transactionSubType);

        transaction.setTransactionSubTypes(transactionSubTypes);
        transactionsWithSubTypes.add(transaction);
        List<TransactionHistory> transList = transactionHistoryConverter.setTransactionSubTypes(transactionsWithSubTypes);
        assertEquals(new BigDecimal(500), transList.get(0).getAmount());
        assertEquals("Third Party Contribution", transList.get(0).getBookingText());
        assertEquals("X", transList.get(0).getTransactionType());
    }
}

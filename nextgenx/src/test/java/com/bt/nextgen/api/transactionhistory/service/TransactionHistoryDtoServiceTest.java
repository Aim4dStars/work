package com.bt.nextgen.api.transactionhistory.service;

import com.bt.nextgen.api.transactionhistory.model.TransactionHistoryDto;
import com.bt.nextgen.api.transactionhistory.model.TransactionType;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria.OperationType;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria.SearchOperation;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.asset.AssetImpl;
import com.bt.nextgen.service.avaloq.code.CodeImpl;
import com.bt.nextgen.service.avaloq.transactionhistory.TransactionHistoryImpl;
import com.bt.nextgen.service.integration.Origin;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.account.ContainerType;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetClass;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.bt.nextgen.service.integration.options.model.OptionKey;
import com.bt.nextgen.service.integration.options.service.OptionsService;
import com.bt.nextgen.service.integration.transaction.TransactionIntegrationService;
import com.bt.nextgen.service.integration.transactionhistory.BTOrderType;
import com.bt.nextgen.service.integration.transactionhistory.TransactionHistory;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import com.btfin.panorama.core.conversion.CodeCategory;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.btfin.panorama.service.integration.asset.AssetType;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Qualifier;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class TransactionHistoryDtoServiceTest {

    public static final double DELTA = 1e-15;
    @InjectMocks
    private TransactionHistoryDtoServiceImpl transactionDtoService;
    @Mock
    private StaticIntegrationService staticIntegrationService;
    @Mock
    private AssetIntegrationService assetIntegrationService;
    @Mock
    @Qualifier("AvaloqTransactionIntegrationServiceImpl")
    private TransactionIntegrationService txnService;
    @Mock
    private OptionsService optionsService;
    private String portfolioId = "333";
    private TransactionHistoryImpl txn;
    private AssetImpl asset;
    private AssetImpl contAsset;
    private AssetImpl cashAsset;

    @Before
    public void setup() throws Exception {
        contAsset = new AssetImpl();
        contAsset.setAssetId("2200");
        contAsset.setAssetName("BT Managed Portfolio");
        contAsset.setAssetType(AssetType.MANAGED_PORTFOLIO);
        contAsset.setAssetCode("BT0001");

        asset = new AssetImpl();
        asset.setAssetId("2234");
        asset.setAssetName("BHP BHP Billiton");
        asset.setAssetType(AssetType.SHARE);
        asset.setAssetCode("BHP");
        asset.setAssetClass(AssetClass.AUSTRALIAN_SHARES);

        cashAsset = new AssetImpl();
        cashAsset.setAssetId("1000");
        cashAsset.setAssetName("BT Cash");
        cashAsset.setAssetType(AssetType.CASH);
        cashAsset.setAssetCode("CA");
        cashAsset.setAssetClass(AssetClass.CASH);

        HashMap<String, Asset> assetMap = new HashMap<>();
        assetMap.put("2234", asset);
        assetMap.put("1000", cashAsset);

        Mockito.when(optionsService.getOption(Mockito.any(OptionKey.class), Mockito.any(AccountKey.class),
                Mockito.any(ServiceErrors.class))).thenReturn("BT Cash");

        Mockito.when(assetIntegrationService.loadAssets(Mockito.anyCollectionOf(String.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(assetMap);

        Mockito.when(staticIntegrationService.loadCode(Mockito.any(CodeCategory.class), Mockito.anyString(),
                Mockito.any(ServiceErrors.class))).thenAnswer(new Answer<Object>() {
            public Object answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                CodeCategory cc = (CodeCategory) args[0];
                if ("TXN_STATUS".equals(cc.name())) {
                    return new CodeImpl("8", "A", "Approved");
                }
                else if ("TRANSACTION_ORDER_TYPE".equals(cc.name())) {
                    String oid = (String) args[1];
                    if ("2222".equals(oid)) {
                        return new CodeImpl("1112", "FEE TRANSFER.AVSR_ADVCONEOFF", "Fee.OnceOff");
                    }
                    return new CodeImpl("1110", "PAYMENT.BPAY_PAY", "Payment.BPAY");
                }
                else {
                    return null;
                }
            }
        });
    }

    @Test
    public void testGetTransactionHistory() {
        txn = new TransactionHistoryImpl();
        txn.setPosAssetId("2234");
        txn.setAsset(asset);
        txn.setContAsset(contAsset);
        txn.setBookingText("buy");
        txn.setTransactionDescription("Optional desc");
        txn.setDocId("1234");
        txn.setAmount(new BigDecimal(2344));
        txn.setPosName("BT0001 BT Managed Account");
        txn.setStatus("Approved");
        txn.setEffectiveDate(new DateTime());
        txn.setValDate(new DateTime());
        txn.setContType(ContainerType.MANAGED_PORTFOLIO);
        txn.setOrigin(Origin.WEB_UI);

        List<TransactionHistory> txnList = new ArrayList<>();
        txnList.add(txn);

        Mockito.when(txnService.loadTransactionHistory(Mockito.eq(portfolioId), Mockito.any(DateTime.class),
                Mockito.any(DateTime.class), Mockito.any(ServiceErrors.class))).thenReturn(txnList);

        List<TransactionHistoryDto> transactions = transactionDtoService.getTransactionHistory(
                EncodedString.fromPlainText(portfolioId).toString(), new DateTime(), new DateTime(), new ServiceErrorsImpl());
        assertNotNull(transactions);
        assertEquals(1d, transactions.size(), DELTA);
        TransactionHistoryDto transaction = transactions.get(0);
        assertEquals(contAsset.getAssetType().name(), transaction.getContType());
        assertEquals(txn.getDocId(), transaction.getOrderId());
        assertEquals(txn.getBookingText() + ". " + txn.getTransactionDescription(), transaction.getDescription());
        assertEquals(txn.getEffectiveDate(), transaction.getTradeDate());
        assertEquals(txn.getValDate(), transaction.getSettlementDate());
        assertEquals(txn.getAmount(), transaction.getQuantity());
        assertEquals(txn.getOrigin().getName(), transaction.getOrigin());
    }

    @Test
    public void testGetTransactionHistoryForNullModel() {
        List<TransactionHistoryDto> transactions = transactionDtoService.getTransactionHistory(
                EncodedString.fromPlainText("").toString(), new DateTime(), new DateTime(), new ServiceErrorsImpl());
        assertEquals(0, transactions.size());
    }

    @Test
    public void testGetDocDescription() {
        txn = new TransactionHistoryImpl();
        txn.setOrderType("2222");
        txn.setBookingText("buy");
        txn.setDocDescription("Doc desc");
        txn.setDocId("1234");
        txn.setAmount(new BigDecimal(2344));
        txn.setPosName("BT0001 BT Managed Account");
        txn.setStatus("Approved");
        txn.setEffectiveDate(new DateTime());
        txn.setValDate(new DateTime());
        txn.setContType(ContainerType.MANAGED_PORTFOLIO);
        List<TransactionHistory> txnList = new ArrayList<>();
        txnList.add(txn);

        Mockito.when(txnService.loadTransactionHistory(Mockito.eq(portfolioId), Mockito.any(DateTime.class),
                Mockito.any(DateTime.class), Mockito.any(ServiceErrors.class))).thenReturn(txnList);

        List<TransactionHistoryDto> transactions = transactionDtoService.getTransactionHistory(
                EncodedString.fromPlainText(portfolioId).toString(), new DateTime(), new DateTime(), new ServiceErrorsImpl());
        TransactionHistoryDto transaction = transactions.get(0);
        assertTrue(transaction.getDescription().equals(txn.getBookingText() + " - " + txn.getDocDescription().toLowerCase()));
    }

    @Test
    public void testSearch() {
        AssetImpl mpasset = new AssetImpl();
        mpasset.setAssetId("2234");
        mpasset.setAssetName("BT0001 BT Managed Account");
        mpasset.setAssetType(AssetType.MANAGED_PORTFOLIO);
        mpasset.setAssetCode("BT0001");

        txn = new TransactionHistoryImpl();
        txn.setBookingText("buy");
        txn.setTransactionDescription("Optional desc");
        txn.setDocId("1234");
        txn.setAsset(mpasset);
        txn.setContAsset(contAsset);
        txn.setAmount(new BigDecimal(2344));
        txn.setPosName("BT0001 BT Managed Account");
        txn.setStatus("Approved");
        txn.setEffectiveDate(new DateTime());
        txn.setValDate(new DateTime());
        txn.setContType(ContainerType.MANAGED_PORTFOLIO);
        txn.setOrigin(Origin.WEB_UI);
        List<TransactionHistory> txnList = new ArrayList<>();

        TransactionHistoryImpl txn1 = new TransactionHistoryImpl();
        txn1 = new TransactionHistoryImpl();
        txn1.setPosAssetId("2234");
        txn1.setAsset(asset);
        txn1.setContAsset(contAsset);
        txn1.setBookingText("buy");
        txn1.setDocDescription("Doc Optional desc");
        txn1.setDocId("1234");
        txn1.setAmount(new BigDecimal(0));
        txn1.setPosName("BT0001 BT Managed Account");
        txn1.setStatus("Approved");
        txn1.setEffectiveDate(new DateTime());
        txn1.setTransactionType("Buy");
        txn1.setValDate(new DateTime());
        txn1.setContType(ContainerType.DIRECT);
        txn1.setOrigin(Origin.WEB_UI);
        txn1.setBTOrderType(BTOrderType.CORPORATE_ACTION);

        txnList.add(txn);
        txnList.add(txn1);

        Mockito.when(txnService.loadTransactionHistory(Mockito.eq(portfolioId), Mockito.any(DateTime.class),
                Mockito.any(DateTime.class), Mockito.any(ServiceErrors.class))).thenReturn(txnList);

        List<ApiSearchCriteria> criteriaList = new ArrayList<ApiSearchCriteria>();

        criteriaList.add(new ApiSearchCriteria(Attribute.ACCOUNT_ID, SearchOperation.EQUALS,
                EncodedString.fromPlainText(portfolioId).toString(), OperationType.STRING));

        criteriaList.add(new ApiSearchCriteria(Attribute.START_DATE, SearchOperation.EQUALS, new DateTime().toString(),
                OperationType.DATE));

        criteriaList.add(
                new ApiSearchCriteria(Attribute.END_DATE, SearchOperation.EQUALS, new DateTime().toString(), OperationType.DATE));

        criteriaList.add(new ApiSearchCriteria(Attribute.ASSET_CODE, SearchOperation.EQUALS, "BT0001", OperationType.STRING));

        List<TransactionHistoryDto> transactions = transactionDtoService.search(criteriaList, new ServiceErrorsImpl());

        assertNotNull(transactions);
        assertEquals(1d, transactions.size(), DELTA);
        TransactionHistoryDto transaction = transactions.get(0);
        assertEquals(contAsset.getAssetType().name(), transaction.getContType());
        assertEquals(txn.getDocId(), transaction.getOrderId());
        assertEquals(txn.getBookingText() + ". " + txn.getTransactionDescription(), transaction.getDescription());
        assertEquals(txn.getEffectiveDate(), transaction.getTradeDate());
        assertEquals(txn.getValDate(), transaction.getSettlementDate());
        assertEquals(txn.getAmount(), transaction.getQuantity());
        assertEquals(null, transaction.getNetAmount());
        assertEquals(TransactionType.BUY.getCode(), transaction.getTransactionType());
        assertEquals(txn.getOrigin().getName(), transaction.getOrigin());
    }

    @Test
    public void testGetTransactionHistory_whenThereIsACashCorporateAction_thenAssetCodeAndNameMustBeFromReferenceAsset() {
        txn = new TransactionHistoryImpl();
        txn.setPosAssetId("1000");
        txn.setAsset(cashAsset);
        txn.setContAsset(contAsset);
        txn.setRefAssetId("2234");
        txn.setRefAsset(asset);
        txn.setBookingText("buy");
        txn.setTransactionDescription("Optional desc");
        txn.setDocId("1234");
        txn.setAmount(new BigDecimal(2344));
        txn.setPosName("BT0001 BT Cash");
        txn.setStatus("Booked");
        txn.setEffectiveDate(new DateTime());
        txn.setValDate(new DateTime());
        txn.setContType(ContainerType.DIRECT);
        txn.setOrigin(Origin.WEB_UI);
        txn.setBTOrderType(BTOrderType.CORPORATE_ACTION);

        List<TransactionHistory> txnList = new ArrayList<>();
        txnList.add(txn);

        Mockito.when(txnService.loadTransactionHistory(Mockito.eq(portfolioId), Mockito.any(DateTime.class),
                Mockito.any(DateTime.class), Mockito.any(ServiceErrors.class))).thenReturn(txnList);

        List<TransactionHistoryDto> transactions = transactionDtoService.getTransactionHistory(
                EncodedString.fromPlainText(portfolioId).toString(), new DateTime(), new DateTime(), new ServiceErrorsImpl());

        assertNotNull(transactions);

        TransactionHistoryDto transaction = transactions.get(0);
        assertEquals(transaction.getAssetCode(), asset.getAssetCode());
        assertEquals(transaction.getAssetName(), asset.getAssetName());
    }

    @Test
    public void testGetTransactionHistory_whenThereIsACashEventThatIsNotCorporateAction_thenAssetCodeAndNameMustBeNull() {
        txn = new TransactionHistoryImpl();
        txn.setPosAssetId("1000");
        txn.setAsset(cashAsset);
        txn.setContAsset(contAsset);
        txn.setRefAssetId("2234");
        txn.setRefAsset(asset);
        txn.setBookingText("buy");
        txn.setTransactionDescription("Optional desc");
        txn.setDocId("1234");
        txn.setAmount(new BigDecimal(2344));
        txn.setPosName("BT0001 BT Cash");
        txn.setStatus("Booked");
        txn.setEffectiveDate(new DateTime());
        txn.setValDate(new DateTime());
        txn.setContType(ContainerType.DIRECT);
        txn.setOrigin(Origin.WEB_UI);

        List<TransactionHistory> txnList = new ArrayList<>();
        txnList.add(txn);

        Mockito.when(txnService.loadTransactionHistory(Mockito.eq(portfolioId), Mockito.any(DateTime.class),
                Mockito.any(DateTime.class), Mockito.any(ServiceErrors.class))).thenReturn(txnList);

        List<TransactionHistoryDto> transactions = transactionDtoService.getTransactionHistory(
                EncodedString.fromPlainText(portfolioId).toString(), new DateTime(), new DateTime(), new ServiceErrorsImpl());

        assertNotNull(transactions);

        TransactionHistoryDto transaction = transactions.get(0);
        assertNull(transaction.getAssetCode());
        assertNull(transaction.getAssetName());
    }

    @Test
    public void testGetTransactionHistory_whenThereIsACashAndNoRefAsset() {
        txn = new TransactionHistoryImpl();
        txn.setPosAssetId("1000");
        txn.setAsset(cashAsset);
        txn.setContAsset(contAsset);
        txn.setRefAssetId("2234");
        txn.setBookingText("buy");
        txn.setTransactionDescription("Optional desc");
        txn.setDocId("1234");
        txn.setAmount(new BigDecimal(2344));
        txn.setPosName("BT0001 BT Cash");
        txn.setStatus("Booked");
        txn.setEffectiveDate(new DateTime());
        txn.setValDate(new DateTime());
        txn.setContType(ContainerType.DIRECT);
        txn.setOrigin(Origin.WEB_UI);

        List<TransactionHistory> txnList = new ArrayList<>();
        txnList.add(txn);

        Mockito.when(txnService.loadTransactionHistory(Mockito.eq(portfolioId), Mockito.any(DateTime.class),
                Mockito.any(DateTime.class), Mockito.any(ServiceErrors.class))).thenReturn(txnList);

        List<TransactionHistoryDto> transactions = transactionDtoService.getTransactionHistory(
                EncodedString.fromPlainText(portfolioId).toString(), new DateTime(), new DateTime(), new ServiceErrorsImpl());

        assertNotNull(transactions);

        TransactionHistoryDto transaction = transactions.get(0);
        assertNull(transaction.getAssetCode());
        assertNull(transaction.getAssetName());
    }

    @Test
    public void testGetTransactionHistory_withCashAndBTOrderTypeIncome() {
        List<TransactionHistory> txnList = new ArrayList<>();

        TransactionHistoryImpl transactionHistory1 = new TransactionHistoryImpl();
        transactionHistory1 = new TransactionHistoryImpl();
        transactionHistory1.setPosAssetId("2234");
        transactionHistory1.setAsset(cashAsset);
        transactionHistory1.setContAsset(contAsset);
        transactionHistory1.setBookingText("buy");
        transactionHistory1.setDocDescription("Doc Optional desc");
        transactionHistory1.setDocId("1234");
        transactionHistory1.setAmount(new BigDecimal("1234"));
        transactionHistory1.setPosName("BT0001 BT Managed Account");
        transactionHistory1.setStatus("Approved");
        transactionHistory1.setEffectiveDate(new DateTime());
        transactionHistory1.setValDate(new DateTime());
        transactionHistory1.setContType(ContainerType.DIRECT);
        transactionHistory1.setOrigin(Origin.WEB_UI);
        transactionHistory1.setRefAsset(cashAsset);
        transactionHistory1.setBTOrderType(BTOrderType.INCOME);
        txnList = new ArrayList<>();
        txnList.add(transactionHistory1);

        Mockito.when(txnService.loadTransactionHistory(Mockito.any(String.class), Mockito.any(DateTime.class),
                Mockito.any(DateTime.class), Mockito.any(ServiceErrors.class))).thenReturn(txnList);

        List<TransactionHistoryDto> transactions = transactionDtoService.getTransactionHistory(
                EncodedString.fromPlainText(portfolioId).toString(), new DateTime(), new DateTime(), new ServiceErrorsImpl());

        assertNotNull(transactions);
        assertEquals(transactions.size(), 1);
        assertEquals(cashAsset.getAssetCode(), transactions.get(0).getAssetCode());
        assertEquals(cashAsset.getAssetName(), transactions.get(0).getAssetName());
    }

    @Test
    public void testGetTransactionHistory_withCashAndTransactionTypeBuy() {
        List<TransactionHistory> txnList = new ArrayList<>();

        TransactionHistoryImpl transactionHistory1 = new TransactionHistoryImpl();
        transactionHistory1 = new TransactionHistoryImpl();
        transactionHistory1.setPosAssetId("2234");
        transactionHistory1.setAsset(cashAsset);
        transactionHistory1.setContAsset(contAsset);
        transactionHistory1.setBookingText("buy");
        transactionHistory1.setDocDescription("Doc Optional desc");
        transactionHistory1.setDocId("1234");
        transactionHistory1.setAmount(new BigDecimal("1234"));
        transactionHistory1.setPosName("BT0001 BT Managed Account");
        transactionHistory1.setStatus("Approved");
        transactionHistory1.setEffectiveDate(new DateTime());
        transactionHistory1.setValDate(new DateTime());
        transactionHistory1.setContType(ContainerType.DIRECT);
        transactionHistory1.setOrigin(Origin.WEB_UI);
        transactionHistory1.setRefAsset(cashAsset);
        transactionHistory1.setTransactionType("Buy");
        transactionHistory1.setBTOrderType(BTOrderType.INCOME);
        txnList = new ArrayList<>();
        txnList.add(transactionHistory1);

        Mockito.when(txnService.loadTransactionHistory(Mockito.any(String.class), Mockito.any(DateTime.class),
                Mockito.any(DateTime.class), Mockito.any(ServiceErrors.class))).thenReturn(txnList);

        List<TransactionHistoryDto> transactions = transactionDtoService.getTransactionHistory(
                EncodedString.fromPlainText(portfolioId).toString(), new DateTime(), new DateTime(), new ServiceErrorsImpl());

        assertNotNull(transactions);
        assertEquals(transactions.size(), 1);
        assertEquals(transactionHistory1.getTransactionType(), transactions.get(0).getTransactionType());
    }

    @Test
    public void testGetTransactionHistory_whenThereIsACashEventThatIsNotCorporateAction_thenAssetCodeAndNameMustBeNullAndTransactionTypePayment() {
        txn = new TransactionHistoryImpl();
        txn.setPosAssetId("1000");
        txn.setAsset(cashAsset);
        txn.setContAsset(contAsset);
        txn.setRefAssetId("2234");
        txn.setRefAsset(asset);
        txn.setBookingText("buy");
        txn.setTransactionDescription("Optional desc");
        txn.setDocId("1234");
        txn.setAmount(new BigDecimal(-2344));
        txn.setPosName("BT0001 BT Cash");
        txn.setStatus("Booked");
        txn.setEffectiveDate(new DateTime());
        txn.setValDate(new DateTime());
        txn.setContType(ContainerType.DIRECT);
        txn.setOrigin(Origin.WEB_UI);

        List<TransactionHistory> txnList = new ArrayList<>();
        txnList.add(txn);

        Mockito.when(txnService.loadTransactionHistory(Mockito.eq(portfolioId), Mockito.any(DateTime.class),
                Mockito.any(DateTime.class), Mockito.any(ServiceErrors.class))).thenReturn(txnList);

        List<TransactionHistoryDto> transactions = transactionDtoService.getTransactionHistory(
                EncodedString.fromPlainText(portfolioId).toString(), new DateTime(), new DateTime(), new ServiceErrorsImpl());

        assertNotNull(transactions);

        TransactionHistoryDto transaction = transactions.get(0);
        assertNull(transaction.getAssetCode());
        assertNull(transaction.getAssetName());
    }

    @Test
    public void testGetTransactionHistory_whenTermDeposit() {
        AssetImpl asset = new AssetImpl();
        asset = new AssetImpl();
        asset.setAssetId("2234");
        asset.setAssetName("BHP BHP Billiton");
        asset.setAssetType(AssetType.TERM_DEPOSIT);
        asset.setAssetCode("BHP");
        asset.setAssetClass(AssetClass.AUSTRALIAN_SHARES);

        txn = new TransactionHistoryImpl();
        txn.setPosAssetId("1000");
        txn.setAsset(asset);
        txn.setContAsset(contAsset);
        txn.setRefAssetId("2234");
        txn.setRefAsset(asset);
        txn.setBookingText("buy");
        txn.setTransactionDescription("Optional desc");
        txn.setDocId("1234");
        txn.setAmount(new BigDecimal(-2344));
        txn.setPosName("BT0001 BT Cash");
        txn.setStatus("Booked");
        txn.setEffectiveDate(new DateTime());
        txn.setValDate(new DateTime());
        txn.setContType(ContainerType.DIRECT);
        txn.setOrigin(Origin.WEB_UI);

        List<TransactionHistory> txnList = new ArrayList<>();
        txnList.add(txn);

        Mockito.when(txnService.loadTransactionHistory(Mockito.eq(portfolioId), Mockito.any(DateTime.class),
                Mockito.any(DateTime.class), Mockito.any(ServiceErrors.class))).thenReturn(txnList);

        List<TransactionHistoryDto> transactions = transactionDtoService.getTransactionHistory(
                EncodedString.fromPlainText(portfolioId).toString(), new DateTime(), new DateTime(), new ServiceErrorsImpl());

        assertNotNull(transactions);

        TransactionHistoryDto transaction = transactions.get(0);
        assertEquals(asset.getAssetCode(), transaction.getAssetCode());
        assertEquals(txn.getPosName(), transaction.getAssetName());
    }

    @Test
    public void testGetTransactionHistory_whenShare_TransactionTypeBuy() {
        txn = new TransactionHistoryImpl();
        txn.setPosAssetId("1000");
        txn.setAsset(asset);
        txn.setContAsset(contAsset);
        txn.setBookingText("buy");
        txn.setTransactionDescription("Optional desc");
        txn.setDocId("1234");
        txn.setAmount(new BigDecimal(2344));
        txn.setPosName("BT0001 BT Cash");
        txn.setStatus("Booked");
        txn.setEffectiveDate(new DateTime());
        txn.setTransactionType("Buy");
        txn.setValDate(new DateTime());
        txn.setContType(ContainerType.DIRECT);
        txn.setTransactionType("Buy");
        txn.setOrigin(Origin.WEB_UI);

        List<TransactionHistory> txnList = new ArrayList<>();
        txnList.add(txn);

        Mockito.when(txnService.loadTransactionHistory(Mockito.eq(portfolioId), Mockito.any(DateTime.class),
                Mockito.any(DateTime.class), Mockito.any(ServiceErrors.class))).thenReturn(txnList);

        List<TransactionHistoryDto> transactions = transactionDtoService.getTransactionHistory(
                EncodedString.fromPlainText(portfolioId).toString(), new DateTime(), new DateTime(), new ServiceErrorsImpl());

        assertNotNull(transactions);

        TransactionHistoryDto transaction = transactions.get(0);
        assertEquals(asset.getAssetCode(), transaction.getAssetCode());
        assertEquals(txn.getPosName(), transaction.getAssetName());
    }

    @Test
    public void testGetTransactionHistory_whenShare_ReturnsTransactionTypeSell() {
        txn = new TransactionHistoryImpl();
        txn.setPosAssetId("1000");
        txn.setAsset(asset);
        txn.setContAsset(contAsset);
        txn.setBookingText("buy");
        txn.setTransactionDescription("Optional desc");
        txn.setDocId("1234");
        txn.setAmount(new BigDecimal(-2344));
        txn.setPosName("BT0001 BT Cash");
        txn.setStatus("Booked");
        txn.setEffectiveDate(new DateTime());
        txn.setValDate(new DateTime());
        txn.setContType(ContainerType.DIRECT);
        txn.setOrigin(Origin.WEB_UI);

        List<TransactionHistory> txnList = new ArrayList<>();
        txnList.add(txn);

        Mockito.when(txnService.loadTransactionHistory(Mockito.eq(portfolioId), Mockito.any(DateTime.class),
                Mockito.any(DateTime.class), Mockito.any(ServiceErrors.class))).thenReturn(txnList);

        List<TransactionHistoryDto> transactions = transactionDtoService.getTransactionHistory(
                EncodedString.fromPlainText(portfolioId).toString(), new DateTime(), new DateTime(), new ServiceErrorsImpl());

        assertNotNull(transactions);

        TransactionHistoryDto transaction = transactions.get(0);
        assertEquals(TransactionType.SELL.getCode(), transaction.getTransactionType());
    }

    @Test
    public void testSearchWithAssetNameFilter() {
        List<TransactionHistory> txnList = new ArrayList<>();

        TransactionHistoryImpl transactionHistory = new TransactionHistoryImpl();
        transactionHistory = new TransactionHistoryImpl();
        transactionHistory.setPosAssetId("2234");
        transactionHistory.setAsset(null);
        transactionHistory.setContAsset(contAsset);
        transactionHistory.setBookingText("buy");
        transactionHistory.setDocDescription("Doc Optional desc");
        transactionHistory.setDocId("1234");
        transactionHistory.setAmount(new BigDecimal(1234));
        transactionHistory.setPosName("BT0001 BT Managed Account");
        transactionHistory.setStatus("Approved");
        transactionHistory.setEffectiveDate(new DateTime());
        transactionHistory.setTransactionType("Buy");
        transactionHistory.setValDate(new DateTime());
        transactionHistory.setContType(ContainerType.MANAGED_PORTFOLIO);
        transactionHistory.setOrigin(Origin.WEB_UI);
        transactionHistory.setBTOrderType(BTOrderType.CORPORATE_ACTION);
        txnList.add(transactionHistory);

        Mockito.when(txnService.loadTransactionHistory(Mockito.any(String.class), Mockito.any(DateTime.class),
                Mockito.any(DateTime.class), Mockito.any(ServiceErrors.class))).thenReturn(txnList);

        List<ApiSearchCriteria> criteriaList = new ArrayList<ApiSearchCriteria>();

        criteriaList.add(new ApiSearchCriteria(Attribute.ACCOUNT_ID, ApiSearchCriteria.SearchOperation.EQUALS,
                EncodedString.fromPlainText("333").toString(), ApiSearchCriteria.OperationType.STRING));

        criteriaList.add(new ApiSearchCriteria(Attribute.START_DATE, ApiSearchCriteria.SearchOperation.EQUALS, new DateTime().toString(),
                ApiSearchCriteria.OperationType.DATE));

        criteriaList.add(
                new ApiSearchCriteria(Attribute.END_DATE, ApiSearchCriteria.SearchOperation.EQUALS, new DateTime().toString(), ApiSearchCriteria.OperationType.DATE));

        criteriaList.add(new ApiSearchCriteria(Attribute.ASSET_CODE, ApiSearchCriteria.SearchOperation.EQUALS, "BT0001", ApiSearchCriteria.OperationType.STRING));

        List<TransactionHistoryDto> transactions = transactionDtoService.search(criteriaList, new ServiceErrorsImpl());

        assertNotNull(transactions);
        assertEquals(transactions.size(), 1);
    }

    @Test
    public void testSearchIfResponseNull() {
        List<ApiSearchCriteria> criteriaList = new ArrayList<ApiSearchCriteria>();

        criteriaList.add(new ApiSearchCriteria(Attribute.ACCOUNT_ID, ApiSearchCriteria.SearchOperation.EQUALS,
                EncodedString.fromPlainText("333").toString(), ApiSearchCriteria.OperationType.STRING));

        criteriaList.add(new ApiSearchCriteria(Attribute.START_DATE, ApiSearchCriteria.SearchOperation.EQUALS, null,
                ApiSearchCriteria.OperationType.DATE));

        criteriaList.add(
                new ApiSearchCriteria(Attribute.END_DATE, ApiSearchCriteria.SearchOperation.EQUALS, null, ApiSearchCriteria.OperationType.DATE));

        Mockito.when(txnService.loadTransactionHistory(Mockito.any(String.class), Mockito.any(DateTime.class),
                Mockito.any(DateTime.class), Mockito.any(ServiceErrors.class))).thenReturn(null);

        List<TransactionHistoryDto> transactions = transactionDtoService.search(criteriaList, new ServiceErrorsImpl());

        assertNotNull(transactions);
        assertEquals(transactions.size(), 0);
    }

    @Test
    public void testSearchWithAssetNull() {
        List<TransactionHistory> txnList = new ArrayList<>();

        TransactionHistoryImpl transactionHistory1 = new TransactionHistoryImpl();
        transactionHistory1.setPosAssetId("2234");
        transactionHistory1.setAsset(null);
        transactionHistory1.setContAsset(null);
        transactionHistory1.setBookingText("buy");
        transactionHistory1.setDocDescription("Doc Optional desc");
        transactionHistory1.setDocId("1234");
        transactionHistory1.setAmount(new BigDecimal("1234"));
        transactionHistory1.setPosName("BT0001 BT Managed Account");
        transactionHistory1.setStatus("Approved");
        transactionHistory1.setEffectiveDate(new DateTime());
        transactionHistory1.setTransactionType("Buy");
        transactionHistory1.setValDate(new DateTime());
        transactionHistory1.setContType(ContainerType.MANAGED_PORTFOLIO);
        transactionHistory1.setOrigin(Origin.WEB_UI);
        transactionHistory1.setBTOrderType(BTOrderType.CORPORATE_ACTION);
        transactionHistory1.setPosName("BT0001 BT Managed Account");
        txnList.add(transactionHistory1);

        Mockito.when(txnService.loadTransactionHistory(Mockito.any(String.class), Mockito.any(DateTime.class),
                Mockito.any(DateTime.class), Mockito.any(ServiceErrors.class))).thenReturn(txnList);

        List<ApiSearchCriteria> criteriaList = new ArrayList<ApiSearchCriteria>();

        criteriaList.add(new ApiSearchCriteria(Attribute.ACCOUNT_ID, ApiSearchCriteria.SearchOperation.EQUALS,
                EncodedString.fromPlainText("333").toString(), ApiSearchCriteria.OperationType.STRING));

        criteriaList.add(new ApiSearchCriteria(Attribute.START_DATE, ApiSearchCriteria.SearchOperation.EQUALS, new DateTime().toString(),
                ApiSearchCriteria.OperationType.DATE));

        criteriaList.add(
                new ApiSearchCriteria(Attribute.END_DATE, ApiSearchCriteria.SearchOperation.EQUALS, new DateTime().toString(), ApiSearchCriteria.OperationType.DATE));

        List<TransactionHistoryDto> transactions = transactionDtoService.search(criteriaList, new ServiceErrorsImpl());

        assertNotNull(transactions);
        assertEquals(transactions.size(), 1);
        assertNull(transactions.get(0).getAssetCode());
        assertEquals(transactionHistory1.getPosName(), transactions.get(0).getAssetName());
    }

    @Test
    public void testSearchWithAssetOptionAndBond() {
        //Asset.OPTION
        AssetImpl asset = new AssetImpl();
        asset = new AssetImpl();
        asset.setAssetId("2234");
        asset.setAssetName("BHP BHP Billiton");
        asset.setAssetType(AssetType.OPTION);
        asset.setAssetCode("BHP");
        asset.setAssetClass(AssetClass.AUSTRALIAN_SHARES);

        List<TransactionHistory> txnList = new ArrayList<>();

        TransactionHistoryImpl transactionHistory1 = new TransactionHistoryImpl();
        transactionHistory1.setPosAssetId("2234");
        transactionHistory1.setAsset(asset);
        transactionHistory1.setContAsset(contAsset);
        transactionHistory1.setBookingText("buy");
        transactionHistory1.setDocDescription("Doc Optional desc");
        transactionHistory1.setDocId("1234");
        transactionHistory1.setAmount(new BigDecimal("1234"));
        transactionHistory1.setPosName("BT0001 BT Managed Account");
        transactionHistory1.setStatus("Approved");
        transactionHistory1.setEffectiveDate(new DateTime());
        transactionHistory1.setTransactionType("Buy");
        transactionHistory1.setValDate(new DateTime());
        transactionHistory1.setContType(ContainerType.DIRECT);
        transactionHistory1.setOrigin(Origin.WEB_UI);
        transactionHistory1.setBTOrderType(BTOrderType.CORPORATE_ACTION);
        transactionHistory1.setPosName("BT0001 BT Managed Account");
        txnList.add(transactionHistory1);

        Mockito.when(txnService.loadTransactionHistory(Mockito.any(String.class), Mockito.any(DateTime.class),
                Mockito.any(DateTime.class), Mockito.any(ServiceErrors.class))).thenReturn(txnList);

        List<ApiSearchCriteria> criteriaList = new ArrayList<ApiSearchCriteria>();

        criteriaList.add(new ApiSearchCriteria(Attribute.ACCOUNT_ID, ApiSearchCriteria.SearchOperation.EQUALS,
                EncodedString.fromPlainText("333").toString(), ApiSearchCriteria.OperationType.STRING));

        criteriaList.add(new ApiSearchCriteria(Attribute.START_DATE, ApiSearchCriteria.SearchOperation.EQUALS, new DateTime().toString(),
                ApiSearchCriteria.OperationType.DATE));

        criteriaList.add(
                new ApiSearchCriteria(Attribute.END_DATE, ApiSearchCriteria.SearchOperation.EQUALS, new DateTime().toString(), ApiSearchCriteria.OperationType.DATE));

        List<TransactionHistoryDto> transactions = transactionDtoService.search(criteriaList, new ServiceErrorsImpl());

        assertNotNull(transactions);
        assertEquals(transactions.size(), 1);
        assertEquals(AssetType.SHARE.getDisplayName(), transactions.get(0).getInvestmentName());

        //Asset.BOND
        asset = new AssetImpl();
        asset = new AssetImpl();
        asset.setAssetId("2234");
        asset.setAssetName("BHP BHP Billiton");
        asset.setAssetType(AssetType.BOND);
        asset.setAssetCode("BHP");
        asset.setAssetClass(AssetClass.AUSTRALIAN_SHARES);

        txnList = new ArrayList<>();

        transactionHistory1 = new TransactionHistoryImpl();
        transactionHistory1.setPosAssetId("2234");
        transactionHistory1.setAsset(asset);
        transactionHistory1.setContAsset(contAsset);
        transactionHistory1.setBookingText("buy");
        transactionHistory1.setDocDescription("Doc Optional desc");
        transactionHistory1.setDocId("1234");
        transactionHistory1.setAmount(new BigDecimal("1234"));
        transactionHistory1.setPosName("BT0001 BT Managed Account");
        transactionHistory1.setStatus("Approved");
        transactionHistory1.setEffectiveDate(new DateTime());
        transactionHistory1.setTransactionType("Buy");
        transactionHistory1.setValDate(new DateTime());
        transactionHistory1.setContType(ContainerType.DIRECT);
        transactionHistory1.setOrigin(Origin.WEB_UI);
        transactionHistory1.setBTOrderType(BTOrderType.CORPORATE_ACTION);
        transactionHistory1.setPosName("BT0001 BT Managed Account");
        txnList.add(transactionHistory1);

        Mockito.when(txnService.loadTransactionHistory(Mockito.any(String.class), Mockito.any(DateTime.class),
                Mockito.any(DateTime.class), Mockito.any(ServiceErrors.class))).thenReturn(txnList);

        transactions = transactionDtoService.search(criteriaList, new ServiceErrorsImpl());

        assertNotNull(transactions);
        assertEquals(transactions.size(), 1);
        assertEquals(AssetType.SHARE.getDisplayName(), transactions.get(0).getInvestmentName());
    }
}

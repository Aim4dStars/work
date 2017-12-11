package com.bt.nextgen.api.transactionhistory.service;

import com.bt.nextgen.api.transactionhistory.model.TransactionHistoryDto;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.asset.AssetImpl;
import com.bt.nextgen.service.avaloq.transactionhistory.TransactionHistoryImpl;
import com.bt.nextgen.service.avaloq.transactionhistory.WrapTransactionHistoryImpl;
import com.bt.nextgen.service.integration.Origin;
import com.bt.nextgen.service.integration.account.ContainerType;
import com.bt.nextgen.service.integration.asset.AssetClass;
import com.bt.nextgen.service.integration.transaction.DashboardTransactionIntegrationService;
import com.bt.nextgen.service.integration.transactionhistory.BTOrderType;
import com.bt.nextgen.service.integration.transactionhistory.TransactionHistory;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.btfin.panorama.service.client.asset.dto.AssetClientImpl;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.btfin.panorama.service.integration.asset.AssetType;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Qualifier;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Created by L062605 on 4/09/2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class WrapTransactionHistoryDtoServiceImplTest {

    @InjectMocks
    private WrapTransactionHistoryDtoServiceImpl wrapTransactionHistoryDtoService;

    @Mock
    @Qualifier("ThirdPartyTransactionIntegrationService")
    private DashboardTransactionIntegrationService wrapTransactionIntegrationService;

    private AssetClientImpl asset1;
    private AssetClientImpl asset2;
    private AssetClientImpl asset3;
    private AssetClientImpl asset4;
    private AssetClientImpl asset5;
    private AssetImpl contAsset;
    private TransactionHistoryImpl transactionHistory1;
    private TransactionHistoryImpl transactionHistory2;
    private TransactionHistoryImpl transactionHistory3;
    private TransactionHistoryImpl transactionHistory4;
    private WrapTransactionHistoryImpl wrapTransactionHistory1;
    private WrapTransactionHistoryImpl wrapTransactionHistory2;
    private WrapTransactionHistoryImpl wrapTransactionHistory3;

    @Before
    public void setup() throws Exception {
        contAsset = new AssetImpl();
        contAsset.setAssetId("2200");
        contAsset.setAssetName("BT Managed Portfolio");
        contAsset.setAssetType(AssetType.MANAGED_PORTFOLIO);
        contAsset.setAssetCode("BT0001");

        asset1 = new AssetClientImpl();
        asset1.setAssetId("2234");
        asset1.setAssetName("BHP BHP Billiton");
        asset1.setAssetType(AssetType.SHARE);
        asset1.setAssetCode("BHP");
        asset1.setAssetClass(AssetClass.AUSTRALIAN_SHARES);

        asset2 = new AssetClientImpl();
        asset2.setAssetId("2234");
        asset2.setAssetName("BHP BHP Billiton");
        asset2.setAssetType(AssetType.CASH);
        asset2.setAssetCode("BHP");
        asset2.setAssetClass(AssetClass.AUSTRALIAN_SHARES);

        asset3 = new AssetClientImpl();
        asset3.setAssetId("2234");
        asset3.setAssetName("BHP BHP Billiton");
        asset3.setAssetType(AssetType.TERM_DEPOSIT);
        asset3.setAssetCode("BHP");
        asset3.setAssetClass(AssetClass.AUSTRALIAN_SHARES);

        asset4 = new AssetClientImpl();
        asset4.setAssetId("2234");
        asset4.setAssetName("BHP BHP Billiton");
        asset4.setAssetType(AssetType.BOND);
        asset4.setAssetCode("BHP");
        asset4.setAssetClass(AssetClass.AUSTRALIAN_SHARES);

        asset5 = new AssetClientImpl();
        asset5.setAssetId("2234");
        asset5.setAssetName("BHP BHP Billiton");
        asset5.setAssetType(AssetType.OPTION);
        asset5.setAssetCode("BHP");
        asset5.setAssetClass(AssetClass.AUSTRALIAN_SHARES);

        transactionHistory1 = new TransactionHistoryImpl();
        transactionHistory1.setPosAssetId("2234");
        transactionHistory1.setAsset(asset1);
        transactionHistory1.setContAsset(contAsset);
        transactionHistory1.setBookingText("buy");
        transactionHistory1.setTransactionDescription("Trans Optional desc");
        transactionHistory1.setDocId("1234");
        transactionHistory1.setAmount(new BigDecimal(2344));
        transactionHistory1.setPosName("BT0001 BT Managed Account");
        transactionHistory1.setStatus("Approved");
        transactionHistory1.setEffectiveDate(new DateTime("2017-01-01"));
        transactionHistory1.setValDate(new DateTime("2017-01-01"));
        transactionHistory1.setContType(ContainerType.MANAGED_PORTFOLIO);
        transactionHistory1.setOrigin(Origin.WEB_UI);
        transactionHistory1.setBTOrderType(BTOrderType.CORPORATE_ACTION);

        transactionHistory2 = new TransactionHistoryImpl();
        transactionHistory2.setPosAssetId("2234");
        transactionHistory2.setAsset(asset4);
        transactionHistory2.setContAsset(contAsset);
        transactionHistory2.setBookingText("buy");
        transactionHistory2.setDocDescription("Doc Optional desc");
        transactionHistory2.setDocId("1234");
        transactionHistory2.setAmount(new BigDecimal(0));
        transactionHistory2.setPosName("BT0001 BT Managed Account");
        transactionHistory2.setStatus("Approved");
        transactionHistory2.setEffectiveDate(new DateTime("2017-01-01"));
        transactionHistory2.setTransactionType("Buy");
        transactionHistory2.setValDate(new DateTime("2017-01-01"));
        transactionHistory2.setContType(ContainerType.DIRECT);
        transactionHistory2.setOrigin(Origin.WEB_UI);

        transactionHistory3 = new TransactionHistoryImpl();
        transactionHistory3.setPosAssetId("2234");
        transactionHistory3.setAsset(asset5);
        transactionHistory3.setContAsset(contAsset);
        transactionHistory3.setBookingText("buy");
        transactionHistory3.setDocDescription("Doc Optional desc");
        transactionHistory3.setDocId("1234");
        transactionHistory3.setAmount(new BigDecimal(0));
        transactionHistory3.setPosName("BT0001 BT Managed Account");
        transactionHistory3.setStatus("Approved");
        transactionHistory3.setEffectiveDate(new DateTime("2017-01-01"));
        transactionHistory3.setTransactionType("Buy");
        transactionHistory3.setValDate(new DateTime("2017-01-01"));
        transactionHistory3.setContType(ContainerType.DIRECT);
        transactionHistory3.setOrigin(Origin.WEB_UI);
        transactionHistory3.setBTOrderType(BTOrderType.CORPORATE_ACTION);

        transactionHistory4 = new TransactionHistoryImpl();
        transactionHistory4.setPosAssetId("2234");
        transactionHistory4.setAsset(asset4);
        transactionHistory4.setContAsset(contAsset);
        transactionHistory4.setBookingText("buy");
        transactionHistory4.setDocDescription("Doc Optional desc");
        transactionHistory4.setDocId("1234");
        transactionHistory4.setAmount(new BigDecimal(2345));
        transactionHistory4.setPosName("BT0001 BT Managed Account");
        transactionHistory4.setStatus("Approved");
        transactionHistory4.setEffectiveDate(new DateTime("2017-01-01"));
        transactionHistory4.setTransactionType("Buy");
        transactionHistory4.setValDate(new DateTime("2017-01-01"));
        transactionHistory4.setContType(ContainerType.TERM_DEPOSIT);
        transactionHistory4.setOrigin(Origin.WEB_UI);

        wrapTransactionHistory1 = new WrapTransactionHistoryImpl();
        wrapTransactionHistory2 = new WrapTransactionHistoryImpl();
        wrapTransactionHistory3 = new WrapTransactionHistoryImpl();

        wrapTransactionHistory1.setDocId("30577963");
        wrapTransactionHistory1.setEffectiveDate(new DateTime("2015-01-01T00:00:00.000+05:30"));
        wrapTransactionHistory1.setValDate(new DateTime("2015-01-01T00:00:00.000+05:30"));
        wrapTransactionHistory1.setQuantity(new BigDecimal("23"));
        wrapTransactionHistory1.setAssetCode("BGL0034AU");
        wrapTransactionHistory1.setAssetName("Security 1");
        wrapTransactionHistory1.setAssetType(AssetType.MANAGED_FUND);
        wrapTransactionHistory1.setTransactionType("Buy");
        wrapTransactionHistory1.setBookingText("Booking text1");

        wrapTransactionHistory2.setDocId("30577964");
        wrapTransactionHistory2.setEffectiveDate(new DateTime("2015-01-01T00:00:00.000+05:30"));
        wrapTransactionHistory2.setValDate(new DateTime("2015-01-01T00:00:00.000+05:30"));
        wrapTransactionHistory2.setAmount(new BigDecimal("2365.14"));
        wrapTransactionHistory2.setAssetType(AssetType.CASH);
        wrapTransactionHistory2.setTransactionType("Buy");
        wrapTransactionHistory2.setBookingText("Booking text1");

        wrapTransactionHistory3.setDocId("30577964");
        wrapTransactionHistory3.setEffectiveDate(new DateTime("2015-01-01T00:00:00.000+05:30"));
        wrapTransactionHistory3.setValDate(new DateTime("2015-01-01T00:00:00.000+05:30"));
        wrapTransactionHistory3.setAmount(new BigDecimal("2365.14"));
        wrapTransactionHistory3.setAssetType(null);
        wrapTransactionHistory3.setTransactionType("Buy");
        wrapTransactionHistory3.setBookingText("Booking text1");

        List<TransactionHistory> txnList = new ArrayList<>();
        txnList.add(transactionHistory1);
        txnList.add(transactionHistory2);
        txnList.add(transactionHistory3);
        txnList.add(transactionHistory4);
        txnList.add(wrapTransactionHistory1);
        txnList.add(wrapTransactionHistory2);
        txnList.add(wrapTransactionHistory3);

        Mockito.when(wrapTransactionIntegrationService.loadTransactionHistory(Mockito.any(String.class), Mockito.any(DateTime.class),
                Mockito.any(DateTime.class), Mockito.any(ServiceErrors.class))).thenReturn(txnList);
    }

    @Test
    public void testSearch() {
        List<ApiSearchCriteria> criteriaList = new ArrayList<ApiSearchCriteria>();

        criteriaList.add(new ApiSearchCriteria(Attribute.ACCOUNT_ID, ApiSearchCriteria.SearchOperation.EQUALS,
                EncodedString.fromPlainText("333").toString(), ApiSearchCriteria.OperationType.STRING));

        criteriaList.add(new ApiSearchCriteria(Attribute.START_DATE, ApiSearchCriteria.SearchOperation.EQUALS, new DateTime("2017-01-01").toString(),
                ApiSearchCriteria.OperationType.DATE));

        criteriaList.add(
                new ApiSearchCriteria(Attribute.END_DATE, ApiSearchCriteria.SearchOperation.EQUALS, new DateTime("2017-01-01").toString(), ApiSearchCriteria.OperationType.DATE));

        List<TransactionHistoryDto> transactions = wrapTransactionHistoryDtoService.search(criteriaList, new ServiceErrorsImpl());

        assertNotNull(transactions);
        assertEquals(6, transactions.size());
        TransactionHistoryDto transaction1 = transactions.get(0);
        assertEquals(transactionHistory2.getDocId(), transaction1.getOrderId());
        assertEquals(transactionHistory2.getBookingText() + " - " + transactionHistory2.getDocDescription().toLowerCase(), transaction1.getDescription());
        assertEquals(transactionHistory2.getEffectiveDate(), transaction1.getTradeDate());
        assertEquals(transactionHistory2.getValDate(), transaction1.getSettlementDate());
        assertEquals(transactionHistory2.getAmount(), transaction1.getQuantity());
        assertEquals(transactionHistory2.getOrigin().getName(), transaction1.getOrigin());

        TransactionHistoryDto transaction2 = transactions.get(1);
        assertEquals(transactionHistory1.getDocId(), transaction2.getOrderId());
        assertEquals(transactionHistory1.getBookingText() + ". " + transactionHistory1.getTransactionDescription(), transaction2.getDescription());
        assertEquals(transactionHistory1.getEffectiveDate(), transaction2.getTradeDate());
        assertEquals(transactionHistory1.getValDate(), transaction2.getSettlementDate());
        assertEquals(transactionHistory1.getAmount(), transaction2.getQuantity());
        assertEquals(transactionHistory1.getOrigin().getName(), transaction2.getOrigin());

        TransactionHistoryDto transaction3 = transactions.get(3);
        assertEquals(wrapTransactionHistory1.getDocId(), transaction3.getOrderId());
        assertEquals(wrapTransactionHistory1.getBookingText(), transaction3.getDescription());
        assertEquals(wrapTransactionHistory1.getEffectiveDate(), transaction3.getTradeDate());
        assertEquals(wrapTransactionHistory1.getValDate(), transaction3.getSettlementDate());
        assertEquals(wrapTransactionHistory1.getQuantity(), transaction3.getQuantity());

        TransactionHistoryDto transaction4 = transactions.get(4);
        assertEquals(wrapTransactionHistory2.getDocId(), transaction4.getOrderId());
        assertEquals(wrapTransactionHistory2.getBookingText(), transaction4.getDescription());
        assertEquals(wrapTransactionHistory2.getEffectiveDate(), transaction4.getTradeDate());
        assertEquals(wrapTransactionHistory2.getValDate(), transaction4.getSettlementDate());
        assertEquals(wrapTransactionHistory2.getAmount(), transaction4.getNetAmount());

        TransactionHistoryDto transaction5 = transactions.get(5);
        assertEquals(wrapTransactionHistory3.getDocId(), transaction5.getOrderId());
        assertEquals("-", transaction5.getInvestmentName());
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

        Mockito.when(wrapTransactionIntegrationService.loadTransactionHistory(Mockito.any(String.class), Mockito.any(DateTime.class),
                Mockito.any(DateTime.class), Mockito.any(ServiceErrors.class))).thenReturn(null);

        List<TransactionHistoryDto> transactions = wrapTransactionHistoryDtoService.search(criteriaList, new ServiceErrorsImpl());

        assertNotNull(transactions);
        assertEquals(transactions.size(), 0);
    }

    @Test
    public void testSearchWithAssetCodeFilter() {
        List<ApiSearchCriteria> criteriaList = new ArrayList<ApiSearchCriteria>();

        criteriaList.add(new ApiSearchCriteria(Attribute.ACCOUNT_ID, ApiSearchCriteria.SearchOperation.EQUALS,
                EncodedString.fromPlainText("333").toString(), ApiSearchCriteria.OperationType.STRING));

        criteriaList.add(new ApiSearchCriteria(Attribute.START_DATE, ApiSearchCriteria.SearchOperation.EQUALS, new DateTime("2017-01-01").toString(),
                ApiSearchCriteria.OperationType.DATE));

        criteriaList.add(
                new ApiSearchCriteria(Attribute.END_DATE, ApiSearchCriteria.SearchOperation.EQUALS, new DateTime("2017-01-01").toString(), ApiSearchCriteria.OperationType.DATE));

        criteriaList.add(new ApiSearchCriteria(Attribute.ASSET_CODE, ApiSearchCriteria.SearchOperation.EQUALS, "BGL0034AU", ApiSearchCriteria.OperationType.STRING));

        List<TransactionHistoryDto> transactions = wrapTransactionHistoryDtoService.search(criteriaList, new ServiceErrorsImpl());

        assertNotNull(transactions);
        assertEquals(transactions.size(), 1);
        TransactionHistoryDto transaction = transactions.get(0);
        assertEquals(wrapTransactionHistory1.getDocId(), transaction.getOrderId());
        assertEquals(wrapTransactionHistory1.getBookingText(), transaction.getDescription());
        assertEquals(wrapTransactionHistory1.getEffectiveDate(), transaction.getTradeDate());
        assertEquals(wrapTransactionHistory1.getValDate(), transaction.getSettlementDate());
        assertEquals(wrapTransactionHistory1.getQuantity(), transaction.getQuantity());
    }

    @Test
    public void testSearchWithAssetNameFilter() {
        transactionHistory3 = new TransactionHistoryImpl();
        transactionHistory3.setPosAssetId("2234");
        transactionHistory3.setAsset(null);
        transactionHistory3.setContAsset(contAsset);
        transactionHistory3.setBookingText("buy");
        transactionHistory3.setDocDescription("Doc Optional desc");
        transactionHistory3.setDocId("1234");
        transactionHistory3.setAmount(new BigDecimal(0));
        transactionHistory3.setPosName("BT0001 BT Managed Account");
        transactionHistory3.setStatus("Approved");
        transactionHistory3.setEffectiveDate(new DateTime("2017-01-01"));
        transactionHistory3.setTransactionType("Buy");
        transactionHistory3.setValDate(new DateTime("2017-01-01"));
        transactionHistory3.setContType(ContainerType.MANAGED_PORTFOLIO);
        transactionHistory3.setOrigin(Origin.WEB_UI);
        transactionHistory3.setBTOrderType(BTOrderType.CORPORATE_ACTION);

        List<ApiSearchCriteria> criteriaList = new ArrayList<ApiSearchCriteria>();

        criteriaList.add(new ApiSearchCriteria(Attribute.ACCOUNT_ID, ApiSearchCriteria.SearchOperation.EQUALS,
                EncodedString.fromPlainText("333").toString(), ApiSearchCriteria.OperationType.STRING));

        criteriaList.add(new ApiSearchCriteria(Attribute.START_DATE, ApiSearchCriteria.SearchOperation.EQUALS, new DateTime("2017-01-01").toString(),
                ApiSearchCriteria.OperationType.DATE));

        criteriaList.add(
                new ApiSearchCriteria(Attribute.END_DATE, ApiSearchCriteria.SearchOperation.EQUALS, new DateTime("2017-01-01").toString(), ApiSearchCriteria.OperationType.DATE));

        criteriaList.add(new ApiSearchCriteria(Attribute.ASSET_CODE, ApiSearchCriteria.SearchOperation.EQUALS, "Security", ApiSearchCriteria.OperationType.STRING));

        List<TransactionHistoryDto> transactions = wrapTransactionHistoryDtoService.search(criteriaList, new ServiceErrorsImpl());

        assertNotNull(transactions);
        assertEquals(transactions.size(), 1);
    }

    @Test
    public void testSearchWithAssetCash() {
        List<TransactionHistory> txnList = new ArrayList<>();

        //if BTOrderType.CORPORATE_ACTION
        TransactionHistoryImpl transactionHistory1 = new TransactionHistoryImpl();
        transactionHistory1.setPosAssetId("2234");
        transactionHistory1.setAsset(asset2);
        transactionHistory1.setContAsset(contAsset);
        transactionHistory1.setBookingText("buy");
        transactionHistory1.setDocDescription("Doc Optional desc");
        transactionHistory1.setDocId("1234");
        transactionHistory1.setAmount(new BigDecimal("1234"));
        transactionHistory1.setPosName("BT0001 BT Managed Account");
        transactionHistory1.setStatus("Approved");
        transactionHistory1.setEffectiveDate(new DateTime("2017-01-01"));
        transactionHistory1.setTransactionType("Buy");
        transactionHistory1.setValDate(new DateTime("2017-01-01"));
        transactionHistory1.setContType(ContainerType.DIRECT);
        transactionHistory1.setOrigin(Origin.WEB_UI);
        transactionHistory1.setBTOrderType(BTOrderType.CORPORATE_ACTION);
        transactionHistory1.setRefAsset(asset2);
        txnList.add(transactionHistory1);

        Mockito.when(wrapTransactionIntegrationService.loadTransactionHistory(Mockito.any(String.class), Mockito.any(DateTime.class),
                Mockito.any(DateTime.class), Mockito.any(ServiceErrors.class))).thenReturn(txnList);

        List<ApiSearchCriteria> criteriaList = new ArrayList<ApiSearchCriteria>();

        criteriaList.add(new ApiSearchCriteria(Attribute.ACCOUNT_ID, ApiSearchCriteria.SearchOperation.EQUALS,
                EncodedString.fromPlainText("333").toString(), ApiSearchCriteria.OperationType.STRING));

        criteriaList.add(new ApiSearchCriteria(Attribute.START_DATE, ApiSearchCriteria.SearchOperation.EQUALS, new DateTime("2017-01-01").toString(),
                ApiSearchCriteria.OperationType.DATE));

        criteriaList.add(
                new ApiSearchCriteria(Attribute.END_DATE, ApiSearchCriteria.SearchOperation.EQUALS, new DateTime("2017-01-01").toString(), ApiSearchCriteria.OperationType.DATE));

        List<TransactionHistoryDto> transactions = wrapTransactionHistoryDtoService.search(criteriaList, new ServiceErrorsImpl());

        assertNotNull(transactions);
        assertEquals(transactions.size(), 1);
        assertEquals(asset2.getAssetCode(), transactions.get(0).getAssetCode());
        assertEquals(asset2.getAssetName(), transactions.get(0).getAssetName());

        //if BTOrderType.INCOME
        transactionHistory1 = new TransactionHistoryImpl();
        transactionHistory1.setPosAssetId("2234");
        transactionHistory1.setAsset(asset2);
        transactionHistory1.setContAsset(contAsset);
        transactionHistory1.setBookingText("buy");
        transactionHistory1.setDocDescription("Doc Optional desc");
        transactionHistory1.setDocId("1234");
        transactionHistory1.setAmount(new BigDecimal("1234"));
        transactionHistory1.setPosName("BT0001 BT Managed Account");
        transactionHistory1.setStatus("Approved");
        transactionHistory1.setEffectiveDate(new DateTime("2017-01-01"));
        transactionHistory1.setValDate(new DateTime("2017-01-01"));
        transactionHistory1.setContType(ContainerType.DIRECT);
        transactionHistory1.setOrigin(Origin.WEB_UI);
        transactionHistory1.setRefAsset(asset2);
        transactionHistory1.setBTOrderType(BTOrderType.INCOME);
        txnList = new ArrayList<>();
        txnList.add(transactionHistory1);

        Mockito.when(wrapTransactionIntegrationService.loadTransactionHistory(Mockito.any(String.class), Mockito.any(DateTime.class),
                Mockito.any(DateTime.class), Mockito.any(ServiceErrors.class))).thenReturn(txnList);

        transactions = wrapTransactionHistoryDtoService.search(criteriaList, new ServiceErrorsImpl());

        assertNotNull(transactions);
        assertEquals(transactions.size(), 1);
        assertEquals(asset2.getAssetCode(), transactions.get(0).getAssetCode());
        assertEquals(asset2.getAssetName(), transactions.get(0).getAssetName());

        //if BTOrderType.BUY
        transactionHistory1 = new TransactionHistoryImpl();
        transactionHistory1.setPosAssetId("2234");
        transactionHistory1.setAsset(asset2);
        transactionHistory1.setContAsset(contAsset);
        transactionHistory1.setBookingText("buy");
        transactionHistory1.setDocDescription("Doc Optional desc");
        transactionHistory1.setDocId("1234");
        transactionHistory1.setAmount(new BigDecimal("-1234"));
        transactionHistory1.setPosName("BT0001 BT Managed Account");
        transactionHistory1.setStatus("Approved");
        transactionHistory1.setEffectiveDate(new DateTime("2017-01-01"));
        transactionHistory1.setValDate(new DateTime("2017-01-01"));
        transactionHistory1.setContType(ContainerType.DIRECT);
        transactionHistory1.setOrigin(Origin.WEB_UI);
        transactionHistory1.setRefAsset(asset2);
        transactionHistory1.setBTOrderType(BTOrderType.BUY);
        txnList = new ArrayList<>();
        txnList.add(transactionHistory1);

        Mockito.when(wrapTransactionIntegrationService.loadTransactionHistory(Mockito.any(String.class), Mockito.any(DateTime.class),
                Mockito.any(DateTime.class), Mockito.any(ServiceErrors.class))).thenReturn(txnList);

        transactions = wrapTransactionHistoryDtoService.search(criteriaList, new ServiceErrorsImpl());

        assertNotNull(transactions);
        assertEquals(transactions.size(), 1);
        assertNull(transactions.get(0).getAssetCode());
        assertNull(transactions.get(0).getAssetName());
    }

    @Test
    public void testSearchWithAssetNull() {
        List<TransactionHistory> txnList = new ArrayList<>();

        TransactionHistoryImpl transactionHistory1 = new TransactionHistoryImpl();
        transactionHistory1.setPosAssetId("2234");
        transactionHistory1.setAsset(null);
        transactionHistory1.setContAsset(contAsset);
        transactionHistory1.setBookingText("buy");
        transactionHistory1.setDocDescription("Doc Optional desc");
        transactionHistory1.setDocId("1234");
        transactionHistory1.setAmount(new BigDecimal("1234"));
        transactionHistory1.setPosName("BT0001 BT Managed Account");
        transactionHistory1.setStatus("Approved");
        transactionHistory1.setEffectiveDate(new DateTime("2017-01-01"));
        transactionHistory1.setTransactionType("Buy");
        transactionHistory1.setValDate(new DateTime("2017-01-01"));
        transactionHistory1.setContType(ContainerType.MANAGED_PORTFOLIO);
        transactionHistory1.setOrigin(Origin.WEB_UI);
        transactionHistory1.setBTOrderType(BTOrderType.CORPORATE_ACTION);
        transactionHistory1.setRefAsset(asset2);
        transactionHistory1.setPosName("BT0001 BT Managed Account");
        txnList.add(transactionHistory1);

        Mockito.when(wrapTransactionIntegrationService.loadTransactionHistory(Mockito.any(String.class), Mockito.any(DateTime.class),
                Mockito.any(DateTime.class), Mockito.any(ServiceErrors.class))).thenReturn(txnList);

        List<ApiSearchCriteria> criteriaList = new ArrayList<ApiSearchCriteria>();

        criteriaList.add(new ApiSearchCriteria(Attribute.ACCOUNT_ID, ApiSearchCriteria.SearchOperation.EQUALS,
                EncodedString.fromPlainText("333").toString(), ApiSearchCriteria.OperationType.STRING));

        criteriaList.add(new ApiSearchCriteria(Attribute.START_DATE, ApiSearchCriteria.SearchOperation.EQUALS, new DateTime("2017-01-01").toString(),
                ApiSearchCriteria.OperationType.DATE));

        criteriaList.add(
                new ApiSearchCriteria(Attribute.END_DATE, ApiSearchCriteria.SearchOperation.EQUALS, new DateTime("2017-01-01").toString(), ApiSearchCriteria.OperationType.DATE));

        List<TransactionHistoryDto> transactions = wrapTransactionHistoryDtoService.search(criteriaList, new ServiceErrorsImpl());

        assertNotNull(transactions);
        assertEquals(transactions.size(), 1);
        assertNull(transactions.get(0).getAssetCode());
        assertEquals(transactionHistory1.getPosName(), transactions.get(0).getAssetName());
    }
}

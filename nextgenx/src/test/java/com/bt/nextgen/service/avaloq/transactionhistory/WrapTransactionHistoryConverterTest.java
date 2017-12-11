package com.bt.nextgen.service.avaloq.transactionhistory;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.asset.AssetImpl;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.bt.nextgen.service.integration.transactionhistory.TransactionHistory;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.btfin.panorama.service.integration.asset.AssetType;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class WrapTransactionHistoryConverterTest {

    @InjectMocks
    private WrapTransactionHistoryConverter transactionHistoryConverter;
    @Mock
    private AssetIntegrationService assetIntegrationService;

    private List<com.btfin.panorama.wrap.model.TransactionHistory> wrapTransactionHistories = new ArrayList<>();
    private com.btfin.panorama.wrap.model.TransactionHistory transactionHistory = new com.btfin.panorama.wrap.model.TransactionHistory();

    @Test
    public void convertWrapTransactionsToPanorama_transactionWithQuantity() {
        transactionHistory.setMovementId("30577963");
        transactionHistory.setClientId("M00533624");
        transactionHistory.setTradeDate("2015-01-01T00:00:00.000+05:30");
        transactionHistory.setSettlementDate("2015-01-01T00:00:00.000+05:30");
        transactionHistory.setQuantity(new BigDecimal("23"));
        transactionHistory.setAmount(new BigDecimal("2365.14"));
        transactionHistory.setSecurityCode("BGL0034AU");
        transactionHistory.setSecurityName("Security 1");
        transactionHistory.setTransactionType("Buy");
        transactionHistory.setPrice(new BigDecimal("23.36"));
        transactionHistory.setBookingText("Booking text1");
        transactionHistory.setServiceType("Application");
        transactionHistory.setServiceSubType("Standard");

        wrapTransactionHistories.add(transactionHistory);

        List<Asset> assets = new ArrayList<>();
        AssetImpl asset = new AssetImpl();
        asset.setAssetCode("BGL0034AU");
        asset.setAssetType(AssetType.MANAGED_FUND);
        asset.setAssetName("Managed fund");
        assets.add(asset);

        Mockito.when(assetIntegrationService.loadAssetsForAssetCodes(Mockito.anyCollectionOf(String.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(assets);

        List<TransactionHistory> transactionHistories = transactionHistoryConverter.convertWrapTransactionsToPanorama(wrapTransactionHistories, new ServiceErrorsImpl());
        Assert.assertNotNull(transactionHistories);
        WrapTransactionHistoryImpl wrapTransactionHistory = (WrapTransactionHistoryImpl) transactionHistories.get(0);
        Assert.assertEquals("30577963", wrapTransactionHistory.getDocId());
        Assert.assertEquals(new BigDecimal("23"), wrapTransactionHistory.getQuantity());
        Assert.assertNull(wrapTransactionHistory.getAmount());
        Assert.assertEquals("BGL0034AU", wrapTransactionHistory.getAssetCode());
        Assert.assertEquals(AssetType.MANAGED_FUND, wrapTransactionHistory.getAssetType());
        Assert.assertNotNull(wrapTransactionHistory.getAsset());
    }

    @Test
    public void convertWrapTransactionsToPanorama_transactionWithCAandCall() {
        transactionHistory.setMovementId("30578763");
        transactionHistory.setClientId("M00513624");
        transactionHistory.setTradeDate("2015-01-01T00:00:00.000+05:30");
        transactionHistory.setSettlementDate("2015-01-01T00:00:00.000+05:30");
        transactionHistory.setQuantity(new BigDecimal("23"));
        transactionHistory.setAmount(new BigDecimal("2361.14"));
        transactionHistory.setSecurityCode("BGL0034AU");
        transactionHistory.setSecurityName("Security 3");
        transactionHistory.setTransactionType("Buy");
        transactionHistory.setBookingText("Booking text2");
        transactionHistory.setServiceType("Corporate Action");
        transactionHistory.setServiceSubType("Call");

        wrapTransactionHistories.add(transactionHistory);

        List<Asset> assets = new ArrayList<>();
        AssetImpl asset = new AssetImpl();
        asset.setAssetCode("BGL0034AU");
        asset.setAssetType(AssetType.MANAGED_FUND);
        asset.setAssetName("Managed fund");
        assets.add(asset);

        Mockito.when(assetIntegrationService.loadAssetsForAssetCodes(Mockito.anyCollectionOf(String.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(assets);

        List<TransactionHistory> transactionHistories = transactionHistoryConverter.convertWrapTransactionsToPanorama(wrapTransactionHistories, new ServiceErrorsImpl());
        Assert.assertNotNull(transactionHistories);
        WrapTransactionHistoryImpl wrapTransactionHistory = (WrapTransactionHistoryImpl) transactionHistories.get(0);
        Assert.assertEquals("30578763", wrapTransactionHistory.getDocId());
        Assert.assertEquals(new BigDecimal("2361.14"), wrapTransactionHistory.getAmount());
        Assert.assertNull(wrapTransactionHistory.getQuantity());
        Assert.assertEquals("BGL0034AU", wrapTransactionHistory.getAssetCode());
        Assert.assertEquals(AssetType.MANAGED_FUND, wrapTransactionHistory.getAssetType());
        Assert.assertNotNull(wrapTransactionHistory.getAsset());
    }

    @Test
    public void convertWrapTransactionsToPanorama_transactionWithCash() {
        transactionHistory.setMovementId("30578963");
        transactionHistory.setClientId("M00533624");
        transactionHistory.setTradeDate("2015-01-01T00:00:00.000+05:30");
        transactionHistory.setSettlementDate("2015-01-01T00:00:00.000+05:30");
        transactionHistory.setAmount(new BigDecimal("2362.14"));
        transactionHistory.setSecurityCode("WRAPWCA");
        transactionHistory.setSecurityName("Security 2");
        transactionHistory.setTransactionType("Buy");
        transactionHistory.setBookingText("Booking text2");
        transactionHistory.setServiceType("Pref. Portfolio Withdrawal");
        transactionHistory.setServiceSubType("Trade");

        wrapTransactionHistories.add(transactionHistory);

        Mockito.when(assetIntegrationService.loadAssetsForAssetCodes(Mockito.anyCollectionOf(String.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(null);

        List<TransactionHistory> transactionHistories = transactionHistoryConverter.convertWrapTransactionsToPanorama(wrapTransactionHistories, new ServiceErrorsImpl());
        Assert.assertNotNull(transactionHistories);
        WrapTransactionHistoryImpl wrapTransactionHistory = (WrapTransactionHistoryImpl) transactionHistories.get(0);
        Assert.assertEquals("30578963", wrapTransactionHistory.getDocId());
        Assert.assertEquals(new BigDecimal("2362.14"), wrapTransactionHistory.getAmount());
        Assert.assertNull(wrapTransactionHistory.getQuantity());
        Assert.assertNull(wrapTransactionHistory.getAssetCode());
        Assert.assertEquals(AssetType.CASH, wrapTransactionHistory.getAssetType());
        Assert.assertNotNull(wrapTransactionHistory.getAsset());
    }

    @Test
    public void convertWrapTransactionsToPanorama_transactionWithTermDeposit() {
        transactionHistory.setMovementId("30578763");
        transactionHistory.setClientId("M00513624");
        transactionHistory.setTradeDate("2015-01-01T00:00:00.000+05:30");
        transactionHistory.setSettlementDate("2015-01-01T00:00:00.000+05:30");
        transactionHistory.setQuantity(new BigDecimal("23"));
        transactionHistory.setAmount(new BigDecimal("2361.14"));
        transactionHistory.setSecurityCode("WBC1234TD");
        transactionHistory.setSecurityName("Security 3");
        transactionHistory.setTransactionType("Buy");
        transactionHistory.setBookingText("Booking text2");
        transactionHistory.setServiceType("Application");
        transactionHistory.setServiceSubType("Adjustment");

        wrapTransactionHistories.add(transactionHistory);

        List<TransactionHistory> transactionHistories = transactionHistoryConverter.convertWrapTransactionsToPanorama(wrapTransactionHistories, new ServiceErrorsImpl());
        Assert.assertNotNull(transactionHistories);
        WrapTransactionHistoryImpl wrapTransactionHistory = (WrapTransactionHistoryImpl) transactionHistories.get(0);
        Assert.assertEquals("30578763", wrapTransactionHistory.getDocId());
        Assert.assertEquals(new BigDecimal("23"), wrapTransactionHistory.getQuantity());
        Assert.assertNull(wrapTransactionHistory.getAmount());
        Assert.assertEquals("WBC1234TD", wrapTransactionHistory.getAssetCode());
        Assert.assertEquals(AssetType.TERM_DEPOSIT, wrapTransactionHistory.getAssetType());
        Assert.assertNotNull(wrapTransactionHistory.getAsset());
    }

    @Test
    public void convertWrapTransactionsToPanorama_transactionWithSecurityClassEquity() {
        transactionHistory.setMovementId("30578763");
        transactionHistory.setClientId("M00513624");
        transactionHistory.setTradeDate("2015-01-01T00:00:00.000+05:30");
        transactionHistory.setSettlementDate("2015-01-01T00:00:00.000+05:30");
        transactionHistory.setQuantity(new BigDecimal("23"));
        transactionHistory.setAmount(new BigDecimal("2361.14"));
        transactionHistory.setSecurityCode("TDD1234TD");
        transactionHistory.setSecurityName("Security 3");
        transactionHistory.setTransactionType("Buy");
        transactionHistory.setBookingText("Booking text2");
        transactionHistory.setServiceType("Application");
        transactionHistory.setServiceSubType("Adjustment");
        transactionHistory.setSecurityClass("Equity");

        wrapTransactionHistories.add(transactionHistory);

        List<TransactionHistory> transactionHistories = transactionHistoryConverter.convertWrapTransactionsToPanorama(wrapTransactionHistories, new ServiceErrorsImpl());
        Assert.assertNotNull(transactionHistories);
        WrapTransactionHistoryImpl wrapTransactionHistory = (WrapTransactionHistoryImpl) transactionHistories.get(0);
        Assert.assertEquals("30578763", wrapTransactionHistory.getDocId());
        Assert.assertEquals(new BigDecimal("23"), wrapTransactionHistory.getQuantity());
        Assert.assertNull(wrapTransactionHistory.getAmount());
        Assert.assertEquals("TDD1234TD", wrapTransactionHistory.getAssetCode());
        Assert.assertEquals(AssetType.SHARE, wrapTransactionHistory.getAssetType());
        Assert.assertNotNull(wrapTransactionHistory.getAsset());
    }

    @Test
    public void convertWrapTransactionsToPanorama_transactionWithSecurityClassUnitTrust() {
        transactionHistory.setMovementId("30578763");
        transactionHistory.setClientId("M00513624");
        transactionHistory.setTradeDate("2015-01-01T00:00:00.000+05:30");
        transactionHistory.setSettlementDate("2015-01-01T00:00:00.000+05:30");
        transactionHistory.setQuantity(new BigDecimal("23"));
        transactionHistory.setAmount(new BigDecimal("2361.14"));
        transactionHistory.setSecurityCode("TDD1234TD");
        transactionHistory.setSecurityName("Security 3");
        transactionHistory.setTransactionType("Buy");
        transactionHistory.setBookingText("Booking text2");
        transactionHistory.setServiceType("Application");
        transactionHistory.setServiceSubType("Adjustment");
        transactionHistory.setSecurityClass("Unit Trust");

        wrapTransactionHistories.add(transactionHistory);

        List<TransactionHistory> transactionHistories = transactionHistoryConverter.convertWrapTransactionsToPanorama(wrapTransactionHistories, new ServiceErrorsImpl());
        Assert.assertNotNull(transactionHistories);
        WrapTransactionHistoryImpl wrapTransactionHistory = (WrapTransactionHistoryImpl) transactionHistories.get(0);
        Assert.assertEquals("30578763", wrapTransactionHistory.getDocId());
        Assert.assertEquals(new BigDecimal("23"), wrapTransactionHistory.getQuantity());
        Assert.assertNull(wrapTransactionHistory.getAmount());
        Assert.assertEquals("TDD1234TD", wrapTransactionHistory.getAssetCode());
        Assert.assertEquals(AssetType.MANAGED_FUND, wrapTransactionHistory.getAssetType());
        Assert.assertNotNull(wrapTransactionHistory.getAsset());
    }
}

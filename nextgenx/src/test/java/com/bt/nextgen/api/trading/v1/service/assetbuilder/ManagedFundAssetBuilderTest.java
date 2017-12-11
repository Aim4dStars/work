package com.bt.nextgen.api.trading.v1.service.assetbuilder;

import com.bt.nextgen.api.trading.v1.model.TradeAssetDto;
import com.bt.nextgen.api.trading.v1.util.TradableAssetsDtoServiceFilter;
import com.bt.nextgen.api.trading.v1.util.TradableAssetsDtoServiceHelper;
import com.bt.nextgen.service.avaloq.asset.ManagedFundAssetImpl;
import com.bt.nextgen.service.avaloq.portfolio.valuation.ManagedFundAccountValuationImpl;
import com.bt.nextgen.service.avaloq.portfolio.valuation.ManagedFundHoldingImpl;
import com.bt.nextgen.service.integration.account.DistributionMethod;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetClass;
import com.bt.nextgen.service.integration.asset.AssetStatus;
import com.btfin.panorama.service.integration.asset.AssetType;
import com.bt.nextgen.service.integration.portfolio.valuation.AccountHolding;
import com.bt.nextgen.service.integration.portfolio.valuation.HoldingKey;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(MockitoJUnitRunner.class)
public class ManagedFundAssetBuilderTest {

    @InjectMocks
    private ManagedFundAssetBuilder managedFundAssetBuilder;

    @Mock
    private TradableAssetsDtoServiceFilter tradableAssetsDtoServiceFilter;

    @Mock
    private TradableAssetsDtoServiceHelper tradableAssetsDtoServiceHelper;

    private ManagedFundAccountValuationImpl managedFundAccountValuation;

    private Map<String, Asset> filteredAssets;
    private Map<String, Asset> availableAssets;
    private ManagedFundAssetImpl abcAsset;
    private ManagedFundAssetImpl defAsset;
    private ManagedFundAssetImpl ghiAsset;
    private ManagedFundHoldingImpl mfHoldingABC1;
    private ManagedFundHoldingImpl mfHoldingABC2;
    private ManagedFundHoldingImpl mfHoldingABC3;
    private ManagedFundHoldingImpl mfHoldingABC4;
    private ManagedFundHoldingImpl mfHoldingDEF;
    private ManagedFundHoldingImpl mfHoldingGHI;
    private Map<String, List<DistributionMethod>> assetDistributionMethods;

    @Before
    public void setUp() throws Exception {

        assetDistributionMethods = new HashMap<>();

        List<DistributionMethod> availableMethods = new ArrayList<>();
        availableMethods.add(DistributionMethod.CASH);
        availableMethods.add(DistributionMethod.REINVEST);

        assetDistributionMethods.put("92655", availableMethods);
        assetDistributionMethods.put("92654", availableMethods);

        filteredAssets = new HashMap<String, Asset>();
        availableAssets = new HashMap<String, Asset>();

        abcAsset = new ManagedFundAssetImpl();
        abcAsset.setAssetId("92655");
        abcAsset.setAssetCode("AMP0255AU");
        abcAsset.setAssetType(AssetType.MANAGED_FUND);
        abcAsset.setAssetClass(AssetClass.AUSTRALIAN_SHARES);
        abcAsset.setAssetName("ABC asset");
        abcAsset.setPrice(new BigDecimal(.89));
        abcAsset.setStatus(AssetStatus.OPEN);
        abcAsset.setDistributionMethod("Cash Only");

        defAsset = new ManagedFundAssetImpl();
        defAsset.setAssetId("92654");
        defAsset.setAssetCode("AMP0254AU");
        defAsset.setAssetType(AssetType.MANAGED_FUND);
        defAsset.setAssetClass(AssetClass.AUSTRALIAN_SHARES);
        defAsset.setAssetName("DEF asset");
        defAsset.setPrice(new BigDecimal(.89));
        defAsset.setStatus(AssetStatus.OPEN);
        defAsset.setDistributionMethod("Cash Only");

        ghiAsset = new ManagedFundAssetImpl();
        ghiAsset.setAssetId("92653");
        ghiAsset.setAssetCode("AMP0254AU");
        ghiAsset.setAssetType(AssetType.MANAGED_FUND);
        ghiAsset.setAssetClass(AssetClass.AUSTRALIAN_SHARES);
        ghiAsset.setAssetName("GHI asset");
        ghiAsset.setPrice(new BigDecimal(.89));
        ghiAsset.setStatus(AssetStatus.OPEN);
        ghiAsset.setDistributionMethod("Cash Only");

        managedFundAccountValuation = new ManagedFundAccountValuationImpl();

        mfHoldingABC1 = new ManagedFundHoldingImpl();
        mfHoldingABC1.setAsset(abcAsset);
        mfHoldingABC1.setMarketValue(new BigDecimal(5000));
        mfHoldingABC1.setAvailableBalance(BigDecimal.ZERO);
        mfHoldingABC1.setAvailableUnits(BigDecimal.ZERO);
        mfHoldingABC1.setCost(new BigDecimal(10));
        mfHoldingABC1.setDistributionMethod(DistributionMethod.CASH);
        mfHoldingABC1.setHoldingKey(HoldingKey.valueOf("hid1", "name"));
        mfHoldingABC1.setAccruedIncome(new BigDecimal(10));
        mfHoldingABC1.setRefAsset(null);
        mfHoldingABC1.setUnitPrice(new BigDecimal(10));
        mfHoldingABC1.setUnitPriceDate(new DateTime());
        mfHoldingABC1.setUnits(new BigDecimal(10));
        mfHoldingABC1.setYield(new BigDecimal(10));

        mfHoldingABC2 = new ManagedFundHoldingImpl();
        mfHoldingABC2.setAsset(abcAsset);
        mfHoldingABC2.setMarketValue(new BigDecimal(10000));
        mfHoldingABC2.setAvailableBalance(new BigDecimal(10000));
        mfHoldingABC2.setAvailableUnits(new BigDecimal(5));
        mfHoldingABC2.setCost(new BigDecimal(10));
        mfHoldingABC2.setDistributionMethod(DistributionMethod.CASH);
        mfHoldingABC2.setHoldingKey(HoldingKey.valueOf("hid2", "name"));
        mfHoldingABC2.setAccruedIncome(new BigDecimal(10));
        mfHoldingABC2.setRefAsset(null);
        mfHoldingABC2.setUnitPrice(new BigDecimal(10));
        mfHoldingABC2.setUnitPriceDate(new DateTime());
        mfHoldingABC2.setUnits(new BigDecimal(10));
        mfHoldingABC2.setYield(new BigDecimal(10));

        mfHoldingABC3 = new ManagedFundHoldingImpl();
        mfHoldingABC3.setAsset(abcAsset);
        mfHoldingABC3.setMarketValue(new BigDecimal(30000));
        mfHoldingABC3.setAvailableBalance(new BigDecimal(20000));
        mfHoldingABC3.setAvailableUnits(new BigDecimal(10));
        mfHoldingABC3.setCost(new BigDecimal(10));
        mfHoldingABC3.setDistributionMethod(DistributionMethod.CASH);
        mfHoldingABC3.setHoldingKey(HoldingKey.valueOf("hid3", "name"));
        mfHoldingABC3.setAccruedIncome(new BigDecimal(10));
        mfHoldingABC3.setRefAsset(null);
        mfHoldingABC3.setUnitPrice(new BigDecimal(10));
        mfHoldingABC3.setUnitPriceDate(new DateTime());
        mfHoldingABC3.setUnits(new BigDecimal(10));
        mfHoldingABC3.setYield(new BigDecimal(10));

        mfHoldingABC4 = new ManagedFundHoldingImpl();
        mfHoldingABC4.setAsset(abcAsset);
        mfHoldingABC4.setMarketValue(new BigDecimal(4000));
        mfHoldingABC4.setAvailableBalance(BigDecimal.ZERO);
        mfHoldingABC4.setAvailableUnits(BigDecimal.ZERO);
        mfHoldingABC4.setCost(new BigDecimal(10));
        mfHoldingABC4.setDistributionMethod(DistributionMethod.CASH);
        mfHoldingABC4.setHoldingKey(HoldingKey.valueOf("hid4", "name"));
        mfHoldingABC4.setAccruedIncome(new BigDecimal(10));
        mfHoldingABC4.setRefAsset(null);
        mfHoldingABC4.setUnitPrice(new BigDecimal(10));
        mfHoldingABC4.setUnitPriceDate(new DateTime());
        mfHoldingABC4.setUnits(new BigDecimal(10));
        mfHoldingABC4.setYield(new BigDecimal(10));

        mfHoldingDEF = new ManagedFundHoldingImpl();
        mfHoldingDEF.setAsset(defAsset);
        mfHoldingDEF.setMarketValue(new BigDecimal(94000));
        mfHoldingDEF.setAvailableBalance(new BigDecimal(90000));
        mfHoldingDEF.setAvailableUnits(new BigDecimal(110));
        mfHoldingDEF.setCost(new BigDecimal(10));
        mfHoldingDEF.setDistributionMethod(DistributionMethod.CASH);
        mfHoldingDEF.setHoldingKey(HoldingKey.valueOf("hid5", "name"));
        mfHoldingDEF.setAccruedIncome(new BigDecimal(10));
        mfHoldingDEF.setRefAsset(null);
        mfHoldingDEF.setUnitPrice(new BigDecimal(10));
        mfHoldingDEF.setUnitPriceDate(new DateTime());
        mfHoldingDEF.setUnits(new BigDecimal(10));
        mfHoldingDEF.setYield(new BigDecimal(10));

        mfHoldingGHI = new ManagedFundHoldingImpl();
        mfHoldingGHI.setAsset(ghiAsset);
        mfHoldingGHI.setMarketValue(new BigDecimal(10));
        mfHoldingGHI.setAvailableBalance(new BigDecimal(10));
        mfHoldingGHI.setAvailableUnits(new BigDecimal(10));
        mfHoldingGHI.setCost(new BigDecimal(10));
        mfHoldingGHI.setDistributionMethod(DistributionMethod.CASH);
        mfHoldingGHI.setHoldingKey(HoldingKey.valueOf("hid6", "name"));
        mfHoldingGHI.setAccruedIncome(new BigDecimal(10));
        mfHoldingGHI.setRefAsset(null);
        mfHoldingGHI.setUnitPrice(new BigDecimal(10));
        mfHoldingGHI.setUnitPriceDate(new DateTime());
        mfHoldingGHI.setUnits(new BigDecimal(10));
        mfHoldingGHI.setYield(new BigDecimal(10));

        List<AccountHolding> mfHoldings = new ArrayList<>();
        mfHoldings.add(mfHoldingABC1);
        mfHoldings.add(mfHoldingABC2);
        mfHoldings.add(mfHoldingDEF);
        mfHoldings.add(mfHoldingABC3);
        mfHoldings.add(mfHoldingGHI);
        mfHoldings.add(mfHoldingABC4);
        managedFundAccountValuation.addHoldings(mfHoldings);

        filteredAssets.put("92655", abcAsset);
        filteredAssets.put("92654", defAsset);
        availableAssets.put("92655", abcAsset);
        availableAssets.put("92654", defAsset);

        Mockito.when(tradableAssetsDtoServiceFilter.isAssetSellable(Mockito.any(Asset.class), Mockito.any(BigDecimal.class),
                Mockito.any(DateTime.class))).thenAnswer(new Answer<Object>() {
                    @Override
                    public Object answer(InvocationOnMock invocation) {
                        Object[] args = invocation.getArguments();
                        BigDecimal amount = (BigDecimal) args[1];
                        if (amount == BigDecimal.ZERO) {
                            return false;
                        }
                        return true;
                    }
                });

        Mockito.when(
                tradableAssetsDtoServiceHelper.getCombinedAmount(Mockito.any(BigDecimal.class), Mockito.any(BigDecimal.class)))
                .thenAnswer(new Answer<Object>() {
                    @Override
                    public Object answer(InvocationOnMock invocation) {
                        Object[] args = invocation.getArguments();
                        BigDecimal amount1 = (BigDecimal) args[0];
                        BigDecimal amount2 = (BigDecimal) args[1];
                        return amount1.add(amount2);
                    }
                });

        Mockito.when(tradableAssetsDtoServiceFilter.isAssetBuyable(Mockito.any(Asset.class), Mockito.anyBoolean(),
                Mockito.any(DateTime.class))).thenReturn(true);
    }

    /**
     * Test method for
     * {@link com.bt.nextgen.api.asset.service.assetbuilder.ManagedFundAssetBuilder#buildTradeAssets(com.bt.nextgen.service.integration.account.SubAccountValuation, java.util.Map, java.util.Map)}
     * .
     */
    @Test
    public final void testBuildTradeAssets() {

        Map<String, TradeAssetDto> tradedManagedAssetMap = managedFundAssetBuilder.buildTradeAssets(managedFundAccountValuation,
                filteredAssets, availableAssets, assetDistributionMethods, DateTime.now());

        Assert.assertEquals(2, tradedManagedAssetMap.size());

        TradeAssetDto abcAssetDto = tradedManagedAssetMap.get("92655");
        Assert.assertEquals("92655", abcAssetDto.getAsset().getAssetId());
        Assert.assertEquals("Managed fund", abcAssetDto.getAssetTypeDescription());
        Assert.assertEquals(15, abcAssetDto.getAvailableQuantity().doubleValue(), 0.1);
        Assert.assertEquals(49000, abcAssetDto.getBalance().doubleValue(), 0.1);
        Assert.assertEquals(30000, abcAssetDto.getAvailableBalance().doubleValue(), 0.1);
        Assert.assertEquals(true, abcAssetDto.getBuyable());
        Assert.assertEquals(true, abcAssetDto.getSellable());
        Assert.assertEquals("ABC asset", abcAssetDto.getAsset().getAssetName());
        Assert.assertEquals("92655", abcAssetDto.getKey());

        TradeAssetDto defAssetDto = tradedManagedAssetMap.get("92654");
        Assert.assertEquals("92654", defAssetDto.getAsset().getAssetId());
        Assert.assertEquals("Managed fund", defAssetDto.getAssetTypeDescription());
        Assert.assertEquals(110, defAssetDto.getAvailableQuantity().doubleValue(), 0.1);
        Assert.assertEquals(94000, defAssetDto.getBalance().doubleValue(), 0.1);
        Assert.assertEquals(90000, defAssetDto.getAvailableBalance().doubleValue(), 0.1);
        Assert.assertEquals(true, defAssetDto.getBuyable());
        Assert.assertEquals(true, defAssetDto.getSellable());
        Assert.assertEquals("DEF asset", defAssetDto.getAsset().getAssetName());
        Assert.assertEquals("92654", defAssetDto.getKey());

        Assert.assertEquals(availableAssets.isEmpty(), true);
    }

    /**
     * 
     * Test method for
     * {@link com.bt.nextgen.api.asset.service.assetbuilder.ManagedFundAssetBuilder#buildTradeAssetFromAvailabeAsset(com.bt.nextgen.service.integration.asset.Asset)}
     * .
     */
    @Test
    public final void testBuildTradeAssetFromAvailabeAsset() {
        TradeAssetDto tradeAssetDto = managedFundAssetBuilder.buildTradeAssetFromAvailabeAsset(abcAsset, assetDistributionMethods,
                DateTime.now());
        Assert.assertEquals("92655", tradeAssetDto.getAsset().getAssetId());
        Assert.assertEquals("Managed fund", tradeAssetDto.getAssetTypeDescription());
        Assert.assertEquals(null, tradeAssetDto.getAvailableQuantity());
        Assert.assertEquals(null, tradeAssetDto.getBalance());
        Assert.assertEquals(true, tradeAssetDto.getBuyable());
        Assert.assertEquals(false, tradeAssetDto.getSellable());
        Assert.assertEquals("ABC asset", tradeAssetDto.getAsset().getAssetName());
        Assert.assertEquals("92655", tradeAssetDto.getKey());
    }
}

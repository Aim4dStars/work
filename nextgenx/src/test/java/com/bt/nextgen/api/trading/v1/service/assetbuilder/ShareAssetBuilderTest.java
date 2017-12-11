package com.bt.nextgen.api.trading.v1.service.assetbuilder;

import com.bt.nextgen.api.asset.model.ShareAssetDto;
import com.bt.nextgen.api.trading.v1.model.TradeAssetDto;
import com.bt.nextgen.api.trading.v1.util.TradableAssetsDtoServiceFilter;
import com.bt.nextgen.api.trading.v1.util.TradableAssetsDtoServiceHelper;
import com.bt.nextgen.service.avaloq.asset.ShareAssetImpl;
import com.bt.nextgen.service.avaloq.portfolio.valuation.ShareAccountValuationImpl;
import com.bt.nextgen.service.avaloq.portfolio.valuation.ShareHoldingImpl;
import com.bt.nextgen.service.integration.account.DistributionMethod;
import com.bt.nextgen.service.integration.asset.Asset;
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
public class ShareAssetBuilderTest {

    @InjectMocks
    private ShareAssetBuilder shareAssetBuilder;

    @Mock
    private TradableAssetsDtoServiceFilter tradableAssetsDtoServiceFilter;

    @Mock
    private TradableAssetsDtoServiceHelper tradableAssetsDtoServiceHelper;

    private ShareAccountValuationImpl shareAccountValuation;
    private Map<String, Asset> filteredAssets;
    private Map<String, Asset> availableAssets;
    private ShareAssetImpl bhpAsset;
    private ShareAssetImpl wbcAsset;
    private ShareAssetImpl fmlAsset;
    private ShareHoldingImpl shareHoldingBHP1;
    private ShareHoldingImpl shareHoldingBHP2;
    private ShareHoldingImpl shareHoldingBHP3;
    private ShareHoldingImpl shareHoldingBHP4;
    private ShareHoldingImpl shareHoldingWBC;
    private ShareHoldingImpl shareHoldingFML;
    Map<String, List<DistributionMethod>> assetDistributionMethods;

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

        List<AccountHolding> shareList = new ArrayList<>();

        bhpAsset = new ShareAssetImpl();
        bhpAsset.setAssetType(AssetType.SHARE);
        bhpAsset.setAssetId("92655");
        bhpAsset.setAssetName("BHP Billiton");
        bhpAsset.setAssetCode("assetCode1");
        bhpAsset.setPrice(new BigDecimal(10));
        bhpAsset.setHybridType("hybrid_eq");
        bhpAsset.setInvestmentHoldingLimit(BigDecimal.TEN);

        wbcAsset = new ShareAssetImpl();
        wbcAsset.setAssetId("92654");
        wbcAsset.setAssetType(AssetType.SHARE);
        wbcAsset.setAssetName("Westpac Corp");
        wbcAsset.setAssetCode("assetCode2");
        wbcAsset.setPrice(new BigDecimal(20));

        fmlAsset = new ShareAssetImpl();
        fmlAsset.setAssetId("92653");
        fmlAsset.setAssetType(AssetType.SHARE);
        fmlAsset.setAssetName("Fortescue metals");
        fmlAsset.setAssetCode("assetCode3");
        fmlAsset.setPrice(new BigDecimal(30));

        shareHoldingBHP1 = new ShareHoldingImpl();
        shareHoldingBHP1.setAsset(bhpAsset);
        shareHoldingBHP1.setMarketValue(BigDecimal.valueOf(5000));
        shareHoldingBHP1.setAvailableBalance(BigDecimal.ZERO);
        shareHoldingBHP1.setAvailableUnits(BigDecimal.ZERO);
        shareHoldingBHP1.setUnitPrice(BigDecimal.valueOf(1111));
        shareHoldingBHP1.setUnits(BigDecimal.valueOf(11111));
        shareHoldingBHP1.setHoldingKey(HoldingKey.valueOf("holding1", bhpAsset.getAssetName()));

        shareHoldingBHP2 = new ShareHoldingImpl();
        shareHoldingBHP2.setAsset(bhpAsset);
        shareHoldingBHP2.setMarketValue(BigDecimal.valueOf(10000));
        shareHoldingBHP2.setAvailableBalance(BigDecimal.valueOf(10000));
        shareHoldingBHP2.setAvailableUnits(BigDecimal.valueOf(5));
        shareHoldingBHP2.setUnitPrice(BigDecimal.valueOf(1111));
        shareHoldingBHP2.setUnits(BigDecimal.valueOf(11111));
        shareHoldingBHP2.setHoldingKey(HoldingKey.valueOf("holding2", bhpAsset.getAssetName()));

        shareHoldingBHP3 = new ShareHoldingImpl();
        shareHoldingBHP3.setAsset(bhpAsset);
        shareHoldingBHP3.setMarketValue(BigDecimal.valueOf(30000));
        shareHoldingBHP3.setAvailableBalance(BigDecimal.valueOf(20000));
        shareHoldingBHP3.setAvailableUnits(BigDecimal.valueOf(10));
        shareHoldingBHP3.setUnitPrice(BigDecimal.valueOf(1111));
        shareHoldingBHP3.setUnits(BigDecimal.valueOf(11111));
        shareHoldingBHP3.setHoldingKey(HoldingKey.valueOf("holding3", bhpAsset.getAssetName()));

        shareHoldingBHP4 = new ShareHoldingImpl();
        shareHoldingBHP4.setAsset(bhpAsset);
        shareHoldingBHP4.setMarketValue(BigDecimal.valueOf(4000));
        shareHoldingBHP4.setAvailableBalance(BigDecimal.ZERO);
        shareHoldingBHP4.setAvailableUnits(BigDecimal.ZERO);
        shareHoldingBHP4.setUnitPrice(BigDecimal.valueOf(1111));
        shareHoldingBHP4.setUnits(BigDecimal.valueOf(11111));
        shareHoldingBHP4.setHoldingKey(HoldingKey.valueOf("holding4", bhpAsset.getAssetName()));

        shareHoldingWBC = new ShareHoldingImpl();
        shareHoldingWBC.setAsset(wbcAsset);
        shareHoldingWBC.setMarketValue(BigDecimal.valueOf(94000));
        shareHoldingWBC.setAvailableBalance(BigDecimal.valueOf(90000));
        shareHoldingWBC.setAvailableUnits(BigDecimal.valueOf(110));
        shareHoldingWBC.setUnitPrice(BigDecimal.valueOf(1111));
        shareHoldingWBC.setUnits(BigDecimal.valueOf(11111));
        shareHoldingWBC.setHoldingKey(HoldingKey.valueOf("holding5", wbcAsset.getAssetName()));

        shareHoldingFML = new ShareHoldingImpl();
        shareHoldingFML.setAsset(fmlAsset);
        shareHoldingFML.setMarketValue(BigDecimal.valueOf(94000));
        shareHoldingFML.setAvailableBalance(BigDecimal.valueOf(90000));
        shareHoldingFML.setAvailableUnits(BigDecimal.valueOf(110));
        shareHoldingFML.setUnitPrice(BigDecimal.valueOf(1111));
        shareHoldingFML.setUnits(BigDecimal.valueOf(11111));
        shareHoldingFML.setHoldingKey(HoldingKey.valueOf("holding6", fmlAsset.getAssetName()));

        shareList.add(shareHoldingBHP1);
        shareList.add(shareHoldingBHP2);
        shareList.add(shareHoldingWBC);
        shareList.add(shareHoldingBHP3);
        shareList.add(shareHoldingFML);
        shareList.add(shareHoldingBHP4);

        shareAccountValuation = new ShareAccountValuationImpl(AssetType.SHARE);
        shareAccountValuation.addHoldings(shareList);

        filteredAssets.put("92655", bhpAsset);
        filteredAssets.put("92654", wbcAsset);
        availableAssets.put("92655", bhpAsset);
        availableAssets.put("92654", wbcAsset);

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
     * {@link com.bt.nextgen.api.asset.service.assetbuilder.ShareAssetBuilder#buildTradeAssets(com.bt.nextgen.service.integration.account.SubAccountValuation, java.util.Map, java.util.Map)}
     * .
     */
    @Test
    public final void testBuildTradeAssets() {
        Map<String, TradeAssetDto> tradedManagedAssetMap = shareAssetBuilder.buildTradeAssets(shareAccountValuation,
                filteredAssets, availableAssets, assetDistributionMethods, DateTime.now());

        Assert.assertEquals(2, tradedManagedAssetMap.size());

        TradeAssetDto tradeAssetDto = tradedManagedAssetMap.get("92655");
        Assert.assertEquals("92655", tradeAssetDto.getAsset().getAssetId());
        Assert.assertEquals("Listed security", tradeAssetDto.getAssetTypeDescription());
        Assert.assertEquals(15, tradeAssetDto.getAvailableQuantity().doubleValue(), 0.1);
        Assert.assertEquals(49000, tradeAssetDto.getBalance().doubleValue(), 0.1);
        Assert.assertEquals(30000, tradeAssetDto.getAvailableBalance().doubleValue(), 0.1);
        Assert.assertEquals(true, tradeAssetDto.getBuyable());
        Assert.assertEquals(true, tradeAssetDto.getSellable());
        Assert.assertEquals("BHP Billiton", tradeAssetDto.getAsset().getAssetName());
        Assert.assertEquals("92655", tradeAssetDto.getKey());
        Assert.assertEquals(10, ((ShareAssetDto) tradeAssetDto.getAsset()).getPrice().doubleValue(), 1);
        Assert.assertEquals("hybrid_eq", ((ShareAssetDto) tradeAssetDto.getAsset()).getHybridType());

        TradeAssetDto wbcAssetDto = tradedManagedAssetMap.get("92654");
        Assert.assertEquals("92654", wbcAssetDto.getAsset().getAssetId());
        Assert.assertEquals("Listed security", wbcAssetDto.getAssetTypeDescription());
        Assert.assertEquals(110, wbcAssetDto.getAvailableQuantity().doubleValue(), 0.1);
        Assert.assertEquals(94000, wbcAssetDto.getBalance().doubleValue(), 0.1);
        Assert.assertEquals(90000, wbcAssetDto.getAvailableBalance().doubleValue(), 0.1);
        Assert.assertEquals(true, wbcAssetDto.getBuyable());
        Assert.assertEquals(true, wbcAssetDto.getSellable());
        Assert.assertEquals("Westpac Corp", wbcAssetDto.getAsset().getAssetName());
        Assert.assertEquals("92654", wbcAssetDto.getKey());
        Assert.assertEquals(20, ((ShareAssetDto) wbcAssetDto.getAsset()).getPrice().doubleValue(), 1);
        Assert.assertEquals(null, ((ShareAssetDto) wbcAssetDto.getAsset()).getHybridType());

        Assert.assertEquals(availableAssets.isEmpty(), true);
    }

    /**
     * 
     * Test method for
     * {@link com.bt.nextgen.api.asset.service.assetbuilder.ShareAssetBuilder#buildTradeAssetFromAvailabeAsset(com.bt.nextgen.service.integration.asset.Asset)}
     * .
     */
    @Test
    public final void testBuildTradeAssetFromAvailabeAsset() {
        TradeAssetDto tradeAssetDto = shareAssetBuilder.buildTradeAssetFromAvailabeAsset(bhpAsset, assetDistributionMethods,
                DateTime.now());
        Assert.assertEquals("92655", tradeAssetDto.getAsset().getAssetId());
        Assert.assertEquals("Listed security", tradeAssetDto.getAssetTypeDescription());
        Assert.assertEquals(null, tradeAssetDto.getAvailableQuantity());
        Assert.assertEquals(null, tradeAssetDto.getBalance());
        Assert.assertEquals(null, tradeAssetDto.getAvailableBalance());
        Assert.assertEquals(true, tradeAssetDto.getBuyable());
        Assert.assertEquals(false, tradeAssetDto.getSellable());
        Assert.assertEquals("BHP Billiton", tradeAssetDto.getAsset().getAssetName());
        Assert.assertEquals("92655", tradeAssetDto.getKey());
        Assert.assertEquals(10, ((ShareAssetDto) tradeAssetDto.getAsset()).getPrice().doubleValue(), 1);
        Assert.assertEquals("hybrid_eq", ((ShareAssetDto) tradeAssetDto.getAsset()).getHybridType());
    }
}

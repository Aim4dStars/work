package com.bt.nextgen.api.trading.v1.service.assetbuilder;

import com.bt.nextgen.api.trading.v1.model.TradeAssetDto;
import com.bt.nextgen.api.trading.v1.util.TradableAssetsDtoServiceFilter;
import com.bt.nextgen.service.avaloq.asset.AssetImpl;
import com.bt.nextgen.service.avaloq.portfolio.valuation.ManagedPortfolioAccountValuationImpl;
import com.bt.nextgen.service.integration.account.DistributionMethod;
import com.bt.nextgen.service.integration.account.SubAccountKey;
import com.bt.nextgen.service.integration.asset.Asset;
import com.btfin.panorama.service.integration.asset.AssetType;
import com.bt.nextgen.service.integration.ips.InvestmentPolicyStatementInterface;
import com.bt.nextgen.service.integration.ips.IpsKey;
import com.bt.nextgen.service.integration.portfolio.valuation.AccountHolding;
import org.joda.time.DateTime;
import org.junit.Assert;
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

@RunWith(MockitoJUnitRunner.class)
public class ManagedPortfolioAssetBuilderTest {

    @InjectMocks
    private ManagedPortfolioAssetBuilder managedPortfolioAssetBuilder;

    @Mock
    private TradableAssetsDtoServiceFilter tradableAssetsDtoServiceFilter;

    private ManagedPortfolioAccountValuationImpl managedPortfolioAccountValuation;
    private DateTime testDate;
    private Map<String, Asset> filteredAssets;
    private Map<String, Asset> availableAssets;
    private AssetImpl mpAsset;
    private BigDecimal accountBalance;

    @Before
    public void setUp() throws Exception {

        List<DistributionMethod> availableMethods = new ArrayList<>();
        availableMethods.add(DistributionMethod.CASH);
        availableMethods.add(DistributionMethod.REINVEST);

        filteredAssets = new HashMap<String, Asset>();
        availableAssets = new HashMap<String, Asset>();
        testDate = new DateTime();
        List<AccountHolding> holdings = new ArrayList<>();

        mpAsset = new AssetImpl();
        mpAsset.setAssetCode("assetCode");
        mpAsset.setAssetId("92655");
        mpAsset.setAssetName("AMP Capital Investors International Bond");
        mpAsset.setAssetType(AssetType.MANAGED_PORTFOLIO);
        mpAsset.setBrand("brand");
        mpAsset.setIndustrySector("industrySector");
        mpAsset.setIndustryType("industryType");

        AssetImpl prepaymentAsset = new AssetImpl();
        prepaymentAsset.setAssetCode("prepayAssetCode");
        prepaymentAsset.setAssetId("assetId1");
        prepaymentAsset.setAssetName("assetName1");
        prepaymentAsset.setAssetType(AssetType.SHARE);
        prepaymentAsset.setBrand("brand1");
        prepaymentAsset.setIndustrySector("industrySector");
        prepaymentAsset.setIndustryType("industryType");
        prepaymentAsset.setMoneyAccountType("Cash Claim Account");

        managedPortfolioAccountValuation = new ManagedPortfolioAccountValuationImpl();
        managedPortfolioAccountValuation.setAsset(mpAsset);
        managedPortfolioAccountValuation.setSubAccountKey(SubAccountKey.valueOf("accountId"));
        managedPortfolioAccountValuation.addHoldings(holdings);

        accountBalance = BigDecimal.valueOf(20000);

        AssetImpl mpAsset1 = new AssetImpl();
        mpAsset1.setAssetCode("assetCode");
        mpAsset1.setAssetId("92654");
        mpAsset1.setAssetName("new asset name");
        mpAsset1.setAssetType(AssetType.SHARE);
        mpAsset1.setBrand("brand");
        mpAsset1.setIndustrySector("industrySector");
        mpAsset1.setIndustryType("industryType");

        filteredAssets.put("92655", mpAsset);
        filteredAssets.put("92654", mpAsset1);
        availableAssets.put("92655", mpAsset);

        Mockito.when(tradableAssetsDtoServiceFilter.isAssetSellable(Mockito.any(Asset.class), Mockito.any(BigDecimal.class),
                Mockito.any(DateTime.class))).thenReturn(true);
        Mockito.when(tradableAssetsDtoServiceFilter.isAssetBuyable(Mockito.any(Asset.class), Mockito.anyBoolean(),
                Mockito.any(DateTime.class))).thenReturn(true);
    }

    /**
     * Test method for
     * {@link com.bt.nextgen.api.asset.service.assetbuilder.ManagedPortfolioAssetBuilder#buildTradeAssets(com.bt.nextgen.service.integration.account.SubAccountValuation, java.util.Map, java.util.Map)}
     * .
     */
    @Test
    public final void testBuildTradeAssets() {

        Map<String, TradeAssetDto> tradedManagedAssetMap = managedPortfolioAssetBuilder
                .buildTradeAssets(managedPortfolioAccountValuation, filteredAssets, availableAssets, DateTime.now());

        Assert.assertNotNull(tradedManagedAssetMap);
        Assert.assertNotNull(tradedManagedAssetMap.values());
        for (TradeAssetDto tradeAssetDto : tradedManagedAssetMap.values()) {
            Assert.assertEquals("92655", tradeAssetDto.getAsset().getAssetId());
            Assert.assertEquals("Managed portfolio", tradeAssetDto.getAssetTypeDescription());
            Assert.assertEquals(null, tradeAssetDto.getAvailableQuantity());
            Assert.assertEquals(0, tradeAssetDto.getBalance().doubleValue(), 0.1);
            Assert.assertEquals(true, tradeAssetDto.getBuyable());
            Assert.assertEquals(true, tradeAssetDto.getSellable());
            Assert.assertEquals("AMP Capital Investors International Bond", tradeAssetDto.getAsset().getAssetName());
            Assert.assertEquals("92655", tradeAssetDto.getKey());
        }

        Assert.assertEquals(availableAssets.isEmpty(), true);
    }

    /**
     * 
     * Test method for
     * {@link com.bt.nextgen.api.asset.service.assetbuilder.ManagedPortfolioAssetBuilder#buildTradeAssetFromAvailabeAsset(com.bt.nextgen.service.integration.asset.Asset)}
     * .
     */
    @Test
    public final void testBuildTradeAsset() {
        Map<IpsKey, InvestmentPolicyStatementInterface> ipsMap = new HashMap<>();
        InvestmentPolicyStatementInterface investmentPolicyStatements = Mockito.mock(InvestmentPolicyStatementInterface.class);
        Mockito.when(investmentPolicyStatements.getTaxAssetDomicile()).thenReturn(Boolean.TRUE);
        Mockito.when(investmentPolicyStatements.getIpsKey()).thenReturn(IpsKey.valueOf("123"));
        mpAsset.setIpsId("123");
        ipsMap.put(IpsKey.valueOf("123"), investmentPolicyStatements);
        TradeAssetDto tradeAssetDto = managedPortfolioAssetBuilder.buildTradeAssetFromAvailableAsset(mpAsset, ipsMap, DateTime.now());

        Assert.assertEquals("92655", tradeAssetDto.getAsset().getAssetId());
        Assert.assertEquals("Managed portfolio", tradeAssetDto.getAssetTypeDescription());
        Assert.assertEquals(null, tradeAssetDto.getAvailableQuantity());
        Assert.assertEquals(null, tradeAssetDto.getBalance());
        Assert.assertEquals(true, tradeAssetDto.getBuyable());
        Assert.assertEquals(false, tradeAssetDto.getSellable());
        Assert.assertEquals("AMP Capital Investors International Bond", tradeAssetDto.getAsset().getAssetName());
        Assert.assertEquals("92655", tradeAssetDto.getKey());
    }
}

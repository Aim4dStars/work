package com.bt.nextgen.api.account.v2.service.valuation;

import com.bt.nextgen.api.account.v2.model.InvestmentAssetDto;
import com.bt.nextgen.api.account.v2.model.InvestmentValuationDto;
import com.bt.nextgen.api.account.v2.model.ManagedPortfolioValuationDto;
import com.bt.nextgen.service.avaloq.asset.AssetImpl;
import com.bt.nextgen.service.avaloq.portfolio.valuation.ManagedFundHoldingImpl;
import com.bt.nextgen.service.avaloq.portfolio.valuation.ManagedPortfolioAccountValuationImpl;
import com.bt.nextgen.service.integration.account.SubAccountKey;
import com.bt.nextgen.service.integration.asset.AssetClass;
import com.btfin.panorama.service.integration.asset.AssetType;
import com.bt.nextgen.service.integration.portfolio.valuation.AccountHolding;
import com.bt.nextgen.service.integration.portfolio.valuation.HoldingKey;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class MPValuationAggregatorTest {

    @InjectMocks
    public MPValuationAggregator mpValuationAggregator;

    private DateTime testDate;
    private ManagedPortfolioAccountValuationImpl subAccount;
    private BigDecimal accountBalance;

    @Before
    public void setup() {

        testDate = new DateTime();

        List<AccountHolding> holdings = new ArrayList<>();

        AssetImpl mpAsset = new AssetImpl();
        mpAsset.setAssetClass(AssetClass.AUSTRALIAN_SHARES);
        mpAsset.setAssetCode("mpAssetCode");
        mpAsset.setAssetId("mpAssetId");
        mpAsset.setAssetName("mpAssetName");
        mpAsset.setAssetType(AssetType.MANAGED_PORTFOLIO);
        mpAsset.setBrand("brand");
        mpAsset.setIndustrySector("industrySector");
        mpAsset.setIndustryType("industryType");

        AssetImpl asset = new AssetImpl();
        asset.setAssetClass(AssetClass.AUSTRALIAN_SHARES);
        asset.setAssetCode("assetCode");
        asset.setAssetId("assetId");
        asset.setAssetName("assetName");
        asset.setAssetType(AssetType.SHARE);
        asset.setBrand("brand");
        asset.setIndustrySector("industrySector");
        asset.setIndustryType("industryType");

        AssetImpl prepaymentAsset = new AssetImpl();
        prepaymentAsset.setAssetClass(AssetClass.AUSTRALIAN_SHARES);
        prepaymentAsset.setAssetCode("prepayAssetCode");
        prepaymentAsset.setAssetId("assetId1");
        prepaymentAsset.setAssetName("assetName1");
        prepaymentAsset.setAssetType(AssetType.SHARE);
        prepaymentAsset.setBrand("brand1");
        prepaymentAsset.setIndustrySector("industrySector");
        prepaymentAsset.setIndustryType("industryType");
        prepaymentAsset.setMoneyAccountType("Cash Claim Account");

        ManagedFundHoldingImpl holding = new ManagedFundHoldingImpl();

        holding.setAsset(asset);
        holding.setAvailableUnits(BigDecimal.valueOf(1));
        holding.setCost(BigDecimal.valueOf(2));
        holding.setUnitPrice(BigDecimal.valueOf(3));
        holding.setUnitPriceDate(testDate);
        holding.setUnits(BigDecimal.valueOf(4));
        holding.setYield(BigDecimal.valueOf(5));
        holding.setMarketValue(BigDecimal.valueOf(11.99));
        holding.setAccruedIncome(BigDecimal.valueOf(0.01));
        holding.setRefAsset(prepaymentAsset);
        holding.setHasPending(true);
        holding.setHoldingKey(HoldingKey.valueOf("holding1", "holdingKeyName"));
        holdings.add(holding);


        subAccount = new ManagedPortfolioAccountValuationImpl();
        subAccount.setSubAccountKey(SubAccountKey.valueOf("accountId"));
        subAccount.setAsset(mpAsset);
        subAccount.addHoldings(holdings);

        accountBalance = BigDecimal.valueOf(20000);
    }

    @Test
    public void testBuildValuationDto_whenMPSubAccountPassed_thenMPValuationDtosCreated() {

        List<InvestmentValuationDto> dtos = mpValuationAggregator.getManagedPortfolioValuationDto(
                subAccount, accountBalance);

        Assert.assertNotNull(dtos);
        Assert.assertEquals(1, dtos.size());

        ManagedPortfolioValuationDto dto = (ManagedPortfolioValuationDto) dtos.get(0);

        InvestmentAssetDto investment = dto.getInvestmentAssets().get(0);
        Assert.assertEquals("assetId", investment.getAssetId());
        Assert.assertEquals("holdingKeyName", investment.getAssetName());
        Assert.assertEquals(AssetType.SHARE.name(), investment.getAssetType());
        Assert.assertEquals(testDate, investment.getEffectiveDate());
        Assert.assertEquals(Boolean.TRUE, investment.getHasPending());
        Assert.assertEquals(Boolean.TRUE, investment.isPrepaymentAsset());

        BigDecimal valueMinusCost = BigDecimal.valueOf(11.99).subtract(BigDecimal.valueOf(2));

        Assert.assertEquals(valueMinusCost, investment.getDollarGain());
        Assert.assertEquals(5, investment.getPercentGain().doubleValue(), 0.005);
        Assert.assertEquals(BigDecimal.valueOf(4), investment.getQuantity());
        Assert.assertEquals(BigDecimal.valueOf(3), investment.getUnitPrice());
        Assert.assertEquals(BigDecimal.valueOf(2), investment.getAverageCost());
        Assert.assertEquals("Managed portfolios", dto.getCategoryName());  
    }

}

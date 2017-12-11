package com.bt.nextgen.api.portfolio.v3.service.valuation;

import com.bt.nextgen.api.portfolio.v3.model.valuation.InvestmentValuationDto;
import com.bt.nextgen.api.portfolio.v3.model.valuation.QuantisedAssetValuationDto;
import com.bt.nextgen.service.avaloq.asset.AssetImpl;
import com.bt.nextgen.service.avaloq.portfolio.valuation.OtherAccountValuationImpl;
import com.bt.nextgen.service.avaloq.portfolio.valuation.OtherHoldingImpl;
import com.bt.nextgen.service.integration.asset.AssetClass;
import com.btfin.panorama.service.integration.asset.AssetType;
import com.bt.nextgen.service.integration.portfolio.valuation.AccountHolding;
import com.bt.nextgen.service.integration.portfolio.valuation.HoldingKey;
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
public class OtherValuationAggregatorTest {

    @InjectMocks
    public OtherValuationAggregator otherValuationAggregator;

    private OtherAccountValuationImpl subAccount;
    private BigDecimal accountBalance;

    @Before
    public void setup() {

        subAccount = new OtherAccountValuationImpl();
        OtherHoldingImpl otherHolding = new OtherHoldingImpl();
        otherHolding.setHoldingKey(HoldingKey.valueOf("123", "BT Cash"));
        otherHolding.setAvailableBalance(BigDecimal.valueOf(2000));
        otherHolding.setMarketValue(BigDecimal.valueOf(4000));
        otherHolding.setAccruedIncome(BigDecimal.valueOf(100));
        otherHolding.setYield(BigDecimal.valueOf(0.1));
        otherHolding.setUnits(BigDecimal.valueOf(5));
        otherHolding.setAsset(getAsset());
        List<AccountHolding> holdingList = new ArrayList<>();
        holdingList.add(otherHolding);
        subAccount.addHoldings(holdingList);

        accountBalance = new BigDecimal(20000);
    }

    @Test
    public void testBuildValuationDto_whenOtherTypeSubAccountPassed_thenOtherValuationDtoCreated() {

        List<InvestmentValuationDto> dtos = otherValuationAggregator.getOtherValuationDto(subAccount.getHoldings(),
                accountBalance, false);

        Assert.assertNotNull(dtos.get(0));

        Assert.assertEquals(BigDecimal.valueOf(4000), dtos.get(0).getBalance());
        Assert.assertEquals(0.2, dtos.get(0).getPortfolioPercent().doubleValue(), 0.005);
        Assert.assertEquals("Other assets", dtos.get(0).getCategoryName());
    }

    @Test
    public void testBuildValuationDto_whenQuantisedTypeSubAccountPassed_thenQuantisedValuationDtoCreated() {

        List<InvestmentValuationDto> dtos = otherValuationAggregator.getOtherValuationDto(subAccount.getHoldings(),
                accountBalance, true);

        Assert.assertNotNull(dtos.get(0));

        Assert.assertEquals(BigDecimal.valueOf(4000), dtos.get(0).getBalance());
        Assert.assertEquals(0.2, dtos.get(0).getPortfolioPercent().doubleValue(), 0.005);
        Assert.assertEquals("Other assets", dtos.get(0).getCategoryName());
        Assert.assertEquals(BigDecimal.valueOf(5), ((QuantisedAssetValuationDto) dtos.get(0)).getUnits());
    }

    private AssetImpl getAsset() {
        AssetImpl asset = new AssetImpl();
        asset.setAssetClass(AssetClass.CASH);
        asset.setAssetCode("assetCode");
        asset.setAssetId("otherAsset");
        asset.setAssetName("BT Cash");
        asset.setAssetType(AssetType.OTHER);
        asset.setBrand("brand");
        asset.setIndustrySector("industrySector");
        asset.setIndustryType("industryType");
        return asset;
    }
}

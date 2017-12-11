package com.bt.nextgen.api.portfolio.v3.service.valuation;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.bt.nextgen.api.portfolio.v3.model.InvestmentAssetDto;
import com.bt.nextgen.api.portfolio.v3.model.valuation.InvestmentValuationDto;
import com.bt.nextgen.api.portfolio.v3.model.valuation.ManagedPortfolioValuationDto;
import com.bt.nextgen.service.integration.account.SubAccountKey;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetClass;
import com.bt.nextgen.service.integration.portfolio.valuation.AccountHolding;
import com.bt.nextgen.service.integration.portfolio.valuation.HoldingKey;
import com.bt.nextgen.service.integration.portfolio.valuation.ManagedPortfolioAccountValuation;
import com.bt.nextgen.service.integration.portfolio.valuation.ManagedPortfolioStatus;
import com.bt.nextgen.service.integration.portfolio.valuation.ShareHolding;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.btfin.panorama.service.integration.asset.AssetType;

@RunWith(MockitoJUnitRunner.class)
public class MPValuationAggregatorTest {

    @InjectMocks
    public MPValuationAggregator mpValuationAggregator;

    private DateTime testDate;
    private ManagedPortfolioAccountValuation subAccount;
    private BigDecimal accountBalance;
    private Asset mpAsset, asset;
    private ShareHolding holding;

    @Before
    public void setup() {

        testDate = new DateTime();

        mpAsset = Mockito.mock(Asset.class);
        Mockito.when(mpAsset.getAssetId()).thenReturn("mpAssetId");
        Mockito.when(mpAsset.getAssetCode()).thenReturn("mpAssetCode");
        Mockito.when(mpAsset.getAssetName()).thenReturn("mpAssetName");
        Mockito.when(mpAsset.getAssetType()).thenReturn(AssetType.MANAGED_PORTFOLIO);

        asset = Mockito.mock(Asset.class);
        Mockito.when(asset.getAssetId()).thenReturn("assetId");
        Mockito.when(asset.getAssetCode()).thenReturn("assetCode");
        Mockito.when(asset.getAssetName()).thenReturn("assetName");
        Mockito.when(asset.getAssetClass()).thenReturn(AssetClass.AUSTRALIAN_SHARES);
        Mockito.when(asset.getAssetType()).thenReturn(AssetType.SHARE);
        Mockito.when(asset.getBrand()).thenReturn("brand");
        Mockito.when(asset.getIsin()).thenReturn("isin");
        Mockito.when(asset.getIndustrySector()).thenReturn("industrySector");
        Mockito.when(asset.getIndustryType()).thenReturn("industryType");

        Asset prepaymentAsset = Mockito.mock(Asset.class);
        Mockito.when(prepaymentAsset.getAssetId()).thenReturn("prepayAssetId");
        Mockito.when(prepaymentAsset.getAssetCode()).thenReturn("prepayAssetCode");
        Mockito.when(prepaymentAsset.getAssetName()).thenReturn("prepayAssetName");
        Mockito.when(prepaymentAsset.getAssetClass()).thenReturn(AssetClass.AUSTRALIAN_SHARES);
        Mockito.when(prepaymentAsset.getAssetType()).thenReturn(AssetType.SHARE);
        Mockito.when(prepaymentAsset.getBrand()).thenReturn("prepayBrand");
        Mockito.when(prepaymentAsset.getIndustrySector()).thenReturn("industrySector");
        Mockito.when(prepaymentAsset.getIndustryType()).thenReturn("industryType");
        Mockito.when(prepaymentAsset.getMoneyAccountType()).thenReturn("Cash Claim Account");

        holding = Mockito.mock(ShareHolding.class);
        Mockito.when(holding.getAsset()).thenReturn(asset);
        Mockito.when(holding.getAvailableUnits()).thenReturn(BigDecimal.ONE);
        Mockito.when(holding.getCost()).thenReturn(BigDecimal.valueOf(2));
        Mockito.when(holding.getUnitPrice()).thenReturn(BigDecimal.valueOf(3));
        Mockito.when(holding.getUnitPriceDate()).thenReturn(testDate);
        Mockito.when(holding.getUnits()).thenReturn(BigDecimal.valueOf(4));
        Mockito.when(holding.getYield()).thenReturn(BigDecimal.valueOf(5));
        Mockito.when(holding.getEstdGainDollar()).thenReturn(BigDecimal.valueOf(100));
        Mockito.when(holding.getEstdGainPercent()).thenReturn(BigDecimal.valueOf(5));
        Mockito.when(holding.getMarketValue()).thenReturn(BigDecimal.valueOf(11.99));
        Mockito.when(holding.getAccruedIncome()).thenReturn(BigDecimal.valueOf(0.01));
        Mockito.when(holding.getReferenceAsset()).thenReturn(prepaymentAsset);
        Mockito.when(holding.getHasPending()).thenReturn(Boolean.TRUE);
        Mockito.when(holding.getHoldingKey()).thenReturn(HoldingKey.valueOf("holding", "holdingKeyName"));

        List<AccountHolding> holdings = new ArrayList<>();
        holdings.add(holding);

        subAccount = Mockito.mock(ManagedPortfolioAccountValuation.class);
        Mockito.when(subAccount.getSubAccountKey()).thenReturn(SubAccountKey.valueOf("accountId"));
        Mockito.when(subAccount.getStatus()).thenReturn(ManagedPortfolioStatus.OPEN);
        Mockito.when(subAccount.getAsset()).thenReturn(mpAsset);
        Mockito.when(subAccount.getMarketValue()).thenReturn(BigDecimal.valueOf(11.99));
        Mockito.when(subAccount.getCost()).thenReturn(BigDecimal.valueOf(2));
        Mockito.when(subAccount.getHoldings()).thenReturn(holdings);

        accountBalance = BigDecimal.valueOf(20000);
    }

    @Test
    public void testBuildValuationDto_whenMPSubAccountPassed_thenMPValuationDtosCreated() {

        List<InvestmentValuationDto> dtos = mpValuationAggregator.getManagedPortfolioValuationDto(subAccount, accountBalance);

        Assert.assertNotNull(dtos);
        Assert.assertEquals(1, dtos.size());

        ManagedPortfolioValuationDto dto = (ManagedPortfolioValuationDto) dtos.get(0);

        Assert.assertEquals("accountId", EncodedString.toPlainText(dto.getSubAccountId()));
        Assert.assertEquals("Managed portfolios", dto.getCategoryName());
        Assert.assertEquals("mpAssetId", dto.getAssetId());
        Assert.assertEquals("mpAssetCode", dto.getAssetCode());
        Assert.assertEquals("mpAssetName", dto.getName());
        Assert.assertEquals(BigDecimal.valueOf(2), dto.getCost());
        Assert.assertEquals(BigDecimal.valueOf(100), dto.getCapgainDollar());
        Assert.assertEquals(BigDecimal.valueOf(0.01), dto.getDividend());
        Assert.assertEquals(BigDecimal.ZERO, dto.getDistribution());
        Assert.assertEquals(BigDecimal.ZERO, dto.getInterestPaid());
        Assert.assertEquals(Boolean.FALSE, dto.getIncomeOnly());
        Assert.assertEquals(Boolean.FALSE, dto.getTailorMade());
        Assert.assertEquals(Boolean.TRUE, dto.getHasPending());
        Assert.assertEquals(Boolean.FALSE, dto.getPendingClosure());
        Assert.assertEquals(Boolean.FALSE, dto.getPendingSellDown());

        InvestmentAssetDto investment = dto.getInvestmentAssets().get(0);

        Assert.assertEquals("assetId", investment.getAssetId());
        Assert.assertEquals("assetCode", investment.getAssetCode());
        Assert.assertEquals("assetName", investment.getAssetName());
        Assert.assertEquals("isin", investment.getIsin());
        Assert.assertEquals("", investment.getStatus());
        Assert.assertEquals(AssetType.SHARE.name(), investment.getAssetType());
        Assert.assertEquals(testDate, investment.getEffectiveDate());
        Assert.assertEquals(Boolean.TRUE, investment.getHasPending());
        Assert.assertEquals(Boolean.TRUE, investment.isPrepaymentAsset());
        Assert.assertEquals(BigDecimal.valueOf(100), investment.getDollarGain());
        Assert.assertEquals(0.05, investment.getPercentGain().doubleValue(), 0.005);
        Assert.assertEquals(BigDecimal.valueOf(4), investment.getQuantity());
        Assert.assertEquals(BigDecimal.valueOf(3), investment.getUnitPrice());
        Assert.assertEquals(BigDecimal.valueOf(2), investment.getAverageCost());
        Assert.assertEquals(BigDecimal.valueOf(1), investment.getAvailableQuantity());
        Assert.assertEquals(Boolean.FALSE, investment.getIncomeOnly());
        Assert.assertEquals(BigDecimal.valueOf(11.99), investment.getMarketValue());

    }

    @Test
    public void testBuildValuationDto_whenExternalMPAccountPassed_thenMPValuationDtosCreated() {

        ShareHolding extHolding = Mockito.mock(ShareHolding.class);
        Mockito.when(extHolding.getAsset()).thenReturn(asset);
        Mockito.when(extHolding.getAvailableUnits()).thenReturn(BigDecimal.ONE);
        Mockito.when(extHolding.getCost()).thenReturn(BigDecimal.valueOf(2));
        Mockito.when(extHolding.getUnitPrice()).thenReturn(BigDecimal.valueOf(3));
        Mockito.when(extHolding.getUnitPriceDate()).thenReturn(testDate);
        Mockito.when(extHolding.getUnits()).thenReturn(BigDecimal.valueOf(4));
        Mockito.when(extHolding.getYield()).thenReturn(BigDecimal.valueOf(5));
        Mockito.when(extHolding.getEstdGainDollar()).thenReturn(BigDecimal.valueOf(100));
        Mockito.when(extHolding.getEstdGainPercent()).thenReturn(BigDecimal.valueOf(5));
        Mockito.when(extHolding.getMarketValue()).thenReturn(BigDecimal.valueOf(11.99));
        Mockito.when(extHolding.getAccruedIncome()).thenReturn(BigDecimal.valueOf(0.01));
        Mockito.when(extHolding.getHasPending()).thenReturn(Boolean.TRUE);
        Mockito.when(extHolding.getExternal()).thenReturn(Boolean.TRUE);
        Mockito.when(extHolding.getHoldingKey()).thenReturn(HoldingKey.valueOf("holding", "holdingKeyName"));

        List<AccountHolding> extHoldings = new ArrayList<>();
        extHoldings.add(extHolding);

        ManagedPortfolioAccountValuation extSubAccount = Mockito.mock(ManagedPortfolioAccountValuation.class);
        Mockito.when(extSubAccount.getSubAccountKey()).thenReturn(SubAccountKey.valueOf("accountId"));
        Mockito.when(extSubAccount.getStatus()).thenReturn(ManagedPortfolioStatus.OPEN);
        Mockito.when(extSubAccount.getAsset()).thenReturn(mpAsset);
        Mockito.when(extSubAccount.getMarketValue()).thenReturn(BigDecimal.valueOf(11.99));
        Mockito.when(extSubAccount.getCost()).thenReturn(BigDecimal.valueOf(2));
        Mockito.when(extSubAccount.getHoldings()).thenReturn(extHoldings);
        List<InvestmentValuationDto> dtos = mpValuationAggregator.getManagedPortfolioValuationDto(extSubAccount, accountBalance);

        Assert.assertNotNull(dtos);
        Assert.assertEquals(1, dtos.size());

        ManagedPortfolioValuationDto dto = (ManagedPortfolioValuationDto) dtos.get(0);

        Assert.assertEquals("Managed portfolios", dto.getCategoryName());
        Assert.assertEquals("assetId", dto.getAssetId());
        Assert.assertEquals("assetCode", dto.getAssetCode());
        Assert.assertEquals("assetName", dto.getName());
        Assert.assertNull(dto.getIncomePreference());
    }

}

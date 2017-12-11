package com.bt.nextgen.api.account.v2.service.valuation;

import com.bt.nextgen.api.account.v2.model.CashManagementValuationDto;
import com.bt.nextgen.api.account.v2.model.InvestmentValuationDto;
import com.bt.nextgen.service.avaloq.asset.AssetImpl;
import com.bt.nextgen.service.avaloq.portfolio.valuation.CashAccountValuationImpl;
import com.bt.nextgen.service.avaloq.portfolio.valuation.CashHoldingImpl;
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
public class CashValuationAggregatorTest {

    @InjectMocks
    public CashValuationAggregator cashValuationAggregator;

    private CashAccountValuationImpl subAccount;
    private CashAccountValuationImpl negSubAccount;
    private BigDecimal accountBalance;

    @Before
    public void setup() {

        subAccount = new CashAccountValuationImpl();
        CashHoldingImpl cashHolding = new CashHoldingImpl();
        cashHolding.setHoldingKey(HoldingKey.valueOf("123", "BT Cash"));
        cashHolding.setAccountName("BT Cash");
        cashHolding.setAvailableBalance(BigDecimal.valueOf(2000));
        cashHolding.setMarketValue(BigDecimal.valueOf(4000));
        cashHolding.setAccruedIncome(BigDecimal.valueOf(100));
        cashHolding.setYield(BigDecimal.valueOf(0.1));
        cashHolding.setAsset(getCashAsset());
        cashHolding.setAsset(getCashAsset());
        List<AccountHolding> cashList = new ArrayList<>();
        cashList.add(cashHolding);
        subAccount.addHoldings(cashList);

        negSubAccount = new CashAccountValuationImpl();
        cashHolding = new CashHoldingImpl();
        cashHolding.setHoldingKey(HoldingKey.valueOf("456", "BT Cash"));
        cashHolding.setAccountName("BT Cash");
        cashHolding.setAvailableBalance(BigDecimal.valueOf(-400));
        cashHolding.setMarketValue(BigDecimal.valueOf(1000));
        cashHolding.setAccruedIncome(BigDecimal.valueOf(100));
        cashHolding.setYield(BigDecimal.valueOf(0.1));
        cashHolding.setAsset(getCashAsset());
        cashList = new ArrayList<>();
        cashList.add(cashHolding);
        negSubAccount.addHoldings(cashList);
        accountBalance = BigDecimal.valueOf(20000);
    }

    @Test
    public void testBuildValuationDto_whenCashTypeSubAccountPassed_thenCashValuationDtoCreated() {

        CashManagementValuationDto dto = (CashManagementValuationDto) cashValuationAggregator.getCashValuationDtos(subAccount,
                accountBalance).get(0);

        Assert.assertNotNull(dto);

        Assert.assertEquals(BigDecimal.valueOf(4000), dto.getBalance());
        Assert.assertEquals(BigDecimal.valueOf(2000), dto.getAvailableBalance());
        Assert.assertEquals(0.2, dto.getPortfolioPercent().doubleValue(), 0.005);
        Assert.assertEquals(BigDecimal.valueOf(0.001), dto.getInterestRate());        
        Assert.assertEquals(BigDecimal.valueOf(100), dto.getIncome());
        Assert.assertEquals("Cash", dto.getCategoryName());        
    }

    @Test
    public void testBuildValuationDto_whenCashSubAccountWithNegativeBalancePassed_thenOtherValuationDtoCreated() {

        InvestmentValuationDto dto = cashValuationAggregator.getCashValuationDtos(negSubAccount, accountBalance).get(0);

        Assert.assertNotNull(dto);
        Assert.assertEquals(BigDecimal.valueOf(1000), dto.getBalance());
        Assert.assertEquals(BigDecimal.ZERO, dto.getAvailableBalance());
        Assert.assertEquals(0.05, dto.getPortfolioPercent().doubleValue(), 0.005);
    }

    private AssetImpl getCashAsset() {
        AssetImpl asset = new AssetImpl();
        asset.setAssetClass(AssetClass.CASH);
        asset.setAssetCode("assetCode");
        asset.setAssetId("cashAsset");
        asset.setAssetName("BT Cash");
        asset.setAssetType(AssetType.CASH);
        asset.setBrand("brand");
        asset.setIndustrySector("industrySector");
        asset.setIndustryType("industryType");
        return asset;
    }

}

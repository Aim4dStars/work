package com.bt.nextgen.api.portfolio.v3.service.valuation;

import com.bt.nextgen.api.portfolio.v3.model.valuation.CashManagementValuationDto;
import com.bt.nextgen.api.portfolio.v3.model.valuation.InvestmentValuationDto;
import com.bt.nextgen.service.avaloq.asset.AssetImpl;
import com.bt.nextgen.service.avaloq.portfolio.valuation.CashAccountValuationImpl;
import com.bt.nextgen.service.avaloq.portfolio.valuation.CashHoldingImpl;
import com.bt.nextgen.service.integration.asset.AssetClass;
import com.bt.nextgen.service.integration.base.SystemType;
import com.bt.nextgen.service.integration.portfolio.valuation.AccountHolding;
import com.bt.nextgen.service.integration.portfolio.valuation.HoldingKey;
import com.bt.nextgen.service.wrap.integration.portfolio.WrapCashHoldingImpl;
import com.btfin.panorama.service.integration.asset.AssetType;
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

        WrapCashHoldingImpl wrapCashHolding = new WrapCashHoldingImpl();
        wrapCashHolding.setHoldingKey(HoldingKey.valueOf("123", "Working Cash Account"));
        wrapCashHolding.setAccountName("Working Cash Account");
        wrapCashHolding.setAvailableBalance(BigDecimal.valueOf(2000));
        wrapCashHolding.setMarketValue(BigDecimal.valueOf(4000));
        wrapCashHolding.setAccruedIncome(BigDecimal.valueOf(100));
        wrapCashHolding.setYield(BigDecimal.valueOf(0.1));
        wrapCashHolding.setAsset(getCashAsset());
        wrapCashHolding.setThirdPartySource(SystemType.WRAP.getName());
        List<AccountHolding> cashList = new ArrayList<>();
        cashList.add(wrapCashHolding);
        subAccount.addHoldings(cashList);

        List<InvestmentValuationDto> cashValuationDtos = cashValuationAggregator.getCashValuationDtos(
                subAccount.getHoldings(),
                accountBalance);
        Assert.assertNotNull(cashValuationDtos);
        Assert.assertTrue(cashValuationDtos.size() == 2);
        CashManagementValuationDto cashManagementValuationDto1 = (CashManagementValuationDto) cashValuationDtos.get(0);
        Assert.assertNotNull(cashManagementValuationDto1);
        Assert.assertEquals(BigDecimal.valueOf(4000), cashManagementValuationDto1.getBalance());
        Assert.assertEquals(BigDecimal.valueOf(2000), cashManagementValuationDto1.getAvailableBalance());
        Assert.assertEquals(0.2, cashManagementValuationDto1.getPortfolioPercent().doubleValue(), 0.005);
        Assert.assertEquals(BigDecimal.valueOf(0.001), cashManagementValuationDto1.getInterestRate());
        Assert.assertEquals(BigDecimal.valueOf(100), cashManagementValuationDto1.getIncome());
        Assert.assertEquals("Cash", cashManagementValuationDto1.getCategoryName());
        Assert.assertEquals("BT Cash", cashManagementValuationDto1.getName());

        CashManagementValuationDto cashManagementValuationDto2 = (CashManagementValuationDto) cashValuationDtos.get(1);
        Assert.assertNotNull(cashManagementValuationDto2);
        Assert.assertEquals(BigDecimal.valueOf(4000), cashManagementValuationDto2.getBalance());
        Assert.assertEquals(BigDecimal.valueOf(2000), cashManagementValuationDto2.getAvailableBalance());
        Assert.assertEquals(0.2, cashManagementValuationDto2.getPortfolioPercent().doubleValue(), 0.005);
        Assert.assertEquals(BigDecimal.valueOf(0.001), cashManagementValuationDto2.getInterestRate());
        Assert.assertEquals(BigDecimal.valueOf(100), cashManagementValuationDto2.getIncome());
        Assert.assertEquals("Cash", cashManagementValuationDto2.getCategoryName());
        Assert.assertEquals("Working Cash Account", cashManagementValuationDto2.getName());
    }

    @Test
    public void testBuildValuationDto_whenCashTypeWrapSubAccountPassed_thenCashValuationDtoCreated() {

        CashManagementValuationDto dto = (CashManagementValuationDto) cashValuationAggregator.getCashValuationDtos(
                subAccount.getHoldings(),
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

        InvestmentValuationDto dto = cashValuationAggregator.getCashValuationDtos(negSubAccount.getHoldings(), accountBalance)
                .get(0);

        Assert.assertNotNull(dto);
        Assert.assertEquals(BigDecimal.valueOf(1000), dto.getBalance());
        Assert.assertEquals(BigDecimal.ZERO, dto.getAvailableBalance());
        Assert.assertEquals(0.05, dto.getPortfolioPercent().doubleValue(), 0.005);
    }

    @Test
    public void testBuildValuationDto_whenCMAisPassedDodgyValues_NothingExplodes() {
        CashHoldingImpl cashHolding = new CashHoldingImpl();
        cashHolding.setHoldingKey(HoldingKey.valueOf("123", "BT Cash"));
        cashHolding.setMarketValue(BigDecimal.valueOf(4000));
        cashHolding.setValueDateBalance(BigDecimal.valueOf(3000));
        List<AccountHolding> holdings = new ArrayList<>();
        holdings.add(cashHolding);
        CashManagementValuationDto dto = (CashManagementValuationDto) cashValuationAggregator
                .getCashValuationDtos(holdings, accountBalance).get(0);
        Assert.assertEquals(BigDecimal.valueOf(1000), dto.getOutstandingCash());
        Assert.assertEquals(BigDecimal.valueOf(3000), dto.getValueDateBalance());
        Assert.assertEquals(0, BigDecimal.valueOf(0.15).compareTo(dto.getValueDatePercent()));
        Assert.assertEquals(0, BigDecimal.valueOf(0.15).compareTo(dto.getOutstandingCashPercent()));

        cashHolding.setMarketValue(null);
        cashHolding.setValueDateBalance(BigDecimal.valueOf(3000));
        dto = (CashManagementValuationDto) cashValuationAggregator.getCashValuationDtos(holdings, accountBalance).get(0);
        Assert.assertEquals(BigDecimal.valueOf(-3000), dto.getOutstandingCash());

        cashHolding.setMarketValue(BigDecimal.valueOf(4000));
        cashHolding.setValueDateBalance(null);
        dto = (CashManagementValuationDto) cashValuationAggregator.getCashValuationDtos(holdings, accountBalance).get(0);
        Assert.assertEquals(BigDecimal.valueOf(4000), dto.getOutstandingCash());

    }

    @Test
    public void testBuildValuationDto_whenExternalCash_NoOustandingCash() {
        CashHoldingImpl cashHolding = new CashHoldingImpl();
        cashHolding.setHoldingKey(HoldingKey.valueOf("123", "BT Cash"));
        cashHolding.setMarketValue(BigDecimal.valueOf(4000));
        cashHolding.setValueDateBalance(BigDecimal.valueOf(3000));
        cashHolding.setExternal(true);
        List<AccountHolding> holdings = new ArrayList<>();
        holdings.add(cashHolding);
        CashManagementValuationDto dto = (CashManagementValuationDto) cashValuationAggregator
                .getCashValuationDtos(holdings, accountBalance).get(0);
        Assert.assertEquals(BigDecimal.valueOf(0), dto.getOutstandingCash());
    }

    @Test
    public void testBuildValuationDto_whenHoldingsBlankOrNull() {

        List<AccountHolding> holdings = new ArrayList<>();
        List<InvestmentValuationDto> dtos = cashValuationAggregator.getCashValuationDtos(holdings, accountBalance);
        Assert.assertNotNull(dtos);
        Assert.assertTrue(dtos.isEmpty());

        dtos = cashValuationAggregator.getCashValuationDtos(null, accountBalance);
        Assert.assertNotNull(dtos);
        Assert.assertTrue(dtos.isEmpty());
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

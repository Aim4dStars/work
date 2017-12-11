package com.bt.nextgen.api.portfolio.v3.service.allocation;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.bt.nextgen.api.portfolio.v3.model.allocation.exposure.AggregateAllocationByExposureDto;
import com.bt.nextgen.api.portfolio.v3.model.allocation.exposure.AllocationByExposureDto;
import com.bt.nextgen.api.portfolio.v3.model.allocation.exposure.AssetAllocationByExposureDto;
import com.bt.nextgen.api.portfolio.v3.model.allocation.exposure.HoldingAllocationByExposureDto;
import com.bt.nextgen.api.portfolio.v3.service.TermDepositPresentationService;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.asset.AssetAllocation;
import com.bt.nextgen.service.avaloq.asset.AssetImpl;
import com.bt.nextgen.service.avaloq.asset.TermDepositAssetImpl;
import com.bt.nextgen.service.avaloq.asset.TermDepositPresentation;
import com.bt.nextgen.service.avaloq.portfolio.valuation.CashAccountValuationImpl;
import com.bt.nextgen.service.avaloq.portfolio.valuation.CashHoldingImpl;
import com.bt.nextgen.service.avaloq.portfolio.valuation.ManagedFundAccountValuationImpl;
import com.bt.nextgen.service.avaloq.portfolio.valuation.ManagedFundHoldingImpl;
import com.bt.nextgen.service.avaloq.portfolio.valuation.TermDepositAccountValuationImpl;
import com.bt.nextgen.service.avaloq.portfolio.valuation.TermDepositHoldingImpl;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.asset.AssetClass;
import com.bt.nextgen.service.integration.asset.AssetKey;
import com.bt.nextgen.service.integration.portfolio.valuation.AccountHolding;
import com.bt.nextgen.service.integration.portfolio.valuation.HoldingKey;
import com.bt.nextgen.service.integration.portfolio.valuation.SubAccountValuation;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.btfin.panorama.service.integration.asset.AssetType;

@RunWith(MockitoJUnitRunner.class)
public class ExposureAggregatorTest {

    @InjectMocks
    public ExposureAggregator exposureAggregator;

    @Mock
    public TermDepositPresentationService tdPresentationService;

    @Mock
    public ExposureAssetAllocationBuilder assetBuilder;

    private CashHoldingImpl cashHolding;

    @Before
    public void Start() {
        // Mock tdPres
        TermDepositPresentation tdPres = new TermDepositPresentation();
        tdPres.setBrandClass("bt");
        tdPres.setBrandName("bt term deposit");
        tdPres.setPaymentFrequency("at maturity");
        tdPres.setTerm("3 month");

        Mockito.when(
                tdPresentationService.getTermDepositPresentation(Mockito.any(AccountKey.class), Mockito.any(String.class),
                        Mockito.any(ServiceErrors.class))).thenReturn(tdPres);

        Map<String, BigDecimal> allocMap = new HashMap<>();
        for (AssetClass assetClass : AssetClass.values()) {
            allocMap.put(assetClass.getDescription(), BigDecimal.ZERO);
        }
        allocMap.put(AssetClass.CASH.getDescription(), BigDecimal.ONE);

        AllocationByExposureDto haExp = new HoldingAllocationByExposureDto(Collections.singletonList(getCashHoldingSource()),
                BigDecimal.valueOf(10), allocMap, false);

        AssetAllocationByExposureDto aaExp = new AssetAllocationByExposureDto(getCashAsset(), Collections.singletonList(haExp));

        Mockito.when(
                assetBuilder.buildAssetAllocation(Mockito.any(AccountKey.class), Mockito.anyList(), Mockito.anyMap(),
                        Mockito.any(BigDecimal.class), Mockito.any(ServiceErrors.class))).thenReturn(aaExp);

        AssetAllocationByExposureDto incExp = new AssetAllocationByExposureDto("income", Collections.singletonList(haExp));
        Mockito.when(assetBuilder.buildIncomeAsset(Mockito.anyMap(), Mockito.anyMap(), Mockito.any(BigDecimal.class)))
                .thenReturn(incExp);
    }

    @Test
    public void testAggregateAllocations_whenSuppliedWithListWithCash_thenAgregatedAllocationReturned() {

        AccountKey accountKey = AccountKey.valueOf("accountKey");

        List<SubAccountValuation> subAccounts = new ArrayList<>();
        CashAccountValuationImpl cashAccount = new CashAccountValuationImpl();
        AssetImpl asset = getCashAsset();
        cashHolding = new CashHoldingImpl();
        cashHolding.setAccountName("accountName");
        cashHolding.setAvailableBalance(BigDecimal.valueOf(1));
        cashHolding.setMarketValue(BigDecimal.valueOf(2));
        cashHolding.setAccruedIncome(BigDecimal.valueOf(3));
        cashHolding.setYield(BigDecimal.valueOf(4));
        cashHolding.setAsset(asset);
        cashHolding.setExternal(Boolean.TRUE);
        List<AccountHolding> cashList = new ArrayList<>();
        cashList.add(cashHolding);
        cashAccount.addHoldings(cashList);
        subAccounts.add(cashAccount);

        TermDepositAccountValuationImpl tdAccount = new TermDepositAccountValuationImpl();
        AssetImpl tdAsset = getTDAsset();
        TermDepositHoldingImpl tdHolding = new TermDepositHoldingImpl();
        tdHolding.setAvailableBalance(BigDecimal.valueOf(1));
        tdHolding.setMarketValue(BigDecimal.valueOf(2));
        tdHolding.setAccruedIncome(BigDecimal.valueOf(3));
        tdHolding.setAsset(tdAsset);
        List<AccountHolding> tdList = new ArrayList<>();
        tdList.add(tdHolding);
        tdAccount.addHoldings(tdList);
        subAccounts.add(tdAccount);

        List<AccountHolding> holdings = new ArrayList<>();
        asset = getShareAsset();
        ManagedFundHoldingImpl holding = new ManagedFundHoldingImpl();
        holding.setHoldingKey(HoldingKey.valueOf("111", "Test holding"));
        holding.setAsset(asset);
        holding.setAvailableUnits(BigDecimal.valueOf(1));
        holding.setCost(BigDecimal.valueOf(2));
        holding.setExternal(Boolean.TRUE);
        holding.setUnitPrice(BigDecimal.valueOf(3));
        holding.setUnitPriceDate(new DateTime());
        holding.setUnits(BigDecimal.valueOf(4));
        holding.setYield(BigDecimal.valueOf(5));
        holding.setMarketValue(BigDecimal.valueOf(11.99));
        holding.setAccruedIncome(BigDecimal.ONE);
        holdings.add(holding);

        ManagedFundAccountValuationImpl mfAccount = new ManagedFundAccountValuationImpl();
        mfAccount.addHoldings(holdings);
        subAccounts.add(mfAccount);

        HashMap<AssetKey, AssetAllocation> allocs = new HashMap<>();

        AggregateAllocationByExposureDto dto = exposureAggregator.aggregateAllocations(accountKey, subAccounts, allocs,
                BigDecimal.valueOf(11.99), new FailFastErrorsImpl());

        Assert.assertEquals(BigDecimal.valueOf(8), dto.getBalance());
        Assert.assertEquals(BigDecimal.valueOf(0), dto.getInternalBalance());
        Assert.assertEquals(BigDecimal.valueOf(8), dto.getExternalBalance());

        Assert.assertEquals("Allocation", dto.getName());

        List<AllocationByExposureDto> allocations = dto.getAllocations();

        for (AllocationByExposureDto allocation : allocations) {

            allocation = (AggregateAllocationByExposureDto) allocation;

            Assert.assertEquals("Cash", allocation.getName());
            Assert.assertEquals(Boolean.TRUE, allocation.getIsExternal());

            Assert.assertEquals(Boolean.TRUE, allocation.getIsExternal());
            Assert.assertEquals("ext source", allocation.getSource());
            Assert.assertEquals(BigDecimal.valueOf(8), allocation.getBalance());
            Assert.assertEquals(BigDecimal.valueOf(0), allocation.getInternalBalance());
            Assert.assertEquals(BigDecimal.valueOf(8), allocation.getExternalBalance());

        }
    }

    private HoldingSource getCashHoldingSource() {
        CashAccountValuationImpl cashAccount = new CashAccountValuationImpl();
        AssetImpl asset = getCashAsset();
        cashHolding = new CashHoldingImpl();
        cashHolding.setAccountName("accountName");
        cashHolding.setAvailableBalance(BigDecimal.valueOf(1));
        cashHolding.setMarketValue(BigDecimal.valueOf(2));
        cashHolding.setAccruedIncome(BigDecimal.valueOf(3));
        cashHolding.setYield(BigDecimal.valueOf(4));
        cashHolding.setAsset(asset);
        cashHolding.setSource("ext source");
        cashHolding.setExternal(Boolean.TRUE);
        HoldingSource cashHoldingSource = new HoldingSource(asset, cashHolding, cashAccount);
        return cashHoldingSource;
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

    private TermDepositAssetImpl getTDAsset() {
        TermDepositAssetImpl asset = new TermDepositAssetImpl();
        asset.setAssetClass(AssetClass.CASH);
        asset.setAssetCode("assetCode");
        asset.setAssetId("tdAsset");
        asset.setAssetName("BT Term Deposit");
        asset.setAssetType(AssetType.TERM_DEPOSIT);
        asset.setBrand("brand");
        asset.setIndustrySector("industrySector");
        asset.setIndustryType("industryType");
        return asset;
    }

    private AssetImpl getShareAsset() {
        AssetImpl asset = new AssetImpl();
        asset.setAssetClass(AssetClass.AUSTRALIAN_SHARES);
        asset.setAssetCode("assetCode");
        asset.setAssetId("shareAsset");
        asset.setAssetName("assetName");
        asset.setAssetType(AssetType.SHARE);
        asset.setBrand("brand");
        asset.setIndustrySector("industrySector");
        asset.setIndustryType("industryType");
        return asset;
    }

}

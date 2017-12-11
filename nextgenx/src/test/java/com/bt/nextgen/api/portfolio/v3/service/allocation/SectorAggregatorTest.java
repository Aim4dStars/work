package com.bt.nextgen.api.portfolio.v3.service.allocation;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.bt.nextgen.api.portfolio.v3.model.allocation.sector.AggregatedAllocationBySectorDto;
import com.bt.nextgen.api.portfolio.v3.model.allocation.sector.AllocationBySectorDto;
import com.bt.nextgen.api.portfolio.v3.model.allocation.sector.AssetAllocationBySectorDto;
import com.bt.nextgen.api.portfolio.v3.model.allocation.sector.HoldingAllocationBySectorDto;
import com.bt.nextgen.api.portfolio.v3.service.TermDepositPresentationService;
import com.bt.nextgen.service.ServiceErrors;
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
import com.bt.nextgen.service.integration.portfolio.valuation.AccountHolding;
import com.bt.nextgen.service.integration.portfolio.valuation.HoldingKey;
import com.bt.nextgen.service.integration.portfolio.valuation.SubAccountValuation;
import com.btfin.panorama.service.integration.asset.AssetType;

@RunWith(MockitoJUnitRunner.class)
public class SectorAggregatorTest {

    @InjectMocks
    public SectorAggregator sectorAggregator;

    @Mock
    public TermDepositPresentationService tdPresentationService;

    @Mock
    public SectorAssetAllocationBuilder assetBuilder;

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

        HoldingSource holding = Mockito.mock(HoldingSource.class);
        Mockito.when(holding.getAsset()).thenReturn(getCashAsset());
        Mockito.when(holding.getMarketValue()).thenReturn(BigDecimal.valueOf(10));
        Mockito.when(holding.getUnits()).thenReturn(BigDecimal.valueOf(20));
        Mockito.when(holding.isPending()).thenReturn(false);
        Mockito.when(holding.isExternal()).thenReturn(true);


        AllocationBySectorDto haSect = new HoldingAllocationBySectorDto(Collections.singletonList(holding),
                BigDecimal.valueOf(0.20), false);

        AssetAllocationBySectorDto aaSector = new AssetAllocationBySectorDto(getCashAsset(), false,
                Collections.singletonList(haSect));

        Mockito.when(
                assetBuilder.buildAssetAllocation(Mockito.any(AccountKey.class), Mockito.anyList(), Mockito.any(BigDecimal.class)))
                .thenReturn(aaSector);

        AssetAllocationBySectorDto incSector = new AssetAllocationBySectorDto("income", Collections.singletonList(haSect));
        Mockito.when(assetBuilder.buildIncomeAsset(Mockito.anyMap(), Mockito.any(BigDecimal.class))).thenReturn(incSector);
    }

    @Test
    public void testAggregateAllocations_whenSuppliedWithListWithCash_thenAgregatedAllocationReturned() {

        AccountKey accountKey = AccountKey.valueOf("accountKey");

        List<SubAccountValuation> subAccounts = new ArrayList<>();
        CashAccountValuationImpl cashAccount = new CashAccountValuationImpl();
        AssetImpl asset = getCashAsset();
        CashHoldingImpl cashHolding = new CashHoldingImpl();
        cashHolding.setAccountName("accountName");
        cashHolding.setAvailableBalance(BigDecimal.valueOf(1));
        cashHolding.setMarketValue(BigDecimal.valueOf(2));
        cashHolding.setAccruedIncome(BigDecimal.valueOf(3));
        cashHolding.setYield(BigDecimal.valueOf(4));
        cashHolding.setAsset(asset);
        cashHolding.setExternal(Boolean.FALSE);
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

        AggregatedAllocationBySectorDto dto = sectorAggregator.aggregateAllocations(accountKey, subAccounts,
                BigDecimal.valueOf(11.99));

        Assert.assertEquals(BigDecimal.valueOf(40), dto.getBalance());
        Assert.assertEquals(BigDecimal.valueOf(0), dto.getInternalBalance());
        Assert.assertEquals(BigDecimal.valueOf(40), dto.getExternalBalance());

        Assert.assertEquals("Allocation", dto.getName());
        Assert.assertEquals(200, dto.getAllocationPercentage().doubleValue(), 0.001);

        List<AllocationBySectorDto> allocations = dto.getAllocations();

        for (AllocationBySectorDto allocation : allocations) {

            allocation = (AggregatedAllocationBySectorDto) allocation;

            Assert.assertEquals(Boolean.TRUE, allocation.getIsExternal());
            Assert.assertEquals(Boolean.FALSE, allocation.getPending());

            List<AllocationBySectorDto> cashAllocations = ((AggregatedAllocationBySectorDto) allocation).getAllocations();

            // cash account
            AllocationBySectorDto cash = cashAllocations.get(0);
            Assert.assertEquals(BigDecimal.valueOf(10), cash.getBalance());
            Assert.assertEquals(BigDecimal.valueOf(0), cash.getInternalBalance());
            Assert.assertEquals(BigDecimal.valueOf(10), cash.getExternalBalance());
        }
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

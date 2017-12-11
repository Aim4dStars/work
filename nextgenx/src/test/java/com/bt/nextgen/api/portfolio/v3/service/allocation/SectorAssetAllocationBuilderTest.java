package com.bt.nextgen.api.portfolio.v3.service.allocation;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.bt.nextgen.api.portfolio.v3.model.allocation.sector.AllocationBySectorDto;
import com.bt.nextgen.api.portfolio.v3.model.allocation.sector.AssetAllocationBySectorDto;
import com.bt.nextgen.api.portfolio.v3.model.allocation.sector.HoldingAllocationBySectorDto;
import com.bt.nextgen.api.portfolio.v3.model.allocation.sector.HoldingAllocationBySourceDto;
import com.bt.nextgen.api.portfolio.v3.model.allocation.sector.TermDepositAggregatedAssetAllocationBySectorDto;
import com.bt.nextgen.api.portfolio.v3.service.TermDepositPresentationService;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.asset.TermDepositPresentation;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetClass;
import com.bt.nextgen.service.integration.asset.TermDepositAsset;
import com.btfin.panorama.service.integration.asset.AssetType;

@RunWith(MockitoJUnitRunner.class)
public class SectorAssetAllocationBuilderTest {

    @Mock
    public TermDepositPresentationService tdPresentationService;

    @InjectMocks
    public SectorAssetAllocationBuilder assetBuilder;

    @Before
    public void Start() {
        TermDepositPresentation tdPres = new TermDepositPresentation();
        tdPres.setBrandClass("bt");
        tdPres.setBrandName("bt term deposit");
        tdPres.setPaymentFrequency("at maturity");
        tdPres.setTerm("3 month");

        Mockito.when(
                tdPresentationService.getTermDepositPresentation(Mockito.any(AccountKey.class), Mockito.any(String.class),
                        Mockito.any(ServiceErrors.class))).thenReturn(tdPres);

    }

    @Test
    public void testAssetAllocationBuilder_whenSuppliedWithATermDeposit_thenTDAllocationReturned() {

        HoldingSource holding = new HoldingSource(getTDAsset(), BigDecimal.valueOf(1000));
        AssetAllocationBySectorDto result = assetBuilder.buildAssetAllocation(AccountKey.valueOf("accountKey"),
                Collections.singletonList(holding), BigDecimal.valueOf(10000));
        Assert.assertNotNull(result);
        Assert.assertTrue(result instanceof TermDepositAggregatedAssetAllocationBySectorDto);
        TermDepositAggregatedAssetAllocationBySectorDto tdResult = (TermDepositAggregatedAssetAllocationBySectorDto) result;
        
        
        Assert.assertEquals("bt", tdResult.getBrand());
        Assert.assertEquals("bt term deposit", tdResult.getName());
        Assert.assertEquals("3 month", tdResult.getTerm());
        Assert.assertEquals("at maturity", tdResult.getPaymentFrequency());

        List<AllocationBySectorDto> allocations = tdResult.getAllocations();
        HoldingAllocationBySectorDto allocation = (HoldingAllocationBySectorDto) allocations.get(0);
        Assert.assertEquals("assetCode", allocation.getAssetCode());
        Assert.assertEquals("tdAsset", allocation.getAssetId());
        Assert.assertEquals("Cash", allocation.getAssetSector());
        Assert.assertEquals("TERM_DEPOSIT", allocation.getAssetType());
        Assert.assertEquals("BT Term Deposit", allocation.getName());
        Assert.assertEquals(null, allocation.getSource());
        Assert.assertEquals("HoldingAllocationBySector", allocation.getType());
        Assert.assertEquals(new BigDecimal(0.10000000, new MathContext(8)), allocation.getAllocationPercentage());
        Assert.assertEquals(BigDecimal.valueOf(1000), allocation.getBalance());
        Assert.assertEquals(BigDecimal.valueOf(0), allocation.getExternalBalance());
        Assert.assertEquals(BigDecimal.valueOf(1000), allocation.getInternalBalance());
        Assert.assertEquals(false, allocation.getIsExternal());
        Assert.assertEquals(false, allocation.getPending());
        Assert.assertEquals(null, allocation.getUnits());


        List<HoldingAllocationBySourceDto> holdingBySourceDtos = allocation.getChildren();
        HoldingAllocationBySourceDto holdingBySourceDto = holdingBySourceDtos.get(0);
        Assert.assertEquals(null, holdingBySourceDto.getSource());
        Assert.assertEquals(new BigDecimal(0.10000000, new MathContext(8)), holdingBySourceDto.getAllocationPercentage());
        Assert.assertEquals(BigDecimal.valueOf(1000), holdingBySourceDto.getBalance());
        Assert.assertEquals(BigDecimal.valueOf(1000), holdingBySourceDto.getUnits());
        Assert.assertEquals(false, holdingBySourceDto.isExternal());
        Assert.assertEquals(false, holdingBySourceDto.isPending());
    }

    @Test
    public void testAssetAllocationBuilder_whenSuppliedWithNonTermDeposit_thenAllocationReturned() {
        HoldingSource holding = new HoldingSource(getCashAsset(), BigDecimal.valueOf(1000));
        AssetAllocationBySectorDto result = assetBuilder.buildAssetAllocation(AccountKey.valueOf("accountKey"),
                Collections.singletonList(holding), BigDecimal.valueOf(10000));
        Assert.assertNotNull(result);

        Assert.assertEquals("BT Cash", result.getName());
        Assert.assertEquals("assetCode", result.getAssetCode());
        Assert.assertEquals(0.1, result.getAllocationPercentage().doubleValue(), 0.001);
        Assert.assertEquals("cashAsset", result.getAssetId());
        Assert.assertEquals("Cash", result.getAssetSector());
    }

    @Test
    public void testAssetAllocationBuilder_whenSuppliedWithAnAssetWithSublass_thenAllocationHasMatchingClass() {
        HoldingSource holding = new HoldingSource(getShareAsset(), BigDecimal.valueOf(1000));
        AssetAllocationBySectorDto result = assetBuilder.buildAssetAllocation(AccountKey.valueOf("accountKey"),
                Collections.singletonList(holding), BigDecimal.valueOf(10000));
        Assert.assertNotNull(result);

        Assert.assertEquals("assetSubclass", result.getAssetSubclass());
    }

    @Test
    public void testAssetAllocationBuilder_whenSuppliedWithMatchingAlloc_thenAllocationAllocValueReturned() {
        HoldingSource holding = new HoldingSource(getShareAsset(), BigDecimal.valueOf(1000));

        Map<AssetClass, BigDecimal> allocationMap = new HashMap<>();

        for (AssetClass assetClass : AssetClass.values()) {
            allocationMap.put(assetClass, BigDecimal.ZERO);
        }
        allocationMap.put(AssetClass.AUSTRALIAN_SHARES, BigDecimal.valueOf(0.6));
        allocationMap.put(AssetClass.INTERNATIONAL_SHARES, BigDecimal.valueOf(0.4));

        AssetAllocationBySectorDto result = assetBuilder.buildAssetAllocation(AccountKey.valueOf("accountKey"),
                Collections.singletonList(holding), BigDecimal.valueOf(10000));
        Assert.assertNotNull(result);

    }

    @Test
    public void testIncomeAllocationBuilder() {
        HoldingSource holding = Mockito.mock(HoldingSource.class);
        Asset asset = getCashAsset();
        Mockito.when(holding.getAsset()).thenReturn(asset);
        asset = getShareAsset();
        Mockito.when(holding.getSource()).thenReturn(asset);
        Mockito.when(holding.getIncome()).thenReturn(BigDecimal.valueOf(2000));
        Map<AllocationGroupKey, List<HoldingSource>> assetHoldingsMap = new HashMap<>();
        assetHoldingsMap.put(new AllocationGroupKey("Cash", true, true), Collections.singletonList(holding));

        AllocationBySectorDto result = assetBuilder.buildIncomeAsset(assetHoldingsMap, BigDecimal.valueOf(100));

        Assert.assertNotNull(result);

        Assert.assertEquals("Income accrued", result.getName());
        Assert.assertEquals(20, result.getAllocationPercentage().doubleValue(), 0.0001);

    }

    @Test
    public void testAssetAllocationBuilder_whenSuppliedWithMatchingExternalAlloc_thenAllocationAllocValueReturned() {
        HoldingSource holding = Mockito.mock(HoldingSource.class);
        Mockito.when(holding.isExternal()).thenReturn(true);
        Mockito.when(holding.isPending()).thenReturn(false);
        Asset asset = getShareAsset();
        Mockito.when(holding.getAsset()).thenReturn(asset);
        Mockito.when(holding.getSource()).thenReturn(asset);
        Mockito.when(holding.getMarketValue()).thenReturn(BigDecimal.valueOf(1000));

        AssetAllocationBySectorDto result = assetBuilder.buildAssetAllocation(AccountKey.valueOf("accountKey"),
                Collections.singletonList(holding), BigDecimal.valueOf(10000));
        Assert.assertNotNull(result);
        List<AllocationBySectorDto> holdingAllocations = result.getAllocations();
        HoldingAllocationBySectorDto holdingAllocationBySectorDto = (HoldingAllocationBySectorDto) holdingAllocations.get(0);
        Assert.assertEquals(BigDecimal.valueOf(0), holdingAllocationBySectorDto.getInternalBalance());
        Assert.assertEquals(BigDecimal.valueOf(1000), holdingAllocationBySectorDto.getExternalBalance());
        Assert.assertEquals(BigDecimal.valueOf(0), holdingAllocationBySectorDto.getUnits());
        Assert.assertEquals(new BigDecimal(0.10000000, new MathContext(8)),
                holdingAllocationBySectorDto.getAllocationPercentage());

    }

    @Test
    public void testAssetAllocationBuilder_whenSuppliedWithZero_thenZeroAllocationAllocValueReturned() {
        HoldingSource holding = Mockito.mock(HoldingSource.class);
        Mockito.when(holding.isExternal()).thenReturn(true);
        Mockito.when(holding.isPending()).thenReturn(true);
        Asset asset = getShareAsset();
        Mockito.when(holding.getAsset()).thenReturn(asset);
        Mockito.when(holding.getSource()).thenReturn(asset);
        Mockito.when(holding.getMarketValue()).thenReturn(BigDecimal.valueOf(0));

        AssetAllocationBySectorDto result = assetBuilder.buildAssetAllocation(AccountKey.valueOf("accountKey"),
                Collections.singletonList(holding), BigDecimal.valueOf(10000));
        Assert.assertNotNull(result);
        List<AllocationBySectorDto> holdingAllocations = result.getAllocations();
        HoldingAllocationBySectorDto holdingAllocationBySectorDto = (HoldingAllocationBySectorDto) holdingAllocations.get(0);
        Assert.assertEquals(BigDecimal.ZERO,
                holdingAllocationBySectorDto.getAllocationPercentage());
        Assert.assertEquals(BigDecimal.valueOf(0), holdingAllocationBySectorDto.getUnits());

    }

    private Asset getCashAsset() {
        Asset asset = mock(Asset.class);
        when(asset.getAssetClass()).thenReturn(AssetClass.CASH);
        when(asset.getAssetCode()).thenReturn("assetCode");
        when(asset.getAssetId()).thenReturn("cashAsset");
        when(asset.getAssetName()).thenReturn("BT Cash");
        when(asset.getAssetType()).thenReturn(AssetType.CASH);
        return asset;
    }

    private Asset getShareAsset() {
        Asset asset = mock(Asset.class);
        when(asset.getAssetClass()).thenReturn(AssetClass.AUSTRALIAN_SHARES);
        when(asset.getAssetSubclass()).thenReturn("assetSubclass");
        when(asset.getAssetCode()).thenReturn("assetCode");
        when(asset.getAssetId()).thenReturn("shareAsset");
        when(asset.getAssetName()).thenReturn("assetName");
        when(asset.getAssetType()).thenReturn(AssetType.SHARE);
        when(asset.getIndustrySector()).thenReturn("industrySector");
        when(asset.getIndustryType()).thenReturn("industryType");
        return asset;
    }

    private TermDepositAsset getTDAsset() {
        TermDepositAsset asset = mock(TermDepositAsset.class);
        when(asset.getAssetClass()).thenReturn(AssetClass.CASH);
        when(asset.getAssetCode()).thenReturn("assetCode");
        when(asset.getAssetId()).thenReturn("tdAsset");
        when(asset.getAssetName()).thenReturn("BT Term Deposit");
        when(asset.getAssetType()).thenReturn(AssetType.TERM_DEPOSIT);
        return asset;
    }
}

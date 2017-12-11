package com.bt.nextgen.api.account.v2.service.allocation;

import com.bt.nextgen.api.account.v2.model.allocation.exposure.AllocationByExposureDto;
import com.bt.nextgen.api.account.v2.model.allocation.exposure.AssetAllocationByExposureDto;
import com.bt.nextgen.api.account.v2.model.allocation.exposure.TermDepositAssetAllocationByExposureDto;
import com.bt.nextgen.api.account.v2.service.TermDepositPresentationService;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.asset.AssetAllocation;
import com.bt.nextgen.service.avaloq.asset.AssetImpl;
import com.bt.nextgen.service.avaloq.asset.TermDepositAssetImpl;
import com.bt.nextgen.service.avaloq.asset.TermDepositPresentation;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.asset.AssetClass;
import com.bt.nextgen.service.integration.asset.AssetKey;
import com.btfin.panorama.service.integration.asset.AssetType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(MockitoJUnitRunner.class)
public class ExposureAssetAllocationBuilderTest {
    @Mock
    public TermDepositPresentationService tdPresentationService;

    @InjectMocks
    public ExposureAssetAllocationBuilder assetBuilder;

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
        Map<AssetKey, AssetAllocation> allocations = Collections.emptyMap();
        AssetAllocationByExposureDto result =  assetBuilder.buildAssetAllocation(AccountKey.valueOf("accountKey"), Collections.singletonList(holding), allocations,
                BigDecimal.valueOf(10000), new FailFastErrorsImpl());
        Assert.assertNotNull(result);
        Assert.assertTrue(result instanceof TermDepositAssetAllocationByExposureDto);
        TermDepositAssetAllocationByExposureDto tdResult = (TermDepositAssetAllocationByExposureDto)result;
        
        
        Assert.assertEquals("bt", tdResult.getBrand());
        Assert.assertEquals("BT Term Deposit", tdResult.getName());
        Assert.assertEquals("3 month", tdResult.getTerm());
        Assert.assertEquals("at maturity", tdResult.getPaymentFrequency());
    }

    @Test
    public void testAssetAllocationBuilder_whenSuppliedWithNonTermDeposit_thenAllocationReturned() {
        HoldingSource holding = new HoldingSource(getCashAsset(), BigDecimal.valueOf(1000));
        Map<AssetKey, AssetAllocation> allocations = Collections.emptyMap();
        AssetAllocationByExposureDto result = assetBuilder.buildAssetAllocation(AccountKey.valueOf("accountKey"),
                Collections.singletonList(holding), allocations, BigDecimal.valueOf(10000), new FailFastErrorsImpl());
        Assert.assertNotNull(result);

        Assert.assertEquals("BT Cash", result.getName());
        Assert.assertEquals("assetCode", result.getAssetCode());
        Assert.assertEquals(0.1, result.getAccountPercent().doubleValue(), 0.001);
        Assert.assertEquals("cashAsset", result.getAssetId());
        Assert.assertEquals("Cash", result.getAssetSector());

        Map<String, BigDecimal> allocMap = result.getAccountAllocationPercentage();

        for (AssetClass assetClass : AssetClass.values()) {
            if (assetClass != AssetClass.CASH) {
                Assert.assertEquals(BigDecimal.ZERO, allocMap.get(assetClass.name()));
            } else {
                Assert.assertEquals(0.1, allocMap.get(assetClass.name()).doubleValue(), 0.0001);
            }
        }

        allocMap = result.getAssetAllocationPercentage();

        for (AssetClass assetClass : AssetClass.values()) {
            if (assetClass != AssetClass.CASH) {
                Assert.assertEquals(BigDecimal.ZERO, allocMap.get(assetClass.name()));
            } else {
                Assert.assertEquals(1, allocMap.get(assetClass.name()).doubleValue(), 0.0001);
            }
        }

        allocMap = result.getAllocationDollar();

        for (AssetClass assetClass : AssetClass.values()) {
            if (assetClass != AssetClass.CASH) {
                Assert.assertEquals(BigDecimal.ZERO, allocMap.get(assetClass.name()));
            } else {
                Assert.assertEquals(1000, allocMap.get(assetClass.name()).doubleValue(), 0.0001);
            }
        }

    }

    @Test
    public void testAssetAllocationBuilder_whenSuppliedWithMatchingAlloc_thenAllocationAllocValueReturned() {
        HoldingSource holding = new HoldingSource(getShareAsset(), BigDecimal.valueOf(1000));
        AssetAllocation assetAllocation = Mockito.mock(AssetAllocation.class);

        Map<AssetClass, BigDecimal> allocationMap = new HashMap<>();

        for (AssetClass assetClass : AssetClass.values()) {
            allocationMap.put(assetClass, BigDecimal.ZERO);
        }
        allocationMap.put(AssetClass.AUSTRALIAN_SHARES, BigDecimal.valueOf(0.6));
        allocationMap.put(AssetClass.INTERNATIONAL_SHARES, BigDecimal.valueOf(0.4));

        Mockito.when(assetAllocation.getAllocations()).thenReturn(allocationMap);

        Map<AssetKey, AssetAllocation> allocations = new HashMap<>();
        allocations.put(AssetKey.valueOf("shareAsset"), assetAllocation);

        AssetAllocationByExposureDto result = assetBuilder.buildAssetAllocation(AccountKey.valueOf("accountKey"),
                Collections.singletonList(holding), allocations, BigDecimal.valueOf(10000), new FailFastErrorsImpl());
        Assert.assertNotNull(result);

        Map<String, BigDecimal> allocMap = result.getAccountAllocationPercentage();

        for (AssetClass assetClass : AssetClass.values()) {
            if (assetClass == AssetClass.AUSTRALIAN_SHARES) {
                Assert.assertEquals(0.06, allocMap.get(assetClass.name()).doubleValue(), 0.001);
            } else if (assetClass == AssetClass.INTERNATIONAL_SHARES) {
                Assert.assertEquals(0.04, allocMap.get(assetClass.name()).doubleValue(), 0.001);
            } else {
                Assert.assertEquals(0, allocMap.get(assetClass.name()).doubleValue(), 0.0001);
            }
        }

        allocMap = result.getAssetAllocationPercentage();

        for (AssetClass assetClass : AssetClass.values()) {            
            if (assetClass == AssetClass.AUSTRALIAN_SHARES) {
                Assert.assertEquals(0.6, allocMap.get(assetClass.name()).doubleValue(), 0.001);
            } else if (assetClass == AssetClass.INTERNATIONAL_SHARES) {
                Assert.assertEquals(0.4, allocMap.get(assetClass.name()).doubleValue(), 0.001);
            } else {
                Assert.assertEquals(0, allocMap.get(assetClass.name()).doubleValue(), 0.0001);
            }
        }

        allocMap = result.getAllocationDollar();

        for (AssetClass assetClass : AssetClass.values()) {
            if (assetClass == AssetClass.AUSTRALIAN_SHARES) {
                Assert.assertEquals(600, allocMap.get(assetClass.name()).doubleValue(), 0.001);
            } else if (assetClass == AssetClass.INTERNATIONAL_SHARES) {
                Assert.assertEquals(400, allocMap.get(assetClass.name()).doubleValue(), 0.001);
            } else {
                Assert.assertEquals(0, allocMap.get(assetClass.name()).doubleValue(), 0.0001);
            }
        }
    }


    @Test
    public void testIncomeAllocationBuilder() {
        HoldingSource holding = Mockito.mock(HoldingSource.class);

        Mockito.when(holding.getAsset()).thenReturn(getCashAsset());
        Mockito.when(holding.getSource()).thenReturn(getShareAsset());
        Mockito.when(holding.getIncome()).thenReturn(BigDecimal.valueOf(2000));

        Map<AssetKey, AssetAllocation> allocations = Collections.emptyMap();
        Map<AllocationGroupKey, List<HoldingSource>> assetHoldingsMap = new HashMap<>();
        assetHoldingsMap.put(new AllocationGroupKey("Cash", true, true), Collections.singletonList(holding));


        AllocationByExposureDto result = assetBuilder.buildIncomeAsset(assetHoldingsMap, allocations, BigDecimal.valueOf(100));

        Assert.assertNotNull(result);

        Assert.assertEquals("Income accrued", result.getName());

        Map<String, BigDecimal> allocMap = result.getAllocationDollar();

        for (AssetClass assetClass : AssetClass.values()) {
            if (assetClass != AssetClass.CASH) {
                Assert.assertEquals(BigDecimal.ZERO, allocMap.get(assetClass.name()));
            } else {
                Assert.assertEquals(2000, allocMap.get(assetClass.name()).doubleValue(), 0.0001);
            }
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
}

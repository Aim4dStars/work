package com.bt.nextgen.reports.account;

import com.bt.nextgen.api.account.v2.model.DatedValuationKey;
import com.bt.nextgen.api.account.v2.model.allocation.exposure.AggregateAllocationByExposureDto;
import com.bt.nextgen.api.account.v2.model.allocation.exposure.AllocationByExposureDto;
import com.bt.nextgen.api.account.v2.model.allocation.exposure.AssetAllocationByExposureDto;
import com.bt.nextgen.api.account.v2.model.allocation.exposure.HoldingAllocationByExposureDto;
import com.bt.nextgen.api.account.v2.model.allocation.exposure.KeyedAllocByExposureDto;
import com.bt.nextgen.api.account.v2.service.allocation.AllocationByExposureDtoService;
import com.bt.nextgen.core.api.UriMappingConstants;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.asset.AssetImpl;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetClass;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(MockitoJUnitRunner.class)
public class ExposureAllocationCsvReportTest {

    @Mock
    private AllocationByExposureDtoService allocationDtoService;

    @InjectMocks
    private ExposureAllocationCsvReport exposureAllocationCsvReport;
    private static final String PARAM_INCLUDE_EXTERNAL = "include-external";
    Map<String, String> params = new HashMap<String, String>();

    @Before
    public void setUp() throws Exception {

        params.put(UriMappingConstants.ACCOUNT_ID_URI_MAPPING, "114129E83B02BCA4D4B3245CEFB3778405F178638F9731DD");
        params.put(UriMappingConstants.EFFECTIVE_DATE_PARAMETER_MAPPING, "2015-07-14");
        params.put(PARAM_INCLUDE_EXTERNAL, "false");
        params.put("exposureType", "dollar");

    }

    @Test
    public final void testGetExposureType() {
        Assert.assertEquals("dollar", exposureAllocationCsvReport.getExposureType(params));
    }

    @Test
    public final void testGetExposureAllocation() {
        Map<String, BigDecimal> alloc = new HashMap<>();
        alloc.put(AssetClass.CASH.getDescription(), BigDecimal.ONE);

        AssetImpl holdingAsset = new AssetImpl();
        holdingAsset.setAssetId("holdingId");
        holdingAsset.setAssetCode("holdingCode");
        holdingAsset.setAssetName("holdingName");

        AllocationByExposureDto holding = new HoldingAllocationByExposureDto(holdingAsset, BigDecimal.valueOf(1),
                BigDecimal.valueOf(2),
                true, "holdingSource", alloc);

        Asset asset = Mockito.mock(Asset.class);
        Mockito.when(asset.getAssetCode()).thenReturn("assetCode");
        Mockito.when(asset.getAssetName()).thenReturn("assetName");
        Mockito.when(asset.getAssetId()).thenReturn("assetId");

        AllocationByExposureDto assetExposure = new AssetAllocationByExposureDto(asset, Collections.singletonList(holding));

        AllocationByExposureDto sectorExposure = new AggregateAllocationByExposureDto("sectorName",
                Collections.singletonList(assetExposure));

        KeyedAllocByExposureDto keyedAllocByExposureDto = new KeyedAllocByExposureDto("name",
                Collections.singletonList(sectorExposure), new DatedValuationKey("accountKey", new DateTime(), true), Boolean.FALSE);

        Mockito.when(allocationDtoService.find(Mockito.any(DatedValuationKey.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(keyedAllocByExposureDto);

        List<AssetAllocationByExposureDto> assetAllocations = exposureAllocationCsvReport.getExposureAllocation(params);

        Assert.assertEquals(1, assetAllocations.size());
        Assert.assertEquals("assetName", assetAllocations.get(0).getName());
        Assert.assertEquals(BigDecimal.ZERO, assetAllocations.get(0).getAccountPercent());
        Assert.assertEquals("assetCode", assetAllocations.get(0).getAssetCode());
        Assert.assertEquals("assetId", assetAllocations.get(0).getAssetId());
        Assert.assertEquals("Cash", assetAllocations.get(0).getAssetSector());
        Assert.assertEquals(BigDecimal.valueOf(2), assetAllocations.get(0).getBalance());
        Assert.assertEquals(BigDecimal.valueOf(2), assetAllocations.get(0).getExternalBalance());
        Assert.assertEquals(BigDecimal.valueOf(0), assetAllocations.get(0).getInternalBalance());
    }
}

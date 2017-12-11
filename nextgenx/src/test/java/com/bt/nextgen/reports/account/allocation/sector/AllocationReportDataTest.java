package com.bt.nextgen.reports.account.allocation.sector;

import com.bt.nextgen.api.portfolio.v3.model.allocation.sector.AssetAllocationBySectorDto;
import com.bt.nextgen.core.reporting.ReportFormat;
import com.bt.nextgen.core.reporting.ReportFormatter;
import com.btfin.panorama.service.integration.asset.AssetType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;

@RunWith(MockitoJUnitRunner.class)
public class AllocationReportDataTest {

    private static final String SEPARATOR = " &#183 ";

    private AssetAllocationBySectorDto allocation;
    private AssetAllocationBySectorDto externalAllocation;

    @Before
    public void setup() {
        allocation = Mockito.mock(AssetAllocationBySectorDto.class);
        Mockito.when(allocation.getName()).thenReturn("assetName");
        Mockito.when(allocation.getAssetCode()).thenReturn("assetCode");
        Mockito.when(allocation.getAssetType()).thenReturn(AssetType.MANAGED_FUND.name());
        Mockito.when(allocation.getSource()).thenReturn(null);
        Mockito.when(allocation.getUnits()).thenReturn(BigDecimal.ONE);
        Mockito.when(allocation.getBalance()).thenReturn(BigDecimal.TEN);
        Mockito.when(allocation.getAllocationPercentage()).thenReturn(BigDecimal.ONE);
        Mockito.when(allocation.getPending()).thenReturn(true);
        Mockito.when(allocation.getIsExternal()).thenReturn(false);

        externalAllocation = Mockito.mock(AssetAllocationBySectorDto.class);
        Mockito.when(externalAllocation.getName()).thenReturn("assetName");
        Mockito.when(externalAllocation.getAssetCode()).thenReturn("assetCode");
        Mockito.when(externalAllocation.getAssetType()).thenReturn(AssetType.SHARE.name());
        Mockito.when(externalAllocation.getSource()).thenReturn("assetSource");
        Mockito.when(externalAllocation.getUnits()).thenReturn(BigDecimal.ONE);
        Mockito.when(externalAllocation.getBalance()).thenReturn(BigDecimal.TEN);
        Mockito.when(externalAllocation.getAllocationPercentage()).thenReturn(BigDecimal.ONE);
        Mockito.when(externalAllocation.getPending()).thenReturn(false);
        Mockito.when(externalAllocation.getIsExternal()).thenReturn(true);
    }

    @Test
    public void testGetAsset() {
        AllocationReportData reportData = new AllocationReportData(allocation);

        String expectedTitle = "<b>assetCode</b>" + SEPARATOR + "assetName";
        String expectedBalance = ReportFormatter.format(ReportFormat.CURRENCY, true, allocation.getBalance());
        String expectedPercent = ReportFormatter.format(ReportFormat.PERCENTAGE, true, allocation.getAllocationPercentage());

        Assert.assertEquals(expectedTitle, reportData.getAssetTitle());
        Assert.assertEquals(expectedBalance, reportData.getBalance());
        Assert.assertEquals(expectedPercent, reportData.getAllocationPercentage());
        Assert.assertEquals("-", reportData.getUnits());
        Assert.assertFalse(reportData.getIsExternal());
    }

    @Test
    public void testGetExternalAsset() {
        AllocationReportData reportData = new AllocationReportData(externalAllocation);

        String expectedTitle = "assetSource<br/>assetCode" + SEPARATOR + "assetName";
        String expectedUnits = ReportFormatter.format(ReportFormat.UNITS, true, allocation.getUnits());

        Assert.assertEquals(expectedTitle, reportData.getExternalAssetTitle());
        Assert.assertEquals(expectedUnits, reportData.getUnits());
        Assert.assertTrue(reportData.getIsExternal());

    }
}

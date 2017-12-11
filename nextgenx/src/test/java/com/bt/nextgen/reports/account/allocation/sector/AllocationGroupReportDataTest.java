package com.bt.nextgen.reports.account.allocation.sector;

import java.math.BigDecimal;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.bt.nextgen.api.portfolio.v3.model.allocation.sector.AllocationBySectorDto;
import com.bt.nextgen.api.portfolio.v3.model.allocation.sector.AssetAllocationBySectorDto;
import com.bt.nextgen.core.reporting.ReportFormat;
import com.bt.nextgen.core.reporting.ReportFormatter;

@RunWith(MockitoJUnitRunner.class)
public class AllocationGroupReportDataTest {

    private AssetAllocationBySectorDto allocation;
    private AssetAllocationBySectorDto externalAllocation;

    @Before
    public void setup() {
        allocation = Mockito.mock(AssetAllocationBySectorDto.class);
        Mockito.when(allocation.getBalance()).thenReturn(BigDecimal.TEN);
        Mockito.when(allocation.getAllocationPercentage()).thenReturn(BigDecimal.valueOf(0.2));

        externalAllocation = Mockito.mock(AssetAllocationBySectorDto.class);
        Mockito.when(externalAllocation.getBalance()).thenReturn(BigDecimal.TEN);
        Mockito.when(externalAllocation.getAllocationPercentage()).thenReturn(BigDecimal.valueOf(0.6));
    }

    @Test
    public void testGetGroup() {
        AllocationGroupReportData reportData = new AllocationGroupReportData("groupName", true, Arrays.asList(
                (AllocationBySectorDto) allocation, (AllocationBySectorDto) externalAllocation));

        String expectedBalance = ReportFormatter.format(ReportFormat.CURRENCY, true, BigDecimal.valueOf(20));
        String expectedPercent = ReportFormatter.format(ReportFormat.PERCENTAGE, true, BigDecimal.valueOf(0.8));

        Assert.assertEquals("groupName", reportData.getGroupName());
        Assert.assertEquals(expectedBalance, reportData.getGroupBalance());
        Assert.assertEquals(expectedPercent, reportData.getGroupAllocationPercentage());
        Assert.assertEquals(2, reportData.getAllocations().size());
        Assert.assertTrue(reportData.getDisplayGroupDetails());
    }
}

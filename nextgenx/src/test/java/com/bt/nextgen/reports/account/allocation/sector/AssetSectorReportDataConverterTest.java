package com.bt.nextgen.reports.account.allocation.sector;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.bt.nextgen.api.portfolio.v3.model.allocation.sector.AggregatedAllocationBySectorDto;
import com.bt.nextgen.api.portfolio.v3.model.allocation.sector.AllocationBySectorDto;
import com.bt.nextgen.api.portfolio.v3.model.allocation.sector.AssetAllocationBySectorDto;

@RunWith(MockitoJUnitRunner.class)
public class AssetSectorReportDataConverterTest {

    @InjectMocks
    AssetSectorReportDataConverter converter;

    @Test
    public void testGetReportData_When_GroupType_IndustrySector() {
        AssetAllocationBySectorDto allocation1 = Mockito.mock(AssetAllocationBySectorDto.class);
        Mockito.when(allocation1.getIndustrySectorSubSector()).thenReturn("Diversified");

        AssetAllocationBySectorDto allocation2 = Mockito.mock(AssetAllocationBySectorDto.class);
        Mockito.when(allocation2.getIndustrySectorSubSector()).thenReturn("Other");

        AssetAllocationBySectorDto allocation3 = Mockito.mock(AssetAllocationBySectorDto.class);
        Mockito.when(allocation3.getIndustrySectorSubSector()).thenReturn("Other");

        List<AllocationBySectorDto> allocations = Arrays.asList((AllocationBySectorDto) allocation1,
                (AllocationBySectorDto) allocation2, (AllocationBySectorDto) allocation3);

        AggregatedAllocationBySectorDto cashSector = Mockito.mock(AggregatedAllocationBySectorDto.class);
        Mockito.when(cashSector.getName()).thenReturn("Cash");
        Mockito.when(cashSector.getAllocations()).thenReturn(allocations);

        AggregatedAllocationBySectorDto groupedSector = Mockito.mock(AggregatedAllocationBySectorDto.class);
        Mockito.when(groupedSector.getName()).thenReturn("Australian Property");
        Mockito.when(groupedSector.getAllocations()).thenReturn(allocations);

        List<AllocationBySectorDto> sectors = Arrays.asList((AllocationBySectorDto) cashSector,
                (AllocationBySectorDto) groupedSector);

        AssetAllocationReportData data = converter.getReportData(AllocationGroupType.INDUSTRY_SUB_SECTOR, sectors);

        List<AssetSectorReportData> sectorData = data.getSectorList();
        Assert.assertEquals(2, sectorData.size());

        // First ungrouped sector
        AssetSectorReportData ungroupedCashSector = sectorData.get(0);
        Assert.assertEquals("Cash", ungroupedCashSector.getSectorName());
        Assert.assertTrue(ungroupedCashSector.getIsCashSector());

        List<AllocationGroupReportData> groups = ungroupedCashSector.getAllocationGroups();
        Assert.assertEquals(1, groups.size());

        AllocationGroupReportData group = groups.get(0);
        Assert.assertEquals(null, group.getGroupName());
        Assert.assertFalse(group.getDisplayGroupDetails());
        Assert.assertEquals(3, group.getAllocations().size());

        // Second grouped sector
        AssetSectorReportData groupedPropertySector = sectorData.get(1);
        Assert.assertEquals("Australian Property", groupedPropertySector.getSectorName());
        Assert.assertFalse(groupedPropertySector.getIsCashSector());

        groups = groupedPropertySector.getAllocationGroups();
        Assert.assertEquals(2, groups.size());

        group = groups.get(0);
        Assert.assertEquals("Other", group.getGroupName());
        Assert.assertTrue(group.getDisplayGroupDetails());
        Assert.assertEquals(2, group.getAllocations().size());

        group = groups.get(1);
        Assert.assertEquals("Diversified", group.getGroupName());
        Assert.assertTrue(group.getDisplayGroupDetails());
        Assert.assertEquals(1, group.getAllocations().size());
    }

    @Test
    public void testGetReportData_noData() {
        AssetAllocationReportData sectorData = converter.getReportData(null, null);
        Assert.assertNotNull(sectorData);
        Assert.assertTrue(sectorData.getSectorList().isEmpty());
    }

    @Test
    public void testGetReportData_When_GroupType_Subclass() {
        AssetAllocationBySectorDto allocation1 = Mockito.mock(AssetAllocationBySectorDto.class);
        Mockito.when(allocation1.getAssetSubclass()).thenReturn("International Shares Emerging Markets");

        AssetAllocationBySectorDto allocation2 = Mockito.mock(AssetAllocationBySectorDto.class);
        Mockito.when(allocation2.getAssetSubclass()).thenReturn("International Shares Unhedged");

        List<AllocationBySectorDto> allocations = Arrays.asList((AllocationBySectorDto) allocation1,
                (AllocationBySectorDto) allocation2);

        AggregatedAllocationBySectorDto cashSector = Mockito.mock(AggregatedAllocationBySectorDto.class);
        Mockito.when(cashSector.getName()).thenReturn("Cash");
        Mockito.when(cashSector.getAllocations()).thenReturn(allocations);

        AggregatedAllocationBySectorDto groupedSector = Mockito.mock(AggregatedAllocationBySectorDto.class);
        Mockito.when(groupedSector.getName()).thenReturn("International shares");
        Mockito.when(groupedSector.getAllocations()).thenReturn(allocations);

        List<AllocationBySectorDto> sectors = Arrays.asList((AllocationBySectorDto) cashSector,
                (AllocationBySectorDto) groupedSector);

        AssetAllocationReportData data = converter.getReportData(AllocationGroupType.ASSET_SUB_CLASS, sectors);

        List<AssetSectorReportData> sectorData = data.getSectorList();
        Assert.assertEquals(2, sectorData.size());

        AssetSectorReportData ungroupedCashSector = sectorData.get(0);
        Assert.assertEquals("Cash", ungroupedCashSector.getSectorName());
        Assert.assertTrue(ungroupedCashSector.getIsCashSector());

        List<AllocationGroupReportData> groups = ungroupedCashSector.getAllocationGroups();
        Assert.assertEquals(2, groups.size());

        AllocationGroupReportData group = groups.get(0);
        Assert.assertEquals("International Shares Emerging Markets", group.getGroupName());
        Assert.assertEquals(1, group.getAllocations().size());

        AssetSectorReportData groupedSharesSector = sectorData.get(1);
        Assert.assertEquals("International shares", groupedSharesSector.getSectorName());
        Assert.assertFalse(groupedSharesSector.getIsCashSector());

        groups = groupedSharesSector.getAllocationGroups();
        Assert.assertEquals(2, groups.size());

        group = groups.get(0);
        Assert.assertEquals("International Shares Emerging Markets", group.getGroupName());
        Assert.assertEquals(1, group.getAllocations().size());

        group = groups.get(1);
        Assert.assertEquals("International Shares Unhedged", group.getGroupName());
        Assert.assertEquals(1, group.getAllocations().size());
    }
}
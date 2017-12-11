package com.bt.nextgen.reports.account.allocation.sector;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.bt.nextgen.api.portfolio.v3.model.allocation.sector.AggregatedAllocationBySectorDto;
import com.bt.nextgen.api.portfolio.v3.model.allocation.sector.AllocationBySectorDto;
import com.bt.nextgen.api.portfolio.v3.model.allocation.sector.AssetAllocationBySectorDto;
import com.bt.nextgen.api.portfolio.v3.model.allocation.sector.HoldingAllocationBySectorDto;
import com.bt.nextgen.api.portfolio.v3.model.allocation.sector.KeyedAllocBySectorDto;
import com.bt.nextgen.api.portfolio.v3.model.allocation.sector.TermDepositAggregatedAssetAllocationBySectorDto;
import com.bt.nextgen.api.portfolio.v3.model.valuation.DatedValuationKey;
import com.bt.nextgen.api.portfolio.v3.service.allocation.AllocationBySectorDtoService;
import com.bt.nextgen.api.portfolio.v3.service.allocation.HoldingSource;
import com.bt.nextgen.service.avaloq.asset.AssetImpl;
import com.bt.nextgen.service.avaloq.asset.TermDepositAssetImpl;
import com.bt.nextgen.service.integration.asset.AssetClass;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.btfin.panorama.service.integration.asset.AssetType;

@RunWith(MockitoJUnitRunner.class)
public class AssetAllocationCsvReportTest {
    @InjectMocks
    private AssetAllocationCsvReport allocationCsvReport;

    @Mock
    private AllocationBySectorDtoService allocationBySectorDtoService;

    private KeyedAllocBySectorDto allocationDto;
    private String accountId;
    private String effectiveDate;
    private DatedValuationKey key;
    private static final String ACCOUNT_ID = "account-id";
    private static final String EFFECTIVE_DATE = "effective-date";

    @Before
    public void setup() {
        accountId = "975AF9B7FE27CF0A2A7134DC4BBC3EDD510AAB21532150E0";
        effectiveDate = "2014-09-09";
        key = new DatedValuationKey(accountId, new DateTime(effectiveDate), false);

        allocationDto = mockAllocationDtoService();
        when(allocationBySectorDtoService.find((Matchers.any(DatedValuationKey.class)), any(ServiceErrorsImpl.class)))
                .thenReturn(allocationDto);
    }

    @Test
    public void testGetAssetAllocationCsvData() throws ParseException {
        Map<String, Object> params = new HashMap<>();
        Map<String, Object> dataCollections = new HashMap<>();
        params.put(ACCOUNT_ID, accountId);
        params.put(EFFECTIVE_DATE, effectiveDate);

        List<AllocationCsvReportData> allocationList = (List<AllocationCsvReportData>) allocationCsvReport.getData(params,
                dataCollections);

        assertNotNull(allocationList);
        assertEquals(4, allocationList.size());

        assertEquals("Asset allocation", allocationCsvReport.getReportType(params, dataCollections));

        AllocationCsvReportData allocation1 = allocationList.get(0);
        AllocationCsvReportData allocation2 = allocationList.get(1);
        AllocationCsvReportData allocation3 = allocationList.get(2);

        assertEquals("sub sector 1", allocation1.getIndustrySector());
        assertEquals("sub sector code 1", allocation1.getIndustryCategory());
        assertEquals("Australian shares", allocation1.getAssetClass());
        assertEquals("assetCode", allocation1.getAssetCode());
        assertEquals("source assetName", allocation1.getAssetName());
        assertEquals("Panorama", allocation1.getExternal());
        assertEquals("sub sector 1", allocation2.getIndustrySector());
        assertEquals("sub sector code 1", allocation2.getIndustryCategory());
        assertEquals("assetName", allocation2.getAssetName());
        assertEquals("External", allocation2.getExternal());
        assertEquals("10.0000", allocation2.getQuantity());
        assertEquals("10", allocation1.getQuantity());
        assertEquals("$1,000.00", allocation1.getBalance());
        assertEquals("100.00%", allocation1.getAllocationPercentage());
        assertEquals(null, allocation3.getQuantity());
        assertNull(allocation1.getAssetSubClass());
    }

    private KeyedAllocBySectorDto mockAllocationDtoService() {

        AssetImpl testAsset = new AssetImpl();
        testAsset.setAssetType(AssetType.SHARE);
        testAsset.setAssetClass(AssetClass.AUSTRALIAN_SHARES);
        testAsset.setAssetCode("assetCode");
        testAsset.setAssetName("assetName");
        testAsset.setIndustrySector("Australian Shares");
        testAsset.setIndustryType("sub sector 1");
        testAsset.setIndustryTypeCode("sub sector code 1");

        AssetImpl testAsset1 = new AssetImpl();
        testAsset1.setAssetType(AssetType.MANAGED_FUND);
        testAsset1.setAssetClass(AssetClass.AUSTRALIAN_PROPERTY);
        testAsset1.setAssetCode("assetCode");
        testAsset1.setAssetName("assetName");
        testAsset1.setIndustrySector("Australian Property");
        testAsset1.setIndustryType("sub sector 1");
        testAsset1.setIndustryTypeCode("sub sector code 1");

        TermDepositAssetImpl testTDAsset = new TermDepositAssetImpl();
        testTDAsset.setAssetType(AssetType.TERM_DEPOSIT);
        testTDAsset.setAssetClass(AssetClass.CASH);
        testTDAsset.setAssetCode("assetCode");
        testTDAsset.setAssetName("assetName");
        testTDAsset.setMaturityDate(new DateTime());

        List<AllocationBySectorDto> fakeHoldings = new ArrayList<AllocationBySectorDto>();
        List<AllocationBySectorDto> holdings = new ArrayList<AllocationBySectorDto>();

        HoldingSource holdingSource = Mockito.mock(HoldingSource.class);
        Mockito.when(holdingSource.getAsset()).thenReturn(testAsset);
        Mockito.when(holdingSource.getMarketValue()).thenReturn(BigDecimal.valueOf(1000));
        Mockito.when(holdingSource.getUnits()).thenReturn(BigDecimal.valueOf(10));
        Mockito.when(holdingSource.isPending()).thenReturn(false);
        Mockito.when(holdingSource.isExternal()).thenReturn(false);
        Mockito.when(holdingSource.getExternalSource()).thenReturn("source");
        Mockito.when(holdingSource.getSource()).thenReturn(testAsset);

        HoldingSource holdingSource1 = Mockito.mock(HoldingSource.class);
        Mockito.when(holdingSource1.getAsset()).thenReturn(testAsset);
        Mockito.when(holdingSource1.getMarketValue()).thenReturn(BigDecimal.valueOf(1000));
        Mockito.when(holdingSource1.getUnits()).thenReturn(BigDecimal.valueOf(10));
        Mockito.when(holdingSource1.isPending()).thenReturn(false);
        Mockito.when(holdingSource1.isExternal()).thenReturn(true);
        Mockito.when(holdingSource1.getExternalSource()).thenReturn(null);
        Mockito.when(holdingSource1.getSource()).thenReturn(testAsset);


        AllocationBySectorDto holding = new HoldingAllocationBySectorDto(Collections.singletonList(holdingSource),
                BigDecimal.valueOf(1000), false);

        AllocationBySectorDto allocationHolding = new HoldingAllocationBySectorDto(Collections.singletonList(holdingSource1),
                BigDecimal.valueOf(1000), false);
        holdings.add(holding);
        List<AllocationBySectorDto> allocationHoldings = new ArrayList<>();
        allocationHoldings.add(allocationHolding);

        List<AllocationBySectorDto> testList = new ArrayList<AllocationBySectorDto>();
        testList.add(new AssetAllocationBySectorDto(testAsset, false, holdings));
        testList.add(new AssetAllocationBySectorDto(testAsset1, false, allocationHoldings));
        testList.add(new AssetAllocationBySectorDto(testAsset, false, fakeHoldings));
        testList.add(new TermDepositAggregatedAssetAllocationBySectorDto(testTDAsset, fakeHoldings, null));

        List<AllocationBySectorDto> testList2 = new ArrayList<AllocationBySectorDto>();

        List<AllocationBySectorDto> testList3 = new ArrayList<AllocationBySectorDto>();

        // Fake allocation data: details are tested in dto service unit tests.
        AggregatedAllocationBySectorDto allocation = new AggregatedAllocationBySectorDto("Allocation", testList);
        AggregatedAllocationBySectorDto allocation2 = new AggregatedAllocationBySectorDto("Allocation2", testList2);
        AggregatedAllocationBySectorDto allocation3 = new AggregatedAllocationBySectorDto("Allocation3", testList3);

        List<AllocationBySectorDto> allocations = new ArrayList<>();
        allocations.add(allocation);
        allocations.add(allocation2);
        allocations.add(allocation3);

        return new KeyedAllocBySectorDto("AllocationTest", allocations, key);
    }
}

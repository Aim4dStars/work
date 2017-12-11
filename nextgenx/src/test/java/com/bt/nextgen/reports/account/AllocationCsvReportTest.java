package com.bt.nextgen.reports.account;

import com.bt.nextgen.api.account.v2.model.DatedValuationKey;
import com.bt.nextgen.api.account.v2.model.allocation.sector.AggregatedAllocationBySectorDto;
import com.bt.nextgen.api.account.v2.model.allocation.sector.AllocationBySectorDto;
import com.bt.nextgen.api.account.v2.model.allocation.sector.AssetAllocationBySectorDto;
import com.bt.nextgen.api.account.v2.model.allocation.sector.KeyedAllocBySectorDto;
import com.bt.nextgen.api.account.v2.model.allocation.sector.TermDepositAggregatedAssetAllocationBySectorDto;
import com.bt.nextgen.api.account.v2.service.allocation.AllocationBySectorDtoService;
import com.bt.nextgen.core.api.UriMappingConstants;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.avaloq.asset.AssetImpl;
import com.bt.nextgen.service.avaloq.asset.TermDepositAssetImpl;
import com.bt.nextgen.service.integration.asset.AssetClass;
import com.btfin.panorama.service.integration.asset.AssetType;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AllocationCsvReportTest {
    @InjectMocks
    private AllocationCsvReport allocationCsvReport;

    @Mock
    private AllocationBySectorDtoService allocationBySectorDtoService;

    private KeyedAllocBySectorDto allocationDto;
    private String accountId;
    private String effectiveDate;
    private DatedValuationKey key;

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
    public void testFlatteningOfAllocationStructure() throws ParseException {
        Map<String, String> params = new HashMap<>();
        params.put(UriMappingConstants.ACCOUNT_ID_URI_MAPPING, accountId);
        params.put(UriMappingConstants.EFFECTIVE_DATE_PARAMETER_MAPPING, effectiveDate);

        List<AllocationBySectorDto> allocationList = allocationCsvReport.getAllocationMap(params);

        assertNotNull(allocationList);
        assertEquals(3, allocationList.size());
    }

    private KeyedAllocBySectorDto mockAllocationDtoService() {

        AssetImpl testAsset = new AssetImpl();
        testAsset.setAssetType(AssetType.CASH);
        testAsset.setAssetClass(AssetClass.CASH);
        testAsset.setAssetCode("assetCode");
        testAsset.setAssetName("assetName");

        TermDepositAssetImpl testTDAsset = new TermDepositAssetImpl();
        testTDAsset.setAssetType(AssetType.TERM_DEPOSIT);
        testTDAsset.setAssetClass(AssetClass.CASH);
        testTDAsset.setAssetCode("assetCode");
        testTDAsset.setAssetName("assetName");
        testTDAsset.setMaturityDate(new DateTime());

        List<AllocationBySectorDto> fakeHoldings = new ArrayList<AllocationBySectorDto>();

        List<AllocationBySectorDto> testList = new ArrayList<AllocationBySectorDto>();
        testList.add(new AssetAllocationBySectorDto(testAsset, false, fakeHoldings));
        testList.add(new TermDepositAggregatedAssetAllocationBySectorDto(testTDAsset, fakeHoldings, null));

        List<AllocationBySectorDto> testList2 = new ArrayList<AllocationBySectorDto>();

        List<AllocationBySectorDto> testList3 = new ArrayList<AllocationBySectorDto>();
        testList.add(new AssetAllocationBySectorDto(testAsset, false, fakeHoldings));

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

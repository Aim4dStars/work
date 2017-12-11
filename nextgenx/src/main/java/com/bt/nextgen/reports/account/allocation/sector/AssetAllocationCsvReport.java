package com.bt.nextgen.reports.account.allocation.sector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;

import com.bt.nextgen.api.portfolio.v3.model.allocation.sector.AggregatedAllocationBySectorDto;
import com.bt.nextgen.api.portfolio.v3.model.allocation.sector.AllocationBySectorDto;
import com.bt.nextgen.api.portfolio.v3.model.allocation.sector.KeyedAllocBySectorDto;
import com.bt.nextgen.api.portfolio.v3.model.valuation.DatedValuationKey;
import com.bt.nextgen.api.portfolio.v3.service.allocation.AllocationBySectorDtoService;
import com.bt.nextgen.core.reporting.stereotype.Report;
import com.bt.nextgen.reports.account.common.AccountReportV2;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;

@Report("assetAllocationAssetClassCsv")
public class AssetAllocationCsvReport extends AccountReportV2 {

    @Autowired
    private AllocationBySectorDtoService allocationDtoService;

    private static final String ACCOUNT_ID = "account-id";
    private static final String EFFECTIVE_DATE = "effective-date";
    private static final String INCLUDE_EXTERNAL = "include-external";
    private static final String REPORT_NAME = "Asset allocation";
    
    @Override
    public Collection<?> getData(Map<String, Object> params, Map<String, Object> dataCollections) {
        List<AllocationCsvReportData> sectorAllocations = new ArrayList<>();
        String accountId = (String) params.get(ACCOUNT_ID);
        String effectiveDate = (String) params.get(EFFECTIVE_DATE);
        String includeExternal = (String) params.get(INCLUDE_EXTERNAL);
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        DatedValuationKey key = new DatedValuationKey(accountId, new DateTime(effectiveDate),
                Boolean.parseBoolean(includeExternal));
        KeyedAllocBySectorDto allocationData = allocationDtoService.find(key, serviceErrors);
        List<AllocationBySectorDto> allocationDtos = allocationData.getAllocations();
        for (AllocationBySectorDto allocationDto : allocationDtos) {
            AggregatedAllocationBySectorDto aggregateAllocationDto = (AggregatedAllocationBySectorDto) allocationDto;
            for (AllocationBySectorDto sectorAllocationDto : aggregateAllocationDto.getAllocations()) {
                sectorAllocations.add(new AllocationCsvReportData(sectorAllocationDto));
            }
        }
        return sectorAllocations;
    }
    
    @Override
    public String getReportType(Map<String, Object> params, Map<String, Object> dataCollections) {
        return REPORT_NAME;
    }
}

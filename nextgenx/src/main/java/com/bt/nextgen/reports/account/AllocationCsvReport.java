package com.bt.nextgen.reports.account;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;

import com.bt.nextgen.api.account.v2.model.DatedValuationKey;
import com.bt.nextgen.api.account.v2.model.allocation.sector.AggregatedAllocationBySectorDto;
import com.bt.nextgen.api.account.v2.model.allocation.sector.AllocationBySectorDto;
import com.bt.nextgen.api.account.v2.model.allocation.sector.KeyedAllocBySectorDto;
import com.bt.nextgen.api.account.v2.service.allocation.AllocationBySectorDtoService;
import com.bt.nextgen.core.api.UriMappingConstants;
import com.bt.nextgen.core.reporting.stereotype.ReportBean;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;


public class AllocationCsvReport {

    private static final String PARAM_INCLUDE_EXTERNAL = "include-external";
    @Autowired
    private AllocationBySectorDtoService allocationBySectorDtoService;

    @ReportBean("allocations")
    public List<AllocationBySectorDto> getAllocationMap(Map<String, String> params) {

        String accountId = params.get(UriMappingConstants.ACCOUNT_ID_URI_MAPPING);
        String effectiveDateStr = params.get(UriMappingConstants.EFFECTIVE_DATE_PARAMETER_MAPPING);

        String includeExternal = params.get(PARAM_INCLUDE_EXTERNAL);
        boolean includeExternalBool = Boolean.parseBoolean(includeExternal);

        if (StringUtils.isBlank(effectiveDateStr)) {
            effectiveDateStr = null;
        }

        DateTime effectiveDate = new DateTime(effectiveDateStr);
        DatedValuationKey key = new DatedValuationKey(accountId, effectiveDate, includeExternalBool);
        ServiceErrors serviceErrors = new FailFastErrorsImpl();

        KeyedAllocBySectorDto allocation = allocationBySectorDtoService.find(key, serviceErrors);

        return flattenAllocationStructure(allocation);
    }

    private List<AllocationBySectorDto> flattenAllocationStructure(KeyedAllocBySectorDto allocation) {

        List<AllocationBySectorDto> allocationsList = new ArrayList<>();

        for (AllocationBySectorDto sector : allocation.getAllocations()) {
            allocationsList.addAll(((AggregatedAllocationBySectorDto) sector).getAllocations());
        }   
        return allocationsList;

    }
}

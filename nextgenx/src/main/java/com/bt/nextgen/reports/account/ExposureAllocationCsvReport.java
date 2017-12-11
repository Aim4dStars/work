package com.bt.nextgen.reports.account;

import com.bt.nextgen.api.account.v2.model.DatedValuationKey;
import com.bt.nextgen.api.account.v2.model.allocation.exposure.AggregateAllocationByExposureDto;
import com.bt.nextgen.api.account.v2.model.allocation.exposure.AllocationByExposureDto;
import com.bt.nextgen.api.account.v2.model.allocation.exposure.AssetAllocationByExposureDto;
import com.bt.nextgen.api.account.v2.model.allocation.exposure.KeyedAllocByExposureDto;
import com.bt.nextgen.api.account.v2.service.allocation.AllocationByExposureDtoService;
import com.bt.nextgen.core.api.UriMappingConstants;
import com.bt.nextgen.core.reporting.BaseReport;
import com.bt.nextgen.core.reporting.stereotype.Report;
import com.bt.nextgen.core.reporting.stereotype.ReportBean;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Report("assetAllocationExposureCsvReport")
@PreAuthorize("isAuthenticated() and hasPermission(null, 'View_account_reports')")
public class ExposureAllocationCsvReport extends BaseReport {

    private static final String PARAM_INCLUDE_EXTERNAL = "include-external";

    @Autowired
    private AllocationByExposureDtoService allocationDtoService;

    @ReportBean("exposureType")
    public String getExposureType(Map<String, String> params) {
        return params.get("exposureType");
    }

    @ReportBean("allocations")
    public List<AssetAllocationByExposureDto> getExposureAllocation(Map<String, String> params) {

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

        KeyedAllocByExposureDto keyedAllocDto = allocationDtoService.find(key, serviceErrors);

        return getAssetAllocations(keyedAllocDto);
    }

    private List<AssetAllocationByExposureDto> getAssetAllocations(KeyedAllocByExposureDto accountAllocation) {
        List<AllocationByExposureDto> accountTypeAllocations = accountAllocation.getAllocations();
        List<AssetAllocationByExposureDto> assetAllocations = new ArrayList<>();
        for (AllocationByExposureDto accountTypeAllocation : accountTypeAllocations) {
            AggregateAllocationByExposureDto aggregateAccountTypeAllocation = (AggregateAllocationByExposureDto) accountTypeAllocation;
            for (AllocationByExposureDto allocation : aggregateAccountTypeAllocation.getAllocations()) {
                AssetAllocationByExposureDto assetAllocation = (AssetAllocationByExposureDto) allocation;
                assetAllocations.add((AssetAllocationByExposureDto) assetAllocation);
            }
        }
        return assetAllocations;
    }

}

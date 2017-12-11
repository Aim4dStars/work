package com.bt.nextgen.reports.account.cgt;

import com.bt.nextgen.api.cgt.model.CgtDto;
import com.bt.nextgen.api.cgt.model.CgtGroupDto;
import com.bt.nextgen.api.cgt.model.CgtKey;
import com.bt.nextgen.api.cgt.model.CgtSecurity;
import com.bt.nextgen.api.cgt.service.RealisedCgtDtoService;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.operation.FindByKey;
import com.bt.nextgen.core.reporting.stereotype.Report;
import com.bt.nextgen.core.reporting.stereotype.ReportBean;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.bt.nextgen.core.api.UriMappingConstants.ACCOUNT_ID_URI_MAPPING;
import static com.bt.nextgen.core.api.UriMappingConstants.END_DATE_PARAMETER_MAPPING;
import static com.bt.nextgen.core.api.UriMappingConstants.START_DATE_PARAMETER_MAPPING;

@Report("realisedCapitalGainsTaxCsvReport")
@PreAuthorize("isAuthenticated() and hasPermission(null, 'View_account_reports')")
public class RealisedCapitalGainsTaxCsvReport {

    private static final String GROUP_BY_ASSET_TYPE = "ASSET_TYPE";

    @Autowired
    private RealisedCgtDtoService realisedCgtDtoService;

    @ReportBean("realisedCgt")
    public List<CgtSecurity> getCgtData(Map<String, String> params) {
        String accountId = params.get(ACCOUNT_ID_URI_MAPPING);
        String startDateStr = params.get(START_DATE_PARAMETER_MAPPING);
        String endDateStr = params.get(END_DATE_PARAMETER_MAPPING);

        startDateStr = StringUtils.isBlank(startDateStr) ? null : startDateStr;
        endDateStr = StringUtils.isBlank(endDateStr) ? null : endDateStr;

        DateTime startDate = new DateTime(startDateStr);
        DateTime endDate = new DateTime(endDateStr);

        CgtKey key = new CgtKey(accountId, startDate, endDate, GROUP_BY_ASSET_TYPE);
        ApiResponse response = new FindByKey<>(ApiVersion.CURRENT_VERSION, realisedCgtDtoService, key).performOperation();

        CgtDto cgtDto = (CgtDto) response.getData();
        List<CgtSecurity> realisedCgtData = new ArrayList<CgtSecurity>();

        // Flatten cgtDto into list of cgtSecurityDtos
        if (cgtDto.getCgtGroupDtoList() != null) {
            for (CgtGroupDto cgtGroupDto : cgtDto.getCgtGroupDtoList()) {
                if (cgtGroupDto.getCgtSecurities() != null) {
                    for (CgtSecurity cgtAssetDto : cgtGroupDto.getCgtSecurities()) {
                        realisedCgtData.add(cgtAssetDto);
                    }
                }
            }
        }

        return realisedCgtData;
    }
}
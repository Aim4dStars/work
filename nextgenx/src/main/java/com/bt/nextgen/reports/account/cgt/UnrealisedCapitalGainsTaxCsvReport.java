package com.bt.nextgen.reports.account.cgt;

import com.bt.nextgen.api.cgt.model.CgtDto;
import com.bt.nextgen.api.cgt.model.CgtGroupDto;
import com.bt.nextgen.api.cgt.model.CgtKey;
import com.bt.nextgen.api.cgt.model.CgtSecurity;
import com.bt.nextgen.api.cgt.service.UnrealisedCgtDtoService;
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

@Report("unrealisedCapitalGainsTaxCsvReport")
@PreAuthorize("isAuthenticated() and hasPermission(null, 'View_account_reports')")
public class UnrealisedCapitalGainsTaxCsvReport {

    private static final String GROUP_BY_ASSET_TYPE = "ASSET_TYPE";

    @Autowired
    private UnrealisedCgtDtoService unrealisedCgtDtoService;

    @ReportBean("unrealisedCgt")
    public List<CgtSecurity> getCgtData(Map<String, String> params) {
        String accountId = params.get(ACCOUNT_ID_URI_MAPPING);
        String dateString = params.get("effective-date");

        dateString = StringUtils.isBlank(dateString) ? null : dateString;
        DateTime effectiveDate = new DateTime(dateString);

        CgtKey key = new CgtKey(accountId, effectiveDate, effectiveDate, GROUP_BY_ASSET_TYPE);
        ApiResponse response = new FindByKey<>(ApiVersion.CURRENT_VERSION, unrealisedCgtDtoService, key).performOperation();

        CgtDto cgtDto = (CgtDto) response.getData();
        List<CgtSecurity> unrealisedCgtData = new ArrayList<CgtSecurity>();

        // Flatten cgtDto into list of cgtSecurityDtos
        if (cgtDto.getCgtGroupDtoList() != null) {
            for (CgtGroupDto cgtGroupDto : cgtDto.getCgtGroupDtoList()) {
                if (cgtGroupDto.getCgtSecurities() != null) {
                    for (CgtSecurity cgtSecurityDto : cgtGroupDto.getCgtSecurities()) {

                        unrealisedCgtData.add(cgtSecurityDto);

                    }
                }
            }
        }

        return unrealisedCgtData;
    }
}

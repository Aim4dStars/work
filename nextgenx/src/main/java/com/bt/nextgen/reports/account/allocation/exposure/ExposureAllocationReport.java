package com.bt.nextgen.reports.account.allocation.exposure;

import com.bt.nextgen.api.portfolio.v3.model.allocation.exposure.AllocationByExposureDto;
import com.bt.nextgen.api.portfolio.v3.model.allocation.exposure.KeyedAllocByExposureDto;
import com.bt.nextgen.api.portfolio.v3.model.valuation.DatedValuationKey;
import com.bt.nextgen.api.portfolio.v3.service.allocation.AllocationByExposureDtoService;
import com.bt.nextgen.core.reporting.ReportFormat;
import com.bt.nextgen.core.reporting.ReportFormatter;
import com.bt.nextgen.core.reporting.stereotype.Report;
import com.bt.nextgen.core.reporting.stereotype.ReportBean;
import com.bt.nextgen.reports.account.common.AccountReportV2;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.userinformation.UserExperience;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

@Report("assetAllocationExposureV2")
public class ExposureAllocationReport extends AccountReportV2 {

    private static final String ACCOUNT_ID = "account-id";
    private static final String EFFECTIVE_DATE = "effective-date";
    private static final String INCLUDE_EXTERNAL = "include-external";

    private static final String DISCLAIMER_KEY = "DS-IP-0002";
    private static final String DISCLAIMER_KEY_DIRECT = "DS-IP-0200";

    @Autowired
    private AllocationByExposureDtoService allocationDtoService;

    @ReportBean("startDate")
    public String getStartDate(Map<String, Object> params) {
        String effectiveDate = (String) params.get(EFFECTIVE_DATE);
        if (effectiveDate == null) {
            return ReportFormatter.format(ReportFormat.SHORT_DATE, new DateTime());
        } else {
            return ReportFormatter.format(ReportFormat.SHORT_DATE, new DateTime(effectiveDate));
        }
    }

    @Override
    public Collection<?> getData(Map<String, Object> params, Map<String, Object> dataCollections) {
        String accountId = (String) params.get(ACCOUNT_ID);

        String effectiveDateStr = (String) params.get(EFFECTIVE_DATE);
        effectiveDateStr = StringUtils.isBlank(effectiveDateStr) ? null : effectiveDateStr;
        DateTime effectiveDate = new DateTime(effectiveDateStr);

        String includeExternal = (String) params.get(INCLUDE_EXTERNAL);
        boolean includeExternalBool = Boolean.parseBoolean(includeExternal);

        String exposureType = (String) params.get("exposureType");

        DatedValuationKey key = new DatedValuationKey(accountId, effectiveDate, includeExternalBool);
        ServiceErrors serviceErrors = new FailFastErrorsImpl();

        KeyedAllocByExposureDto keyedAllocDto = (KeyedAllocByExposureDto) allocationDtoService.find(key, serviceErrors);
        ExposureAllocationData exposureData = new ExposureAllocationData((AllocationByExposureDto) keyedAllocDto, exposureType);
        return Collections.singletonList(exposureData);
    }

    @ReportBean("disclaimer")
    public String getDisclaimer(Map<String, Object> params, Map<String, Object> dataCollections) {
        final UserExperience userExperience = getUserExperience(params, dataCollections);
        return getContent(UserExperience.DIRECT.equals(userExperience) ? DISCLAIMER_KEY_DIRECT : DISCLAIMER_KEY);
    }

    @ReportBean("includeExternal")
    public boolean getIncludeExternalParam(Map<String, String> params) {
        String includeExternal = params.get(INCLUDE_EXTERNAL);
        return Boolean.parseBoolean(includeExternal);
    }

    @Override
    public String getSummaryDescription(Map<String, Object> params, Map<String, Object> dataCollections) {
        return "";
    }

    @Override
    public String getSummaryValue(Map<String, Object> params, Map<String, Object> dataCollections) {
        return "";
    }

    @Override
    public String getReportType(Map<String, Object> params, Map<String, Object> dataCollections) {
        return "Portfolio asset allocation";
    }

}

package com.bt.nextgen.reports.account.cgt.v2;

import com.bt.nextgen.api.cgt.model.CgtDto;
import com.bt.nextgen.api.cgt.model.CgtGroupDto;
import com.bt.nextgen.api.cgt.model.CgtKey;
import com.bt.nextgen.api.cgt.service.UnrealisedCgtDtoService;
import com.bt.nextgen.content.api.model.ContentDto;
import com.bt.nextgen.content.api.model.ContentKey;
import com.bt.nextgen.content.api.service.ContentDtoService;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.operation.FindByKey;
import com.bt.nextgen.core.reporting.ReportFormat;
import com.bt.nextgen.core.reporting.ReportFormatter;
import com.bt.nextgen.core.reporting.stereotype.Report;
import com.bt.nextgen.core.reporting.stereotype.ReportBean;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.bt.nextgen.service.ServiceErrors;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

import static com.bt.nextgen.core.api.UriMappingConstants.*;

@Report(value = "unrealisedCapitalGainsTaxReportV2", filename = "Unrealised capital gains tax")
public class UnrealisedCapitalGainsTaxReport extends AbstractCapitalGainsTaxReport {
    private static final String DISCLAIMER_CONTENT = "DS-IP-0007";
    private static final String GROUP_BY_ASSET_TYPE = "ASSET_TYPE";
    private static final String SUMMARY_DESCRIPTION = "Estimated unrealised gain";
    private static final String REPORT_TITLE = "Unrealised capital gains tax";

    @Autowired
    private ContentDtoService contentService;

    @Autowired
    private UnrealisedCgtDtoService unrealisedCgtDtoService;

    @ReportBean("startDate")
    public String getStartDate(Map<String, String> params) {
        String startDate = params.get(EFFECTIVE_DATE_PARAMETER_MAPPING);
        if (startDate == null) {
            return ReportFormatter.format(ReportFormat.SHORT_DATE, new DateTime());
        } else {
            return ReportFormatter.format(ReportFormat.SHORT_DATE, new DateTime(startDate));
        }

    }

    protected List<CgtGroupDto> getCgtData(Map<String, Object> params) {
        String accountId = params.get(ACCOUNT_ID_URI_MAPPING).toString();
        String startDateStr = params.get(EFFECTIVE_DATE_PARAMETER_MAPPING).toString();

        DateTime startDate = new DateTime(startDateStr);

        CgtKey key = new CgtKey(accountId, startDate, startDate, GROUP_BY_ASSET_TYPE);
        ApiResponse response = new FindByKey<>(ApiVersion.CURRENT_VERSION, unrealisedCgtDtoService, key).performOperation();

        return ((CgtDto) response.getData()).getCgtGroupDtoList();
    }

    @ReportBean("disclaimer")
    public String getDisclaimer() {
        ServiceErrors serviceErrors = new FailFastErrorsImpl();
        ContentKey key = new ContentKey(DISCLAIMER_CONTENT);
        ContentDto content = contentService.find(key, serviceErrors);
        return content.getContent();
    }

    @ReportBean("reportTitle")
    public String getReportTitle() {
        return REPORT_TITLE;
    }

    @Override
    public String getReportType(Map<String, Object> params, Map<String, Object> dataCollections) {
        return REPORT_TITLE;
    }
}

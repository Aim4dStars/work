package com.bt.nextgen.reports.corporateaction;

import java.util.Collection;
import java.util.Map;

import ch.lambdaj.function.matcher.Predicate;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;

import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionApprovalDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionBaseDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionListDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionListDtoKey;
import com.bt.nextgen.api.corporateaction.v1.service.CorporateActionApprovalListDtoService;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.operation.FindByKey;
import com.bt.nextgen.core.reporting.BaseReportV2;
import com.bt.nextgen.core.reporting.stereotype.Report;
import com.bt.nextgen.core.reporting.stereotype.ReportBean;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionStatus;
import com.bt.nextgen.service.integration.trustee.IrgApprovalStatus;

import static ch.lambdaj.Lambda.select;

@Report("histCorporateActionCsvReport")
@PreAuthorize("isAuthenticated() and hasPermission(null, 'View_security_events_approval')")
public class TrusteeCorporateActionCsvReport extends BaseReportV2 {

    private static final String CURRENT_VERSION = "v1_0";

    @Autowired
    private CorporateActionApprovalListDtoService corporateActionApprovalListDtoService;

    @Autowired
    private UserProfileService userProfileService;

    @ReportBean("trusteeCorporateActionDtos")
    public Collection<CorporateActionBaseDto> retrieveTrusteeCorporateActionDtos(Map<String, String> params) {
        String endDate = params.get("endDate");
        String startDate = params.get("startDate");

        ApiResponse response =
                new FindByKey<>(CURRENT_VERSION, corporateActionApprovalListDtoService,
                        new CorporateActionListDtoKey(startDate, endDate, null, null, null))
                        .performOperation();

        CorporateActionListDto corporateActionListDto = (CorporateActionListDto) response.getData();

        return select(corporateActionListDto.getCorporateActions(), new Predicate<CorporateActionBaseDto>() {
            public boolean apply(CorporateActionBaseDto corporateActionBaseDto) {
                if (corporateActionBaseDto instanceof CorporateActionApprovalDto) {
                    CorporateActionApprovalDto caApproval = (CorporateActionApprovalDto) corporateActionBaseDto;
                    return !(caApproval.getIrgApprovalStatus() == IrgApprovalStatus.PENDING && caApproval
                            .getStatus() == CorporateActionStatus.OPEN);
                }

                return false;
            }
        });
    }

    @ReportBean("trusteeName")
    public String getTrusteeName(Map<String, String> params) {
        return userProfileService.getActiveProfile().getFullName();
    }

    @ReportBean("startDate")
    public DateTime getStartDate(Map<String, String> params) {
        String startDate = params.get("startDate");
        return startDate == null ? new DateTime() : new DateTime(startDate);
    }

    @ReportBean("endDate")
    public DateTime getEndDate(Map<String, String> params) {
        String endDate = params.get("endDate");
        return endDate == null ? new DateTime() : new DateTime(endDate);
    }
}

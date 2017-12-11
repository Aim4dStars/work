package com.bt.nextgen.reports.account;

import com.bt.nextgen.api.transactionhistory.model.CashTransactionHistoryDto;
import com.bt.nextgen.api.transactionhistory.service.CashTransactionHistoryDtoService;
import com.bt.nextgen.content.api.model.ContentDto;
import com.bt.nextgen.content.api.model.ContentKey;
import com.bt.nextgen.content.api.service.ContentDtoService;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.reporting.stereotype.ReportBean;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.core.web.AvaloqFormatter;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.userinformation.JobRole;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class CashTransactions extends AccountReport {

    private static final String DISCLAIMER_CONTENT = "DS-IP-0028";
    private static final String INFORMATION_CONTENT = "DS-IP-0057";

    @Autowired
    private CashTransactionHistoryDtoService cashTransactionHistoryDtoService;

    @Autowired
    private ContentDtoService contentService;

    @Autowired
    private UserProfileService userProfileService;

    @ReportBean("cashTransactionDtos")
    public Collection<CashTransactionHistoryDto> retrieveCashTransactionDtos(Map<String, String> params) {

        String accountId = params.get("account-id");

        DateTime startDateTime = getStartDate(params);
        DateTime endDateTime = getEndDate(params);

        List<ApiSearchCriteria> criteria = new ArrayList<ApiSearchCriteria>();
        criteria.add(new ApiSearchCriteria(Attribute.ACCOUNT_ID, ApiSearchCriteria.SearchOperation.EQUALS, accountId,
                ApiSearchCriteria.OperationType.STRING));
        criteria.add(new ApiSearchCriteria(Attribute.FROM_DATE, ApiSearchCriteria.SearchOperation.EQUALS,
                AvaloqFormatter.asAvaloqFormatDate(startDateTime), ApiSearchCriteria.OperationType.STRING));
        criteria.add(new ApiSearchCriteria(Attribute.TO_DATE, ApiSearchCriteria.SearchOperation.EQUALS,
                AvaloqFormatter.asAvaloqFormatDate(endDateTime), ApiSearchCriteria.OperationType.STRING));

        ServiceErrors serviceErrors = new FailFastErrorsImpl();
        List<CashTransactionHistoryDto> cashTransactionDtos = cashTransactionHistoryDtoService.search(criteria, serviceErrors);

        return cashTransactionDtos;
    }

    @ReportBean("moreInformation")
    @SuppressWarnings("squid:S1172")
    // Warnings suppressed as parameter is enforced by reflection driven api.
    public String getMoreInfo(Map<String, String> params) {
        String adviser = "your adviser";

        if (userProfileService.getActiveProfile().getJobRole() == JobRole.INVESTOR) {
            adviser = getAccount(params).iterator().next().getAdviserName();
        }

        return cmsService.getDynamicContent(INFORMATION_CONTENT, new String[] { adviser });
    }

    @ReportBean("disclaimer")
    @SuppressWarnings("squid:S1172")
    // Warnings suppressed as parameter is enforced by reflection driven api.
    public String getDisclaimer(Map<String, String> params) {
        ServiceErrors serviceErrors = new FailFastErrorsImpl();
        ContentKey key = new ContentKey(DISCLAIMER_CONTENT);
        ContentDto content = contentService.find(key, serviceErrors);
        return content.getContent();
    }

    @ReportBean("reportType")
    @SuppressWarnings("squid:S1172")
    // Warnings suppressed as parameter is enforced by reflection driven api.
    public String getReportName(Map<String, String> params) {
        return "Cash transactions";
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

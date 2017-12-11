package com.bt.nextgen.service.avaloq.contributionhistory;

import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.avaloq.AbstractAvaloqIntegrationService;
import com.bt.nextgen.service.avaloq.AvaloqExecute;
import com.bt.nextgen.service.avaloq.gateway.AvaloqReportRequest;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;

import static com.bt.nextgen.reports.service.ReportGenerationServiceImpl.PARAM_RELV_DATE_FROM;
import static com.bt.nextgen.reports.service.ReportGenerationServiceImpl.PARAM_RELV_DATE_TO;
import static com.bt.nextgen.service.avaloq.Template.SUPER_CONTRIBUTIONS_HISTORY;

/**
 * Avaloq service fir ccontribution history.
 */
@Service("ContributionHistoryIntegrationServiceImpl")
public class ContributionHistoryIntegrationServiceImpl extends AbstractAvaloqIntegrationService
        implements ContributionHistoryIntegrationService {
    /**
     * Executor for avaloq requests.
     */
    @Autowired
    private AvaloqExecute avaloqExecute;


    @Override
    public ContributionHistory getContributionHistory(AccountKey accountKey, DateTime financialYearStartDate,
                                                      DateTime financialYearEndDate) {
        AvaloqReportRequest req = new AvaloqReportRequest(SUPER_CONTRIBUTIONS_HISTORY.getName());

        if (accountKey == null || financialYearStartDate == null || financialYearEndDate == null) {
            throw new IllegalArgumentException("Account key, start and end dates of finandial year must be specified");
        }

        req = req.forBpList(Collections.singletonList(accountKey.getAccountId()));
        req = req.forDateTime(PARAM_RELV_DATE_FROM, financialYearStartDate);
        req = req.forDateTime(PARAM_RELV_DATE_TO, financialYearEndDate);

        return avaloqExecute.executeReportRequestToDomain(req,
                ContributionHistoryImpl.class, new ServiceErrorsImpl());
    }
}

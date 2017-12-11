package com.bt.nextgen.service.avaloq.superannuation.caps.service;


import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.avaloq.gateway.AvaloqGatewayHelperService;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.avaloq.AbstractAvaloqIntegrationService;
import com.bt.nextgen.service.avaloq.AvaloqExecute;
import com.bt.nextgen.service.avaloq.Template;
import com.bt.nextgen.service.avaloq.gateway.AvaloqReportRequest;
import com.bt.nextgen.service.avaloq.superannuation.caps.model.ContributionCaps;
import com.bt.nextgen.service.avaloq.superannuation.caps.model.ContributionCapsImpl;
import com.bt.nextgen.service.integration.account.AccountKey;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class ContributionCapsIntegrationServiceImpl extends AbstractAvaloqIntegrationService
        implements ContributionCapIntegrationService {
    @Autowired
    private AvaloqGatewayHelperService webServiceClient;

    @Autowired
    private AvaloqExecute avaloqExecute;

    /**
     * Retrieve superannuation contribution caps for a given account and financial year from avaloq.
     * Uses the avaloq report template <code>BTFG$TASK_BP_LIST.SA_CAP</code>
     *
     * @param accountKey        account key of account to retrieve caps for
     * @param financialYearDate starting date of the financial year to return caps for
     * @param serviceErrors     errors object
     *
     * @return ContributionCaps for the given account and financial year
     */
    @Override
    public ContributionCaps getContributionCaps(final AccountKey accountKey, final DateTime financialYearDate,
                                                ServiceErrors serviceErrors) {
        return new AbstractAvaloqIntegrationService.IntegrationSingleOperation<ContributionCaps>("getContributionCaps",
                new ServiceErrorsImpl()) {
            @Override
            public ContributionCaps performOperation() {
                return avaloqExecute.executeReportRequestToDomain(
                        new AvaloqReportRequest(Template.SUPER_CONTRIBUTIONS_CAPS.getName())
                                .forBpIdList(Collections.singletonList(accountKey.getId()))
                                .forDateTime("ref_date", financialYearDate),
                        ContributionCapsImpl.class,
                        new ServiceErrorsImpl());
            }
        }.run();
    }
}
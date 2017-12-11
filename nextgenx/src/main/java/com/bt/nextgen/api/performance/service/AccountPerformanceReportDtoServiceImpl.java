package com.bt.nextgen.api.performance.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bt.nextgen.api.performance.model.AccountPerformanceKey;
import com.bt.nextgen.api.performance.model.AccountPerformanceReportDto;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.portfolio.performance.WrapAccountPerformanceImpl;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.performance.PeriodicPerformance;
import com.bt.nextgen.service.integration.portfolio.performance.AccountPerformanceIntegrationService;

@Service
public class AccountPerformanceReportDtoServiceImpl implements AccountPerformanceReportDtoService {

    @Autowired
    private AccountPerformanceIntegrationService accountPeformanceService;

    @Autowired
    private AccountPerformanceReportDtoServiceDataAggregatorImpl accountPerformanceReportDtoServiceAggregator;

    @Override
    public AccountPerformanceReportDto find(AccountPerformanceKey key, ServiceErrors serviceErrors) {
        String accountId = EncodedString.toPlainText(key.getAccountId());
        String bmrk = key.getBenchmarkId();
        int bmrkId = key.getBenchmarkId() == null ? null : Integer.parseInt(bmrk);
        if (bmrkId <= 0) {
            bmrk = null;
        }

        WrapAccountPerformanceImpl perfSummary = (WrapAccountPerformanceImpl) accountPeformanceService
                .loadAccountPerformanceReport(AccountKey.valueOf(accountId), bmrk, key.getStartDate(), key.getEndDate(),
                        serviceErrors);

        PeriodicPerformance incepPerf = accountPeformanceService.loadAccountPerformanceSummarySinceInception(
                AccountKey.valueOf(accountId), bmrk, key.getEndDate(), serviceErrors);

        // Add notes to reportDto, notes is different when other fees are present

         return accountPerformanceReportDtoServiceAggregator.buildReportDto(key, perfSummary, incepPerf.getPerformanceData());
    }

}

package com.bt.nextgen.api.portfolio.v3.service.performance;

import com.bt.nextgen.api.portfolio.v3.model.DatedAccountKey;
import com.bt.nextgen.api.portfolio.v3.model.performance.PerformanceSummaryDto;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.performance.Performance;
import com.bt.nextgen.service.integration.performance.PeriodicPerformance;
import com.bt.nextgen.service.integration.portfolio.performance.AccountPerformanceIntegrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service("AccountPerformanceInceptionDtoServiceImplV3")
public class AccountPerformanceInceptionDtoServiceImpl implements AccountPerformanceInceptionDtoService {
    @Autowired
    private AccountPerformanceIntegrationService accountService;

    public PerformanceSummaryDto<DatedAccountKey> find(DatedAccountKey key, ServiceErrors serviceErrors) {
        String accountId = EncodedString.toPlainText(key.getAccountId());

        PeriodicPerformance perfSummarySinceInception = (PeriodicPerformance) accountService
                .loadAccountPerformanceSummarySinceInception(AccountKey.valueOf(accountId), null, key.getEffectiveDate(),
                        serviceErrors);

        return getPerformanceData(key, perfSummarySinceInception);
    }


    private PerformanceSummaryDto<DatedAccountKey> getPerformanceData(DatedAccountKey key,
            PeriodicPerformance accountPerformanceSinceInception) {
        BigDecimal percentagePerformance = null;
        BigDecimal dollarPerformance = null;
        BigDecimal capitalPerformance = null;
        BigDecimal incomePerformance = null;

        Performance accountPerformanceSinceInceptionData = accountPerformanceSinceInception.getPerformanceData();
        if (accountPerformanceSinceInceptionData != null) {
            percentagePerformance = accountPerformanceSinceInceptionData.getPerformance() != null ? accountPerformanceSinceInceptionData
                    .getPerformance().divide(BigDecimal.valueOf(100)) : null;
            dollarPerformance = accountPerformanceSinceInception.getPerformanceData().getNetGainLoss();
            capitalPerformance = accountPerformanceSinceInceptionData.getCapitalGrowth() != null ? accountPerformanceSinceInceptionData
                    .getCapitalGrowth().divide(BigDecimal.valueOf(100)) : null;
            incomePerformance = accountPerformanceSinceInceptionData.getIncomeRtn() != null ? accountPerformanceSinceInceptionData
                    .getIncomeRtn().divide(BigDecimal.valueOf(100)) : null;
        }


        PerformanceSummaryDto<DatedAccountKey> summary = new PerformanceSummaryDto<>(key, percentagePerformance,
                dollarPerformance, capitalPerformance, incomePerformance);

        return summary;

    }

}

package com.bt.nextgen.api.portfolio.v3.service.performance;

import com.bt.nextgen.api.portfolio.v3.model.DateRangeAccountKey;
import com.bt.nextgen.api.portfolio.v3.model.performance.AccountPerformanceTotalDto;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.performance.Performance;
import com.bt.nextgen.service.integration.portfolio.performance.AccountPerformanceIntegrationService;
import com.bt.nextgen.service.integration.portfolio.performance.WrapAccountPerformance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service("AccountPerformanceTotalDtoServiceV3")
public class AccountPerformanceTotalDtoServiceImpl implements AccountPerformanceTotalDtoService {

    @Autowired
    private AccountPerformanceIntegrationService accountPerformanceService;

    @Override
    public AccountPerformanceTotalDto find(DateRangeAccountKey key, ServiceErrors serviceErrors) {

        AccountKey accountKey = AccountKey.valueOf(EncodedString.toPlainText(key.getAccountId()));

        WrapAccountPerformance accountPeriodPerformance = accountPerformanceService.loadAccountTotalPerformance(accountKey, null,
                key.getStartDate(), key.getEndDate(), serviceErrors);

        if (accountPeriodPerformance != null) {
            Performance periodPerformance = accountPeriodPerformance.getPeriodPerformanceData();
            if (periodPerformance != null) {
                return buildDto(key, periodPerformance);
            }
        }
        return new AccountPerformanceTotalDto(key, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
                BigDecimal.ZERO, BigDecimal.ZERO);
    }

    private AccountPerformanceTotalDto buildDto(DateRangeAccountKey key, Performance data) {

        BigDecimal perfBeforeFeeDollar = data.getPerformanceBeforeFee();
        BigDecimal perfBeforeFeePercent = getSafePercent(data.getTwrrGross());
        BigDecimal perfAfterFeeDollar = data.getPerformanceAfterFee();
        BigDecimal perfAfterFeePercent = getSafePercent(data.getPerformance());
        BigDecimal incomeReturn = getSafePercent(data.getIncomeRtn());
        BigDecimal capitalGrowth = getSafePercent(data.getCapitalGrowth());

        return new AccountPerformanceTotalDto(key, perfBeforeFeeDollar, perfBeforeFeePercent, perfAfterFeeDollar,
                perfAfterFeePercent, incomeReturn, capitalGrowth);
    }

    private BigDecimal getSafePercent(BigDecimal value) {
        if (value == null) {
            return BigDecimal.ZERO;
        }
        return value.divide(BigDecimal.valueOf(100));
    }

}

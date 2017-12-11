package com.bt.nextgen.api.portfolio.v3.service.performance;

import com.bt.nextgen.api.portfolio.v3.model.DateRangeAccountKey;
import com.bt.nextgen.api.portfolio.v3.model.performance.PerformanceReportDto;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.performance.Performance;
import com.bt.nextgen.service.integration.portfolio.performance.AccountPerformanceIntegrationService;
import com.bt.nextgen.service.integration.portfolio.performance.WrapAccountPerformance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("AccountPerformanceReportDtoServiceV3")
public class AccountPeriodPerformanceDtoServiceImpl implements AccountPeriodPerformanceDtoService {
    @Autowired
    private AccountPerformanceIntegrationService accountService;

    @Override
    public PerformanceReportDto find(DateRangeAccountKey key, ServiceErrors serviceErrors) {
        Performance performance;

        WrapAccountPerformance wrapAccountPerformance = accountService.loadAccountTotalPerformance(
                AccountKey.valueOf(EncodedString.toPlainText(key.getAccountId())),
                null, key.getStartDate(), key.getEndDate(), serviceErrors);

        if (wrapAccountPerformance != null) {
            performance = wrapAccountPerformance.getPeriodPerformanceData();
            return convertToDto(performance, key);
        }
        return null;
    }

    private PerformanceReportDto convertToDto(Performance performance, DateRangeAccountKey key) {
        PerformanceReportDto performanceReportDto = new PerformanceReportDto();
        performanceReportDto.setPerformance(performance.getPerformance());
        performanceReportDto.setIncome(performance.getIncome());
        performanceReportDto.setCapitalGrowth(performance.getCapitalGrowth());
        performanceReportDto.setActiveRor(performance.getActiveRor());
        performanceReportDto.setBmrkRor(performance.getBmrkRor());
        performanceReportDto.setEopAftFee(performance.getClosingBalanceAfterFee());
        performanceReportDto.setEopBfrFee(performance.getClosingBalanceBeforeFee());
        performanceReportDto.setExpenses(performance.getExpenses());
        performanceReportDto.setFee(performance.getFee());
        performanceReportDto.setIncomeRtn(performance.getIncomeRtn());
        performanceReportDto.setInflows(performance.getInflows());
        performanceReportDto.setMktMvt(performance.getMktMvt());
        performanceReportDto.setNetGainLoss(performance.getNetGainLoss());
        performanceReportDto.setOutflows(performance.getOutflows());
        performanceReportDto.setPeriodEop(performance.getPeriodEop());
        performanceReportDto.setPeriodSop(performance.getPeriodSop());
        performanceReportDto.setSopCurrValRef(performance.getOpeningBalance());
        performanceReportDto.setTwrrAccum(performance.getTwrrAccum());
        performanceReportDto.setTwrrGross(performance.getTwrrGross());
        performanceReportDto.setPerformanceAfterFee(performance.getPerformanceAfterFee());
        performanceReportDto.setPerformanceBeforeFee(performance.getPerformanceBeforeFee());
        performanceReportDto.setKey(key);
        return performanceReportDto;
    }
}

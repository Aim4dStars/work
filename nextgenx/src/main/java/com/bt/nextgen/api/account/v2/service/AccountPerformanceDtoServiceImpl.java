package com.bt.nextgen.api.account.v2.service;

import com.bt.nextgen.api.account.v2.model.AccountKey;
import com.bt.nextgen.api.account.v2.model.PerformanceDto;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.performance.Performance;
import com.bt.nextgen.service.integration.portfolio.performance.AccountPerformanceIntegrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Deprecated
@Service("AccountPerformanceDtoServiceV2")
public class AccountPerformanceDtoServiceImpl implements AccountPerformanceDtoService
{
	@Autowired
	private AccountPerformanceIntegrationService accountService;

	@Override
	public PerformanceDto find(AccountKey key, ServiceErrors serviceErrors)
	{
		Performance performance = accountService.loadAccountQuarterlyPerformance(new EncodedString(key.getAccountId()).plainText(),
			serviceErrors);
		PerformanceDto performanceDto = convertToDto(performance, key);
		return performanceDto;
	}

	protected PerformanceDto convertToDto(Performance performance, AccountKey key)
	{
		PerformanceDto performanceDto = new PerformanceDto();
		performanceDto.setPerformance(performance.getPerformance());
		performanceDto.setIncome(performance.getIncome());
		performanceDto.setCapitalGrowth(performance.getCapitalGrowth());
		performanceDto.setActiveRor(performance.getActiveRor());
		performanceDto.setBmrkRor(performance.getBmrkRor());
		performanceDto.setEopAftFee(performance.getClosingBalanceAfterFee());
		performanceDto.setEopBfrFee(performance.getClosingBalanceBeforeFee());
		performanceDto.setExpenses(performance.getExpenses());
		performanceDto.setFee(performance.getFee());
	    performanceDto.setOtherFee(performance.getFee());
		performanceDto.setIncomeRtn(performance.getIncomeRtn());
		performanceDto.setInflows(performance.getInflows());
		performanceDto.setMktMvt(performance.getMktMvt());
		performanceDto.setNetGainLoss(performance.getNetGainLoss());
		performanceDto.setOutflows(performance.getOutflows());
		performanceDto.setPeriodEop(performance.getPeriodEop());
		performanceDto.setPeriodSop(performance.getPeriodSop());
		performanceDto.setSopCurrValRef(performance.getOpeningBalance());
		performanceDto.setTwrrAccum(performance.getTwrrAccum());
		performanceDto.setTwrrGross(performance.getTwrrGross());
        performanceDto.setPerformanceBeforeFee(performance.getPerformanceBeforeFee());
        performanceDto.setPerformanceAfterFee(performance.getPerformanceAfterFee());
		performanceDto.setKey(key);
		return performanceDto;
	}
}

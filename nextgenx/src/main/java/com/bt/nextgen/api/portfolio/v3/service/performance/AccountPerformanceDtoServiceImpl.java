package com.bt.nextgen.api.portfolio.v3.service.performance;

import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.bt.nextgen.api.portfolio.v3.model.performance.PerformanceDto;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.performance.Performance;
import com.bt.nextgen.service.integration.portfolio.performance.AccountPerformanceIntegrationService;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.btfin.panorama.service.integration.account.WrapAccountDetail;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service("AccountPerformanceDtoServiceV3")
public class AccountPerformanceDtoServiceImpl implements AccountPerformanceDtoService
{
	@Autowired
	private AccountPerformanceIntegrationService accountService;

	@Autowired
	@Qualifier("avaloqAccountIntegrationService")
	private AccountIntegrationService accountIntegrationService;


	@Override
    public PerformanceDto find(AccountKey key, ServiceErrors serviceErrors)
	{
		Performance performance = accountService.loadAccountQuarterlyPerformance(new EncodedString(key.getAccountId()).plainText(),
			serviceErrors);
        PerformanceDto performanceDto = convertToDto(performance, key);
		//Check if the header values can be displayed
		performanceDto.setAccountOverviewPerformanceAvailable(!isAccountPerformanceUnavailable(key, serviceErrors));
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

	/**
	 * This will retrieve the account detail and compare the current date and migrated date quarters to show the header values on account overview
	 * @param key
	 * @param serviceErrors
	 * @return boolean isAccountPerformanceUnavailable
	 */
	public boolean isAccountPerformanceUnavailable(AccountKey key, ServiceErrors serviceErrors) {
		boolean valueNotFound = false;
		com.bt.nextgen.service.integration.account.AccountKey accountKey = com.bt.nextgen.service.integration.account.AccountKey.valueOf(new EncodedString(key.getAccountId()).plainText());
		final WrapAccountDetail account = accountIntegrationService.loadWrapAccountDetail(accountKey, serviceErrors);
		if(account!=null) {
			DateTime migrationDate = account.getMigrationDate();
			if (account.getMigrationKey() != null && migrationDate != null) {
				DateTime currentDate = new DateTime();
                int migrationDateQuarter = ((migrationDate.getMonthOfYear() - 1) / 3) + 1;
                int currentDateQuarter = ((currentDate.getMonthOfYear() - 1) / 3) + 1;
                // If migration date falls in the current quarter or previous quarter
				if (migrationDateQuarter == currentDateQuarter || currentDateQuarter - migrationDateQuarter == 1) {
					valueNotFound = true;
				}
			}
		}
		return valueNotFound;
	}

}

package com.bt.nextgen.api.dashboard.service;

import com.bt.nextgen.api.dashboard.model.AdviserPerformanceDto;
import com.bt.nextgen.api.dashboard.model.AdviserPerformanceSummaryDto;
import com.bt.nextgen.api.dashboard.model.PeriodKey;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.dashboard.AdviserPerformanceImpl;
import com.btfin.panorama.service.integration.broker.Broker;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.dashboard.AdviserPerformanceIntegrationService;
import com.bt.nextgen.service.integration.dashboard.PerformanceData;
import com.bt.nextgen.service.integration.userprofile.JobProfileIdentifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class AdviserDashboardDtoServiceImpl implements AdviserDashboardService
{

	@Autowired
	private AdviserPerformanceIntegrationService adviserPerformanceService;

	@Autowired
	private UserProfileService userProfileService;

	@Autowired
	private BrokerIntegrationService brokerService;

	@Override
	public AdviserPerformanceSummaryDto find(PeriodKey periodKey, ServiceErrors serviceErrors)
	{
		String brokerId = userProfileService.getPositionId();
		String periodType = periodKey.getId();
		AdviserPerformanceSummaryDto performanceSummaryDto = getAdviserPerformance(brokerId, periodType, serviceErrors);
		return performanceSummaryDto;
	}

	public AdviserPerformanceSummaryDto getAdviserPerformance(String brokerId, String periodType, ServiceErrors serviceErrors)
	{
		AdviserPerformanceImpl adviserPerformance = null;
		AdviserPerformanceSummaryDto performanceSummaryDto = null;
		List <PerformanceData> performances = null;
		List <PerformanceData> periodPerformance = null;
		List <AdviserPerformanceDto> performanceDtos = new ArrayList <>();
		BrokerKey brokerKey = BrokerKey.valueOf(brokerId);
		switch (periodType)
		{
			case PeriodKey.THIS_YEAR:
				adviserPerformance = adviserPerformanceService.loadCurrentYearPerformanceData(brokerKey, serviceErrors);
				performances = adviserPerformance.getMonthlyPerformanceData();
				break;
			case PeriodKey.LAST_30_DAYS:
				adviserPerformance = adviserPerformanceService.loadCurrentMonthPerformanceData(brokerKey, serviceErrors);
				performances = adviserPerformance.getDailyPerformanceData();
				break;
			case PeriodKey.CURR_QUAR:
				adviserPerformance = adviserPerformanceService.loadCurrentQuarterPerformanceData(brokerKey, serviceErrors);
				performances = adviserPerformance.getMonthlyPerformanceData();
				break;
			case PeriodKey.THIS_FY:
				adviserPerformance = adviserPerformanceService.loadCurrentFinancialYearPerformanceData(brokerKey, serviceErrors);
				performances = adviserPerformance.getMonthlyPerformanceData();
				break;
			case PeriodKey.LAST_FY:
				adviserPerformance = adviserPerformanceService.loadLastFinancialYearPerformanceData(brokerKey, serviceErrors);
				performances = adviserPerformance.getMonthlyPerformanceData();
				break;
			default:
				adviserPerformance = adviserPerformanceService.loadCurrentYearPerformanceData(brokerKey, serviceErrors);
				performances = adviserPerformance.getMonthlyPerformanceData();
				break;
		}
		periodPerformance = adviserPerformance.getPeriodPerformanceData();
		if (performances != null)
		{
			for (PerformanceData performance : performances)
			{
				AdviserPerformanceDto performanceDto = new AdviserPerformanceDto(performance.getPeriodSop(),
					performance.getPeriodEop(),
					performance.getObjCount(),
					performance.getFua(),
					performance.getInflows(),
					performance.getOutflows(),
					performance.getNetFlows(),
					performance.getFee());
				performanceDtos.add(performanceDto);
			}
		}
		if (periodPerformance != null)
		{
			performanceSummaryDto = new AdviserPerformanceSummaryDto(PeriodKey.valueOf(periodType),
				periodPerformance.get(0).getFua(),
				periodPerformance.get(0).getInflows(),
				periodPerformance.get(0).getOutflows(),
				periodPerformance.get(0).getNetFlows(),
				periodPerformance.get(0).getFee(),
				periodPerformance.get(0).getObjCount(),
				performanceDtos);
		}
		else
		{
			performanceSummaryDto = new AdviserPerformanceSummaryDto(PeriodKey.valueOf(periodType),
				null,
				null,
				null,
				null,
				null,
				null,
				null);
		}
		return performanceSummaryDto;

	}

	@Override
	public AdviserPerformanceSummaryDto findOne(ServiceErrors serviceErrors)
	{
		JobProfileIdentifier jobProfile = userProfileService.getActiveProfile();
		//List <JobProfile> list = userProfileService.getAvailableProfiles();
		List <Broker> brokerList = brokerService.getBrokersForJob(jobProfile, serviceErrors);
        BigDecimal fua = new BigDecimal(0);
        Integer noOfAccount = 0;
        for (Broker broker : brokerList) {
            fua = fua.add(broker.getFua()==null?new BigDecimal(0):broker.getFua());
            noOfAccount = noOfAccount + (broker.getNumberOfAccounts()== null?0:broker.getNumberOfAccounts());
        }
        AdviserPerformanceSummaryDto summaryDto = new AdviserPerformanceSummaryDto(null, fua,null,null,null,null,noOfAccount,null);
		return summaryDto;
	}
}

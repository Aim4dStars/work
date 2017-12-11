package com.bt.nextgen.service.avaloq.dashboard;

import java.util.List;

import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.dashboard.AdviserPerformance;
import com.bt.nextgen.service.integration.dashboard.PerformanceData;
import com.bt.nextgen.service.integration.performance.PerformancePeriodType;

public class AdviserPerformanceImpl implements AdviserPerformance
{

	private BrokerKey brokerKey;
	private List <PerformanceData> dailyPerformanceData;
	private List <PerformanceData> weeklyPerformanceData;
	private List <PerformanceData> monthlyPerformanceData;
	private List <PerformanceData> quarterlyPerformanceData;
	private List <PerformanceData> yearlyPerformanceData;
	private List <PerformanceData> periodPerformanceData;

	public BrokerKey getBrokerKey()
	{
		return brokerKey;
	}

	public void setBrokerKey(BrokerKey brokerKey)
	{
		this.brokerKey = brokerKey;
	}

	public void setQuarterlyPerformanceData(List <PerformanceData> quarterlyPerformanceData)
	{
		this.quarterlyPerformanceData = quarterlyPerformanceData;
	}

	public void setDailyPerformanceData(List <PerformanceData> dailyPerformanceData)
	{
		this.dailyPerformanceData = dailyPerformanceData;
	}

	public void setWeeklyPerformanceData(List <PerformanceData> weeklyPerformanceData)
	{
		this.weeklyPerformanceData = weeklyPerformanceData;
	}

	public void setMonthlyPerformanceData(List <PerformanceData> monthlyPerformanceData)
	{
		this.monthlyPerformanceData = monthlyPerformanceData;
	}

	public void setYearlyPerformanceData(List <PerformanceData> yearlyPerformanceData)
	{
		this.yearlyPerformanceData = yearlyPerformanceData;
	}

	public void setPeriodPerformanceData(List <PerformanceData> periodPerformanceData)
	{
		this.periodPerformanceData = periodPerformanceData;
	}

	@Override
	public List <PerformanceData> getDailyPerformanceData()
	{
		return dailyPerformanceData;
	}

	@Override
	public List <PerformanceData> getWeeklyPerformanceData()
	{
		return weeklyPerformanceData;
	}

	@Override
	public List <PerformanceData> getMonthlyPerformanceData()
	{
		return monthlyPerformanceData;
	}

	@Override
	public List <PerformanceData> getQuarterlyPerformanceData()
	{
		return quarterlyPerformanceData;
	}

	@Override
	public List <PerformanceData> getYearlyPerformanceData()
	{
		return yearlyPerformanceData;
	}

	@Override
	public List <PerformanceData> getPeriodPerformanceData()
	{
		return periodPerformanceData;
	}

	public void setPerformanceData(PerformancePeriodType period, List <PerformanceData> data)
	{
		switch (period)
		{
			case DAILY:
				setDailyPerformanceData(data);
				break;
			case WEEKLY:
				setWeeklyPerformanceData(data);
				break;
			case MONTHLY:
				setMonthlyPerformanceData(data);
				break;
			case QUARTERLY:
				setQuarterlyPerformanceData(data);
				break;
			case YEARLY:
				setYearlyPerformanceData(data);
				break;
			case PERIOD:
				setPeriodPerformanceData(data);
				break;
		}
	}

}

package com.bt.nextgen.service.integration.dashboard;

import java.util.List;

public interface AdviserPerformance
{
	public List <PerformanceData> getDailyPerformanceData();

	public List <PerformanceData> getWeeklyPerformanceData();

	public List <PerformanceData> getMonthlyPerformanceData();

	public List <PerformanceData> getQuarterlyPerformanceData();

	public List <PerformanceData> getYearlyPerformanceData();

	public List <PerformanceData> getPeriodPerformanceData();

}

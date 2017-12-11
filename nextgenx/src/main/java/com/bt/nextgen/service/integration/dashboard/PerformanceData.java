package com.bt.nextgen.service.integration.dashboard;

import java.math.BigDecimal;

import org.joda.time.DateTime;

public interface PerformanceData
{
	public DateTime getPeriodSop();

	public DateTime getPeriodEop();

	public Integer getObjCount();

	public BigDecimal getFua();

	public BigDecimal getInflows();

	public BigDecimal getOutflows();

	public BigDecimal getNetFlows();

	public BigDecimal getFee();

}

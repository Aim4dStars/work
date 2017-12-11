package com.bt.nextgen.service.avaloq.dashboard;

import java.math.BigDecimal;

import org.joda.time.DateTime;

import com.bt.nextgen.service.integration.dashboard.PerformanceData;

public class PerformanceDataImpl implements PerformanceData
{
	private DateTime periodSop;
	private DateTime periodEop;
	private Integer objCount;
	private BigDecimal fua;
	private BigDecimal inflows;
	private BigDecimal outflows;
	private BigDecimal netFlows;
	private BigDecimal fee;

	public DateTime getPeriodSop()
	{
		return periodSop;
	}

	public void setPeriodSop(DateTime periodSop)
	{
		this.periodSop = periodSop;
	}

	public DateTime getPeriodEop()
	{
		return periodEop;
	}

	public void setPeriodEop(DateTime periodEop)
	{
		this.periodEop = periodEop;
	}

	public Integer getObjCount()
	{
		return objCount;
	}

	public void setObjCount(Integer objCount)
	{
		this.objCount = objCount;
	}

	public BigDecimal getFua()
	{
		return fua;
	}

	public void setFua(BigDecimal fua)
	{
		this.fua = fua;
	}

	public BigDecimal getInflows()
	{
		return inflows;
	}

	public void setInflows(BigDecimal inflows)
	{
		this.inflows = inflows;
	}

	public BigDecimal getOutflows()
	{
		return outflows;
	}

	public void setOutflows(BigDecimal outflows)
	{
		this.outflows = outflows;
	}

	public BigDecimal getNetFlows()
	{
		return netFlows;
	}

	public void setNetFlows(BigDecimal netFlows)
	{
		this.netFlows = netFlows;
	}

	public BigDecimal getFee()
	{
		return fee;
	}

	public void setFee(BigDecimal fee)
	{
		this.fee = fee;
	}

}

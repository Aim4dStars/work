package com.bt.nextgen.api.performance.model;

import java.math.BigDecimal;
import java.util.List;

import com.bt.nextgen.core.api.model.BaseDto;

public class PerformanceReportDataDto extends BaseDto
{
	private String description;
	private List <BigDecimal> dataList;

	public PerformanceReportDataDto(String description, List <BigDecimal> dataList)
	{
		this.description = description;
		this.dataList = dataList;
	}

	public String getDescription()
	{
		return description;
	}

	public BigDecimal getDataPeriod1()
	{
		return getData(1);
	}

	public BigDecimal getDataPeriod2()
	{
		return getData(2);
	}

	public BigDecimal getDataPeriod3()
	{
		return getData(3);
	}

	public BigDecimal getDataPeriod4()
	{
		return getData(4);
	}

	public BigDecimal getDataPeriod5()
	{
		return getData(5);
	}

	public BigDecimal getDataPeriod6()
	{
		return getData(6);
	}

	public BigDecimal getDataPeriod7()
	{
		return getData(7);
	}

	public BigDecimal getDataPeriod8()
	{
		return getData(8);
	}

	public BigDecimal getDataPeriod9()
	{
		return getData(9);
	}

	public BigDecimal getDataPeriod10()
	{
		return getData(10);
	}

	public BigDecimal getDataPeriod11()
	{
		return getData(11);
	}

	public BigDecimal getDataPeriod12()
	{
		return getData(12);
	}

	public BigDecimal getDataPeriod13()
	{
		return getData(13);
	}

	public BigDecimal getDataPeriod14()
	{
		return getData(14);
	}

	public BigDecimal getDataPeriod15()
	{
		return getData(15);
	}

	public BigDecimal getDataPeriod16()
	{
		return getData(16);
	}

	public BigDecimal getDataPeriod17()
	{
		return getData(17);
	}

	public BigDecimal getDataPeriod18()
	{
		return getData(18);
	}

	public BigDecimal getDataPeriod19()
	{
		return getData(19);
	}

	public BigDecimal getDataPeriod20()
	{
		return getData(20);
	}

	private BigDecimal getData(int period)
	{
		if (dataList.size() > period - 1)
		{
			return dataList.get(period - 1);
		}
		return null;
	}

	public List <BigDecimal> getDataList()
	{
		return dataList;
	}

}

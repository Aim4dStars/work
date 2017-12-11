package com.bt.nextgen.portfolio.domain;


@Deprecated
public class StatementDetail
{
	private String periodTypeStatement;
	private String periodFromDate;
	private String periodToDate;
	private String StatementGeneratedDate;

	public String getPeriodTypeStatement()
	{
		return periodTypeStatement;
	}

	public void setPeriodTypeStatement(String periodTypeStatement)
	{
		this.periodTypeStatement = periodTypeStatement;
	}

	public String getPeriodFromDate()
	{
		return periodFromDate;
	}

	public void setPeriodFromDate(String periodFromDate)
	{
		this.periodFromDate = periodFromDate;
	}

	public String getPeriodToDate()
	{
		return periodToDate;
	}

	public void setPeriodToDate(String periodToDate)
	{
		this.periodToDate = periodToDate;
	}

	public String getStatementGeneratedDate()
	{
		return StatementGeneratedDate;
	}

	public void setStatementGeneratedDate(String statementGeneratedDate)
	{
		StatementGeneratedDate = statementGeneratedDate;
	}

}

package com.bt.nextgen.portfolio.domain;

import java.util.ArrayList;
import java.util.List;

@Deprecated
public class StatementType
{
	private String statementType;
	private List <StatementDetail> statementDetailList = new ArrayList <StatementDetail>();

	public String getStatementType()
	{
		return statementType;
	}

	public void setStatementType(String statementType)
	{
		this.statementType = statementType;
	}

	public List <StatementDetail> getStatementDetailList()
	{
		return statementDetailList;
	}

	public void setStatementDetailList(List <StatementDetail> statementDetailList)
	{
		this.statementDetailList = statementDetailList;
	}

}

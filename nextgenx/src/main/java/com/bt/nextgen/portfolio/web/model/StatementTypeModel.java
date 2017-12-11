package com.bt.nextgen.portfolio.web.model;

import java.util.ArrayList;
import java.util.List;

@Deprecated
public class StatementTypeModel
{
	private String statementType;
	private List <StatementDetailModel> statementDetailList = new ArrayList <StatementDetailModel>();

	public StatementTypeModel()
	{

	}

	public StatementTypeModel(String statementType, List <StatementDetailModel> statementDetailList)
	{
		this.statementType = statementType;
		this.statementDetailList = statementDetailList;
	}

	public String getStatementType()
	{
		return statementType;
	}

	public void setStatementType(String statementType)
	{
		this.statementType = statementType;
	}

	public List <StatementDetailModel> getStatementDetailList()
	{
		return statementDetailList;
	}

	public void setStatementDetailList(List <StatementDetailModel> statementDetailList)
	{
		this.statementDetailList = statementDetailList;
	}

}

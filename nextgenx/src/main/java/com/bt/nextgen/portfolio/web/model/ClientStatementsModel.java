package com.bt.nextgen.portfolio.web.model;

import java.util.ArrayList;
import java.util.List;

@Deprecated
public class ClientStatementsModel implements ClientStatementsInterface
{
	private List <StatementTypeModel> statementTypeModelList = new ArrayList <StatementTypeModel>();
	private List<StatementTypeModel> annualStatementTypeModelList = new ArrayList<>();
	private List <StatementTypeModel> accountConfirmationModelList = new ArrayList <StatementTypeModel>();
	private StatementTypeErrorModel statementTypeErrorModel;
	
	@Override
	public List <StatementTypeModel> getStatementTypeModelList()
	{
		return statementTypeModelList;
	}

	@Override
	public void setStatementTypeModelList(List <StatementTypeModel> statementTypeModelList)
	{
		this.statementTypeModelList = statementTypeModelList;
	}

	@Override
	public List<StatementTypeModel> getAccountConfirmationModelList() {
		return accountConfirmationModelList;
	}

	@Override
	public void setAccountConfirmationModelList(
			List<StatementTypeModel> accountConfirmationModelList) {
		this.accountConfirmationModelList = accountConfirmationModelList;
	}

	@Override
	public StatementTypeErrorModel getStatementTypeErrorModel() {
		return statementTypeErrorModel;
	}

	@Override
	public void setStatementTypeErrorModel(
			StatementTypeErrorModel statementTypeErrorModel) {
		this.statementTypeErrorModel = statementTypeErrorModel;
	}

	@Override
	public List<StatementTypeModel> getAnnualStatementTypeModelList()
	{
		return annualStatementTypeModelList;
	}

	@Override
	public void setAnnualStatementTypeModelList(List<StatementTypeModel> annualStatementTypeModelList)
	{
		this.annualStatementTypeModelList = annualStatementTypeModelList;
	}

}

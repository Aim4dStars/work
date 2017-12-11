package com.bt.nextgen.portfolio.web.model;

import java.util.List;

@Deprecated
public interface ClientStatementsInterface
{
	List <StatementTypeModel> getStatementTypeModelList();
	
	void setStatementTypeModelList(List <StatementTypeModel> statementTypeModelList);
	
	List<StatementTypeModel> getAccountConfirmationModelList();
	
	void setAccountConfirmationModelList(List<StatementTypeModel> accountConfirmationModelList);
	
	StatementTypeErrorModel getStatementTypeErrorModel();
	
	void setStatementTypeErrorModel(StatementTypeErrorModel statementTypeErrorModel);
	
	List<StatementTypeModel> getAnnualStatementTypeModelList();
	
	void setAnnualStatementTypeModelList(List<StatementTypeModel> annualStatementTypeModelList);

}

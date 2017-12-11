package com.bt.nextgen.portfolio.web.model;

@Deprecated
public class StatementDetailModel 
{
	private String periodTypeStatement;
	private String periodFromDate;
	private String periodToDate;
	private String documentId;
	private String newTagFlag;
	private String reportSource;
	
	public StatementDetailModel(){}
	
	public StatementDetailModel(String periodTypeStatement,String periodFromDate,
			String periodToDate,String newTagFlag ) 
	{
		this.periodTypeStatement = periodTypeStatement;
		this.periodFromDate = periodFromDate;
		this.periodToDate = periodToDate;
		this.newTagFlag=newTagFlag;
		
	}
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
	public String getNewTagFlag() 
	{
		return newTagFlag;
	}
	public void setNewTagFlag(String newTagFlag)
	{
		this.newTagFlag = newTagFlag;
	}
	public String getDocumentId() {
		return documentId;
	}

	public void setDocumentId(String documentId) {
		this.documentId = documentId;
	}
	public String getReportSource()
	{
		return reportSource;
	}

	public void setReportSource(String reportSource)
	{
		this.reportSource = reportSource;
	}
}

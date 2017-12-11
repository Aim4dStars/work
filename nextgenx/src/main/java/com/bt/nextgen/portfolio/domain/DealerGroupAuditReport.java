package com.bt.nextgen.portfolio.domain;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@Deprecated
@XmlRootElement(name = "dealerGroupAuditReport")
public class DealerGroupAuditReport
{
	private String name;
	private String fromDate;
	private String toDate;
	private String displayName;
	private String pdfFile;

	public String getName()
	{
		return name;
	}

	@XmlElement
	public void setName(String name)
	{
		this.name = name;
	}

	public String getFromDate()
	{
		return fromDate;
	}

	@XmlElement
	public void setFromDate(String fromDate)
	{
		this.fromDate = fromDate;
	}

	public String getToDate()
	{
		return toDate;
	}

	@XmlElement
	public void setToDate(String toDate)
	{
		this.toDate = toDate;
	}

	public String getDisplayName()
	{
		return displayName;
	}

	@XmlElement
	public void setDisplayName(String displayName)
	{
		this.displayName = displayName;
	}

	public String getPdfFile()
	{
		return pdfFile;
	}

	@XmlElement
	public void setPdfFile(String pdfFile)
	{
		this.pdfFile = pdfFile;
	}
}

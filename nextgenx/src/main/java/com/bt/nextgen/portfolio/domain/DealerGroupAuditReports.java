package com.bt.nextgen.portfolio.domain;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@Deprecated
@XmlRootElement(name = "dealerGroupAuditReports")
public class DealerGroupAuditReports
{
	private List<DealerGroupAuditReport> dealerGroupAuditReport;

	public List<DealerGroupAuditReport> getDealerGroupAuditReport()
	{
		return dealerGroupAuditReport;
	}

	@XmlElement
	public void setDealerGroupAuditReport(List<DealerGroupAuditReport> dealerGroupAuditReport)
	{
		this.dealerGroupAuditReport = dealerGroupAuditReport;
	}
}

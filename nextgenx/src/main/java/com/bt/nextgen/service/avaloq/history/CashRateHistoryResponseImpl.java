package com.bt.nextgen.service.avaloq.history;

import java.util.List;

import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceBeanType;
import com.bt.nextgen.integration.xml.annotation.ServiceElementList;
import com.bt.nextgen.service.avaloq.AvaloqBaseResponseImpl;
import com.bt.nextgen.service.integration.history.CashRateHistoryResponse;
import com.bt.nextgen.service.integration.history.CashReport;

@ServiceBean(xpath = "/", type = ServiceBeanType.CONCRETE)
public class CashRateHistoryResponseImpl extends AvaloqBaseResponseImpl implements CashRateHistoryResponse {

	@ServiceElementList(xpath = "//data/report/report_foot_list", type = CashReportImpl.class)
	private List <CashReport> cashReports;

	public List<CashReport> getCashReports() {
		return cashReports;
	}

	public void setCashReports(List<CashReport> cashReports) {
		this.cashReports = cashReports;
	}
}

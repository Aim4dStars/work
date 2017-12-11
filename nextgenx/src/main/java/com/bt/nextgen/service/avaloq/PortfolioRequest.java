package com.bt.nextgen.service.avaloq;

import org.joda.time.DateTime;

import com.bt.nextgen.core.web.model.SearchParameters;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.gateway.businessunit.Payee;

public interface PortfolioRequest
{
	public String getPortfolioId();
	public void setPortfolioId(String portfolioId);
	public String getFromDate();
	public void setFromDate(String fromDate);
	public String getToDate();
	public void setToDate(String toDate);
	public SearchParameters getSearchParameters();
	public void setSearchParameters(SearchParameters searchParameters);
	public DateTime getMaxValDate();
	public void setMaxValDate(DateTime maxValDate);
	public String getBglDatadownloadType();
	public void setBglDatadownloadType(String bglDatadownloadType);
	public Payee getGenericPayee();
	public void setGenericPayee(Payee genericPayee);
	public int hashCode();
}

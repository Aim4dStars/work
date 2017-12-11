package com.bt.nextgen.service.avaloq;

import java.util.Objects;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.joda.time.DateTime;

import com.bt.nextgen.core.web.model.SearchCriteria;
import com.bt.nextgen.core.web.model.SearchParameters;
import com.bt.nextgen.core.web.model.SearchParams;
import com.bt.nextgen.service.avaloq.gateway.businessunit.Payee;

public class PortfolioRequestModel implements PortfolioRequest
{
	String portfolioId;
	String fromDate;
	String toDate;
	SearchParameters searchParameters;
	DateTime maxValDate;
	String bglDatadownloadType;
	Payee genericPayee;

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		PortfolioRequestModel that = (PortfolioRequestModel) o;

		return Objects.equals(bglDatadownloadType, that.bglDatadownloadType)
				&& Objects.equals(fromDate, that.fromDate)
				&& Objects.equals(genericPayee, that.genericPayee)
				&& Objects.equals(maxValDate, that.maxValDate)
				&& Objects.equals(portfolioId, that.portfolioId)
				&& Objects.equals(searchParameters, that.searchParameters)
				&& Objects.equals(toDate, that.toDate);
	}

	public int hashCode()
	{
		String enumeratedSearchCriteria = "19";
		
		if (searchParameters != null)
		{
			if (searchParameters.getSearchCriterias() != null)
			{
				for (SearchCriteria searchCriteria : searchParameters.getSearchCriterias())
				{
					if (searchCriteria.getSearchKey() == SearchParams.startDate || searchCriteria.getSearchKey() == SearchParams.endDate)
						enumeratedSearchCriteria += searchCriteria.getSearchKey().name() + searchCriteria.getSearchValue();
				}
			}
		}
		
		return new HashCodeBuilder(17, 31).append(portfolioId).append(enumeratedSearchCriteria).hashCode();
	}
	
	public String getPortfolioId()
	{
		return portfolioId;
	}
	public void setPortfolioId(String portfolioId)
	{
		this.portfolioId = portfolioId;
	}
	public String getFromDate()
	{
		return fromDate;
	}
	public void setFromDate(String fromDate)
	{
		this.fromDate = fromDate;
	}
	public String getToDate()
	{
		return toDate;
	}
	public void setToDate(String toDate)
	{
		this.toDate = toDate;
	}
	public SearchParameters getSearchParameters()
	{
		return searchParameters;
	}
	public void setSearchParameters(SearchParameters searchParameters)
	{
		this.searchParameters = searchParameters;
	}
	public DateTime getMaxValDate()
	{
		return maxValDate;
	}
	public void setMaxValDate(DateTime maxValDate)
	{
		this.maxValDate = maxValDate;
	}
	public String getBglDatadownloadType()
	{
		return bglDatadownloadType;
	}
	public void setBglDatadownloadType(String bglDatadownloadType)
	{
		this.bglDatadownloadType = bglDatadownloadType;
	}
	public Payee getGenericPayee()
	{
		return genericPayee;
	}
	public void setGenericPayee(Payee genericPayee)
	{
		this.genericPayee = genericPayee;
	}

}

package com.bt.nextgen.service.avaloq.modelportfolio;

import com.bt.nextgen.service.integration.ips.IpsKey;
import com.bt.nextgen.service.integration.modelportfolio.CashForecast;
import com.bt.nextgen.service.integration.modelportfolio.ModelPortfolio;
import com.bt.nextgen.service.integration.modelportfolio.ShadowPortfolio;
import com.bt.nextgen.service.integration.modelportfolio.ShadowTransaction;
import org.joda.time.DateTime;

import javax.validation.constraints.NotNull;

import java.util.List;

public class ModelPortfolioImpl implements ModelPortfolio
{
	@NotNull
    private IpsKey modelKey;

	private DateTime lastUpdateDate;

	@NotNull
	private String status;

	private CashForecast cashForecast;

	private ShadowPortfolio shadowPortfolio;

	private List <ShadowTransaction> shadowTransactions;

	@Override
    public IpsKey getModelKey()
	{
		return modelKey;
	}

	@Override
	public DateTime getLastUpdateDate()
	{
		return lastUpdateDate;
	}

	@Override
	public String getStatus()
	{
		return status;
	}

	@Override
	public CashForecast getCashForecast()
	{
		return cashForecast;
	}

	@Override
	public ShadowPortfolio getShadowPortfolio()
	{
		return shadowPortfolio;
	}

	@Override
	public List <ShadowTransaction> getShadowTransactions()
	{
		return shadowTransactions;
	}

    public void setModelKey(IpsKey modelKey)
	{
        this.modelKey = modelKey;
	}

	public void setLastUpdateDate(DateTime lastUpdateDate)
	{
		this.lastUpdateDate = lastUpdateDate;
	}

	public void setStatus(String status)
	{
		this.status = status;
	}

	public void setShadowPortfolio(ShadowPortfolio shadowPortfolio)
	{
		this.shadowPortfolio = shadowPortfolio;
	}

	public void setShadowTransactions(List <ShadowTransaction> shadowTransactions)
	{
		this.shadowTransactions = shadowTransactions;
	}

	public void setCashForecast(CashForecast cashForecast)
	{
		this.cashForecast = cashForecast;
	}
}

package com.bt.nextgen.service.integration.modelportfolio;

import com.bt.nextgen.service.integration.ips.IpsKey;
import org.joda.time.DateTime;

import java.util.List;

public interface ModelPortfolio
{
    public IpsKey getModelKey();

	public DateTime getLastUpdateDate();

	public String getStatus();

	public CashForecast getCashForecast();

	public ShadowPortfolio getShadowPortfolio();

	public List <ShadowTransaction> getShadowTransactions();
}

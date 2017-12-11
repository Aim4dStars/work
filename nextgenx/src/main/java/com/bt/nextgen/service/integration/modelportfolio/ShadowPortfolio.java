package com.bt.nextgen.service.integration.modelportfolio;

import java.util.List;

import org.joda.time.DateTime;

public interface ShadowPortfolio
{
	public DateTime getAsAtDate();

	public List <ShadowPortfolioAssetSummary> getAssetSummaries();

	public ShadowPortfolioDetail getTotal();
}

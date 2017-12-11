package com.bt.nextgen.service.avaloq.modelportfolio;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.joda.time.DateTime;

import com.bt.nextgen.service.integration.modelportfolio.ShadowPortfolio;
import com.bt.nextgen.service.integration.modelportfolio.ShadowPortfolioAssetSummary;
import com.bt.nextgen.service.integration.modelportfolio.ShadowPortfolioDetail;

public class ShadowPortfolioImpl implements ShadowPortfolio
{
	@NotNull
	private DateTime asAtDate;

	@NotNull
	@Valid
	private List <ShadowPortfolioAssetSummary> assetSummaries;

	@NotNull
	@Valid
	private ShadowPortfolioDetail total;

	@Override
	public DateTime getAsAtDate()
	{
		return asAtDate;
	}

	@Override
	public List <ShadowPortfolioAssetSummary> getAssetSummaries()
	{
		return assetSummaries;
	}

	@Override
	public ShadowPortfolioDetail getTotal()
	{
		return total;
	}

	public void setAsAtDate(DateTime asAtDate)
	{
		this.asAtDate = asAtDate;
	}

	public void setAssetSummaries(List <ShadowPortfolioAssetSummary> assetSummaries)
	{
		this.assetSummaries = assetSummaries;
	}

	public void setTotal(ShadowPortfolioDetail total)
	{
		this.total = total;
	}
}

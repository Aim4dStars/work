package com.bt.nextgen.service.avaloq.modelportfolio;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.bt.nextgen.service.integration.modelportfolio.ShadowPortfolioAsset;
import com.bt.nextgen.service.integration.modelportfolio.ShadowPortfolioAssetSummary;
import com.bt.nextgen.service.integration.modelportfolio.ShadowPortfolioDetail;

public class ShadowPortfolioAssetSummaryImpl implements ShadowPortfolioAssetSummary
{
	@NotNull
	private String assetClass;

	@NotNull
	@Valid
	private List <ShadowPortfolioAsset> assets;

	@NotNull
	@Valid
	private ShadowPortfolioDetail total;

	@Override
	public String getAssetClass()
	{
		return assetClass;
	}

	@Override
	public List <ShadowPortfolioAsset> getAssets()
	{
		return assets;
	}

	@Override
	public ShadowPortfolioDetail getTotal()
	{
		return total;
	}

	public void setAssetClass(String assetClass)
	{
		this.assetClass = assetClass;
	}

	public void setAssets(List <ShadowPortfolioAsset> assets)
	{
		this.assets = assets;
	}

	public void setTotal(ShadowPortfolioDetail total)
	{
		this.total = total;
	}
}

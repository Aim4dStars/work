package com.bt.nextgen.service.integration.modelportfolio;

import java.util.List;

public interface ShadowPortfolioAssetSummary
{
	public String getAssetClass();

	public List <ShadowPortfolioAsset> getAssets();

	public ShadowPortfolioDetail getTotal();
}

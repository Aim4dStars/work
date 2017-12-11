package com.bt.nextgen.service.avaloq.modelportfolio;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.bt.nextgen.service.integration.modelportfolio.ShadowPortfolioAsset;
import com.bt.nextgen.service.integration.modelportfolio.ShadowPortfolioDetail;

public class ShadowPortfolioAssetImpl implements ShadowPortfolioAsset
{
	@NotNull
	private String assetId;

	@NotNull
	@Valid
	private ShadowPortfolioDetail shadowDetail;

	@Override
	public String getAssetId()
	{
		return assetId;
	}

	@Override
	public ShadowPortfolioDetail getShadowDetail()
	{
		return shadowDetail;
	}

	public void setAssetId(String assetId)
	{
		this.assetId = assetId;
	}

	public void setShadowDetail(ShadowPortfolioDetail shadowDetail)
	{
		this.shadowDetail = shadowDetail;
	}
}

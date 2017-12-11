package com.bt.nextgen.order.web.model;

public class ManagedPortfolioProduct implements ManagedPortfolioProductInterface
{
	private String assetId;
	private String assetName;
	private String assetCode;

	public ManagedPortfolioProduct(String assetId, String assetName, String assetCode)
	{
		this.assetId = assetId;
		this.assetName = assetName;
		this.assetCode = assetCode;
	}

	public String getAssetId()
	{
		return assetId;
	}

	public String getAssetName()
	{
		return assetName;
	}

	public String getAssetCode()
	{
		return assetCode;
	}

}

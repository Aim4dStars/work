package com.bt.nextgen.api.asset.model;

import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.model.KeyedDto;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetPrice;
import com.bt.nextgen.service.integration.asset.AssetPriceSource;
import com.bt.nextgen.service.integration.asset.AssetPriceStatus;

public class AssetPriceDto extends BaseDto implements KeyedDto<AssetPriceDtoKey> {
	private String assetId;
	private String assetCode;
	private Double lastPrice;
	private AssetPriceStatus status;
	private AssetPriceSource source;
	private String message;

	public AssetPriceDto() {
	}

	public AssetPriceDto(AssetPrice assetPrice) {
		this.assetId = assetPrice.getAsset().getAssetId();
		this.assetCode = assetPrice.getAsset().getAssetCode();
		this.lastPrice = assetPrice.getLastPrice();
		this.status = assetPrice.getAssetPriceStatus();
		this.message = assetPrice.getMessage();
		this.source = assetPrice.getAssetPriceSource();
	}

	public AssetPriceDto(Asset asset, AssetPriceStatus status, AssetPriceSource source, Double lastPrice) {
		this.assetId = asset.getAssetId();
		this.status = status;
		this.source = source;
		this.lastPrice = lastPrice;
	}

	public String getAssetCode() {
		return assetCode;
	}

	public String getAssetId() {
		return assetId;
	}

	public Double getLastPrice() {
		return lastPrice;
	}

	public AssetPriceStatus getStatus() {
		return status;
	}

	public String getMessage() {
		return message;
	}

	public AssetPriceSource getSource() {
		return source;
	}

	@Override
	public AssetPriceDtoKey getKey() {
		return null;
	}
}

package com.bt.nextgen.api.asset.model;

import com.bt.nextgen.core.domain.key.StringIdKey;

public class AssetPriceDtoKey extends StringIdKey {
	private Boolean live;
	private Boolean comprehensive;
	private Boolean useFallback;

	public AssetPriceDtoKey(String assetId, Boolean live, Boolean comprehensive, Boolean useFallback) {
		super(assetId);

		this.live = live;
		this.comprehensive = comprehensive;
		this.useFallback = useFallback;
	}

	public Boolean isComprehensive() {
		return comprehensive;
	}

	public void setComprehensive(Boolean comprehensive) {
		this.comprehensive = comprehensive;
	}

	public Boolean isLive() {
		return live;
	}

	public void setLive(Boolean live) {
		this.live = live;
	}

	public Boolean isUseFallback() {
		return useFallback;
	}

	public void setUseFallback(Boolean useFallback) {
		this.useFallback = useFallback;
	}
}

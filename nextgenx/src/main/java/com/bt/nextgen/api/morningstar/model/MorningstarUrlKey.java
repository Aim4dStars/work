package com.bt.nextgen.api.morningstar.model;

import com.bt.nextgen.core.domain.key.StringIdKey;

/**
 * Key to generate Morningstar URL
 *
 * Currently consists of only asset ID
 */
public class MorningstarUrlKey extends StringIdKey {
	private String assetId;

	public MorningstarUrlKey(String assetId) {
		super(assetId);
		this.assetId = assetId;
	}

	public String getAssetId() {
		return assetId;
	}
}

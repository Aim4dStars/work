package com.bt.nextgen.api.morningstar.model;

public class MorningstarDocumentKey {
	private String assetId;
	private String documentType;

	public MorningstarDocumentKey() {
	}

	public MorningstarDocumentKey(String assetId, String documentType) {
		this.assetId = assetId;
		this.documentType = documentType;
	}

	public String getAssetId() {
		return assetId;
	}

	public String getDocumentType() {
		return documentType;
	}
}

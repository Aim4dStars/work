package com.bt.nextgen.api.morningstar.model;

public class MorningstarAssetProfileKey {
	private String symbol;
	private String type;
	private String client;

	public MorningstarAssetProfileKey() {
	}

	public MorningstarAssetProfileKey(String symbol, String type, String client) {
		this.symbol = symbol;
		this.type = type;
		this.client = client;
	}

	public String getSymbol() {
		return symbol;
	}

	public String getType() {
		return type;
	}

	public String getClient() {
		return client;
	}
}

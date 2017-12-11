package com.bt.nextgen.api.marketdata.v1.model;

public class UrlParam
{
	public final static String equals = "=";
	private String key;
	private String value;

	public UrlParam(String key, String value)
	{
		this.key = key;
		this.value = value;
	}

	public String getUrlParam()
	{
		return this.key + equals + this.value;
	}
}

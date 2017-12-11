package com.bt.nextgen.api.statements.model;

public class SupplimentaryDocument
{
	private String name;
	private String url;

	public SupplimentaryDocument(String name, String url)
	{
		this.name = name;
		this.url = url;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getUrl()
	{
		return url;
	}

	public void setUrl(String url)
	{
		this.url = url;
	}
}
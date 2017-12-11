package com.bt.nextgen.api.account.v2.model;


import org.joda.time.DateTime;

import java.util.Map;

@Deprecated
public class ParameterisedDatedValuationKey extends DatedValuationKey
{
	private Map<String, String> parameters;

	public ParameterisedDatedValuationKey(String accountId, DateTime effectiveDate, Boolean includeExternal, Map<String, String> parameters)
	{
		super(accountId, effectiveDate, includeExternal);
		this.parameters = parameters;
	}

	public Map<String, String> getParameters()
	{
		return parameters;
	}

	public void setParameters(Map<String, String> parameters)
	{
		this.parameters = parameters;
	}
}

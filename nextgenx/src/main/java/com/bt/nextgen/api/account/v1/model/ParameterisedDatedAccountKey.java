package com.bt.nextgen.api.account.v1.model;


import org.joda.time.DateTime;

import java.util.Map;

@Deprecated
public class ParameterisedDatedAccountKey extends DatedAccountKey
{
	Map<String, String> parameters;

	public ParameterisedDatedAccountKey(String accountId, DateTime effectiveDate, Map<String, String> parameters) {
		super(accountId, effectiveDate);
		this.parameters = parameters;
	}

	public Map<String, String> getParameters()
	{
		return parameters;
	}
}
package com.bt.nextgen.api.account.v2.model;

import com.bt.nextgen.api.account.v2.model.AccountKey;

import java.util.HashMap;
import java.util.Map;

@Deprecated
public class ParameterisedAccountKey extends AccountKey
{
	private Map<String, String> parameters = new HashMap<>();

	public ParameterisedAccountKey(String accountId, Map<String, String> parameters)
	{
		super(accountId);
		this.parameters = parameters;
	}

	public Map<String, String> getParameters()
	{
		return parameters;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((parameters == null) ? 0 : parameters.hashCode());
		return result;
	}

	@SuppressWarnings({ "squid:MethodCyclomaticComplexity", "squid:S1142" })
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		ParameterisedAccountKey other = (ParameterisedAccountKey) obj;
		if (parameters == null) {
			if (other.parameters != null)
				return false;
		} else if (!parameters.equals(other.parameters))
			return false;
		return true;
	}
}
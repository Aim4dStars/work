package com.bt.nextgen.api.portfolio.v3.model.valuation;

import com.bt.nextgen.api.account.v3.model.AccountKey;
import org.joda.time.DateTime;

import java.util.Map;


public class ParameterisedAccountKey extends AccountKey
{
	private Map<String, String> parameters;

	public ParameterisedAccountKey(String accountId, Map<String, String> parameters)
	{
		super(accountId);
		this.parameters = parameters;
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
package com.bt.nextgen.api.statements.model;


public class StatementKey
{
	private String statementId;

	public StatementKey()
	{}

	public StatementKey(String statementId)
	{
		super();
		this.statementId = statementId;
	}

	public String getStatementId()
	{
		return statementId;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((statementId == null) ? 0 : statementId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		StatementKey other = (StatementKey)obj;
		if (statementId == null)
		{
			if (other.statementId != null)
				return false;
		}
		else if (!statementId.equals(other.statementId))
			return false;
		return true;
	}

}

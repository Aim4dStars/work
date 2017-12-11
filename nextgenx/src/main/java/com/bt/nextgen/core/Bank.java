package com.bt.nextgen.core;

public class Bank implements Comparable<Bank>
{
	private final String id;

	public Bank(String id)
	{
		this.id = id;
	}

	public String getId()
	{
		return id;
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (o == null || getClass() != o.getClass())
		{
			return false;
		}

		Bank bank = (Bank) o;

		if (!id.equals(bank.id))
		{
			return false;
		}

		return true;
	}

	@Override
	public int hashCode()
	{
		return id.hashCode();
	}

	@Override
	public int compareTo(Bank o)
	{
		return id.compareTo(o.getId());
		
	}
}

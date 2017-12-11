package com.bt.nextgen.api.termdeposit.model;

public class Brand implements Comparable<Brand>
{
	private final String id;

	public Brand(String id)
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

		Brand bank = (Brand) o;

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
	public int compareTo(Brand o)
	{
		return id.compareTo(o.getId());
		
	}
}

package com.bt.nextgen.api.termdeposit.model;

import com.bt.nextgen.api.product.model.ProductKey;

public class Badge extends ProductKey implements Comparable<Badge>
{
	private final String name;

	public Badge(String id, String name)
	{
		super(id);
		this.name = name;
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
		return true;
	}

	@Override
	public int hashCode()
	{
		return getProductId().hashCode();
	}

	@Override
    public int compareTo(Badge o) {
        if (getName() == null) {
            return 1;
        }
        return getName().compareTo(o.getName());
    }

	public String getName() {
		return name;
	}
}

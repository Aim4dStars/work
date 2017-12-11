package com.bt.nextgen.api.product.model;

@Deprecated
public class ProductKey
{
	private String productId;

	public ProductKey()
	{}

	public ProductKey(String productId)
	{
		super();
		this.productId = productId;
	}

	public String getProductId()
	{
		return productId;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((productId == null) ? 0 : productId.hashCode());
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
		ProductKey other = (ProductKey)obj;
		if (productId == null)
		{
			if (other.productId != null)
				return false;
		}
		else if (!productId.equals(other.productId))
			return false;
		return true;
	}
}

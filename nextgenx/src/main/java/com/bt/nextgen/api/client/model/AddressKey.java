package com.bt.nextgen.api.client.model;

/**
 * Created by L062329 on 8/09/2014.
 */
public class AddressKey
{
	private String addressId;

	public AddressKey()
	{}

	public AddressKey(String addressId)
	{
		super();
		this.addressId = addressId;
	}

	public String getAddressId()
	{
		return addressId;
	}

	public void setAddressId(String addressId)
	{
		this.addressId = addressId;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((addressId == null) ? 0 : addressId.hashCode());
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
		AddressKey other = (AddressKey)obj;
		if (addressId == null)
		{
			if (other.addressId != null)
				return false;
		}
		else if (!addressId.equals(other.addressId))
			return false;
		return true;
	}
}

package com.bt.nextgen.service.integration.domain;

public interface AddressUpdate
{
	public void setAddressKey(AddressKey addressKey);

	public AddressKey getAddressKey();

	public void setAddrModificationNumber(String addrModificationNumber);

	public String getAddrModificationNumber();
}

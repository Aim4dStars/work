package com.bt.nextgen.api.client.model;

import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.model.KeyedDto;

public class ClientIdentificationDto extends BaseDto implements KeyedDto <ClientKey>
{
	private ClientKey key;

	public final ClientKey getKey()
	{
		return key;
	}

	public final void setKey(ClientKey key)
	{
		this.key = key;
	}
}

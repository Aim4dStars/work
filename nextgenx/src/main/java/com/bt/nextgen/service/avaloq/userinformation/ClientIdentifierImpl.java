package com.bt.nextgen.service.avaloq.userinformation;

import com.bt.nextgen.service.integration.userinformation.ClientKey;
import com.bt.nextgen.service.integration.userinformation.ClientIdentifier;

/**
 * ClientIdentifier will hold the specific information that can be used to Identify a Person across the application
 *
 */
public class ClientIdentifierImpl implements ClientIdentifier
{
	private ClientKey clientKey;
	
	@Override
	public ClientKey getClientKey()
	{
		return clientKey;
	}

	@Override
	public void setClientKey(ClientKey clientKey)
	{
		this.clientKey = clientKey;
	}

}

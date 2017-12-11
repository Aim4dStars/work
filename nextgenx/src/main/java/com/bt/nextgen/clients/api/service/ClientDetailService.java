package com.bt.nextgen.clients.api.service;

import com.bt.nextgen.service.integration.userinformation.ClientKey;
import com.bt.nextgen.service.integration.broker.BrokerUser;

public interface ClientDetailService 
{
	public BrokerUser getAdviserForInvestor(ClientKey clientKey);
}

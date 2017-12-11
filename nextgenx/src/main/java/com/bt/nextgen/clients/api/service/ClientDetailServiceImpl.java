package com.bt.nextgen.clients.api.service;

import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.userinformation.ClientDetail;
import com.bt.nextgen.service.integration.userinformation.ClientKey;
import com.btfin.panorama.service.integration.account.WrapAccountDetail;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.broker.BrokerUser;
import com.bt.nextgen.service.integration.client.ClientIntegrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Collection;

/**
 * High level abstraction to retrieve client details
 */
@Service
public class ClientDetailServiceImpl implements ClientDetailService
{

	@Autowired
	@Qualifier("avaloqAccountIntegrationService")
	AccountIntegrationService accountIntegrationService;
	
	@Autowired
	@Qualifier("avaloqClientIntegrationService")
    ClientIntegrationService clientIntegationService;
	
	@Autowired BrokerIntegrationService brokerIntegrationService;
	
	
	/***
	 * Returns the adviser acting on behalf of an investor
	 * @param clientKey investor client key
	 * @return BrokerUser adviser of the investor
	 */
	public BrokerUser getAdviserForInvestor(ClientKey clientKey) 
	{
		BrokerUser adviserBrokerUser = null;
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		
		ClientDetail investor = clientIntegationService.loadClientDetails(clientKey, serviceErrors);
		Collection<AccountKey> wrapAccountKeys = investor.getWrapAccounts();
				
		//TOOD: What do we do in the case of multiple adviser?
		if (wrapAccountKeys != null && wrapAccountKeys.size() >= 1)
		{
			WrapAccountDetail wrapAccountDetail = accountIntegrationService.loadWrapAccountDetail(wrapAccountKeys.iterator().next(), serviceErrors);
			BrokerKey brokerKey = wrapAccountDetail.getAdviserPositionId();
			
			adviserBrokerUser = brokerIntegrationService.getAdviserBrokerUser(brokerKey, serviceErrors);
		}
		
		return adviserBrokerUser;
	}

}

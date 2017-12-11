package com.bt.nextgen.service.integration.payeedetails;

import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.integration.wrapaccount.WrapAccountIdentifier;

public interface PayeeDetailsIntegrationService
{
	PayeeDetails loadPayeeDetails(WrapAccountIdentifier identifier, ServiceErrors serviceErrors);

	void clearCache(WrapAccountIdentifier identifier);
}

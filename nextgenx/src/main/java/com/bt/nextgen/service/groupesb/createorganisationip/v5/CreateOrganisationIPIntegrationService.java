package com.bt.nextgen.service.groupesb.createorganisationip.v5;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.group.customer.groupesb.CustomerRawData;

public interface CreateOrganisationIPIntegrationService {
	CustomerRawData createorganisationIP(
			CreateOrganisationIPReq request, ServiceErrors serviceErrors);

}

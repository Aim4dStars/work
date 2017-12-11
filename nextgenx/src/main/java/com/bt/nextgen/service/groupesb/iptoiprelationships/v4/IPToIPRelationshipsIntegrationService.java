package com.bt.nextgen.service.groupesb.iptoiprelationships.v4;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.group.customer.groupesb.CustomerRawData;
import com.bt.nextgen.service.group.customer.groupesb.maintainarrangementandiparrangementrelationships.RetriveIPToIPRequest;

public interface IPToIPRelationshipsIntegrationService {
	CustomerRawData retrieveIpToIpRelationshipsInformation(
			RetriveIPToIPRequest request, ServiceErrors serviceErrors);

}

package com.bt.nextgen.service.group.maintainarrangementandiparrangementrelationships;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.group.customer.groupesb.CustomerRawData;
import com.bt.nextgen.service.group.customer.groupesb.maintainarrangementandiparrangementrelationships.ArrangementAndRelationshipManagementRequest;

public interface MaintainArrangementAndRelationshipIntegrationService {

	public CustomerRawData createArrangementAndRelationShip(
			ArrangementAndRelationshipManagementRequest input,
			ServiceErrors serviceError);
}

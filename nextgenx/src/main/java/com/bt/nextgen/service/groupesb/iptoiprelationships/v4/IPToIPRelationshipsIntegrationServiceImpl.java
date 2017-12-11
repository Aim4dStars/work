package com.bt.nextgen.service.groupesb.iptoiprelationships.v4;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.group.customer.groupesb.CustomerRawData;
import com.bt.nextgen.service.group.customer.groupesb.GroupEsbCustomerManagementImpl;
import com.bt.nextgen.service.group.customer.groupesb.maintainarrangementandiparrangementrelationships.RetriveIPToIPRequest;

@Service("iptoiprelationshipsintegrationservice")
public class IPToIPRelationshipsIntegrationServiceImpl implements
		IPToIPRelationshipsIntegrationService {
	private static final Logger logger = LoggerFactory.getLogger(IPToIPRelationshipsIntegrationServiceImpl.class);
	@Autowired
	@Qualifier("iptoiprelationshipsintegrationservicev4")
	private IPToIPRelationshipsIntegrationService integrationServiceV4;

	@Override
	public CustomerRawData retrieveIpToIpRelationshipsInformation(
			RetriveIPToIPRequest request, ServiceErrors serviceErrors) {
		logger.info("IPToIPRelationshipsIntegrationServiceImpl.retrieveIpToIpRelationshipsInformation(): Performing operation on SVC0260 Version 4.");
		CustomerRawData customerRawData = integrationServiceV4.retrieveIpToIpRelationshipsInformation(request, serviceErrors);
		return customerRawData;
	}

}

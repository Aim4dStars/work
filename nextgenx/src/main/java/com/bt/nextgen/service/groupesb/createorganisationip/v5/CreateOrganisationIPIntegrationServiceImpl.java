package com.bt.nextgen.service.groupesb.createorganisationip.v5;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.group.customer.groupesb.CustomerRawData;

@Service("createorganisationipintegrationservice")
public class CreateOrganisationIPIntegrationServiceImpl implements
		CreateOrganisationIPIntegrationService {
	private static final Logger logger = LoggerFactory.getLogger(CreateOrganisationIPIntegrationServiceImpl.class);
	@Autowired
	@Qualifier("createorganisationipintegrationservicev5")
	private CreateOrganisationIPIntegrationService integrationServiceV5;

	@Override
	public CustomerRawData createorganisationIP(
			CreateOrganisationIPReq request, ServiceErrors serviceErrors) {
		logger.info("CreateOrganisationIPIntegrationServiceImpl.createorganisationIP(): Performing operation on SVC0337 Version 5.");
		CustomerRawData customerRawData = integrationServiceV5.createorganisationIP(request, serviceErrors);
		return customerRawData;
	}

}

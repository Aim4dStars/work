/**
 * 
 */
package com.bt.nextgen.service.gesb.maintainipcontactmethod.v1;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.group.customer.groupesb.CustomerRawData;

/**
 * @author L081050
 * 
 */
@Service("maintainipcontactintegrationservice")
@SuppressWarnings("squid:S1200")
public class MaintainIpContactIntegrationServiceImpl implements
		MaintainIpContactIntegrationService {
	private static final Logger logger = LoggerFactory
			.getLogger(MaintainIpContactIntegrationServiceImpl.class);
	@Autowired
	@Qualifier("maintainipcontactintegrationservicev1")
	private MaintainIpContactIntegrationService maintainIpContactIntegrationServicev1;

	public CustomerRawData maintain(MaintainIpContactRequest req,
			ServiceErrors serviceErrors) {
		logger.info("Calling maintainipcontactintegrationservicev1.maintain");
		CustomerRawData customerData = maintainIpContactIntegrationServicev1
				.maintain(req, serviceErrors);

		return customerData;
	}

}

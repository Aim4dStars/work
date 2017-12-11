/**
 * 
 */
package com.bt.nextgen.service.gesb.maintainidvdetail.v5;

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
@Service("maintainidvdetailintegrationservice")
@SuppressWarnings("squid:S1200")
public class MaintainIdvDetailIntegrationServiceImpl implements
		MaintainIdvDetailIntegrationService {
	private static final Logger logger = LoggerFactory
			.getLogger(MaintainIdvDetailIntegrationServiceImpl.class);
	@Autowired
	@Qualifier("maintainidvdetailintegrationservicev5")
	private MaintainIdvDetailIntegrationService maintainIdvDetailIntegrationServiceV5;

	@Override
	public CustomerRawData maintain(MaintainIdvRequest req,
			ServiceErrors serviceErrors) {
		logger.info("Calling maintainIdvDetailIntegrationServiceV5.maintain");
		CustomerRawData customerData = maintainIdvDetailIntegrationServiceV5
				.maintain(req, serviceErrors);

		return customerData;
	}

}

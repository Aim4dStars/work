package com.bt.nextgen.service.group.customer.groupesb.retriveidvdetails.v6;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.group.customer.groupesb.CustomerRawData;

@Service("idvdetailsintegrationservice")
public class RetriveIDVDetaisIntegrationServiceImpl implements RetriveIDVDetailsIntegrationService {
    private static final Logger logger = LoggerFactory.getLogger(RetriveIDVDetaisIntegrationServiceImpl.class);

    @Autowired
    @Qualifier("retriveidvdetialsintegrationservicev6")
    private RetriveIDVDetailsIntegrationServiceImplv6 integrationServiceV6;

    @Override
    public CustomerRawData retrieveIDVDetails(RetriveIDVDtlRequest request, ServiceErrors serviceErrors) {
        logger.info("RetriveIDVDetaisIntegrationServiceImpl.retrieveIDVDetails(): Performing operation on SVC0324 Version 6.");
        CustomerRawData customerRawData = integrationServiceV6.retrieveIDVDetails(request, serviceErrors);
        return customerRawData;
    }

}

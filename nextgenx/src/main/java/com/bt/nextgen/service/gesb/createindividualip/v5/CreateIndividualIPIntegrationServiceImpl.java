package com.bt.nextgen.service.gesb.createindividualip.v5;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.group.customer.groupesb.CustomerRawData;

/**
 * @author L081050
 */
@Service("createindividualipintegrationservice")
@SuppressWarnings("squid:S1200")
public class CreateIndividualIPIntegrationServiceImpl implements CreateIndividualIPIntegrationService {

    @Autowired
    @Qualifier("createindividualipintegrationservicev5")
    private CreateIndividualIPIntegrationService integrationServiceV5;

    @Override
    public CustomerRawData create(CreateIndvIPRequest req, ServiceErrors serviceErrors) {
        CustomerRawData customerRawData = integrationServiceV5.create(req, serviceErrors);

        return customerRawData;
    }

}
package com.bt.nextgen.service.group.maintainiptoiprelationships.groupesb;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.group.customer.groupesb.CustomerRawData;

@Service("maintainIpToIpRelationshipIntegrationService")
@SuppressWarnings("squid:S1200")
public class GroupEsbIpToIpRelationManagementImpl implements MaintainIpToIpRelationshipIntegrationService {

    @Autowired
    @Qualifier("maintainIpToIpRelationshipIntegrationServiceV1")
    private MaintainIpToIpRelationshipIntegrationService integrationServiceV1;

    @Override
    public CustomerRawData maintainIpToIpRelationship(IpToIpRelationshipRequest input, ServiceErrors serviceError) {
        CustomerRawData customerRawData = integrationServiceV1.maintainIpToIpRelationship(input, serviceError);

        return customerRawData;
    }

}

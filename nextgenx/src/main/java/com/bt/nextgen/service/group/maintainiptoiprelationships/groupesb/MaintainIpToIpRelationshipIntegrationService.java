package com.bt.nextgen.service.group.maintainiptoiprelationships.groupesb;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.group.customer.groupesb.CustomerRawData;

public interface MaintainIpToIpRelationshipIntegrationService {

    public CustomerRawData maintainIpToIpRelationship(IpToIpRelationshipRequest input, ServiceErrors serviceError);
}

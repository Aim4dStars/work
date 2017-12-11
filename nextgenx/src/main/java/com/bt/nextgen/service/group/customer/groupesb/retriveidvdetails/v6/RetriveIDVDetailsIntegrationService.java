package com.bt.nextgen.service.group.customer.groupesb.retriveidvdetails.v6;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.group.customer.groupesb.CustomerRawData;

public interface RetriveIDVDetailsIntegrationService {
    CustomerRawData retrieveIDVDetails(RetriveIDVDtlRequest request, ServiceErrors serviceErrors);

}

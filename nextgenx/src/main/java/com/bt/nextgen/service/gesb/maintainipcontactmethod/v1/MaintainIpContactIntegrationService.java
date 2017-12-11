package com.bt.nextgen.service.gesb.maintainipcontactmethod.v1;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.group.customer.groupesb.CustomerRawData;

public interface MaintainIpContactIntegrationService {
    CustomerRawData maintain(MaintainIpContactRequest req, ServiceErrors serviceErrors);
}

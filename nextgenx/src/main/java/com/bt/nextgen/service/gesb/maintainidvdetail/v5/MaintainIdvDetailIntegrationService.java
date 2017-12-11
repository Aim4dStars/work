package com.bt.nextgen.service.gesb.maintainidvdetail.v5;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.group.customer.groupesb.CustomerRawData;

public interface MaintainIdvDetailIntegrationService {
    CustomerRawData maintain(MaintainIdvRequest req, ServiceErrors serviceErrors);
}

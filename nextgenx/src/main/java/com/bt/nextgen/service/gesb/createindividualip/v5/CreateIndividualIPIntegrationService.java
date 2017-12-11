package com.bt.nextgen.service.gesb.createindividualip.v5;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.group.customer.groupesb.CustomerRawData;

/**
 * @author L081050
 *
 */
public interface CreateIndividualIPIntegrationService {
    CustomerRawData create(CreateIndvIPRequest req, ServiceErrors serviceErrors);
}
/**
 * 
 */
package com.bt.nextgen.api.client.service;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.group.customer.groupesb.CustomerRawData;
import com.bt.nextgen.serviceops.model.MaintainIPContactMethodModel;

/**
 * @author L081050
 *
 */
public interface MaintainIpContactDtoService {
    CustomerRawData maintain(MaintainIPContactMethodModel reqModel, ServiceErrors serviceErrors);
}

/**
 * 
 */
package com.bt.nextgen.api.client.service;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.group.customer.groupesb.CustomerRawData;
import com.bt.nextgen.serviceops.model.MaintainIdvDetailReqModel;

/**
 * @author L081050
 *
 */
public interface MaintainIdvDetailDtoService {
    CustomerRawData maintain(MaintainIdvDetailReqModel reqModel, ServiceErrors serviceErrors);
}

/**
 * this is the dto service interfase
 * @since 1.0
 *  @author L081050
 * @version 1.0
 */
package com.bt.nextgen.api.client.service;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.group.customer.groupesb.CustomerRawData;
import com.bt.nextgen.serviceops.model.RetriveIpToIpRelationshipReqModel;

/**
 * @author L081050
 */
public interface IPToIPRelationshipsDataDtoService {
    /**
     * @param reqModel ...
     * @param serviceErrors ...
     * @return CustomerRawData ...
     */
    CustomerRawData retrieve(RetriveIpToIpRelationshipReqModel reqModel, ServiceErrors serviceErrors);

}

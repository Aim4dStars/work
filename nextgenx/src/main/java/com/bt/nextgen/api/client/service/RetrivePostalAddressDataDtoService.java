package com.bt.nextgen.api.client.service;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.group.customer.groupesb.CustomerRawData;
import com.bt.nextgen.serviceops.model.RetrivePostalAddressReqModel;

/**
 * this is the dto service interface
 * @since 1.0
 * @version 1.0
 */
public interface RetrivePostalAddressDataDtoService {
    /**
     * @param reqModel ...
     * @param serviceErrors ...
     * @return CustomerRawData ...
     */
    CustomerRawData retrieve(RetrivePostalAddressReqModel reqModel, ServiceErrors serviceErrors);

}

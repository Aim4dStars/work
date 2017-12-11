/**
 * 
 */
package com.bt.nextgen.api.client.service;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.group.customer.groupesb.CustomerRawData;
import com.bt.nextgen.serviceops.model.CreateIndividualIPEmailPhoneContactMethodsReqModel;
import com.bt.nextgen.serviceops.model.CreateIndividualIPReqModel;

/**
 * @author L081050
 *
 */
public interface CreateIndividualIPDtoService {
    CustomerRawData create(CreateIndividualIPReqModel reqModel,CreateIndividualIPEmailPhoneContactMethodsReqModel emailPhoneReqMode, ServiceErrors serviceErrors);
}
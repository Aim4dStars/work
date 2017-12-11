package com.bt.nextgen.api.client.service;

import com.bt.nextgen.api.client.model.ClientUpdateKey;
import com.bt.nextgen.core.api.dto.FindByKeyDtoService;
import com.bt.nextgen.core.api.dto.UpdateDtoService;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.group.customer.groupesb.CustomerRawData;

/**
 * Created by L070815 on 30/07/2015.
 */
public interface CustomerDataDtoService extends FindByKeyDtoService<ClientUpdateKey, CustomerDataDto>,
        UpdateDtoService<ClientUpdateKey, CustomerDataDto> {

    public CustomerRawData retrieve(ClientUpdateKey key, String silo, String[] operationTypes, ServiceErrors serviceErrors);
}

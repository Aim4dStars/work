package com.bt.nextgen.api.client.service;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.group.customer.groupesb.CustomerRawData;
import com.bt.nextgen.serviceops.model.MaintainIpToIpRelationshipReqModel;

public interface MaintainIpToIpRelationshipDTOService {

   public CustomerRawData maintainIpToIpRelationship(MaintainIpToIpRelationshipReqModel input, ServiceErrors serviceError);
}

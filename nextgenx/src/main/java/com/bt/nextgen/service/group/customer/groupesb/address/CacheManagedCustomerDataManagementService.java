package com.bt.nextgen.service.group.customer.groupesb.address;

import com.bt.nextgen.core.webservice.provider.CorrelatedResponse;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.group.customer.groupesb.CustomerManagementRequest;

public interface CacheManagedCustomerDataManagementService {
    public CorrelatedResponse retrieveCustomerInformation(CustomerManagementRequest request, final ServiceErrors serviceErrors);

    public void clearCustomerInformation(CustomerManagementRequest request);
}

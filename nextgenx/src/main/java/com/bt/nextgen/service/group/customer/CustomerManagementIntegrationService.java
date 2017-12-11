package com.bt.nextgen.service.group.customer;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.group.customer.groupesb.CustomerManagementRequest;
import com.bt.nextgen.service.group.customer.groupesb.CustomerRawData;

import java.util.List;

/**
 * Created by L069679 on 16/01/2017.
 */
public interface CustomerManagementIntegrationService {

    CustomerRawData retrieveCustomerRawInformation(CustomerManagementRequest request, List<String> operationTypes, ServiceErrors errors);
}

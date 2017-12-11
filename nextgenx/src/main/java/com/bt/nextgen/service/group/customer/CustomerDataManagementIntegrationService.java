package com.bt.nextgen.service.group.customer;

import java.util.List;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.group.customer.groupesb.CustomerData;
import com.bt.nextgen.service.group.customer.groupesb.CustomerManagementRequest;

/**
 * Created by F057654 on 23/07/2015.
 */
public interface CustomerDataManagementIntegrationService {

    CustomerData retrieveCustomerInformation(CustomerManagementRequest request, List<String> operationTypes, ServiceErrors errors);
    boolean updateCustomerInformation(CustomerData updatedData, ServiceErrors serviceErrors);
}

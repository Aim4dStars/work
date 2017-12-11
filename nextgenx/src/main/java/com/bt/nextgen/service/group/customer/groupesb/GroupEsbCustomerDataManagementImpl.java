package com.bt.nextgen.service.group.customer.groupesb;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.group.customer.CustomerDataManagementIntegrationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by F057654 on 23/07/2015.
 */
@Service("customerDataManagementService")
public class GroupEsbCustomerDataManagementImpl implements CustomerDataManagementIntegrationService {

    private static final Logger logger = LoggerFactory.getLogger(GroupEsbCustomerDataManagementImpl.class);

    @Resource(name = "customerDataManagementV11Service")
    private CustomerDataManagementIntegrationService groupEsbCustomerDataManagementV11Impl;

    @Override
    public CustomerData retrieveCustomerInformation(CustomerManagementRequest request, List<String> operationTypes, ServiceErrors serviceErrors) {
        logger.info("GroupEsbCustomerDataManagementImpl.retrieveCustomerInformation(): Performing operation on SVC0258 Version 11.");
        return groupEsbCustomerDataManagementV11Impl.retrieveCustomerInformation(request, operationTypes, serviceErrors);
    }

    @Override
    public boolean updateCustomerInformation(CustomerData updatedData, ServiceErrors serviceErrors) {
        //Update should be handled separately as update service for each operation is different
        /*
         * P.S. GroupEsbCustomerAddressManagementImpl
         */
        throw new UnsupportedOperationException();
    }
}

package com.bt.nextgen.service.group.customer.groupesb;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.group.customer.CustomerManagementIntegrationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by L069679 on 16/01/2017.
 */
@Service("customerManagementService")
public class GroupEsbCustomerManagementImpl implements CustomerManagementIntegrationService {

    private static final Logger logger = LoggerFactory.getLogger(GroupEsbCustomerManagementImpl.class);

    @Resource(name = "customerManagementV11Service")
    private CustomerManagementIntegrationService groupEsbCustomerManagementV11Impl;

    @Override
    public CustomerRawData retrieveCustomerRawInformation(CustomerManagementRequest request, List<String> operationTypes, ServiceErrors serviceErrors) {
        logger.info("GroupEsbCustomerManagementImpl.retrieveCustomerRawInformation(): Performing operation on SVC0258 Version 10.");
        return groupEsbCustomerManagementV11Impl.retrieveCustomerRawInformation(request, operationTypes, serviceErrors);

    }
}

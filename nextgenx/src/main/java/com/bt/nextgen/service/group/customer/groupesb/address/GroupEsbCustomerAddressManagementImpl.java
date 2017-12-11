package com.bt.nextgen.service.group.customer.groupesb.address;

import com.bt.nextgen.core.toggle.FeatureTogglesService;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.group.customer.CustomerDataManagementIntegrationService;
import com.bt.nextgen.service.group.customer.groupesb.CustomerData;
import com.bt.nextgen.service.group.customer.groupesb.CustomerManagementRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service("addressManagementService")
public class GroupEsbCustomerAddressManagementImpl implements CustomerDataManagementIntegrationService {

    private static final Logger logger = LoggerFactory.getLogger(GroupEsbCustomerAddressManagementImpl.class);

    @Resource(name = "addressManagementV10Service")
    private CustomerDataManagementIntegrationService customerAddressManagementIntegrationService;

    @Autowired
    private FeatureTogglesService togglesService;

    @Override
    public CustomerData retrieveCustomerInformation(CustomerManagementRequest request, List<String> operationTypes,
                                                    ServiceErrors errors) {
        logger.info("GroupEsbCustomerAddressManagementImpl.retrieveCustomerInformation(): Performing operation on SVC0258 Version 10.");
        return customerAddressManagementIntegrationService.retrieveCustomerInformation(request, operationTypes, errors);
    }

    @Override
    public boolean updateCustomerInformation(CustomerData updatedData, ServiceErrors serviceErrors) {
        logger.info("GroupEsbCustomerAddressManagementImpl.updateCustomerInformation(): Performing operation on SVC0258 Version 10.");
        return customerAddressManagementIntegrationService.updateCustomerInformation(updatedData, serviceErrors);
    }

}

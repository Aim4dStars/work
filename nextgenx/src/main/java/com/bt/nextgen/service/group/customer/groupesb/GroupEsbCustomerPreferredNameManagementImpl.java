package com.bt.nextgen.service.group.customer.groupesb;

import com.bt.nextgen.core.toggle.FeatureTogglesService;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.group.customer.CustomerDataManagementIntegrationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service("preferredNameManagementService")
public class GroupEsbCustomerPreferredNameManagementImpl implements CustomerDataManagementIntegrationService {
    private static final Logger logger = LoggerFactory.getLogger(GroupEsbCustomerPreferredNameManagementImpl.class);
    @Resource(name = "preferredNameManagementV10Service")
    private CustomerDataManagementIntegrationService preferredNameManagementV10Service;
    @Autowired
    private FeatureTogglesService togglesService;

    @Override
    public CustomerData retrieveCustomerInformation(CustomerManagementRequest request, List<String> operationTypes,
                                                    ServiceErrors serviceErrors) {
        logger.info("GroupEsbCustomerPreferredNameManagementImpl.retrieveCustomerInformation(): Performing operation on SVC0258 Version 10.");
        return preferredNameManagementV10Service.retrieveCustomerInformation(request, operationTypes, serviceErrors);
    }

    @Override
    public boolean updateCustomerInformation(CustomerData updatedData, ServiceErrors serviceErrors) {
        logger.info("GroupEsbCustomerAddressManagementImpl.updateCustomerInformation(): Performing operation on SVC0258 Version 10.");
        return preferredNameManagementV10Service.updateCustomerInformation(updatedData, serviceErrors);
    }

}

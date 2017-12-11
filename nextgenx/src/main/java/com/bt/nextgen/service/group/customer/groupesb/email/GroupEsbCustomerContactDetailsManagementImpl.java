package com.bt.nextgen.service.group.customer.groupesb.email;

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

@Service("contactDetailsManagementService")
public class GroupEsbCustomerContactDetailsManagementImpl implements CustomerDataManagementIntegrationService {

    private static final Logger logger = LoggerFactory.getLogger(GroupEsbCustomerContactDetailsManagementImpl.class);
    @Resource(name = "contactDetailsManagementv10Service")
    private CustomerDataManagementIntegrationService contactDetailsManagementv10Service;
    @Autowired
    private FeatureTogglesService togglesService;

    @Override
    public CustomerData retrieveCustomerInformation(CustomerManagementRequest request, List<String> operationTypes,
                                                    ServiceErrors serviceErrors) {
        logger.info("GroupEsbCustomerContactDetailsManagementImpl.retrieveCustomerInformation(): Performing operation on SVC0258 Version 10.");
        return contactDetailsManagementv10Service.retrieveCustomerInformation(request, operationTypes, serviceErrors);
    }

    @Override
    public boolean updateCustomerInformation(CustomerData updatedData, ServiceErrors serviceErrors) {
        logger.info("GroupEsbCustomerContactDetailsManagementImpl.updateCustomerInformation(): Performing operation on SVC0258 Version 10.");
        return contactDetailsManagementv10Service.updateCustomerInformation(updatedData, serviceErrors);
    }
}

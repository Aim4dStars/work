package com.bt.nextgen.service.group.customer.groupesb.state;

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

@Service("regStateManagementService")
public class GroupEsbRegStateManagementImpl implements CustomerDataManagementIntegrationService {

    private static final Logger logger = LoggerFactory.getLogger(GroupEsbRegStateManagementImpl.class);

    @Resource(name = "regStateManagementV10Service")
    private CustomerDataManagementIntegrationService regStateManagementV10Service;

    @Autowired
    private FeatureTogglesService togglesService;

    @Override
    public CustomerData retrieveCustomerInformation(CustomerManagementRequest request, List<String> operationTypes, ServiceErrors errors) {
        logger.info("GroupEsbRegStateManagementImpl.retrieveCustomerInformation(): Performing operation on SVC0258 Version 10.");
        return regStateManagementV10Service.retrieveCustomerInformation(request, operationTypes, errors);
    }

    @Override
    public boolean updateCustomerInformation(CustomerData customerData, ServiceErrors serviceErrors) {
        logger.info("GroupEsbRegStateManagementImpl.updateCustomerInformation(): Performing operation on SVC0258 Version 10.");
        return regStateManagementV10Service.updateCustomerInformation(customerData, serviceErrors);
    }
}

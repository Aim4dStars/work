package com.bt.nextgen.service.group.customer.groupesb.address;

import com.bt.nextgen.core.toggle.FeatureTogglesService;
import com.bt.nextgen.core.webservice.provider.CorrelatedResponse;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.group.customer.groupesb.CustomerManagementRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by F057654 on 30/07/2015.
 */
@Service("cacheManagedCustomerDataManagementService")
public class CacheManagedCustomerDataManagementServiceImpl implements CacheManagedCustomerDataManagementService {

    private static final Logger logger = LoggerFactory.getLogger(CacheManagedCustomerDataManagementServiceImpl.class);

    @Resource(name = "cacheManagedCustomerDataManagementv7Service")
    private CacheManagedCustomerDataManagementService cacheManagedCustomerDataManagementV7Service;

    @Resource(name = "cacheManagedCustomerDataManagementv10Service")
    private CacheManagedCustomerDataManagementService cacheManagedCustomerDataManagementv10Service;

    @Autowired
    private FeatureTogglesService togglesService;

    public CorrelatedResponse retrieveCustomerInformation(CustomerManagementRequest request, final ServiceErrors serviceErrors) {
        logger.info("CacheManagedCustomerDataManagementService.retrieveCustomerInformation(): Performing operation on SVC0258 Version 10.");
        return cacheManagedCustomerDataManagementv10Service.retrieveCustomerInformation(request, serviceErrors);
    }

    public void clearCustomerInformation(CustomerManagementRequest request) {
        logger.info("CacheManagedCustomerDataManagementService.clearCustomerInformation(): Performing operation on SVC0258 Version 10.");
        cacheManagedCustomerDataManagementv10Service.clearCustomerInformation(request);
    }
}

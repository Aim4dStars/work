package com.bt.panorama.direct.service.group.customer;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.panorama.direct.api.email.model.PortfolioDetailDto;
import com.bt.panorama.direct.service.group.customer.groupesb.GroupEsbCustomerCommunicationAdapter;


public interface CustomerCommunicationIntegrationService {

    GroupEsbCustomerCommunicationAdapter generateEmailCommunication(PortfolioDetailDto portfolioDetailDto, ServiceErrors serviceErrors);
}
